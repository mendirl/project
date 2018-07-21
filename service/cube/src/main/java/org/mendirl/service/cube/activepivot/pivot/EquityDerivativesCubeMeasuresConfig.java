/*
 * (C) Quartet FS 2017
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of Quartet Financial Systems Limited. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */
package org.mendirl.service.cube.activepivot.pivot;

import com.activeviam.copper.builders.BuildingContext;
import com.activeviam.copper.builders.ColumnMapping;
import com.activeviam.copper.builders.dataset.Datasets.Dataset;
import com.activeviam.copper.builders.dataset.Datasets.StoreDataset;
import com.activeviam.copper.columns.Columns;
import com.activeviam.desc.build.ICanStartBuildingMeasures;
import com.activeviam.desc.build.IHasAtLeastOneMeasure;
import com.quartetfs.biz.pivot.cube.hierarchy.axis.impl.DefaultTimeBucketer;
import com.quartetfs.biz.pivot.postprocessing.impl.MinimumLevelsPostProcessor;
import com.quartetfs.fwk.types.ITime;
import org.mendirl.service.cube.activepivot.context.IReferenceCurrency;
import org.mendirl.service.cube.activepivot.model.City;

import java.time.LocalDate;
import java.util.Date;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.concurrent.TimeUnit;

import static com.activeviam.copper.columns.Columns.col;
import static com.activeviam.copper.columns.Columns.sum;
import static org.mendirl.service.cube.activepivot.pivot.EquityDerivativesCubeConfig.PNL_FOLDER;
import static org.mendirl.service.cube.activepivot.pivot.EquityDerivativesCubeConfig.SENSITIVITIES_FOLDER;
import static org.mendirl.service.cube.activepivot.pivot.EquityDerivativesCubeDimensionsConfig.TIME_BUCKET_DYNAMIC_HIERARCHY;
import static org.mendirl.service.cube.activepivot.pivot.EquityDerivativesCubeDimensionsConfig.TIME_DIMENSION;
import static org.mendirl.service.cube.configuration.ActivePivotManagerDescriptionConfiguration.CURRENCY;
import static org.mendirl.service.cube.configuration.DatastoreDescriptionConfiguration.*;


/**
 * Definition of the measures used in {@link EquityDerivativesCubeConfig}.
 *
 * @author ActiveViam
 */
public class EquityDerivativesCubeMeasuresConfig {

    /**
     * The folder for the geography measures.
     */
    private static final String GEOGRAPHY = "Geography";

    /* ************************* */
    /* Measures names */
    /* ************************* */

    /**
     * The sum(pv) measure name.
     */
    public static final String PV_SUM = "pv.SUM";
    /**
     * The sum(pnl) measure name.
     */
    public static final String PNL_SUM = "pnl.SUM";
    /**
     * The sum(pnlDelta) measure name.
     */
    public static final String PNL_DELTA_SUM = "pnlDelta.SUM";
    /**
     * The forex PP on pnl.
     */
    public static final String PNL_FOREX = "pnl.FOREX";
    /**
     * The limit PP on pnl.
     */
    public static final String PNL_LIMIT = "PnL Limit";
    /**
     * The status PP on pnl.
     */
    public static final String PNL_STATUS = "PnL Limit Status";
    /**
     * The sum(delta) measure name.
     */
    public static final String DELTA_SUM = RISK__DELTA + ".SUM";
    /**
     * The sum(theta) measure name.
     */
    public static final String THETA_SUM = PRODUCT__THETA + ".SUM";
    /**
     * The sum(rho) measure name.
     */
    public static final String RHO_SUM = PRODUCT__RHO + ".SUM";
    /**
     * The sum(gamma) measure name.
     */
    public static final String GAMMA_SUM = RISK__GAMMA + ".SUM";
    /**
     * The sum(vega) measure name.
     */
    public static final String VEGA_SUM = RISK__VEGA + ".SUM";
    /**
     * The sum(pnlVega) measure name
     */
    public static final String PNL_VEGA_SUM = "pnlVega.SUM";
    /**
     * The name of a bucketed post processor for pv.SUM
     */
    public static final String PV_BUCKET_SUM = "pv.bucket.SUM";
    /**
     * The name of a bucketed post processor for pnl.SUM
     */
    public static final String PNL_BUCKET_SUM = "pnl.bucket.SUM";
    /**
     * The name of a bucketed post processor for pnlDelta.SUM
     */
    public static final String PNL_DELTA_BUCKET_SUM = "pnlDelta.bucket.SUM";
    /**
     * The name of a bucketed post processor for pnlVega.SUM
     */
    public static final String PNL_VEGA_BUCKET_SUM = "pnlVega.bucket.SUM";
    /**
     * The name for a measure that illustrates how to use a formula post processor.
     */
    public static final String PV_UNDERLYINGS_RATIO = "pv.UnderlyingsRatio";
    /**
     * The name for a post processor that calculates pnl.SUM using {@link MinimumLevelsPostProcessor} to exclude AllMembers.
     */
    public static final String PNL_MINIMUM_DEPTH = "pnl.MINIMUM_DEPTH";

