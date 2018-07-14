/*
 * (C) Quartet FS 2007-2018
 * ALL RIGHTS RESERVED. This material is the CONFIDENTIAL and PROPRIETARY
 * property of Quartet Financial Systems Limited. Any unauthorized use,
 * reproduction or transfer of this material is strictly prohibited
 */
package org.mendirl.service.cube.activeviam;

import com.qfs.desc.IStoreSecurity;
import com.qfs.desc.IStoreSecurityBuilder;
import com.qfs.desc.impl.StoreSecurityBuilder;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.mendirl.service.cube.CubeSecurityConfiguration.ROLE_ADMIN;
import static org.mendirl.service.cube.CubeSecurityConfiguration.ROLE_USER;
import static org.mendirl.service.cube.activeviam.DatastoreDescriptionConfiguration.RISK_STORE_NAME;

/**
 * Configuration class for the ReST security of the stores.
 * <p>
 * One can set up basic operations such as insertion or deletion for each store; for instance, see
 * {@link #counterPartyStorePermission()} for a store which supports both, and
 * {@link #forexStorePermission()} where only insertion is allowed.
 * <p>
 * Access rights can be set up by specifying the list of writers and also readers for each store.
 * Note that if a user is a writer for a store, they will also become a reader for that same store
 * without needing to specify it. See {@link #tradeStorePermission()} for a store where only
 * administrators can edit the store but which all users (including admins) can read.
 * <p>
 * Specific field permissions can also be configured, by specifying the field name, and then a set
 * of owners and a set of readers, just like store read/write access configuration. See
 * {@link #productStorePermission()} to see how specific fields are configured to be writable only
 * by admins, and readable by no one else than the admins.
 *
 * @author ActiveViam
 */
public class DatastoreRestStoresSecurityConfig {

    /**
     * The security for each store name
     */
    protected Map<String, IStoreSecurity> storesSecurity;

    /**
     * Constructor of {@link DatastoreRestStoresSecurityConfig}.
     */
    public DatastoreRestStoresSecurityConfig() {
        this.storesSecurity = buildStorePermission();
    }

    /**
     * Getter for the security of the stores.
     *
     * @return The security for each store name
     */
    public Map<String, IStoreSecurity> getStoresSecurity() {
        return storesSecurity;
    }

    /**
     * Builds the permissions for the counterparty store. It supports insertion and deletion. All
     * users can [...]
     *
     * @return The permissions for the counterparty store
     */
    protected IStoreSecurity counterPartyStorePermission() {
        IStoreSecurityBuilder builder = StoreSecurityBuilder.startBuildingStoreSecurity()
            .supportInsertion()
            .supportDeletion()
            .withStoreWriters(ROLE_ADMIN)
            .withStoreReaders(ROLE_USER);
        return builder.build();
    }

    /**
     * Builds the permissions for the trade store.
     *
     * @return The permissions for the trade store
     */
    protected IStoreSecurity tradeStorePermission() {
        IStoreSecurityBuilder builder = StoreSecurityBuilder.startBuildingStoreSecurity()
            .supportInsertion()
            .supportDeletion()
            .withStoreWriters(ROLE_ADMIN)
            .withStoreReaders(ROLE_USER);
        return builder.build();
    }

    /**
     * Builds the permissions for the city store.
     *
     * @return The permissions for the city store
     */
    protected IStoreSecurity cityStorePermission() {
        IStoreSecurityBuilder builder = StoreSecurityBuilder.startBuildingStoreSecurity()
            .supportInsertion()
            .supportDeletion()
            .withStoreWriters(ROLE_ADMIN)
            .withStoreReaders(ROLE_USER);
        return builder.build();
    }

    /**
     * Builds the permissions for the product store.
     *
     * @return The permissions for the product store
     */
    protected IStoreSecurity productStorePermission() {
        IStoreSecurityBuilder builder = StoreSecurityBuilder.startBuildingStoreSecurity()
            .supportInsertion()
            .supportDeletion()
            .withStoreWriters(ROLE_ADMIN)
            .withStoreReaders(ROLE_USER)
            .addFieldPermission("Id", Arrays.asList(ROLE_ADMIN), Collections.emptyList())
            .addFieldPermission("ProductName", Arrays.asList(ROLE_ADMIN), Collections.emptyList())
            .addFieldPermission("ProductType", Arrays.asList(ROLE_ADMIN), Collections.emptyList());
        return builder.build();
    }

    /**
     * Builds the permissions for the forex store.
     *
     * @return The permissions for the forex store
     */
    protected IStoreSecurity forexStorePermission() {
        IStoreSecurityBuilder builder = StoreSecurityBuilder.startBuildingStoreSecurity()
            .supportInsertion()
            .withStoreWriters(ROLE_ADMIN)
            .withStoreReaders(ROLE_USER)
            .addFieldPermission("Id", Arrays.asList(ROLE_ADMIN), Collections.emptyList());
        return builder.build();
    }

    /**
     * Builds the permissions for the risk store.
     *
     * @return The permissions for the risk store
     */
    protected IStoreSecurity riskStorePermission() {
        IStoreSecurityBuilder builder = StoreSecurityBuilder.startBuildingStoreSecurity()
            .supportInsertion()
            .supportDeletion()
            .withStoreWriters(ROLE_ADMIN)
            .withStoreReaders(ROLE_USER);
        return builder.build();
    }

    /**
     * Builds the permissions for the all of the stores.
     *
     * @return The permissions for the all of the stores
     */
    protected Map<String, IStoreSecurity> buildStorePermission() {
        Map<String, IStoreSecurity> storesPermissions = new HashMap<>();

        storesPermissions.put(RISK_STORE_NAME, riskStorePermission());

        return storesPermissions;
    }
}
