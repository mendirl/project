package org.mendirl.service.cube.activepivot.drillthrough;

import com.quartetfs.biz.pivot.IActivePivotSession;
import com.quartetfs.biz.pivot.IActivePivotVersion;
import com.quartetfs.biz.pivot.query.ILocationInterpreter;
import com.quartetfs.biz.pivot.query.aggregates.IDrillthroughExecutor;
import com.quartetfs.biz.pivot.query.aggregates.impl.DrillthroughExecutor;
import com.quartetfs.fwk.QuartetExtendedPluginValue;

import java.util.Properties;

/**
 * A drillthrough executor that uses the {@link TimeBucketLocationInterpreter}
 * as {@link ILocationInterpreter}.
 *
 * @author Quartet FS
 */
@QuartetExtendedPluginValue(intf = IDrillthroughExecutor.class, key = TimeBucketDrillthroughExecutor.PLUGIN_KEY)
public class TimeBucketDrillthroughExecutor extends DrillthroughExecutor {

    private static final long serialVersionUID = 1L;

    /**
     * The plugin key
     */
    public static final String PLUGIN_KEY = "TIME_BUCKET_DT_EXECUTOR";

    /**
     * Default constructor.
     *
     * @param session    the current {@link IActivePivotSession} bound to an {@link IActivePivotVersion version}
     * @param properties some additional {@link Properties properties}
     */
    public TimeBucketDrillthroughExecutor(IActivePivotSession session, Properties properties) {
        super(session, properties);
    }

    @Override
    public String getType() {
        return PLUGIN_KEY;
    }
}
