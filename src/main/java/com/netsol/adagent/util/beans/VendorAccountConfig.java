/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.beans;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a vendor account configuration. 
 */
public class VendorAccountConfig extends BaseData {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:10 VendorAccountConfig.java NSI";

    private boolean masterAccount;
    // The properties are assumed to be unmodifiable.
    private Map<String, String> properties = Collections.emptyMap();
    private int vendorAccountId;
    private int vendorId;
    
    public void setMasterAccount(boolean masterAccount) {
        this.masterAccount = masterAccount;
    }

    public boolean isMasterAccount() {
        return masterAccount;
    }
    
    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setVendorAccountId(int vendorAccountId) {
        this.vendorAccountId = vendorAccountId;
    }

    public int getVendorAccountId() {
        return vendorAccountId;
    }

    public void setVendorId(int vendorId) {
        this.vendorId = vendorId;
    }

    public int getVendorId() {
        return vendorId;
    }
    
    public void addProperties(Collection<Pair<String, String>> properties) {
        Map<String, String> newProperties = new HashMap<String, String>(this.properties);
        for (Pair<String, String> property : properties) {
            newProperties.put(property.getFirst(), property.getSecond());
        }
        this.properties = Collections.unmodifiableMap(newProperties);
    }
    
    public void removeProperties(Collection<String> keys) {
        Map<String, String> newProperties = new HashMap<String, String>(this.properties);
        for (String key : keys) {
            newProperties.remove(key);
        }
        this.properties = Collections.unmodifiableMap(newProperties);;
    }
}
