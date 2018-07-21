package org.mendirl.service.cube.activepivot.drillthrough;

import com.quartetfs.biz.pivot.context.drillthrough.ICalculatedDrillthroughColumn;
import com.quartetfs.biz.pivot.context.drillthrough.impl.ASimpleCalculatedDrillthroughColumn;
import com.quartetfs.fwk.QuartetExtendedPluginValue;
import org.mendirl.service.cube.activepivot.model.Trade.BookId;

import java.util.Properties;

/**
 * A drillthrough column that hides the BookId object for the drill-through.
 * This is necessary for AP Live: since AP Live does not depends on the sandbox,
 * it does not see the BookId object. We could make it work with AP Live simply
 * by making it depend on the sandbox.
 *
 * @author Quartet FS
 */
@QuartetExtendedPluginValue(intf = ICalculatedDrillthroughColumn.class, key = BookIdColumn.PLUGIN_KEY)
public class BookIdColumn extends ASimpleCalculatedDrillthroughColumn {

    private static final long serialVersionUID = 1L;

    public static final String PLUGIN_KEY = "BookIdColumn";

    public BookIdColumn(String name, String fields, Properties properties) {
        super(name, fields, properties);
    }

    @Override
    public Object evaluate(Object underlyingField) {
        BookId book = (BookId) underlyingField;
        return book.getId();
    }

    @Override
    public String getType() {
        return PLUGIN_KEY;
    }

}
