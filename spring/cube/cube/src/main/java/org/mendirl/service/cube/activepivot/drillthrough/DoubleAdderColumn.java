/*
 * (C) Quartet FS 2013
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of Quartet Financial Systems Limited. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */
package org.mendirl.service.cube.activepivot.drillthrough;

import com.quartetfs.biz.pivot.context.drillthrough.ICalculatedDrillthroughColumn;
import com.quartetfs.biz.pivot.context.drillthrough.impl.ACalculatedDrillthroughColumn;
import com.quartetfs.fwk.QuartetExtendedPluginValue;

import java.util.Properties;

/**
 * Example of calculated drillthrough column.
 *
 * @author Quartet FS
 */
@QuartetExtendedPluginValue(intf = ICalculatedDrillthroughColumn.class, key = DoubleAdderColumn.PLUGIN_KEY)
public class DoubleAdderColumn extends ACalculatedDrillthroughColumn {

    private static final long serialVersionUID = 1L;

    public static final String PLUGIN_KEY = "DoubleAdder";

    public DoubleAdderColumn(String name, String fields, Properties properties) {
        super(name, fields, properties);
    }

    @Override
    public Object evaluate(Object[] underlyingFields) {
        Double v1 = (Double) underlyingFields[0];
        Double v2 = (Double) underlyingFields[1];

        // Return the sum
        if (v1 == null)
            return v2;
        else if (v2 == null)
            return v1;
        else
            return v1 + v2;
    }

    @Override
    public String getType() {
        return PLUGIN_KEY;
    }

}
