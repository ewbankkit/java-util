/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.beans;

/**
 * Represents an NS negative keyword.
 */
public class NsNegativeKeyword extends NsEntity {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:02 NsNegativeKeyword.java NSI";

    private Long marketSubCategoryGroupId;
    private long nsAdGroupId;

    public void setKeyword(String keyword) {
        setName(keyword);
    }

    public String getKeyword() {
        return getName();
    }

    public void setMarketSubCategoryGroupId(Long marketSubCategoryGroupId) {
        this.marketSubCategoryGroupId = marketSubCategoryGroupId;
    }

    public Long getMarketSubCategoryGroupId() {
        return marketSubCategoryGroupId;
    }

    public void setNsAdGroupId(long nsAdGroupId) {
        this.nsAdGroupId = nsAdGroupId;
    }

    public long getNsAdGroupId() {
        return nsAdGroupId;
    }

    public void setNsNegativeId(long nsNegativeId) {
        setNsEntityId(nsNegativeId);
    }

    public long getNsNegativeId() {
        return getNsEntityId();
    }

    public void setVendorNegativeId(Long vendorNegativeId) {
        setVendorEntityId(vendorNegativeId);
    }

    public Long getVendorNegativeId() {
        return getVendorEntityId();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof NsNegativeKeyword)) {
            return false;
        }
        return equals(this, (NsNegativeKeyword)o);
    }
}
