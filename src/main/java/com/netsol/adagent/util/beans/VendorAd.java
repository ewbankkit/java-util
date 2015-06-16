/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.beans;

/**
 * Represents a vendor ad.
 */
public class VendorAd extends VendorEntity {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:10 VendorAd.java NSI";

    public void setNsAdId(long nsAdId) {
        setNsEntityId(nsAdId);
    }

    public long getNsAdId() {
        return getNsEntityId();
    }
}
