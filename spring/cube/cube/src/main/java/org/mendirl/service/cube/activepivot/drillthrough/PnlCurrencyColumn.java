package org.mendirl.service.cube.activepivot.drillthrough;

import com.quartetfs.biz.pivot.context.drillthrough.ICalculatedDrillthroughColumn;
import com.quartetfs.biz.pivot.context.drillthrough.impl.ACalculatedDrillthroughColumn;
import com.quartetfs.fwk.QuartetExtendedPluginValue;
import gnu.trove.map.TObjectDoubleMap;

import java.util.Properties;

/**
 * A calculated column to display in a drillthrough blotter pnl values
 * converted in a given currency. The conversion rate is retrieved from
 * the datastore during the generation of the column.
 * One column will be generated for each currency.
 *
 * @author Quartet FS
 * @see PnlCurrencyColumnSet
 */
@QuartetExtendedPluginValue(intf = ICalculatedDrillthroughColumn.class, key = PnlCurrencyColumn.PLUGIN_KEY)
public class PnlCurrencyColumn extends ACalculatedDrillthroughColumn {

    private static final long serialVersionUID = 1L;

    /**
     * Calculated column plugin key
     */
    public static final String PLUGIN_KEY = "PnlCurrencyColumn";

    /**
     * Key to retrieve from the {@link Properties} the rates between currency
     */
    public static final String RATE_KEY = "rate";

    /**
     * The currency rate to apply
     */
    public final TObjectDoubleMap<String> rates;

    @SuppressWarnings("unchecked")
    public PnlCurrencyColumn(String name, String fields, Properties properties) {
        super(name, fields, properties);

        // Retrieve the convert rates map that will be used to retrieve a rate for each currency.
        rates = (TObjectDoubleMap<String>) properties.get(RATE_KEY);
    }

    @Override
    public Object evaluate(Object[] underlyingFields) {
        double pnl = (double) underlyingFields[0];
        String currency = (String) underlyingFields[1];
        double rate = rates.get(currency);
        if (rate == 0)
            return 0; // the rate is not in the map, return 0.
        return pnl / rate;
    }

    @Override
    public String getType() {
        return PLUGIN_KEY;
    }

}
