/*
 * (C) Quartet FS 2010
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of Quartet Financial Systems Limited. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */
package org.mendirl.service.cube.activepivot.postprocessor;

import com.quartetfs.biz.pivot.IActivePivotVersion;
import com.quartetfs.biz.pivot.ILocation;
import com.quartetfs.biz.pivot.context.IContext;
import com.quartetfs.biz.pivot.impl.LocationDiscriminator;
import com.quartetfs.biz.pivot.query.aggregates.IAggregatesContinuousHandler;
import com.quartetfs.biz.pivot.query.aggregates.IImpact;
import com.quartetfs.biz.pivot.query.aggregates.impl.AAggregatesContinuousHandler;
import com.quartetfs.biz.pivot.query.aggregates.impl.Impact;
import com.quartetfs.fwk.QuartetException;
import com.quartetfs.fwk.QuartetExtendedPluginValue;
import org.mendirl.service.cube.activepivot.context.IReferenceCurrency;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <b>ForexHandler</b>
 * <p>
 * extends for the abstract class AAggregatesContinuousHandler.
 *
 * @author Quartet FS
 */
@QuartetExtendedPluginValue(intf = IAggregatesContinuousHandler.class, key = ForexHandler.PLUGIN_KEY)
public class ForexHandler extends AAggregatesContinuousHandler<Object> {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -5399980219497341596L;

    /**
     * the logger
     **/
    private static Logger logger = Logger.getLogger(ForexHandler.class.getName());

    /**
     * plugin key
     */
    public static final String PLUGIN_KEY = "FOREX";

    /**
     * field for the location discriminator
     */
    private String currencyLevel;

    /**
     * Constructor
     *
     * @param pivot the {@link IActivePivotVersion} the handler is attached to
     */
    public ForexHandler(IActivePivotVersion pivot) {
        super(pivot);
    }

    /**
     * Setter to allow injection of currency level in Spring XML file
     *
     * @param currencyLevel the name of the currency level
     */
    public void setCurrencyLevel(String currencyLevel) {
        this.currencyLevel = currencyLevel;
    }

    @SuppressWarnings("unchecked")
    @Override
    public IImpact computeImpact(ILocation location, Object event) {
        //get the ReferenceCurrency context
        IContext currentContext = pivot.getContext();
        IReferenceCurrency referenceCurrencyCtx = currentContext.get(IReferenceCurrency.class);
        if (referenceCurrencyCtx == null) {
            logger.log(Level.SEVERE, "Reference currency context is not set.");
        }

        // get the reference currency
        String referenceCurrency = referenceCurrencyCtx.getCurrency();

        // Debug logging.
        if (logger.isLoggable(Level.FINE)) {
            logger.fine("Handler reference currency: " + referenceCurrency);
        }

        //get the updated currencies from the Forex Stream
        if (!(event instanceof Set))
            return new Impact(location, null, null);
        Set<String> updatedCurrencies = (Set<String>) event; // != null

        // determine the impacted currencies in the location
        if (!updatedCurrencies.contains(referenceCurrency)) {
            // if the reference currency is not one of the updated currencies,
            // there is no impact on the leaf level aggregation
            return new Impact(location, null, null);
        }

        // instantiate location discriminator with the current location and the Currency field
        LocationDiscriminator locationDiscriminator = new LocationDiscriminator(Collections.singleton(location), Collections.singleton(currencyLevel), pivot);

        // set the currency values of the impacts to the impacted currencies values
        Collection<Map<String, Object>> discriminators = new HashSet<Map<String, Object>>();
        for (String currency : updatedCurrencies) {
            if (currency.equals(referenceCurrency)) {
                // skip the reference currency since the aggregates expressed in the
                // reference currency are not impacted
                continue;
            }
            Map<String, Object> discriminator = new HashMap<String, Object>(1);
            discriminator.put(currencyLevel, currency);
            discriminators.add(discriminator);
        }

        //compute the impacted locations
        Collection<ILocation> impactedLocs = null;
        try {
            impactedLocs = locationDiscriminator.discriminate(discriminators);
        } catch (QuartetException e) {
            logger.log(Level.WARNING, "Exception while computing the impacted location", e);
        }

        //return the impacted location
        return new Impact(location, (Set<ILocation>) impactedLocs, null);
    }

    /**
     * @return the identifier of the stream associated with this handler.
     */
    @Override
    public String getStreamKey() {
        return ForexStream.PLUGIN_KEY;
    }

    /**
     * @return the type identifying this plugin value.
     */
    @Override
    public String getType() {
        return PLUGIN_KEY;
    }

}
