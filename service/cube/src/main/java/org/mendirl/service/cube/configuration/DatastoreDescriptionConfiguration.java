package org.mendirl.service.cube.configuration;

import com.qfs.desc.IDatastoreSchemaDescription;
import com.qfs.desc.IReferenceDescription;
import com.qfs.desc.IStoreDescription;
import com.qfs.desc.IStoreDescriptionBuilder;
import com.qfs.desc.impl.DatastoreSchemaDescription;
import com.qfs.desc.impl.ReferenceDescription;
import com.qfs.desc.impl.StoreDescriptionBuilder;
import com.qfs.server.cfg.IDatastoreDescriptionConfig;
import com.quartetfs.fwk.format.impl.LocalDateParser;
import org.mendirl.service.cube.activepivot.model.Trade;
import org.mendirl.service.cube.activepivot.type.BookIdLiteralType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collection;
import java.util.LinkedList;

import static com.qfs.literal.ILiteralType.*;

@Configuration
public class DatastoreDescriptionConfiguration implements IDatastoreDescriptionConfig {

    @Bean
    @Override
    public IDatastoreSchemaDescription schemaDescription() {
        return new DatastoreSchemaDescription(stores(), references());
    }

    /**
     * Name of the product store
     */
    private static final String PRODUCT_STORE_NAME = "Product";

    /**
     * Name of the trade store
     */
    public static final String TRADE_STORE_NAME = "Trade";

    /**
     * Name of the risk store
     */
    public static final String RISK_STORE_NAME = "Risk";

    /**
     * Name of the forex store
     */
    public static final String FOREX_STORE_NAME = "Forex";

    /**
     * Name of the counterparty store
     */
    private static final String COUNTERPARTY_STORE_NAME = "CounterParty";

    /**
     * Name of the city store
     */
    private static final String CITY_STORE_NAME = "City";

    /**
     * Name of the reference from the risk store to the trade store
     */
    private static final String RISK_TO_TRADE_REF = "RiskToTrade";

    /**
     * Name of the reference from the trade store to the product store
     */
    private static final String TRADE_TO_PRODUCT_REF = "TradeToProduct";

    /**
     * Name of the reference from the trade store to the counterparty store
     */
    private static final String TRADE_TO_COUNTERPARTY_REF = "TradeToCounterparty";

    /**
     * Name of the reference from the counterparty store to the city store
     */
    private static final String COUNTERPARTY_TO_CITY_REF = "CounterpartyToCity";

    // ////////////////////////////////////////////////
    // Fields
    // ////////////////////////////////////////////////

    // ///////////////////////////////////////////////
    // Product store fields

    private static final String PRODUCT__ID = "Id";
    public static final String PRODUCT__NAME = "ProductName";
    public static final String PRODUCT__TYPE = "ProductType";
    public static final String PRODUCT__UNDERLIER_CODE = "UnderlierCode";
    public static final String PRODUCT__UNDERLIER_CURRENCY = "UnderlierCurrency";
    public static final String PRODUCT__UNDERLIER_TYPE = "UnderlierType";
    private static final String PRODUCT__UNDERLIER_VALUE = "UnderlierValue";
    public static final String PRODUCT__BASE_MTM = "ProductBaseMtm";
    private static final String PRODUCT__BUMPED_MTM_UP = "BumpedMtmUp";
    private static final String PRODUCT__BUMPED_MTM_DOWN = "BumpedMtmDown";
    public static final String PRODUCT__THETA = "theta";
    public static final String PRODUCT__RHO = "rho";

    // ///////////////////////////////////////////////
    // Trade store fields

    private static final String TRADE__ID = "Id";
    public static final String TRADE__PRODUCT_ID = "ProductId";
    private static final String TRADE__PRODUCT_QTY_MULTIPLIER = "ProductQtyMultiplier";
    public static final String TRADE__DESK = "Desk";
    public static final String TRADE__BOOK_ID = "BookId";
    private static final String TRADE__COUNTERPARTY = "CounterParty";
    public static final String TRADE__DATE = "Date";
    public static final String TRADE__DATE_BUCKET = "DateBucket";
    public static final String TRADE__STATUS = "Status";
    public static final String TRADE__IS_SIMULATED = "IsSimulated";

