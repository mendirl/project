package org.mendirl.service.cube.configuration;

import com.qfs.condition.impl.BaseConditions;
import com.qfs.msg.*;
import com.qfs.msg.csv.*;
import com.qfs.msg.csv.filesystem.impl.FileSystemCSVTopicFactory;
import com.qfs.msg.csv.impl.CSVSource;
import com.qfs.msg.csv.translator.impl.AColumnCalculator;
import com.qfs.msg.impl.EmptyCalculator;
import com.qfs.server.cfg.IDatastoreConfig;
import com.qfs.source.impl.CSVMessageChannelFactory;
import com.qfs.source.impl.Fetch;
import com.qfs.source.impl.POJOMessageChannelFactory;
import com.qfs.store.IDatastoreSchemaMetadata;
import com.qfs.store.log.impl.LogWriteException;
import com.qfs.store.selection.impl.Selection;
import com.qfs.store.transaction.DatastoreTransactionException;
import com.qfs.util.impl.QfsFiles;
import com.quartetfs.fwk.QuartetRuntimeException;
import org.mendirl.service.cube.activepivot.model.Rate;
import org.mendirl.service.cube.activepivot.source.ForexRateGenerator;
import org.mendirl.service.cube.activepivot.source.RiskCalculator;
import org.mendirl.service.cube.activepivot.source.TradeGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import java.io.File;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.*;

import static org.mendirl.service.cube.activepivot.source.DataSource.TOPIC_FOREX;
import static org.mendirl.service.cube.configuration.DatastoreDescriptionConfiguration.*;

@Configuration
public class SourceConfig {

    protected static Logger LOGGER = LoggerFactory.getLogger(SourceConfig.class);

    /**
     * Application datastore, automatically wired
     */
    @Autowired
    private IDatastoreConfig datastoreConfig;

    // ---------------------------------------------------------- //
    // This section handles the loading from CSV files            //
    // ---------------------------------------------------------- //

    /**
     * The name of the trade source file.
     */
    public static final String TRADE_STORE_FILE = "trades.csv";
    /**
     * The name of the product source file.
     */
    public static final String PRODUCT_STORE_FILE = "products.csv";
    /**
     * The name of the risk source file.
     */
    public static final String RISKENTRIES_STORE_FILE = "riskentries.csv";
    /**
     * The name of the forex source file.
     */
    public static final String FOREX_STORE_FILE = "forex.csv";
    /**
     * The name of the counterparty source file.
     */
    public static final String COUNTERPARTY_STORE_FILE = "counterparties.csv";

    /**
     * The separator used to separate two columns in the CSV files
     */
    public static final char CSV_SEPARATOR = '|';

    public static final String TRADES_TOPIC = TRADE_STORE_NAME;
    public static final String RISKS_TOPIC = RISK_STORE_NAME;
    public static final String PRODUCTS_TOPIC = PRODUCT_STORE_NAME;
    public static final String COUNTERPARTY_TOPIC = COUNTERPARTY_STORE_NAME;

    /**
     * Once the ActivePivot Manager is ready, we fetch the initial data set.
     * Loads data from CSV files.
     *
     * @return nothing
     * @throws LogWriteException if the log cannot be written or handled
     */
    @Bean
    @DependsOn({"startManager", "registerUpdateWhereTriggers"})
    public Void csvInitialLoad() throws LogWriteException {
        // CSV loading is disabled by default.
//        final boolean doLoad = Boolean.parseBoolean(env.getProperty("csvSource.enabled", "false"));
        final boolean doLoad = true;

        //If CSV loading is disabled, we only fetch for the forex store
        final Collection<String> stores = doLoad
            ? Arrays.asList(PRODUCT_STORE_NAME, TRADE_STORE_NAME, COUNTERPARTY_STORE_NAME, RISK_STORE_NAME, FOREX_STORE_NAME)
            : Arrays.asList(FOREX_STORE_NAME);
        final Fetch<IFileInfo<Path>, ILineReader> initialLoad = new Fetch<IFileInfo<Path>, ILineReader>(csvChannelFactory(), stores) {
            @Override
            protected void doFetch(final ISource<IFileInfo<Path>, ILineReader> source) {
                super.doFetch(source);
                initializeForex();
            }
        };
        final long before = System.currentTimeMillis();
        initialLoad.fetch(csvSource());
        final long elapsed = System.currentTimeMillis() - before;

        if (doLoad) {
            LOGGER.info("CSV data load completed in " + elapsed + "ms.");
        }
        return null;
    }

