package org.mendirl.service.cube.activepivot.pivot;

import com.activeviam.desc.build.ICanBuildCubeDescription;
import com.activeviam.desc.build.dimensions.ICanStartBuildingDimensions;
import com.qfs.fwk.ordering.impl.ReverseEpochComparator;
import com.quartetfs.biz.pivot.cube.dimension.IDimension.DimensionType;
import com.quartetfs.biz.pivot.cube.hierarchy.ILevelInfo.LevelType;
import com.quartetfs.biz.pivot.definitions.IActivePivotInstanceDescription;
import com.quartetfs.fwk.impl.PropertyInfo;
import com.quartetfs.fwk.ordering.impl.ReverseOrderComparator;
import org.mendirl.service.cube.activepivot.property.DeskNextTradeDateProperty;

import static com.quartetfs.biz.pivot.cube.hierarchy.IOlapElement.XMLA_DESCRIPTION;
import static org.mendirl.service.cube.activepivot.pivot.EquityDerivativesCubeConfig.BUCKETING_FOLDER;
import static org.mendirl.service.cube.configuration.ActivePivotManagerDescriptionConfiguration.CURRENCY;
import static org.mendirl.service.cube.configuration.DatastoreDescriptionConfiguration.*;

/**
 * Definition of the dimensions used in {@link EquityDerivativesCubeConfig}.
 *
 * @author ActiveViam
 */
public class EquityDerivativesCubeDimensionsConfig {

    /* **************************************** */
    /* Levels, hierarchies and dimensions names */
    /* **************************************** */

    public static final String UNDERLYINGS_HIERARCHY = "Underlyings";
    private static final String PRODUCTS_HIERARCHY = "Products";
    private static final String HOST_NAME_HIERARCHY = "HostName";
    private static final String COUNTERPARTY_HIERARCHY = "CounterParty";
    private static final String GEOGRAPHY_DIMENSION = "Geography";
    public static final String TRADES_HIERARCHY = "Trades";
    private static final String BOOKING_DIMENSION = "Booking";
    private static final String STATUS_HIERARCHY = "Status";
    public static final String TIME_DIMENSION = "Time";
    public static final String HISTORICAL_DATES_HIERARCHY = "HistoricalDates";
    private static final String TIME_BUCKET_HIERARCHY = "TimeBucket";
    public static final String VALUE_DATE_LEVEL = "Value Date";
    public static final String TIME_BUCKET_DYNAMIC_HIERARCHY = "TimeBucketDynamic";


    /**
     * Adds the dimensions descriptions to the input
     * builder.
     *
     * @param builder The cube builder
     * @return The builder for chained calls
     */
    public static ICanBuildCubeDescription<IActivePivotInstanceDescription> dimensions(
        ICanStartBuildingDimensions builder) {
        return builder
            .withDimension(UNDERLYINGS_HIERARCHY)
            .withProperty(XMLA_DESCRIPTION, "Dimension of underlyings")
            .withHierarchyOfSameName()
            .withLevels(PRODUCT__UNDERLIER_TYPE, PRODUCT__UNDERLIER_CODE)
            .withHierarchy(PRODUCTS_HIERARCHY).asDefaultHierarchy()
            .withProperty(XMLA_DESCRIPTION, "Hierarchy of products")
            .withLevel(PRODUCT__TYPE)
            .withLevel(PRODUCT__NAME)
            .withProperty(XMLA_DESCRIPTION, "Level of product names")

            .withSingleLevelDimension(HOST_NAME_HIERARCHY)

            .withDimension(CURRENCY)
            .withHierarchyOfSameName()
            .withLevelOfSameName()
            .withFirstObjects("EUR", "GBP", "USD", "JPY")

            .withDimension(COUNTERPARTY_HIERARCHY)
            .withHierarchyOfSameName()
            .withLevel(COUNTERPARTY__COUNTERPARTY_GROUP)
            .withLevel(COUNTERPARTY__COUNTERPARTY)

            .withDimension(GEOGRAPHY_DIMENSION)
            .withHierarchy(CITY_OBJECT)
            .withLevelOfSameName()
            .withMemberProperties(
                new PropertyInfo("Latitude", "latitude"),
                new PropertyInfo("Longitude", "longitude"))

            .withDimension(TRADES_HIERARCHY)
            .withHierarchyOfSameName()
            .withLevel(RISK__TRADE_ID)

            .withDimension(BOOKING_DIMENSION)
            .withMeasureGroups("PnL")
            .withHierarchy(TRADE__DESK)
            .withLevelOfSameName()
            .withMemberProperty().withNameAndExpression("nextTradeDate").withPluginKey(DeskNextTradeDateProperty.PLUGIN_KEY)
            .withLevel(TRADE__BOOK_ID)
            .withMemberProperty().withName("owner").withExpression("ownerName").end()
            .withHierarchy(STATUS_HIERARCHY)
            .withLevel(TRADE__IS_SIMULATED)
            .withLevel(TRADE__STATUS)

            .withDimension(TIME_DIMENSION)
            .withType(DimensionType.TIME)
            .withHierarchy(HISTORICAL_DATES_HIERARCHY).slicing()
            .withLevel(RISK__AS_OF_DATE)
            .withType(LevelType.TIME)
            .withFormatter("DATE[yyyy-MM-dd]")
            .withComparator(ReverseOrderComparator.type)
            .withHierarchy(TIME_BUCKET_HIERARCHY).withinFolder(BUCKETING_FOLDER)
            .withLevel(TRADE__DATE_BUCKET)
            .withFirstObjects("1D", "2D", "3D", "1W", "2W", "3W", "1M", "2M", "3M", "6M", "9M", "1Y", "2Y", "5Y")
            .withLevel(VALUE_DATE_LEVEL)
            .withPropertyName(TRADE__DATE)
            .withType(LevelType.TIME)
            .withFormatter("DATE[dd-MM-yyyy]")

            .withEpochDimension()
            .withEpochsLevel()
            .withComparator(ReverseEpochComparator.TYPE)
            .withFormatter("EPOCH[HH:mm:ss]")
            .end()
            ;
    }

}
