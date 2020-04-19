package org.mendirl.service.cube.activepivot.property;

import com.qfs.condition.ICondition;
import com.qfs.condition.impl.BaseConditions;
import com.qfs.store.IDatastoreVersion;
import com.qfs.store.query.ICursor;
import com.qfs.store.query.IRecordQuery;
import com.qfs.store.query.condition.impl.RecordQuery;
import com.quartetfs.biz.pivot.property.impl.ADatastoreVersionAwareProperty;
import com.quartetfs.fwk.ICustomProperty;
import com.quartetfs.fwk.QuartetExtendedPluginValue;

import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.util.Arrays;

import static org.mendirl.service.cube.configuration.DatastoreDescriptionConfiguration.TRADE_STORE_NAME;

/**
 * A custom property that retrieves the date of the earliest trade for a given
 * desk.
 *
 * @author Quartet FS
 */
@QuartetExtendedPluginValue(intf = ICustomProperty.class, key = DeskNextTradeDateProperty.PLUGIN_KEY)
public class DeskNextTradeDateProperty extends ADatastoreVersionAwareProperty {

    /**
     * For serialization.
     */
    private static final long serialVersionUID = 3592828338042225441L;

    /**
     * Plugin type
     */
    public static final String PLUGIN_KEY = "DESK_NEXT_TRADE_DATE";

    /**
     * Constructs the plugin.
     *
     * @param name The name of the plugin.
     */
    public DeskNextTradeDateProperty(String name) {
        super(name);
    }

    public DeskNextTradeDateProperty(String name, String expression) {
        super(name, expression);
    }

    @Override
    public Object getValue(Object desk, IDatastoreVersion datastore) {
        // Warning: doing complex computations in this function will
        // hurt performance, especially when doing big cross-joins.

        // In a distributed environment, the datastore is null
        if (datastore == null) {
            return null;
        }

        //////////////////////////////////////////////////////////////
        // Retrieve all the trade dates corresponding to this desk,
        // using the condition query API.
        final ICondition cond = BaseConditions.Equal("Desk", desk);
        final IRecordQuery getProductNameQuery = new RecordQuery(
            TRADE_STORE_NAME,
            cond,
            Arrays.asList("Date"));
        final ICursor results = datastore.execute(getProductNameQuery);

        //////////////////////////////////////////////////////////////
        // Iterate over the result to find the next trade date
        Temporal nextDate = null;
        while (results.next()) {
            // Read the first and only value, which is the date.
            Object result = results.getRecord().read(0);
            // Result might also be the N/A String.
            if (result instanceof Temporal) {
                final Temporal date = (Temporal) result;

                ChronoUnit unit = null;
                if (date.isSupported(ChronoUnit.MILLIS)) {
                    unit = ChronoUnit.MILLIS;
                } else {
                    unit = ChronoUnit.DAYS;
                }

                if (nextDate == null || date.until(nextDate, unit) > 0) {
                    nextDate = date;
                }
            } else {
                // Verifies that it was actually the N/A string.
                assert result instanceof String && ((String) result).equals("N/A") : "Expected N/A String, but was: " + result;
            }
        }

        return nextDate;
    }

    @Override
    public String getType() {
        return PLUGIN_KEY;
    }

}
