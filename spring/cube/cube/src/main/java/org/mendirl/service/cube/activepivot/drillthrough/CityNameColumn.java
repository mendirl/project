package org.mendirl.service.cube.activepivot.drillthrough;

import com.quartetfs.biz.pivot.context.drillthrough.ICalculatedDrillthroughColumn;
import com.quartetfs.biz.pivot.context.drillthrough.impl.ASimpleCalculatedDrillthroughColumn;
import com.quartetfs.fwk.QuartetExtendedPluginValue;
import org.mendirl.service.cube.activepivot.model.City;

import java.util.Properties;

/**
 * A drillthrough column that hides the City object for the drill-through,
 * simply returning its name.
 *
 * @author Quartet FS
 */
@QuartetExtendedPluginValue(intf = ICalculatedDrillthroughColumn.class, key = CityNameColumn.PLUGIN_KEY)
public class CityNameColumn extends ASimpleCalculatedDrillthroughColumn {

    private static final long serialVersionUID = 1L;

    public static final String PLUGIN_KEY = "CityNameColumn";

    public CityNameColumn(String name, String fields, Properties properties) {
        super(name, fields, properties);
    }

    @Override
    public Object evaluate(Object underlyingField) {
        City city = (City) underlyingField;
        return city.getName();
    }

    @Override
    public String getType() {
        return PLUGIN_KEY;
    }

}
