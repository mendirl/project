package org.mendirl.service.cube.configuration;

import com.qfs.desc.IDatastoreSchemaDescription;
import com.qfs.desc.IReferenceDescription;
import com.qfs.desc.IStoreDescription;
import com.qfs.desc.IStoreDescriptionBuilder;
import com.qfs.desc.impl.DatastoreSchemaDescription;
import com.qfs.desc.impl.StoreDescriptionBuilder;
import com.qfs.literal.ILiteralType;
import com.qfs.server.cfg.IDatastoreDescriptionConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collection;
import java.util.LinkedList;

@Configuration
public class DatastoreDescriptionConfiguration implements IDatastoreDescriptionConfig {

    /**
     * Name of the risk store
     */
    public static final String RISK_STORE_NAME = "Risk";

    public static final String RISK__TRADE_ID = "TradeId";
    public static final String RISK__AS_OF_DATE = "AsOfDate";
    public static final String RISK__HOST_NAME = "HostName";
    public static final String RISK__DELTA = "delta";
    public static final String RISK__PNL_DELTA = "pnlDelta";
    public static final String RISK__GAMMA = "gamma";
    public static final String RISK__VEGA = "vega";
    public static final String RISK__PNL_VEGA = "pnlVega";
    public static final String RISK__PNL = "pnl";

    @Bean
    @Override
    public IDatastoreSchemaDescription schemaDescription() {
        return new DatastoreSchemaDescription(stores(), references());
    }

    /**
     * @return the description of the risk store
     */
    private IStoreDescription riskStoreDescription() {
        IStoreDescriptionBuilder.IKeyed sb = new StoreDescriptionBuilder()
            .withStoreName(RISK_STORE_NAME)
            .withField(RISK__TRADE_ID, ILiteralType.LONG).asKeyField()
            .withField(RISK__AS_OF_DATE, ILiteralType.LOCAL_DATE)
            .asKeyField()
            .withField(RISK__HOST_NAME)
            .withField(RISK__DELTA, ILiteralType.DOUBLE)
            .withField(RISK__PNL_DELTA, ILiteralType.DOUBLE)
            .withField(RISK__GAMMA, ILiteralType.DOUBLE)
            .withField(RISK__VEGA, ILiteralType.DOUBLE)
            .withField(RISK__PNL_VEGA, ILiteralType.DOUBLE)
            .withField(RISK__PNL, ILiteralType.DOUBLE)
            .withModuloPartitioning(RISK__TRADE_ID, 8)
            .withValuePartitioningOn(RISK__AS_OF_DATE);

        return sb.build();
    }

    public Collection<IStoreDescription> stores() {
        final Collection<IStoreDescription> stores = new LinkedList<>();

        stores.add(riskStoreDescription());

        return stores;
    }

    public Collection<IReferenceDescription> references() {
        final Collection<IReferenceDescription> references = new LinkedList<>();

        return references;
    }
}
