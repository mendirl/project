/*
 * (C) Quartet FS 2007-2013
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of Quartet Financial Systems Limited. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */
package org.mendirl.service.cube.activepivot.model;

import com.quartetfs.fwk.format.impl.DateFormatter;
import com.quartetfs.fwk.format.impl.LocalDateParser;
import org.mendirl.service.cube.configuration.SourceConfig;

import java.lang.management.ManagementFactory;
import java.time.temporal.Temporal;

/**
 * Risk entry record, to indicate a risk to be computed.
 * It contains a reference to a trade, and a date.
 * The values of the risk are computed through a store trigger.
 *
 * @author Quartet FS
 */
public class RiskEntry {

    /**
     * The host name of the JVM
     */
    public static final String HOST_NAME = ManagementFactory.getRuntimeMXBean().getName();

    /**
     * The pattern to parse/format dates.
     */
    public static final String DATE_PATTERN = LocalDateParser.DEFAULT_PATTERN;

    public static final DateFormatter RISK_CSV_DATE_FORMAT = new DateFormatter(DATE_PATTERN);

    private long tradeId;
    private Temporal asOfDate;

    /**
     * Empty constructor
     */
    public RiskEntry() {
    }

    public long getTradeId() {
        return this.tradeId;
    }

    public void setTradeId(long tradeId) {
        this.tradeId = tradeId;
    }

    public String getHostName() {
        return HOST_NAME;
    }

    public Temporal getAsOfDate() {
        return this.asOfDate;
    }

    public void setAsOfDate(Temporal asOfDate) {
        this.asOfDate = asOfDate;
    }

    /**
     * Compute a CSV representation of this object. For simplier loading of the
     * CSV files, the fields are aligned with the one in the datastore.
     *
     * @return A CSV String representing this object.
     */
    public String toCsvString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getTradeId());
        sb.append(SourceConfig.CSV_SEPARATOR).append(RISK_CSV_DATE_FORMAT.format(getAsOfDate()));
        return sb.toString();
    }

}
