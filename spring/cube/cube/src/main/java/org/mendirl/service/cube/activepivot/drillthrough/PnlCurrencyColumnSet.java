package org.mendirl.service.cube.activepivot.drillthrough;

import com.qfs.store.IDatastoreVersion;
import com.qfs.store.query.IDictionaryCursor;
import com.qfs.store.record.IRecordReader;
import com.quartetfs.biz.pivot.context.drillthrough.ICalculatedDrillthroughColumnSet;
import com.quartetfs.biz.pivot.context.drillthrough.IDrillthroughQueryContext;
import com.quartetfs.biz.pivot.context.drillthrough.impl.ACalculatedDrillthroughColumn;
import com.quartetfs.biz.pivot.context.drillthrough.impl.ACalculatedDrillthroughColumnSet;
import com.quartetfs.biz.pivot.definitions.ICalculatedDrillthroughColumnDescription;
import com.quartetfs.biz.pivot.definitions.impl.CalculatedDrillthroughColumnDescription;
import com.quartetfs.biz.pivot.query.IQueryCache;
import com.quartetfs.fwk.QuartetExtendedPluginValue;
import gnu.trove.map.TObjectDoubleMap;
import gnu.trove.map.hash.TObjectDoubleHashMap;
import org.mendirl.service.cube.activepivot.postprocessor.ForexHandler;
import org.mendirl.service.cube.activepivot.postprocessor.ForexStream;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.mendirl.service.cube.configuration.DatastoreDescriptionConfiguration.*;

/**
 * A drillthrough column set that is able to generate "on the fly"; that is to say when a drillthrough query
 * is launched (in real time or not), a set of new (calculated) drillthrough columns. In this example, it creates
 * six columns to display pnl values for each currency defined in the sandbox.
 * <p>
 * There are two important things to notice. The first is the use of {@link IDrillthroughQueryContext context} to generate
 * new columns into which different attributes deduced from the context are injected. The second is the fact that
 * conversion rates are computed when the columns are generated (when the query is launched). It means that in real
 * time, the rate applied to the pnl values are the ones computed when the query was launched. <b>The rate is and must
 * be the same as long as the continuous query lives.</b> This is mainly because calculated columns can't have an associated
 * streams and handlers like post processors (cf. {@link ForexHandler},  {@link ForexStream}).
 *
 * @author Quartet FS
 * @see IDrillthroughQueryContext
 * @see PnlCurrencyColumn
 */
@QuartetExtendedPluginValue(intf = ICalculatedDrillthroughColumnSet.class, key = PnlCurrencyColumnSet.PLUGIN_KEY)
public class PnlCurrencyColumnSet extends ACalculatedDrillthroughColumnSet {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /**
     * the logger
     **/
    private static Logger logger = Logger.getLogger(PnlCurrencyColumnSet.class.getName());

    /**
     * Calculated column set plugin key
     */
    public static final String PLUGIN_KEY = "PnlCurrencyColumnSet";

    protected static final String SEPARATOR = ",";

    /**
     * Key to retrieve from the {@link Properties} the prefix to apply
     * to each column's name that will be generated.
     */
    public static final String PREFIX_KEY = "prefix";

    /**
     * Key to retrieve from the {@link Properties} the existing currencies
     */
    public static final String CURRENCIES_KEY = "currencies";

    /**
     * List of available currencies
     */
    protected final String[] underlierCurrencies;

    /**
     * Store the prefix retrieve from the {@link Properties}
     */
    public final String baseName;

    /**
     * Constructor.
     *
     * @param properties properties associated to be given to this {@link ICalculatedDrillthroughColumnSet}
     *                   to parameterize the implementation class.
     */
    public PnlCurrencyColumnSet(Properties properties) {
        super(properties);

        this.baseName = (String) properties.get(PREFIX_KEY);
        this.underlierCurrencies = ((String) properties.get(CURRENCIES_KEY)).split(SEPARATOR);
    }

    @Override
    public Collection<ICalculatedDrillthroughColumnDescription> generate(IDrillthroughQueryContext queryContext) {
        // Retrieve the IQueryCache from the context. It is used to get
        // access to the current datastore version.
        final IQueryCache queryCache = queryContext.getContext().get(IQueryCache.class);
        if (queryCache == null) {
            logger.log(Level.FINE, "The query cache is null, cannot retrieve a " + IDatastoreVersion.class);
            return null;
        }

        // Get rates from the datastore for this version
        Map<String, TObjectDoubleMap<String>> currencies = getAllRatesByCurrency(queryCache.getDatastoreVersion());

        // underlying field of the columns to generate
        String underlyingFields = ACalculatedDrillthroughColumn.buildFieldProperty(
            RISK__PNL, "Currency");

        // For each currency, it creates a new column responsible for
        // giving the pnl in a given currency.
        List<ICalculatedDrillthroughColumnDescription> descriptions = new ArrayList<>();
        for (String underlierCurrency : underlierCurrencies) {
            // The rates for the given currency is added into properties
            // The calculated column generated will introspect it to
            // extract the conversion rate and convert the pnl value
            Properties properties = new Properties();
            properties.put(PnlCurrencyColumn.RATE_KEY, currencies.get(underlierCurrency));

            // Use a different name for each column. Here the name depends on the currency.
            descriptions.add(new CalculatedDrillthroughColumnDescription(PnlCurrencyColumn.PLUGIN_KEY,
                baseName + " " + underlierCurrency, underlyingFields, properties));
        }

        return descriptions;
    }

    /**
     * Retrieve the all rates between each currency from the {@link IDatastoreVersion datastore}.
     *
     * @param datastoreVersion the {@link IDatastoreVersion datastore}.
     * @return a map containing the rates between each currency indexed by currency.
     */
    protected Map<String, TObjectDoubleMap<String>> getAllRatesByCurrency(IDatastoreVersion datastoreVersion) {
        // Get the content of the datastore for this version
        IDictionaryCursor cursor = datastoreVersion.getQueryRunner().forStore(FOREX_STORE_NAME)
            .withoutCondition().selectingAllStoreFields().onCurrentThread().run();

        Map<String, TObjectDoubleMap<String>> currencies = new HashMap<>();
        for (IRecordReader reader : cursor) {
            String cur1 = (String) reader.read(FOREX_CURRENCY);
            String cur2 = (String) reader.read(FOREX_TARGET_CURRENCY);
            double rate = (double) reader.read(FOREX_RATE);
            if (!currencies.containsKey(cur1)) {
                currencies.put(cur1, new TObjectDoubleHashMap<String>());
                currencies.get(cur1).put(cur1, 1.0d);// 1 for the currency itself
            }
            currencies.get(cur1).put(cur2, rate);
        }

        return currencies;
    }

    @Override
    public String getType() {
        return PnlCurrencyColumnSet.PLUGIN_KEY;
    }

}
