/*
 * (C) Quartet FS 2007-2010
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of Quartet Financial Systems Limited. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */
package org.mendirl.service.cube.activepivot.source;

import com.quartetfs.biz.pivot.cube.hierarchy.IBucketer;
import com.quartetfs.biz.pivot.cube.hierarchy.axis.impl.DefaultTimeBucketer;
import com.quartetfs.fwk.QuartetRuntimeException;
import org.mendirl.service.cube.activepivot.model.CounterParty;
import org.mendirl.service.cube.activepivot.model.Product;
import org.mendirl.service.cube.activepivot.model.Trade;
import org.mendirl.service.cube.activepivot.model.Trade.BookId;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * This class generates random trades.
 *
 * @author Quartet FS
 */
public class TradeGenerator {

    //statuses
    private static final String[] statuses = {"SIMULATION", "MATCHED", "DONE"};

    // desks are mapped with underlying types
    private static final Map<String, String> desks;

    static {
        desks = new HashMap<>();
        desks.put("EquityIndex", "DeskA");
        desks.put("SingleStock", "DeskB");
    }

    //misc params
    private static final int PRODUCTQTY_MAX = 10000;
    private static final int DATE_DEPTH_MAX = 10 * 365; //max generated days, up to 10 years
    private static final String[] NAMES = {"John", "Will", "Charles", "Henry", "Stan", "Eric", "Sam", "Lucy", "Luke"};
    // The actual number of book ids will also depend on the numbers found in
    // the csv files.
    private static volatile BookId[] book_ids = new BookId[10];

    static {
        for (int i = 0; i < book_ids.length; ++i) {
            book_ids[i] = new BookId(i, NAMES[i % NAMES.length]);
        }
    }

    private final LocalDate nowAsLocalDate = LocalDate.now();

    /**
     * Maps of buckets to bucket dates
     */
    private final NavigableMap<Long, Object> bucketMap;

    public TradeGenerator() {
        // Create the time bucket map
        IBucketer<Long> dateBucketer = new DefaultTimeBucketer();
        this.bucketMap = dateBucketer.createBucketMap(TimeUnit.DAYS.toMillis(nowAsLocalDate.toEpochDay()));
    }

    /**
     * Retrieve the {@link BookId} associated to the given id.
     *
     * @param id The integer id.
     * @return The associated {@link BookId}.
     */
    public static BookId getBookId(int id) {
        ensureBookIds(id);
        return book_ids[id];
    }

    /**
     * Ensure that there are at least size {@link BookId}.
     *
     * @param size The minimum number of needed {@link BookId}.
     */
    protected static void ensureBookIds(int size) {
        if (size >= book_ids.length) {
            synchronized (TradeGenerator.class) {
                if (size >= book_ids.length) {
                    final int prevLgth = book_ids.length;
                    int newSize = Math.max(prevLgth << 1, 1);
                    while (newSize < size) {
                        newSize <<= 1;
                    }
                    final BookId[] newBI = new BookId[newSize];
                    System.arraycopy(book_ids, 0, newBI, 0, prevLgth);
                    for (int i = prevLgth; i < newSize; ++i) {
                        newBI[i] = new BookId(i, NAMES[i % NAMES.length]);
                    }
                    book_ids = newBI;
                }
            }
        }
    }

    /**
     * Randomly generate one trade.
     *
     * @param tradeId      the id of the trade to generate
     * @param product      the product to attach to the trade
     * @param counterParty the counter party to attach to the trade
     * @return the generated trade
     */
    public Trade generate(long tradeId, Product product, CounterParty counterParty) {
        return generate(tradeId, product, counterParty, ThreadLocalRandom.current());
    }

    /**
     * Randomly generate one trade.
     *
     * @param tradeId      the id of the trade to generate
     * @param product      the product to attach to the trade
     * @param counterParty the counter party to attach to the trade
     * @param random       externally provided random
     * @return the generated trade
     */
    public Trade generate(long tradeId, Product product, CounterParty counterParty, Random random) {

        Trade trade = new Trade();
        //id
        trade.setId(tradeId);
        //productId
        trade.setProductId(product.getId());
        //quantity
        trade.setProductQtyMultiplier(random.nextInt(PRODUCTQTY_MAX));
        //desk
        trade.setDesk(getDesk(product.getUnderlierType()));
        //bookId
        trade.setBookId(getBookId(random.nextInt(book_ids.length)));
        //status
        trade.setStatus(statuses[random.nextInt(statuses.length)]);
        //is simulated
        if ("SIMULATION".equals(trade.getStatus())) {
            trade.setIsSimulated("SIMULATION");
        } else {
            trade.setIsSimulated("LIVE");
        }
        //value date
        final LocalDate generatedDate = nowAsLocalDate.plusDays(random.nextInt(DATE_DEPTH_MAX));
        trade.setDate(generatedDate);
        //Counterparty
        trade.setCounterParty(counterParty.getCounterParty());

        // Bucket the date of the trade
        Entry<Long, Object> ceilingEntry = getBucketEntry(trade.getDate());
        if (ceilingEntry == null) {
            throw new QuartetRuntimeException("There is no bucket large enough to hold: " + trade.getDate());
        } else {
            trade.setDateBucket(ceilingEntry.getValue().toString());
        }

        return trade;
    }

    /**
     * Retrieve the date bucket associated to a date.
     *
     * @param date The date from which the bucket must be found.
     * @return The bucket.
     */
    public Object getDateBucket(LocalDate date) {
        return getBucketEntry(date).getValue();
    }

    /**
     * Retrieves the matching bucket entry for a given date.
     *
     * @param date The date.
     * @return The matching bucket.
     */
    protected Entry<Long, Object> getBucketEntry(LocalDate date) {
        return this.bucketMap.ceilingEntry(TimeUnit.DAYS.toMillis(date.toEpochDay()));
    }

    /**
     * Retrieves the desk for a given underlier type.
     *
     * @param underlierType The underlier type.
     * @return The associated desk.
     */
    protected String getDesk(String underlierType) {
        return desks.get(underlierType);
    }

    /**
     * Main function is only used for debugging and testing the random generation.
     *
     * @param args arguments
     */
    public static void main(String[] args) {
        int count = 10; //number of generated objects
        TradeGenerator generator = new TradeGenerator();
        CounterPartyGenerator counterPartyGenerator = new CounterPartyGenerator();

        for (int i = 0; i < count; i++) {
            int counterPartyId = i % CounterPartyGenerator.getNumberOfCounterParties();
            System.out.println(generator.generate(i, new Product(i), counterPartyGenerator.generate(counterPartyId)));
        }
    }

}
