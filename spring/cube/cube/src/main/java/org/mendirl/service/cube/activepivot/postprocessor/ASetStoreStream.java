/*
 * (C) Quartet FS 2014-2015
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of Quartet Financial Systems Limited. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */
package org.mendirl.service.cube.activepivot.postprocessor;

import com.qfs.store.IDatastore;
import com.qfs.store.record.IRecordBlock;
import com.qfs.store.record.IRecordReader;
import com.quartetfs.biz.pivot.IMultiVersionActivePivot;
import com.quartetfs.biz.pivot.postprocessing.streams.impl.AStoreStream;
import com.quartetfs.biz.pivot.query.aggregates.IAggregatesContinuousQueryEngine;
import com.quartetfs.biz.pivot.query.aggregates.IStream;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A {@link IStream} that listens to changes in one store.
 * <p>
 * When a record is added/modified/updated, a pre-event <code>(Type S)</code> is created by {@link #addEvent(IRecordReader, Set)}
 * When a commit is taken, we {@link #transform(Object)} these pre-events to events <code>(Type T)</code>
 * and publish them to ActivePivot continuous query engine.
 *
 * @param <S> Pre-event type
 * @param <T> Event type
 * @author Quartet FS
 */
public abstract class ASetStoreStream<S, T> extends AStoreStream<Set<S>, Set<T>> {

    private static final long serialVersionUID = -2488045611687167032L;

    protected String[] selectedFields = null;

    /**
     * Constructor.
     *
     * @param engine The {@link IAggregatesContinuousQueryEngine continuous query engine} that
     *               created it
     * @param pivot  The {@link IMultiVersionActivePivot pivot} it belongs to
     * @param ds     the current {@link IDatastore}
     * @param store  name of the listening store
     */
    public ASetStoreStream(final IAggregatesContinuousQueryEngine engine, final IMultiVersionActivePivot pivot, IDatastore ds, String store) {
        super(engine, pivot);
        this.datastore = ds;
        this.store = store;
    }

    /**
     * Constructor.
     *
     * @param engine The {@link IAggregatesContinuousQueryEngine continuous query engine} that
     *               created it
     * @param pivot  The {@link IMultiVersionActivePivot pivot} it belongs to
     */
    public ASetStoreStream(final IAggregatesContinuousQueryEngine engine, final IMultiVersionActivePivot pivot) {
        super(engine, pivot);
    }

    /**
     * @param fields field names of the store
     *               to listen.
     */
    public void setSelectedFields(String[] fields) {
        this.selectedFields = fields;
    }

    @Override
    protected String[] getSelectedFields(String[] storeFields) {
        return selectedFields == null ? storeFields : selectedFields;
    }

    /**
     * This method is called when a record has been updated/removed/added.
     * To add the events related to this change into the event set.
     * Those events will  be {@link #transform(Object)} and then be published
     * into the ActivePivot continuous query engine as real-time events.
     *
     * @param record the reader for records that have changed
     * @param events the event set to fill (OUT)
     */
    protected abstract void addEvent(IRecordReader record, Set<S> events);

    /**
     * Add events related to a set of records.
     * Those events will be  {@link #transform(Object)} and then be published
     * into the ActivePivot continuous query engine as real-time events.
     *
     * @param records the records related to the event
     * @param events  the event set to fill (OUT)
     */
    protected void addEvent(IRecordBlock<IRecordReader> records, Set<S> events) {
        for (IRecordReader r : records)
            addEvent(r, events);
    }

    /**
     * Transform a pre-event to an event that will be published to ActivePivot
     * continuous query engine.
     *
     * @param preEvent the event to transform
     * @return the transformed event
     */
    protected abstract T transform(S preEvent);

    @SuppressWarnings("unchecked")
    @Override
    public Class<Set<T>> getEventType() {
        Set<T> set = new HashSet<T>();
        return (Class<Set<T>>) set.getClass();
    }

    @Override
    protected Set<S> createNew() {
        // Create a new set. We need to be thread safe.
        return Collections.newSetFromMap(new ConcurrentHashMap<S, Boolean>());
    }

    @Override
    protected void collectAdded(IRecordBlock<IRecordReader> records, Set<S> collector) {
        addEvent(records, collector);
    }

    @Override
    protected void collectDeleted(IRecordBlock<IRecordReader> records, Set<S> collector) {
        addEvent(records, collector);
    }

    @Override
    protected void collectUpdated(IRecordBlock<IRecordReader> oldValues, IRecordBlock<IRecordReader> newValues, Set<S> collector) {
        addEvent(newValues, collector);
    }

    @Override
    protected Set<T> toEvent(Set<S> collector) {
        Set<T> eventToBeSent = new HashSet<>();
        for (S e : collector)
            eventToBeSent.add(transform(e));
        return eventToBeSent;
    }

}
