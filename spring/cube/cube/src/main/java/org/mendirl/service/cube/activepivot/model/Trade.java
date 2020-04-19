package org.mendirl.service.cube.activepivot.model;


/*
 * (C) Quartet FS 2007-2014
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of Quartet Financial Systems Limited. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */


import com.qfs.store.record.IRecordFormat;
import com.quartetfs.fwk.IClone;
import com.quartetfs.fwk.format.impl.DateFormatter;
import com.quartetfs.fwk.format.impl.LocalDateParser;
import org.mendirl.service.cube.configuration.SourceConfig;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * <b>TradeDefinition</b>
 * <p>
 * This class is used in order to build a simple trade.
 *
 * @author Quartet Financial Systems
 */
public class Trade implements IClone<Trade>, Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -3147979015813031848L;

    /**
     * The pattern used for parsing/formatting dates
     */
    public static final String DATE_PATTERN = LocalDateParser.DEFAULT_PATTERN;

    /**
     * The CSV format to parse/format dates.
     */
    public static final DateFormatter TRADE_CSV_DATE_FORMAT = new DateFormatter(DATE_PATTERN);

    /**
     * Unique identifier of the trade
     */
    protected long id;
    protected BookId bookId;
    private String desk;
    protected LocalDate date;
    protected String status;
    protected Object dateBucket;
    protected String isSimulated;
    protected int productId;
    protected int productQtyMultiplier;

    /**
     * The counterparty associated with the trade
     */
    protected String counterParty;

    /**
     * Default constructor
     */
    public Trade() {
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getClass().getSimpleName());
        sb.append(" (id=").append(getId());
        sb.append(", productId=").append(getProductId());
        sb.append(", status=").append(getStatus());
        sb.append(", bookId=").append(getBookId());
        sb.append(", isSimulated=").append(getIsSimulated());
        sb.append(", date=").append(getDate());
        sb.append(", counterParty=").append(getCounterParty());
        sb.append(")");
        return sb.toString();
    }

    /**
     * Compute a CSV representation of this object. For simplier loading of the
     * CSV files, the fields are aligned with the one in the datastore.
     *
     * @return A CSV String representing this object.
     */
    public String toCsvString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(getId());
        sb.append(SourceConfig.CSV_SEPARATOR).append(getProductId());
        sb.append(SourceConfig.CSV_SEPARATOR).append(getProductQtyMultiplier());
        sb.append(SourceConfig.CSV_SEPARATOR).append(getDesk());
        sb.append(SourceConfig.CSV_SEPARATOR).append(getBookId().toCsvString());
        sb.append(SourceConfig.CSV_SEPARATOR).append(getCounterParty());
        sb.append(SourceConfig.CSV_SEPARATOR).append(TRADE_CSV_DATE_FORMAT.format(getDate()));
        sb.append(SourceConfig.CSV_SEPARATOR).append(getStatus());
        sb.append(SourceConfig.CSV_SEPARATOR).append(getIsSimulated());
        return sb.toString();
    }

    @Override
    public Trade clone() {
        try {
            Trade clone = (Trade) super.clone();
            return clone;
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    /**
     * @return The id
     */
    public long getId() {
        return this.id;
    }

    /**
     * Sets the id
     *
     * @param id The id to set
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * @return The bookId
     */
    public BookId getBookId() {
        return this.bookId;
    }

    /**
     * Sets the bookId
     *
     * @param bookId The bookId to set
     */
    public void setBookId(BookId bookId) {
        this.bookId = bookId;
    }

    /**
     * @return The desk
     */
    public String getDesk() {
        return this.desk;
    }

    /**
     * Sets the desk
     *
     * @param desk The desk to set
     */
    public void setDesk(String desk) {
        this.desk = desk;
    }

    /**
     * @return The counterparty
     */
    public String getCounterParty() {
        return this.counterParty;
    }

    /**
     * Sets the counterParty
     *
     * @param counterParty The counterParty to set
     */
    public void setCounterParty(String counterParty) {
        this.counterParty = counterParty;
    }

    /**
     * @return The date
     */
    public LocalDate getDate() {
        return this.date;
    }

    /**
     * Sets the date
     *
     * @param date The date to set
     */
    public void setDate(LocalDate date) {
        this.date = date;
    }

    /**
     * @return The status
     */
    public String getStatus() {
        return this.status;
    }

    /**
     * Sets the status
     *
     * @param status The status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return The dateBucket
     */
    public Object getDateBucket() {
        return this.dateBucket;
    }

    /**
     * Sets the dateBucket
     *
     * @param dateBucket The dateBucket to set
     */
    public void setDateBucket(Object dateBucket) {
        this.dateBucket = dateBucket;
    }

    /**
     * @return The isSimulated
     */
    public String getIsSimulated() {
        return this.isSimulated;
    }

    /**
     * Sets the isSimulated
     *
     * @param isSimulated The isSimulated to set
     */
    public void setIsSimulated(String isSimulated) {
        this.isSimulated = isSimulated;
    }

    /**
     * @return The productId
     */
    public int getProductId() {
        return this.productId;
    }

    /**
     * Sets the productId
     *
     * @param productId The productId to set
     */
    public void setProductId(int productId) {
        this.productId = productId;
    }

    /**
     * @return The productQtyMultiplier
     */
    public int getProductQtyMultiplier() {
        return this.productQtyMultiplier;
    }

    /**
     * Sets the productQtyMultiplier
     *
     * @param productQtyMultiplier The productQtyMultiplier to set
     */
    public void setProductQtyMultiplier(int productQtyMultiplier) {
        this.productQtyMultiplier = productQtyMultiplier;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Trade other = (Trade) obj;
        if (id != other.id) {
            return false;
        }
        return true;
    }

    /**
     * Base class for a bookId, that also wraps the owner name.
     *
     * @author Quartet FS
     */
    public static class BookId implements Serializable {

        /**
         * For serialization.
         */
        private static final long serialVersionUID = 8802468991351465487L;

        /**
         * The book id separator (for CSV serialization)
         */
        public static final String SEPARATOR = ":";

        /**
         * The underlying id of the book.
         */
        protected final int id;

        /**
         * The owner of this book id.
         */
        protected final String ownerName;

        public BookId(int id, String ownerName) {
            this.id = id;
            this.ownerName = ownerName;
        }

        /**
         * Retrieves the underlying id of this book id.
         *
         * @return the underlying Id of this book id
         */
        public int getId() {
            return id;
        }

        /**
         * Retrieve the owner name of this book id.
         *
         * @return the owner name of this book id
         */
        public String getOwnerName() {
            return ownerName;
        }

        // The toString is the member discriminator.
        @Override
        public String toString() {
            return String.valueOf(id);
        }

        // Hashcode and equals implementation.
        // They must match your toString implementation:
        // a equals b <=> a.toString() equals b.toString()
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + id;
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            BookId other = (BookId) obj;
            if (id != other.id) {
                return false;
            }
            return true;
        }

        /**
         * @return A CSV serialized {@link BookId}.
         */
        public String toCsvString() {
            return String.valueOf(id) + SEPARATOR + ownerName;
        }

    }

    /**
     * The default book id (used when we cannot find one)
     */
    public static final class DefaultBookId extends BookId {

        private static final long serialVersionUID = 1L;

        public DefaultBookId() {
            super(-1, IRecordFormat.GLOBAL_DEFAULT_STRING);
        }

        @Override
        public String toString() {
            // Will be used as the name of the member
            return IRecordFormat.GLOBAL_DEFAULT_STRING;
        }
    }

}
