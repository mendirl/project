package org.mendirl.service.cube.activepivot.pivot;

import com.activeviam.builders.StartBuilding;
import com.activeviam.copper.HierarchyCoordinate;
import com.activeviam.desc.build.ICanBuildCubeDescription;
import com.activeviam.desc.build.ICubeDescriptionBuilder.INamedCubeDescriptionBuilder;
import com.quartetfs.biz.pivot.context.IMdxContext;
import com.quartetfs.biz.pivot.context.drillthrough.IDrillthroughProperties;
import com.quartetfs.biz.pivot.context.impl.QueriesTimeLimit;
import com.quartetfs.biz.pivot.cube.hierarchy.measures.IMeasureHierarchy;
import com.quartetfs.biz.pivot.definitions.IActivePivotInstanceDescription;
import com.quartetfs.biz.pivot.definitions.ICalculatedMemberDescription;
import com.quartetfs.biz.pivot.definitions.IKpiDescription;
import com.quartetfs.fwk.ordering.impl.CustomComparator;
import org.mendirl.service.cube.activepivot.context.IReferenceCurrency;
import org.mendirl.service.cube.activepivot.drillthrough.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import static org.mendirl.service.cube.activepivot.pivot.EquityDerivativesCubeDimensionsConfig.*;
import static org.mendirl.service.cube.activepivot.pivot.EquityDerivativesCubeKpisConfig.*;
import static org.mendirl.service.cube.activepivot.pivot.EquityDerivativesCubeMeasuresConfig.PNL_DELTA_SUM;
import static org.mendirl.service.cube.activepivot.pivot.EquityDerivativesCubeMeasuresConfig.PNL_SUM;
import static org.mendirl.service.cube.configuration.ActivePivotManagerDescriptionConfiguration.CURRENCY;
import static org.mendirl.service.cube.configuration.DatastoreDescriptionConfiguration.*;


/**
 * Configuration for the "EquityDerivativesCube"
 * ActivePivot instance.
 *
 * @author ActiveViam
 */
public class EquityDerivativesCubeConfig {

    /**
     * The name of the cube
     */
    public static final String CUBE_NAME = "EquityDerivativesCube";

    /* ******* */
    /* Folders */
    /* ******* */

    /**
     * The folder for bucketed measures.
     */
    public static final String BUCKETING_FOLDER = "Bucketing";
    /**
     * The folder for sensitivities related measures.
     */
    public static final String SENSITIVITIES_FOLDER = "Sensitivities";
    /**
     * The folder for the PnL related measures.
     */
    public static final String PNL_FOLDER = "PnL";

    /**
     * Creates the cube description.
     *
     * @param isActiveMonitorEnabled Whether ActiveMonitor is enabled or not.
     * @return The created cube description
     */
    public static IActivePivotInstanceDescription createCubeDescription(final boolean isActiveMonitorEnabled) {
        return configureCubeBuilder(StartBuilding.cube(CUBE_NAME), isActiveMonitorEnabled).build();
    }

    /**
     * Configures the given builder in order to created the cube
     * description.
     *
     * @param builder                The builder to configure
     * @param isActiveMonitorEnabled Whether ActiveMonitor is enabled or not.
     * @return The configured builder
     */
    public static ICanBuildCubeDescription<IActivePivotInstanceDescription> configureCubeBuilder(
        final INamedCubeDescriptionBuilder builder,
        final boolean isActiveMonitorEnabled) {
        return builder
            .withMeasures(EquityDerivativesCubeMeasuresConfig::nativeMeasures)
            .withDimensions(EquityDerivativesCubeDimensionsConfig::dimensions)
            .withAggregateProvider()
            .withPartialProvider().bitmap()
            .excludingHierarchies(new HierarchyCoordinate(TRADES_HIERARCHY))
            .includingOnlyMeasures(PNL_DELTA_SUM, PNL_SUM)
            .withPartialProvider().leaf()
            .includingOnlyHierarchies(
                new HierarchyCoordinate(UNDERLYINGS_HIERARCHY),
                new HierarchyCoordinate(TIME_DIMENSION, HISTORICAL_DATES_HIERARCHY))
            .withPartialProvider()
            .excludingMeasures(PNL_DELTA_SUM, PNL_SUM)

            .withDrillthroughExecutor()
            .withKey(TimeBucketDrillthroughExecutor.PLUGIN_KEY)
            .withProperties(
                TimeBucketLocationInterpreter.BUCKET_HIERARCHY_PROPERTY, TIME_BUCKET_DYNAMIC_HIERARCHY,
                TimeBucketLocationInterpreter.BUCKETED_LEVEL_PROPERTY, VALUE_DATE_LEVEL)

            .withAggregatesCache()
            .withSize(1_000)
            .cachingOnlyMeasures(IMeasureHierarchy.COUNT_ID, PNL_SUM)

            // Shared context values
            .withSharedContextValue(drillthroughProperties())
//            .withSharedContextValue(mdxContext(isActiveMonitorEnabled))
            // Query maximum execution time (before timeout cancellation): 30s
            .withSharedContextValue(QueriesTimeLimit.of(30, TimeUnit.SECONDS))
            .withDescriptionPostProcessor(StartBuilding
                .copperCalculations()
                .withDefinition(EquityDerivativesCubeMeasuresConfig::calculations)
                .withContextValue(IReferenceCurrency.class)
                .build())
            ;
    }

