/*
 * (C) Quartet FS 2007-2016
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of Quartet Financial Systems Limited. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */
package org.mendirl.service.cube.activepivot.source;


import com.qfs.store.IDatastore;
import com.qfs.store.query.ICursor;
import com.qfs.store.query.IQueryRunner;
import com.qfs.store.query.impl.DatastoreQueryHelper;
import org.mendirl.service.cube.activepivot.SandboxActivePivotUtils;
import org.mendirl.service.cube.activepivot.model.Rate;

import java.util.*;

import static org.mendirl.service.cube.configuration.DatastoreDescriptionConfiguration.*;


public class ForexRateGenerator {

    protected static String BASE_CURRENCY = "EUR";
    protected static Random RAND = new Random();

    protected Rate[] rates;
    protected String baseCurrency;
    protected Map<String, Double> ratesWithBaseCurrency;

    protected IDatastore datastore;

    public ForexRateGenerator(IDatastore ds) {
        this.datastore = ds;
    }

    /**
     * Initialises forex datastore generating all the rates from the base currency;
     *
     * @return An array of {@link Rate rates}
     */
    public Rate[] init() {
        final IQueryRunner queryRunner = this.datastore.getTransactionManager().getQueryRunner();
        final ICursor c = queryRunner
            .forStore(FOREX_STORE_NAME)
            .withoutCondition()
            .selecting(Arrays.asList(FOREX_CURRENCY, FOREX_TARGET_CURRENCY, FOREX_RATE))
            .onCurrentThread().run();
        final int size = DatastoreQueryHelper.getCursorSize(c);

        if (size == 0) {
            return null;
        }

        this.ratesWithBaseCurrency = new HashMap<>();
        this.baseCurrency = BASE_CURRENCY;
        this.ratesWithBaseCurrency.put(BASE_CURRENCY, 1d);
        while (c.next()) {
            Object[] r = c.getRecord().toTuple();
            assert this.baseCurrency.equals(r[0]);
            this.ratesWithBaseCurrency.put((String) r[1], (double) r[2]);
        }

        computeRates();
        return this.rates;
    }

    /**
     * Computes rate between all currencies
     */
    protected void computeRates() {
        final int size = this.ratesWithBaseCurrency.size();
        this.rates = new Rate[size * size];
        Set<String> listCurrency = new HashSet<>(this.ratesWithBaseCurrency.keySet());
        Iterator<String> it1 = listCurrency.iterator();
        int i = 0;
        while (it1.hasNext()) {
            String currency1 = it1.next();
            Iterator<String> it2 = listCurrency.iterator();
            while (it2.hasNext()) {
                String currency2 = it2.next();
                double r;
                if (currency1.equals(currency2)) {
                    r = 1d;
                } else if (currency1.equals(this.baseCurrency)) {
                    r = this.ratesWithBaseCurrency.get(currency2);
                } else if (currency2.equals(this.baseCurrency)) {
                    r = 1. / this.ratesWithBaseCurrency.get(currency1);
                } else {
                    r = this.ratesWithBaseCurrency.get(currency2) / this.ratesWithBaseCurrency.get(currency1);
                }
                this.rates[i] = new Rate(currency1, currency2, r);
                i++;
            }
        }
    }

    /**
     * Generates random exchange currency and replace them in rates
     *
     * @param count number of rate update
     * @return a set with all rates
     */
    public Set<Rate> generateRandomQuotations(int count) {

        final Set<Rate> modifiedRates = new HashSet<>();
        int n = this.ratesWithBaseCurrency.size();
        for (int i = 0; i < count; i++) {
            int index = RAND.nextInt(n);
            String currency = getKeyByIndex(this.ratesWithBaseCurrency, index);
            double value = this.ratesWithBaseCurrency.get(currency);

            // shift it randomly
            final double shiftedValue = value + SandboxActivePivotUtils.nextDouble(-0.01, 0.01, RAND);

            // insert it in the quotation Map
            // as we play with random values we don't insert negative ones
            if (shiftedValue > .0) {
                this.ratesWithBaseCurrency.put(currency, shiftedValue);
                computeRates();
                for (Rate r : this.rates) {
                    if (r.getCurrency().equals(currency) || r.getTargetCurrency().equals(currency)) {
                        modifiedRates.add(r);
                    }
                }
            }
        }
        return modifiedRates;
    }

    /**
     * Gets the currency at the index in the map
     *
     * @param rates map which contains rates between the base currency and another currency
     * @param index index element
     * @return the currency at the index in the map
     */
    protected String getKeyByIndex(Map<String, Double> rates, int index) {
        return rates.keySet().toArray(new String[]{})[index];
    }
}