    /**
     * Register an update where procedure trigger that computes all the risks attributes (pnl,
     * delta, ...) for any given risk entry.
     *
     * @return null
     * @throws DatastoreTransactionException if could not register the trigger
     */
    @Bean
    public Void registerUpdateWhereTriggers() throws DatastoreTransactionException {
        IDatastoreSchemaMetadata datastoreSchemaMetadata = datastoreConfig.datastore().getSchemaMetadata();

        int deltaIndex = datastoreSchemaMetadata.getFieldIndex(RISK_STORE_NAME, RISK__DELTA);
        int hostnameIndex = datastoreSchemaMetadata.getFieldIndex(RISK_STORE_NAME, RISK__HOST_NAME);
        int pnlDeltaIndex = datastoreSchemaMetadata.getFieldIndex(RISK_STORE_NAME, RISK__PNL_DELTA);
        int gammaIndex = datastoreSchemaMetadata.getFieldIndex(RISK_STORE_NAME, RISK__GAMMA);
        int vegaIndex = datastoreSchemaMetadata.getFieldIndex(RISK_STORE_NAME, RISK__VEGA);
        int pnlIndex = datastoreSchemaMetadata.getFieldIndex(RISK_STORE_NAME, RISK__PNL);
        int pnlVegaIndex = datastoreSchemaMetadata.getFieldIndex(RISK_STORE_NAME, RISK__PNL_VEGA);

        datastoreConfig.datastore()
            .getTransactionManager()
            .registerCommitTimeUpdateWhereTrigger(
                "Risk Calculator Trigger",
                0, // Priority is not important, as we have only one trigger.
                // The fields required to perform the computation
                new Selection(RISK_STORE_NAME,
                    "RiskToTrade/TradeToProduct/UnderlierValue",
                    "RiskToTrade/TradeToProduct/BumpedMtmUp",
                    "RiskToTrade/TradeToProduct/BumpedMtmDown",
                    "RiskToTrade/ProductQtyMultiplier"
                ),
                BaseConditions.TRUE, // We want to match all facts
                new RiskCalculator(hostnameIndex, deltaIndex, pnlDeltaIndex, gammaIndex,
                    vegaIndex, pnlIndex, pnlVegaIndex)
            );

        return null;
    }

    /**
     * Initialises forex datastore with all currency rates
     */
    private void initializeForex() {
        // Creates a channel to initialise forex store with all currencies
        final POJOMessageChannelFactory factory = new POJOMessageChannelFactory(SourceConfig.this.datastoreConfig.datastore());
        final IMessageChannel<String, Object> channel = factory.createChannel(TOPIC_FOREX, FOREX_STORE_NAME);

        // Loads all currency rates
        final List<Rate> items = Arrays.asList(forexRateGenerator().init());
        final Iterator<Rate> itemIterator = items.iterator();

        // Creates a message to send the trades on the trade channel
        // This method also starts a transaction on the datastore
        final IMessage<String, Object> itemMessage = channel.newMessage(TOPIC_FOREX);
        final IMessageChunk<Object> itemChunk = itemMessage.newChunk();

        while (itemIterator.hasNext()) {
            itemChunk.append(itemIterator.next());
        }
        itemMessage.append(itemChunk);

        // Completes the message i.e. commit the transaction
        channel.send(itemMessage);
    }

    /**
     * The forex generator is the object used to generate forex exchange rate.
     *
     * @return The forex generator.
     */
    @Bean
    public ForexRateGenerator forexRateGenerator() {
        return new ForexRateGenerator(datastoreConfig.datastore());
    }

    /**
     * The trade generator is the object used to generate trades. Using the same
     * instance makes sure that we use the same date buckets.
     *
     * @return The trade generator.
     */
    @Bean
    public TradeGenerator tradeGenerator() {
        return new TradeGenerator();
    }

    /**
     * Channel factory bean. To create channels that convey data from the source
     * to the datastore.
     * <p>
     * That is here that we add the {@link IColumnCalculator column calculators}
     * that are able to parse complex data in the csv.
     *
     * @return channel factory
     */
    @Bean
    public CSVMessageChannelFactory<Path> csvChannelFactory() {
        final CSVMessageChannelFactory<Path> csvChannelFactory = new CSVMessageChannelFactory<>(
            csvSource(),
            datastoreConfig.datastore());

        // The trade generator, used to retrieve the date bucket.
        final TradeGenerator tg = tradeGenerator();

        // We used the name of the store as the topic name (see
        // createWatcher)
        final String tradeStore = TRADE_STORE_NAME;
        final String riskStore = RISK_STORE_NAME;

        // Define column calculators

        // Calculator that calculates the date and data bucket for the trade store
        AColumnCalculator<ILineReader> dateCalculator = new AColumnCalculator<ILineReader>(TRADE__DATE_BUCKET) {

            @Override
            public Object compute(final IColumnCalculationContext<ILineReader> context) {
                // Use getValue so that we parse the Date column only
                // once.
                return tg.getDateBucket((LocalDate) context.getValue(TRADE__DATE));
            }
        };

        // Calculators that calculates the date for the risk store.
        List<IColumnCalculator<ILineReader>> riskCalculators = Arrays.<IColumnCalculator<ILineReader>>asList(
            new EmptyCalculator<>("HostName"),
            new EmptyCalculator<>("delta"),
            new EmptyCalculator<>("pnlDelta"),
            new EmptyCalculator<>("gamma"),
            new EmptyCalculator<>("vega"),
            new EmptyCalculator<>("pnlVega"),
            new EmptyCalculator<>("pnl"));

        csvChannelFactory.setCalculatedColumns(
            TRADES_TOPIC,
            tradeStore,
            Collections.<IColumnCalculator<ILineReader>>singletonList(dateCalculator));

        csvChannelFactory.setCalculatedColumns(riskStore, riskStore, riskCalculators);

        return csvChannelFactory;
    }

