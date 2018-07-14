package org.mendirl.service.cube.activeviam;

import com.activeviam.builders.StartBuilding;
import com.activeviam.desc.build.ICanBuildCubeDescription;
import com.activeviam.desc.build.ICanStartBuildingMeasures;
import com.activeviam.desc.build.IHasAtLeastOneMeasure;
import com.activeviam.desc.build.dimensions.ICanStartBuildingDimensions;
import com.qfs.desc.IDatastoreSchemaDescription;
import com.qfs.server.cfg.IActivePivotManagerDescriptionConfig;
import com.quartetfs.biz.pivot.definitions.IActivePivotInstanceDescription;
import com.quartetfs.biz.pivot.definitions.IActivePivotManagerDescription;
import com.quartetfs.biz.pivot.definitions.ISelectionDescription;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.mendirl.service.cube.activeviam.DatastoreDescriptionConfiguration.*;

@Configuration
public class ActivePivotManagerDescriptionConfiguration implements IActivePivotManagerDescriptionConfig {

    private static final String MANAGER_NAME = "EquityDerivativesManager";
    private static final String CATALOG_NAME = "Catalog";
    private static final String SANDBOX_SCHEMA_NAME = "SandboxSchema";
    private static final String CUBE_NAME = "EquityDerivativesCube";

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
            .withCube(createActivePivotInstanceDescription())
            .build();
    }

    private IActivePivotInstanceDescription createActivePivotInstanceDescription() {
        return StartBuilding.cube(CUBE_NAME)
            .withMeasures(ActivePivotManagerDescriptionConfiguration::measures)
            .withDimensions(ActivePivotManagerDescriptionConfiguration::dimensions)
            .build();
    }

    private static ICanBuildCubeDescription<IActivePivotInstanceDescription> dimensions(ICanStartBuildingDimensions builder) {
        return builder.withDimension(RISK__AS_OF_DATE).withHierarchyOfSameName().slicing().withLevelOfSameName()
            .withSingleLevelDimension(RISK__HOST_NAME).withSingleLevelDimension(RISK__TRADE_ID);
    }

    private static IHasAtLeastOneMeasure measures(ICanStartBuildingMeasures builder) {
        return builder.withContributorsCount().withUpdateTimestamp().withAggregatedMeasure().sum(RISK__DELTA);
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
            .build();
    }

}
