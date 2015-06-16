/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.beans;

/**
 * Represents a vendor keyword.
 */
public class VendorKeyword extends VendorEntity {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:12 VendorKeyword.java NSI";

    public void setNsKeywordId(long nsKeywordId) {
        setNsEntityId(nsKeywordId);
    }

    public long getNsKeywordId() {
        return getNsEntityId();
    }
}
