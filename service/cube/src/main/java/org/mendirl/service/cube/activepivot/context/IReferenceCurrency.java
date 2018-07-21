package org.mendirl.service.cube.activepivot.context;

import com.quartetfs.biz.pivot.context.IContextValue;

/**
 * Context value storing a reference currency.
 *
 * @author Quartet Financial Systems
 */
public interface IReferenceCurrency extends IContextValue {

    /**
     * @return the reference currency
     */
    String getCurrency();

}
