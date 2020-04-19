/*
 * (C) Quartet FS 2010
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of Quartet Financial Systems Limited. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */
package org.mendirl.service.cube.activepivot.postprocessor;

import com.qfs.store.record.IRecordReader;
import com.quartetfs.biz.pivot.IMultiVersionActivePivot;
import com.quartetfs.biz.pivot.query.aggregates.IAggregatesContinuousQueryEngine;
import com.quartetfs.biz.pivot.query.aggregates.IStream;
import com.quartetfs.fwk.QuartetExtendedPluginValue;

import java.util.Set;

import static org.mendirl.service.cube.configuration.DatastoreDescriptionConfiguration.FOREX_STORE_NAME;

/**
 * <b>ForexStream</b>
 * <p>
 * Listen to the forex store
 * and publish event into the ActivePivot continuous query engine
 *
 * @author Quartet Financial Systems
 */
@QuartetExtendedPluginValue(intf = IStream.class, key = ForexStream.PLUGIN_KEY)
public class ForexStream extends ASetStoreStream<String, String> {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -8004742130808337138L;

    /**
     * plugin key
     */
    public static final String PLUGIN_KEY = "FOREX";

    /**
     * Constructor.
     *
     * @param engine The {@link IAggregatesContinuousQueryEngine continuous query engine} that
     *               created it
     * @param pivot  The {@link IMultiVersionActivePivot pivot} it belongs to
     */
    public ForexStream(IAggregatesContinuousQueryEngine engine, IMultiVersionActivePivot pivot) {
        super(engine, pivot);
        setStore(FOREX_STORE_NAME);
    }

    /**
     * Being a Plugin, it returns the Type it is attached to.
     */
    @Override
    public String getType() {
        return PLUGIN_KEY;
    }

    /**
     * When a record in forex store is updated/added/removed,
     * The set of related currencies (currency and targetCurrency) is published
     * into the ActivePivot continuous query engine as real-time events.
     */
    @Override
    protected void addEvent(IRecordReader record, Set<String> events) {
        events.add((String) dictionaries.getDictionary(0).read(record.readInt(0)));
        events.add((String) dictionaries.getDictionary(1).read(record.readInt(1)));
    }

    @Override
    protected String transform(String event) {
        return event;
    }
}
