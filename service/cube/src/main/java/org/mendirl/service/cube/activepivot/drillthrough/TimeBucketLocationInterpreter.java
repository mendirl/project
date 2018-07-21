package org.mendirl.service.cube.activepivot.drillthrough;

import com.qfs.condition.ICondition;
import com.qfs.condition.IConstantCondition;
import com.qfs.condition.impl.BaseConditions;
import com.qfs.store.IFieldInformation;
import com.quartetfs.biz.pivot.IActivePivot;
import com.quartetfs.biz.pivot.IActivePivotVersion;
import com.quartetfs.biz.pivot.ILocation;
import com.quartetfs.biz.pivot.context.IActivePivotContext;
import com.quartetfs.biz.pivot.context.subcube.ISubCubeProperties;
import com.quartetfs.biz.pivot.context.subcube.ISubCubeTree;
import com.quartetfs.biz.pivot.context.subcube.impl.AllExcludedSubCubeTree;
import com.quartetfs.biz.pivot.cube.hierarchy.IHierarchy;
import com.quartetfs.biz.pivot.cube.hierarchy.ILevelInfo;
import com.quartetfs.biz.pivot.cube.hierarchy.axis.IAxisHierarchy;
import com.quartetfs.biz.pivot.cube.hierarchy.axis.IAxisMember;
import com.quartetfs.biz.pivot.cube.hierarchy.axis.impl.DefaultTimeBucketer;
import com.quartetfs.biz.pivot.cube.hierarchy.axis.impl.TimeBucketHierarchy;
import com.quartetfs.biz.pivot.cube.hierarchy.impl.HierarchiesUtil;
import com.quartetfs.biz.pivot.impl.LocationUtil;
import com.quartetfs.biz.pivot.postprocessing.impl.ABucketerPostProcessor;
import com.quartetfs.biz.pivot.postprocessing.impl.TimeBucketerPostProcessor;
import com.quartetfs.biz.pivot.query.ILocationInterpreter;
import com.quartetfs.biz.pivot.query.impl.DefaultLocationInterpreter;
import com.quartetfs.fwk.QuartetException;
import com.quartetfs.fwk.QuartetRuntimeException;

import java.util.*;

/**
 * This {@link ILocationInterpreter} is able to create a {@link ICondition}
 * that follows the business logic of {@link TimeBucketerPostProcessor} and
 * {@link TimeBucketHierarchy}. The condition will be used while performing
 * a drillthrough taking into account the location coordinates and subcubes
 * on the {@link TimeBucketHierarchy analysis hierarchy}.
 * <p>
 * Only trades whose date belong to the selected buckets (this selection can
 * be deduced from locations and subcubes) will appear in the drillthrough
 * result.
 * </p>
 * <p>
 * Pay attention that this <b>object is shared</b> among queries so it must
 * support concurrency calls.
 * </p>
 *
 * @author Quartet FS
 */
public class TimeBucketLocationInterpreter extends DefaultLocationInterpreter {

    /**
     * Bucket hierarchy property
     */
    public static final String BUCKET_HIERARCHY_PROPERTY = ABucketerPostProcessor.BUCKET_HIERARCHY_PROPERTY;

    /**
     * Bucketed Level property
     */
    public static final String BUCKETED_LEVEL_PROPERTY = ABucketerPostProcessor.BUCKETED_LEVEL_PROPERTY;

    /**
     * The ordinal of the bucket level
     */
    public static final int BUCKET_LEVEL_ORDINAL = ABucketerPostProcessor.BUCKET_LEVEL_ORDINAL;

    /**
     * Name of the analysis hierachy
     */
    protected final String bucketHiearchyName;

    /**
     * Property of the bucketed level
     */
    protected final String bucketedLevel;

    /**
     * The time bucketer used
     */
    protected DefaultTimeBucketer timeBucketer;

    /**
     * The current time in milliseconds
     */
    protected final long now;

    /**
     * The bucket level info
     */
    protected ILevelInfo bucketLevelInfo;

    /**
     * The bucketed level info
     */
    protected ILevelInfo bucketedLevelInfo;

    /**
     * The bucket hierarchy.
     */
    protected final IHierarchy bucketHierarchy;

    /**
     * Default constructor.
     *
     * @param pivot             The pivot on which the constructor is instantiated.
     * @param fieldInformations the {@link IFieldInformation} used to search for
     *                          a specific field
     * @param properties        some additional {@link Properties properties}
     */
    public TimeBucketLocationInterpreter(IActivePivotVersion pivot, IFieldInformation fieldInformations, Properties properties) {
        super(pivot, fieldInformations, properties);
        // It will create the bucket map.
        this.timeBucketer = new DefaultTimeBucketer();
        this.bucketedLevel = properties.getProperty(BUCKETED_LEVEL_PROPERTY);
        this.bucketHiearchyName = properties.getProperty(BUCKET_HIERARCHY_PROPERTY);
        this.now = System.currentTimeMillis();
        // Initialization
        this.bucketHierarchy = init(pivot);
    }