    /* ********** */
    /* Formatters */
    /* ********** */
    /**
     * The formatter for double measures with at most 2 digits after the decimal separator.
     */
    public static final String DOUBLE_FORMATTER = "DOUBLE[#,###.00;-#,###.00]";
    /**
     * The formatter for double measures with no number after the decimal separator.
     */
    public static final String DOUBLE_FORMATTER_NO_ZEROES = "DOUBLE[#,###.##]";
    /**
     * The int formatter.
     */
    public static final String INT_FORMATTER = "INT[#,###]";
    /**
     * The date formatters for timestamps.
     */
    public static final String TIMESTAMP_FORMATTER = "DATE[HH:mm:ss]";

    /* ******************* */
    /* Measures definition */
    /* ******************* */

    /**
     * Adds all the measures to the cube builder.
     *
     * @param builder The builder to enrich with the measures.
     * @return The builder with the new measures.
     */
    public static IHasAtLeastOneMeasure nativeMeasures(final ICanStartBuildingMeasures builder) {
        return builder
            .withContributorsCount()
            .withAlias("Count")
            .withFormatter(INT_FORMATTER)
            .withUpdateTimestamp()
            .withAlias("Timestamp")
            .withFormatter(TIMESTAMP_FORMATTER);
    }

    /**
     * Creates sensitivities calculations.
     *
     * @param context The CoPPer build context.
     * @return The sensitivities calculations.
     */
    protected static Dataset sensitivities(final BuildingContext context) {
        return context
            .withinFolder(SENSITIVITIES_FOLDER)
            .withFormatter(DOUBLE_FORMATTER)
            .createDatasetFromFacts()
            .agg(
                sum(RISK__DELTA),
                sum(RISK__GAMMA),
                sum(RISK__VEGA),
                sum(PRODUCT__THETA),
                sum(PRODUCT__RHO))
            ;
    }

    /**
     * The calculations to add in the cube.
     *
     * @param context The context with which to build the calculations.
     */
    public static void calculations(BuildingContext context) {
        EquityDerivativesCubeMeasuresConfig.sensitivities(context).publish();
        EquityDerivativesCubeMeasuresConfig.pnlSums(context).publish();
        EquityDerivativesCubeMeasuresConfig.buckets(context).publish();
        EquityDerivativesCubeMeasuresConfig.pnlMinimumDepth(context).publish();
        EquityDerivativesCubeMeasuresConfig.pvUnderlyingsRatio(context).publish();
        EquityDerivativesCubeMeasuresConfig.pnlForex(context).publish();
        EquityDerivativesCubeMeasuresConfig.pnlMonitoring(context).publish();
        EquityDerivativesCubeMeasuresConfig.geographical(context).publish();
    }


    /**
     * Defines the calculations for KPIs on top of the pnl.
     *
     * @param context The context with which to build the calculations.
     * @return The monitoring calculations.
     */
    public static Dataset pnlMonitoring(final BuildingContext context) {
        return pnlSums(context)
            .withColumn(PNL_LIMIT, Columns.literal(0d))
            .withColumn(PNL_STATUS, Columns.col(PNL_SUM)
                .combineWith(Columns.col(PNL_LIMIT))
                .mapToDouble(a -> a.readDouble(0) >= a.readDouble(1) ? 1 : -1));
    }

    /**
     * Creates the sum calculations on top of the pnl fields.
     *
     * @param context The building context.
     * @return The sum calculations.
     */
    public static Dataset pnlSums(final BuildingContext context) {
        return context
            .withinFolder(PNL_FOLDER)
            .withFormatter(DOUBLE_FORMATTER)
            .createDatasetFromFacts()
            .agg(
                sum(RISK__PNL),
                sum(RISK__PNL_DELTA).as(PNL_DELTA_SUM),
                sum(RISK__PNL_VEGA),
                // The pv measure is an alias of productBaseMtm.
                // This measure will be seen as pv.SUM in your front end.
                sum(PRODUCT__BASE_MTM).as(PV_SUM)
            );
    }