    // ///////////////////////////////////////////////
    // Risk store fields

    public static final String RISK__TRADE_ID = "TradeId";
    public static final String RISK__AS_OF_DATE = "AsOfDate";
    public static final String RISK__HOST_NAME = "HostName";
    public static final String RISK__DELTA = "delta";
    public static final String RISK__PNL_DELTA = "pnlDelta";
    public static final String RISK__GAMMA = "gamma";
    public static final String RISK__VEGA = "vega";
    public static final String RISK__PNL_VEGA = "pnlVega";
    public static final String RISK__PNL = "pnl";

    // ///////////////////////////////////////////////
    // Forex store fields

    public static final String FOREX_CURRENCY = "Currency";
    public static final String FOREX_TARGET_CURRENCY = "TargetCurrency";
    public static final String FOREX_RATE = "Rate";

    // ///////////////////////////////////////////////
    // Counterparty store fields

    public static final String COUNTERPARTY__COUNTERPARTY = "CounterParty";
    public static final String COUNTERPARTY__COUNTERPARTY_GROUP = "CounterPartyGroup";
    private static final String COUNTERPARTY__SECTOR = "Sector";
    private static final String COUNTERPARTY__RATING = "Rating";
    private static final String COUNTERPARTY__CITY = "City";

    // ///////////////////////////////////////////////
    // City store fields

    private static final String CITY_NAME = "Name";
    public static final String CITY_OBJECT = "City";

    // ////////////////////////////////////////////////
    // Stores
    // ////////////////////////////////////////////////

    /**
     * @return the description of the product store
     */
    private IStoreDescription productStoreDescription() {
        return new StoreDescriptionBuilder()
            .withStoreName(PRODUCT_STORE_NAME)
            .withField(PRODUCT__ID, INT).asKeyField()
            .withField(PRODUCT__NAME)
            .withField(PRODUCT__TYPE)
            .withField(PRODUCT__UNDERLIER_CODE)
            .withField(PRODUCT__UNDERLIER_CURRENCY)
            .withField(PRODUCT__UNDERLIER_TYPE)
            .withField(PRODUCT__UNDERLIER_VALUE, DOUBLE)
            .withField(PRODUCT__BASE_MTM, DOUBLE)
            .withField(PRODUCT__BUMPED_MTM_UP, DOUBLE)
            .withField(PRODUCT__BUMPED_MTM_DOWN, DOUBLE)
            .withField(PRODUCT__THETA, DOUBLE)
            .withField(PRODUCT__RHO, DOUBLE)
            .build();
    }

    /**
     * @return the description of the trade store
     */
    private IStoreDescription tradeStoreDescription() {
        return new StoreDescriptionBuilder()
            .withStoreName(TRADE_STORE_NAME)
            .withField(TRADE__ID, LONG).asKeyField()
            .withField(TRADE__PRODUCT_ID, INT)
            .withField(TRADE__PRODUCT_QTY_MULTIPLIER, DOUBLE)
            .withField(TRADE__DESK)
            .withField(TRADE__BOOK_ID, BookIdLiteralType.LITERAL, new Trade.DefaultBookId())
            .withField(TRADE__COUNTERPARTY)
            .withField(TRADE__DATE, "localDate[" + LocalDateParser.DEFAULT_PATTERN + "]")
            .withField(TRADE__DATE_BUCKET, OBJECT)
            .withField(TRADE__STATUS)
            .withField(TRADE__IS_SIMULATED)
            .withModuloPartitioning(TRADE__ID, 8)
            .build();
    }

