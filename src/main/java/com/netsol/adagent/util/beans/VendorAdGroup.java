/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.beans;

/**
 * Represents a vendor ad group.
 */
public class VendorAdGroup extends VendorEntityWithShareOfVoice {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:10 VendorAdGroup.java NSI";

    public void setNsAdGroupId(long nsAdGroupId) {
        setNsEntityId(nsAdGroupId);
    }

    public long getNsAdGroupId() {
        return getNsEntityId();
    }
}