    /**
     * The KPIs that are always enabled, whether ActiveMonitor is enabled or not.
     *
     * @return The KPIs that are always enabled.
     */
    protected static IKpiDescription[] mainKpis() {
        return new IKpiDescription[]{
            kpiPnlMonitoring(),
            kpiPnlMonitoringWithMdx(),
            kpiSmoothedPnl()
        };
    }

    /**
     * The KPIs that are enabled when ActiveMonitor is enabled because they rely on its features.
     *
     * @return The KPIs that are enabled when ActiveMonitor is enabled.
     */
    protected static IKpiDescription[] kpisUsingActiveMonitor() {
        return new IKpiDescription[]{
            kpiPnlMonitoringWithLookup(),
            kpiTunnelCountMonitoring(),
            kpiPnlSensitivity(),
            kpiDataContribution()
        };
    }

    /**
     * The calculated members that are enabled when ActiveMonitor is enabled because they rely on its features.
     *
     * @return The calculated members that are enabled when ActiveMonitor is enabled.
     */
    protected static ICalculatedMemberDescription[] calculatedMembersUsingActiveMonitor() {
        return new ICalculatedMemberDescription[]{
            // This calculated member shows that KPI expressions can be reused elsewhere than in a KPI
            StartBuilding.calculatedMember()
                .withName("[Measures].[Currency contribution]")
                .withExpression("Divide(100 * [Measures].[Data contribution Value], [Measures].[Data contribution Goal])")
                .withFormatString("'##.##'")
                .build()
        };
    }

    /**
     * The MDX context of the cube.
     *
     * @return The MDX context.
     */
    protected static IMdxContext mdxContext(final boolean isActiveMonitorEnabled) {
        return StartBuilding.mdxContext()
            // Restrict formula evaluation on the cross-joined tuples existing in
            // the cube, instead of all possible tuples.
            .aggressiveFormulaEvaluation(true)
            .withKpis(mainKpis())
            .withKpis(isActiveMonitorEnabled ? kpisUsingActiveMonitor() : new IKpiDescription[0])
            .withNamedSet()
            .withName("[Top2Underlyings]")
            .withExpression("TopCount([Underlyings].[ALL].[AllMember].Children, 2, [Measures].[contributors.COUNT])")
            .withCaption("Top two of underlyings")
            .withDescription("The two elements of underlyings which have the highest value")
            .end()
            .withCalculatedMember()
            .withName("[Currency].[Currency].[ALL].[AllMember].[EUR + USD]")
            .withExpression("[Currency].[Currency].[ALL].[AllMember].[USD] + [Currency].[Currency].[ALL].[AllMember].[EUR]")
            .end()
            .withCalculatedMembers(isActiveMonitorEnabled ? calculatedMembersUsingActiveMonitor() : new ICalculatedMemberDescription[0])
            .build();
    }

    /**
     * @return The shared {@link com.quartetfs.biz.pivot.context.drillthrough.IDrillthroughProperties} context value.
     */
    protected static IDrillthroughProperties drillthroughProperties() {
        return StartBuilding.drillthroughProperties()
            // Setting some columns first in order based on their names
            .withHeaderComparator(new CustomComparator<>(
                Arrays.asList(TRADE__DESK, CURRENCY),
                Collections.emptyList()))
            .hideColumn(TRADE__BOOK_ID)
            .hideColumn(CITY_OBJECT)
            .withCalculatedColumn()
            .withName("delta + gamma")
            .withPluginKey(DoubleAdderColumn.PLUGIN_KEY)
            .withUnderlyingFields("delta", "gamma")
            .end()
            .withCalculatedColumn()
            .withName("Book ID")
            .withPluginKey(BookIdColumn.PLUGIN_KEY)
            .withUnderlyingFields(TRADE__BOOK_ID)
            .end()
            .withCalculatedColumn()
            .withName("City name")
            .withPluginKey(CityNameColumn.PLUGIN_KEY)
            .withUnderlyingFields(CITY_OBJECT)
            .end()
            .withCalculatedColumnSet()
            .withPluginKey(PnlCurrencyColumnSet.PLUGIN_KEY)
            .withProperty("prefix", "pnl in")
            .withProperty("currencies", "EUR,USD,GBP,JPY,CHF,ZAR")
            .end()
            // Hard limit for the number of rows returned by drillthrough queries
            .withMaxRows(10000)
            .build();
    }

}
