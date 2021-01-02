package org.mendirl.service.cube.activepivot.type;

import com.qfs.literal.ILiteralType;
import com.qfs.literal.impl.LiteralType;
import com.qfs.store.Types;
import com.quartetfs.fwk.QuartetPluginValue;

@QuartetPluginValue(intf = ILiteralType.class)
public class BookIdLiteralType extends LiteralType {

    private static final long serialVersionUID = 1L;

    public static String LITERAL = "BOOK_ID";

    public BookIdLiteralType() {
        super(LITERAL, Types.CONTENT_OBJECT, false, LITERAL, false);
    }

}