    /**
     * Example of a calculation that has only a value when a level is expressed.
     *
     * @param context The building context.
     * @return The minimum depth calculation.
     */
    public static Dataset pnlMinimumDepth(final BuildingContext context) {
        return context
            .createDatasetFromFacts()
            .groupBy(CURRENCY)
            .agg(sum(RISK__PNL).as(PNL_MINIMUM_DEPTH).withinFolder(PNL_FOLDER).withFormatter(DOUBLE_FORMATTER))
            .doNotAggregateAbove();
    }

    /**
     * Creates the pnl forex calculation.
     *
     * @param context The building context.
     * @return The pnl forex calculation.
     */
    public static Dataset pnlForex(final BuildingContext context) {
        StoreDataset forex = context
            .createDatasetFromStore(FOREX_STORE_NAME)
            .filter(context.contextValue(IReferenceCurrency.class).map(IReferenceCurrency::getCurrency).equalTo(col(FOREX_TARGET_CURRENCY)));
        return pnlSums(context)
            .join(forex, ColumnMapping.mapping(CURRENCY).to(FOREX_CURRENCY))
            .select(col(PNL_SUM).multiply(col(FOREX_RATE)).as(PNL_FOREX))
            .agg(sum(PNL_FOREX).as(PNL_FOREX).withinFolder(PNL_FOLDER).withFormatter(DOUBLE_FORMATTER))
            ;
    }

    /**
     * Creates the pv underlying ratio calculation.
     *
     * @param context The building context.
     * @return The underlying ratio calculations.
     */
    public static Dataset pvUnderlyingsRatio(final BuildingContext context) {
        return pnlSums(context)
            .withColumn(PV_UNDERLYINGS_RATIO, col(PV_SUM).divide(col(PV_SUM).drillUp(CURRENCY)));
    }

    /**
     * Creates bucketing calculations.
     *
     * @param context The building context.
     * @return The dataset that contains the bucketing calculations.
     */
    public static Dataset buckets(BuildingContext context) {

        DefaultTimeBucketer bucketer = new DefaultTimeBucketer();
        NavigableMap<Long, Object> bucketMap = bucketer.createBucketMap(System.currentTimeMillis());

        return pnlSums(context).withColumn("Bucket", col(TRADE__DATE).map(d -> findBucket(d, bucketMap))
            .asHierarchy(TIME_BUCKET_DYNAMIC_HIERARCHY)
            .inDimension(TIME_DIMENSION)
            .withFirstObjectsFromList(bucketer.getAllBuckets()));
    }

    /**
     * Attributes a bucket to time entries (entries that expose an absolute time).
     *
     * @param entry     The value to bucket.
     * @param bucketMap The existing buckets.
     * @return the bucket the entry belongs to.
     * @throws IllegalArgumentException if the entry is not supported by the reference bucketer.
     */
    public static Object findBucket(Object entry, NavigableMap<Long, Object> bucketMap) {
        final long entryTime;
        if (entry instanceof ITime) {
            entryTime = ((ITime) entry).getTime();
        } else if (entry instanceof Date) {
            entryTime = ((Date) entry).getTime();
        } else if (entry instanceof LocalDate) {
            entryTime = TimeUnit.DAYS.toMillis(((LocalDate) entry).toEpochDay());
        } else {
            throw new IllegalArgumentException("Unsupported entry for bucketing (only time entries are supported): " + entry);
        }

        final Entry<Long, Object> ceilingEntry = bucketMap.ceilingEntry(entryTime);
        if (ceilingEntry == null) {
            return null;
        } else {
            return ceilingEntry.getValue();
        }
    }

    /**
     * Create the dataset representing the geographical calculations.
     *
     * @param context The building context.
     * @return The geographical calculations.
     */
    public static Dataset geographical(BuildingContext context) {
        return context
            .withFormatter(DOUBLE_FORMATTER)
            .withinFolder(GEOGRAPHY)
            .createDatasetFromFacts()
            .select(
                col(CITY_OBJECT).map((City city) -> city.getLatitude()).as("Latitude"),
                col(CITY_OBJECT).map((City city) -> city.getLongitude()).as("Longitude"))
            .agg(
                Columns.avg("Latitude").as("Latitude"),
                Columns.avg("Longitude").as("Longitude")
            )
            ;
    }
}
