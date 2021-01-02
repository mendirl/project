package org.mendirl.service.cube.configuration;

import com.activeviam.builders.StartBuilding;
import com.qfs.desc.IDatastoreSchemaDescription;
import com.qfs.server.cfg.IActivePivotManagerDescriptionConfig;
import com.quartetfs.biz.pivot.definitions.IActivePivotManagerDescription;
import com.quartetfs.biz.pivot.definitions.ISelectionDescription;
import org.mendirl.service.cube.activepivot.pivot.EquityDerivativesCubeConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.mendirl.service.cube.configuration.DatastoreDescriptionConfiguration.*;

@Configuration
public class ActivePivotManagerDescriptionConfiguration implements IActivePivotManagerDescriptionConfig {

    private static final String MANAGER_NAME = "EquityDerivativesManager";
    private static final String CATALOG_NAME = "Catalog";
    private static final String SANDBOX_SCHEMA_NAME = "SandboxSchema";

    public static final String CURRENCY = "Currency";
    private static final String PRODUCT_ID = "productId";

    private IDatastoreSchemaDescription datastoreDescription;

    public ActivePivotManagerDescriptionConfiguration(IDatastoreSchemaDescription datastoreDescription) {
        this.datastoreDescription = datastoreDescription;
    }

    @Bean
    @Override
    public IActivePivotManagerDescription managerDescription() {
        return StartBuilding.managerDescription(MANAGER_NAME)
            .withCatalog(CATALOG_NAME)
            .containingAllCubes()
            .withSchema(SANDBOX_SCHEMA_NAME)
            .withSelection(createSelectionDescription(this.datastoreDescription))
            .withCube(EquityDerivativesCubeConfig.createCubeDescription(false))
            .build();
    }

    /**
     * Creates the {@link ISelectionDescription} for the Sandbox schema.
     *
     * @param datastoreDescription The datastore description
     * @return The created selection description
     */
    private ISelectionDescription createSelectionDescription(
        final IDatastoreSchemaDescription datastoreDescription) {
        return StartBuilding.selection(datastoreDescription)
            .fromBaseStore(RISK_STORE_NAME)
            .withAllReachableFields()
            .withAlias(PRODUCT__UNDERLIER_CURRENCY, CURRENCY)
            .withAlias(TRADE__PRODUCT_ID, PRODUCT_ID)
            .build();
    }

}