    /**
     * Creates the {@link CSVSource} responsible for loading the initial data in
     * the datastore from csv files.
     *
     * @return the {@link CSVSource}
     */
    @Bean
    public ICSVSource<Path> csvSource() {
        final FileSystemCSVTopicFactory factory = csvTopicFactory();
        final CSVSource<Path> source = new CSVSource<>();

        // Verify that the CSV folder exists
        final String dataSet = "data";
        try {
            final File file = new File(QfsFiles.getResourceUrl(dataSet).toURI());
            if (!file.exists() && !file.isDirectory()) {
                LOGGER.error("The csvSource.dataset does not point to a valid directory: " + file.getAbsolutePath());
                throw new QuartetRuntimeException(
                    "The csvSource.dataset does not point to a valid directory: " + file.getAbsolutePath());
            }
        } catch (Exception e) {
            LOGGER.error("The csvSource.dataset does not point to a valid directory: " + dataSet, e);
            throw new QuartetRuntimeException(
                "The csvSource.dataset does not point to a valid directory: " + dataSet,
                e);
        }

        final IDatastoreSchemaMetadata schemaMetadata = datastoreConfig.datastore().getSchemaMetadata();

        final String filePath = dataSet + System.getProperty("file.separator");
        // Create the CSV topics.
        final List<String> tradeFileColumns = schemaMetadata.getFields(TRADE_STORE_NAME);
        tradeFileColumns.remove(TRADE__DATE_BUCKET);
        final ICSVTopic<Path> trades = factory.createTopic(TRADES_TOPIC, filePath + TRADE_STORE_FILE, source.createParserConfiguration(tradeFileColumns));
        trades.getParserConfiguration().setSeparator(CSV_SEPARATOR);
        source.addTopic(trades);

        final List<String> productFileColumns = schemaMetadata.getFields(PRODUCT_STORE_NAME);
        final ICSVTopic<Path> products = factory.createTopic(PRODUCTS_TOPIC, filePath + PRODUCT_STORE_FILE, source.createParserConfiguration(productFileColumns));
        products.getParserConfiguration().setSeparator(CSV_SEPARATOR);
        source.addTopic(products);

        final List<String> riskFileColumns = Arrays.asList(RISK__TRADE_ID, RISK__AS_OF_DATE);

        final ICSVTopic<Path> risks = factory.createTopic(RISKS_TOPIC, filePath + RISKENTRIES_STORE_FILE, source.createParserConfiguration(riskFileColumns));
        risks.getParserConfiguration().setSeparator(CSV_SEPARATOR);
        source.addTopic(risks);

        final List<String> fxFileColumns = schemaMetadata.getFields(FOREX_STORE_NAME);
        final ICSVTopic<Path> forex = factory.createTopic(FOREX_STORE_NAME, filePath + FOREX_STORE_FILE, source.createParserConfiguration(fxFileColumns));
        forex.getParserConfiguration().setSeparator(CSV_SEPARATOR);
        source.addTopic(forex);

        final List<String> counterpartyFileColumns = schemaMetadata.getFields(COUNTERPARTY_STORE_NAME);
        final ICSVTopic<Path> counterparties = factory.createTopic(COUNTERPARTY_TOPIC, filePath + COUNTERPARTY_STORE_FILE, source.createParserConfiguration(counterpartyFileColumns));
        counterparties.getParserConfiguration().setSeparator(CSV_SEPARATOR);
        source.addTopic(counterparties);

        final Properties sourceProps = new Properties();
        sourceProps.setProperty(ICSVSourceConfiguration.PARSER_THREAD_PROPERTY, "4");

        sourceProps.setProperty(ICSVSourceConfiguration.BUFFER_SIZE_PROPERTY, "1024");


        source.configure(sourceProps);

        return source;
    }

    /**
     * Topic factory bean. Allows to create CSV topics and watch changes to directories. Autocloseable.
     *
     * @return the topic factory
     */
    @Bean
    public FileSystemCSVTopicFactory csvTopicFactory() {
        return new FileSystemCSVTopicFactory(false);
    }

}