    /**
     * Initialization of the location interpreter. It is called each time a
     * condition is created (i.e per query)
     *
     * @param pivot the current cube
     * @return the analysis hierarchy (TimeBucketDynamic in this case)
     */
    protected IHierarchy init(IActivePivot pivot) {
        // Retrieve the bucket hierarchy.
        IHierarchy bucketHierarchy;
        try {
            bucketHierarchy = HierarchiesUtil.getHierarchy(pivot, bucketHiearchyName);
        } catch (QuartetException e) {
            throw new QuartetRuntimeException("Unable to find hierarchy " + bucketHiearchyName
                + " in cube "
                + pivot.getId());
        }

        if (bucketHierarchy.getLevels().size() != BUCKET_LEVEL_ORDINAL + 1) {
            throw new QuartetRuntimeException("The bucket hierarchy must have exactly one level: "
                + bucketHierarchy.getName() + ", " + bucketHierarchy.getLevels());
        }

        // Extract the level info
        bucketLevelInfo = bucketHierarchy.getLevels().get(BUCKET_LEVEL_ORDINAL).getLevelInfo();

        // Retrieve the bucketed level info.
        try {
            bucketedLevelInfo = HierarchiesUtil.getLevel(pivot, bucketedLevel).getLevelInfo();
        } catch (QuartetException e) {
            throw new QuartetRuntimeException("Unable to find level " + bucketedLevel
                + " from cube " + pivot.getId());
        }
        return bucketHierarchy;

    }

    @Override
    public ICondition buildCondition(IActivePivot pivot, Collection<ILocation> locations, Collection<String> measures) {
        // The conditions built from the locations and taking into account the analysis hierarchy.
        ICondition locationCondition = super.buildCondition(pivot, locations, measures);

        // The condition deduced from the subcubes.
        IConstantCondition subCubesCondition = getSubCubeCondition(pivot.getContext(), bucketHierarchy);

        if (locationCondition == null && subCubesCondition == null)
            return null;
        else if (locationCondition != null && subCubesCondition != null)
            return BaseConditions.And(locationCondition, subCubesCondition);
        else if (locationCondition != null && subCubesCondition == null)
            return locationCondition;
        else
            return subCubesCondition;// Only the subcubes conditions is not null

    }

    @Override
    protected List<ICondition> locationConditions(List<? extends IHierarchy> hierarchies, ILocation location) {
        // First, compute the condition using the default behavior,
        // that is to say the one ignoring the analysis hierarchies.
        List<ICondition> originalConditions = super.locationConditions(hierarchies, location);

        // Introspect the location
        if (LocationUtil.isAtOrBelowLevel(location, bucketLevelInfo)) {
            Object[] values = LocationUtil.extractValues(location, new ILevelInfo[]{bucketLevelInfo});
            if (values != null) {
                // The condition deduced from the location.
                IConstantCondition locationCondition = createBucketCondition(values);
                if (locationCondition != null)
                    originalConditions.add(locationCondition);
            }
        }

        return originalConditions;
    }

    /**
     * Extract a {@link ICondition condition} from the subcubes.
     *
     * @param context         the current pivot context
     * @param bucketHierarchy the bucket hierarchy (TimeBucketDynamic)
     * @return the created condition. It can be null.
     */
    protected IConstantCondition getSubCubeCondition(IActivePivotContext context, IHierarchy bucketHierarchy) {
        // The condition deduced from the subcubes.
        IConstantCondition subCubesCondition = null;

        // Introspect the sub cubes
        ISubCubeProperties subCubes = context.get(ISubCubeProperties.class);
        if (subCubes != null) {
            ISubCubeTree subCubeTree = subCubes.getSubCubeTree(
                bucketHierarchy.getHierarchyInfo().getDimensionInfo().getName(),
                bucketHierarchy.getName());
            if (subCubeTree != null) {
                if (subCubeTree == AllExcludedSubCubeTree.getInstance())
                    subCubesCondition = BaseConditions.FALSE;

                int depth = subCubeTree.getRestrictionDepth();
                if (depth >= BUCKET_LEVEL_ORDINAL) {
                    Set<IAxisMember> axisMembers = subCubeTree.retrieveAxisMembers(BUCKET_LEVEL_ORDINAL + 1,
                        (IAxisHierarchy) bucketHierarchy);

                    if (axisMembers != null) {
                        if (axisMembers.size() == 0)
                            subCubesCondition = BaseConditions.FALSE;

                        Object[] grantedBuckets = new Object[axisMembers.size()];
                        int i = 0;
                        for (IAxisMember member : axisMembers)
                            grantedBuckets[i++] = member.getObjectPath()[depth];

                        subCubesCondition = createBucketCondition(grantedBuckets);
                    }
                }
            }
        }

        return subCubesCondition;
    }

    /**
     * Create a {@link ICondition} from input buckets and relatively to
     * the current time.
     *
     * @param buckets the bucket from which a {@link ICondition} will be created.
     * @return the deduced {@link ICondition}. It can be returned null.
     */
    protected IConstantCondition createBucketCondition(Object[] buckets) {
        NavigableSet<Object> elements = new TreeSet<Object>(bucketLevelInfo.getComparator());
        for (Object value : buckets) {
            if (value != null)
                elements.add(value);
        }

        Object lastBucket = elements.last();
        if (lastBucket != null) {
            final String fieldName = bucketedLevelInfo.getField().getProperty().getName();
            final String fieldExpression = fieldInformations.getFieldExpressions().get(fieldName);

            Long lastDateElement = timeBucketer.getBoundary(this.now, lastBucket);
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(lastDateElement);

            return BaseConditions.LesserOrEqual(fieldExpression, cal.getTime());
        }
        return null;
    }

}
