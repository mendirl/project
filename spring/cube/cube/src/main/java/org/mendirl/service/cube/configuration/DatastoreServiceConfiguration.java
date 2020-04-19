package org.mendirl.service.cube.configuration;

import com.qfs.desc.IStoreSecurity;
import com.qfs.service.store.IDatastoreServiceConfiguration;
import com.quartetfs.fwk.format.IFormatter;
import com.quartetfs.fwk.format.IParser;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class DatastoreServiceConfiguration implements IDatastoreServiceConfiguration {
    /**
     * the store security configuration
     */
    protected DatastoreRestStoresSecurityConfig storesSecurityConfig;

    /**
     * Date pattern for date calculators
     */
    protected static final String DATE_PATTERN = "yyyy-MM-dd";

    /**
     * @see #getCustomParsers()
     */
    protected Map<String, Map<String, IParser<?>>> customParsers;

    /**
     * @see #getCustomFormatters()
     */
    protected Map<String, Map<String, IFormatter>> customFormatters;

    /**
     * Default query timeout for queries
     */
    protected static final long DEFAULT_QUERY_TIMEOUT = 30_000L;

    /**
     * Constructor of {@link DatastoreServiceConfiguration}.
     */
    public DatastoreServiceConfiguration() {
        // SECURITY CONFIGURATION
        this.storesSecurityConfig = new DatastoreRestStoresSecurityConfig();

        // ADDITIONAL FORMATTERS
        this.customFormatters = new HashMap<>();

        // ADDITIONAL PARSERS
        this.customParsers = new HashMap<>();
    }

    @Override
    public Map<String, Map<String, IParser<?>>> getCustomParsers() {
        return this.customParsers;
    }

    @Override
    public Map<String, Map<String, IFormatter>> getCustomFormatters() {
        return this.customFormatters;
    }

    @Override
    public Map<String, IStoreSecurity> getStoresSecurity() {
        return storesSecurityConfig.getStoresSecurity();
    }

    @Override
    public long getDefaultQueryTimeout() {
        return DEFAULT_QUERY_TIMEOUT;
    }
}