    /**
     * @return the description of the risk store
     */
    private IStoreDescription riskStoreDescription() {
        IStoreDescriptionBuilder.IKeyed sb = new StoreDescriptionBuilder()
            .withStoreName(RISK_STORE_NAME)
            .withField(RISK__TRADE_ID, LONG).asKeyField()
            .withField(RISK__AS_OF_DATE, "localDate[" + LocalDateParser.DEFAULT_PATTERN + "]")
            .asKeyField()
            .withField(RISK__HOST_NAME)
            .withField(RISK__DELTA, DOUBLE)
            .withField(RISK__PNL_DELTA, DOUBLE)
            .withField(RISK__GAMMA, DOUBLE)
            .withField(RISK__VEGA, DOUBLE)
            .withField(RISK__PNL_VEGA, DOUBLE)
            .withField(RISK__PNL, DOUBLE)
            .withModuloPartitioning(RISK__TRADE_ID, 8)
            .withValuePartitioningOn(RISK__AS_OF_DATE);

        return sb.build();
    }

    /**
     * @return the description of the forex store
     */
    private IStoreDescription forexStoreDescription() {
        return new StoreDescriptionBuilder()
            .withStoreName(FOREX_STORE_NAME)
            .withField(FOREX_CURRENCY).asKeyField()
            .withField(FOREX_TARGET_CURRENCY).asKeyField()
            .withField(FOREX_RATE, DOUBLE)
            .build();
    }

    /**
     * @return The description of the counterparty store
     */
    private IStoreDescription counterpartyStoreDescription() {
        return new StoreDescriptionBuilder().withStoreName(COUNTERPARTY_STORE_NAME)
            .withField(COUNTERPARTY__COUNTERPARTY, STRING).asKeyField()
            .withField(COUNTERPARTY__COUNTERPARTY_GROUP, STRING)
            .withField(COUNTERPARTY__SECTOR, STRING)
            .withField(COUNTERPARTY__RATING, STRING)
            .withField(COUNTERPARTY__CITY, STRING)
            .build();
    }

    /**
     * @return The description of the city store
     */
    private IStoreDescription cityStoreDescription() {
        return new StoreDescriptionBuilder().withStoreName(CITY_STORE_NAME)
            .withField(CITY_NAME, STRING).asKeyField()
            .withField(CITY_OBJECT, OBJECT)
            .build();
    }

    // ////////////////////////////////////////////////
    // References
    // ////////////////////////////////////////////////

    /**
     * @return the references between stores
     */
    private Collection<IReferenceDescription> references() {
        final Collection<IReferenceDescription> references = new LinkedList<>();
        references.add(ReferenceDescription.builder()
            .fromStore(RISK_STORE_NAME)
            .toStore(TRADE_STORE_NAME)
            .withName(RISK_TO_TRADE_REF)
            .withMapping(RISK__TRADE_ID, TRADE__ID)
            .build());
        references.add(ReferenceDescription.builder()
            .fromStore(TRADE_STORE_NAME)
            .toStore(PRODUCT_STORE_NAME)
            .withName(TRADE_TO_PRODUCT_REF)
            .withMapping(TRADE__PRODUCT_ID, PRODUCT__ID)
            .dontIndexOwner()
            .build());
        references.add(ReferenceDescription.builder()
            .fromStore(TRADE_STORE_NAME)
            .toStore(COUNTERPARTY_STORE_NAME)
            .withName(TRADE_TO_COUNTERPARTY_REF)
            .withMapping(TRADE__COUNTERPARTY, COUNTERPARTY__COUNTERPARTY)
            .build());
        references.add(ReferenceDescription.builder()
            .fromStore(COUNTERPARTY_STORE_NAME)
            .toStore(CITY_STORE_NAME)
            .withName(COUNTERPARTY_TO_CITY_REF)
            .withMapping(COUNTERPARTY__CITY, CITY_NAME)
            .build());
        return references;
    }


    private Collection<IStoreDescription> stores() {
        final Collection<IStoreDescription> stores = new LinkedList<>();

        stores.add(productStoreDescription());
        stores.add(tradeStoreDescription());
        stores.add(counterpartyStoreDescription());
        stores.add(cityStoreDescription());
        stores.add(riskStoreDescription());
        stores.add(forexStoreDescription());

        return stores;
    }

}
