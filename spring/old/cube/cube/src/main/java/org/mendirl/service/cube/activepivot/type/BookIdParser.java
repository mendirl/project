package org.mendirl.service.cube.activepivot.type;


import com.quartetfs.fwk.QuartetPluginValue;
import com.quartetfs.fwk.QuartetRuntimeException;
import com.quartetfs.fwk.format.IParser;
import com.quartetfs.fwk.types.impl.PluginValue;
import org.mendirl.service.cube.activepivot.model.Trade.BookId;
import org.mendirl.service.cube.activepivot.source.TradeGenerator;

import java.util.logging.Logger;

/**
 * @author Quartet FS
 */
@QuartetPluginValue(intf = IParser.class)
public class BookIdParser extends PluginValue implements IParser<BookId> {

    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER = Logger.getLogger(BookIdParser.class.getName());

    @Override
    public String description() {
        return "book id parser";
    }

    @Override
    public Object key() {
        return BookIdLiteralType.LITERAL;
    }

    @Override
    public BookId parse(CharSequence sequence) throws NumberFormatException {
        String s = sequence.toString();
        int idx = s.indexOf(BookId.SEPARATOR);
        if (idx == -1)
            throw new QuartetRuntimeException("Wrong line " + s + " expected " + BookId.SEPARATOR + " in the string");

        final int id = Integer.parseInt(s.substring(0, idx));
        final String ownerName = s.substring(idx + 1, s.length());
        final BookId expected = TradeGenerator.getBookId(id);
        if (!expected.getOwnerName().equals(ownerName)) {
            LOGGER.warning("For bookId " + id
                + ", expected owner "
                + ownerName
                + " but was "
                + expected.getOwnerName()
                + ". There is a disruptancy between the expected owner name"
                + " retrieved from the trade generator and the one found in the CSV data.");
        }
        // We use the one from the TradeGenerator to avoid duplicating objects.
        return expected;
    }

}
