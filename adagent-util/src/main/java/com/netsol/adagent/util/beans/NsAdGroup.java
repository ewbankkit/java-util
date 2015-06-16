/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.beans;

import java.math.BigDecimal;

import com.netsol.adagent.util.dbhelpers.BaseHelper;

/**
 * Represents an NS ad group.
 */
public class NsAdGroup extends NsEntity {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:53:59 NsAdGroup.java NSI";

    private static final double DEFAULT_TARGET_CONVERSION_RATE = 0.1D;

    private static final int MAX_CONTENT_CPC_SCALE = TWO_DECIMAL_PLACES;
    private static final int MAX_CPC_SCALE = TWO_DECIMAL_PLACES;
    private static final int MAX_CPM_SCALE = TWO_DECIMAL_PLACES;

    private BigDecimal maxContentCpc;
    private BigDecimal maxCpc;
    private BigDecimal maxCpm;
    private long nsCampaignId;
    private double targetConversionRate = DEFAULT_TARGET_CONVERSION_RATE;

    public void setNsAdGroupId(long nsAdGroupId) {
        setNsEntityId(nsAdGroupId);
    }

    public long getNsAdGroupId() {
        return getNsEntityId();
    }

    public void setMaxContentCpc(Double maxContentCpc) {
        this.maxContentCpc = BaseHelper.toBigDecimal(maxContentCpc, MAX_CONTENT_CPC_SCALE);
    }

    public BigDecimal getMaxContentCpc() {
        return maxContentCpc;
    }

    public void setMaxCpc(Double maxCpc) {
        this.maxCpc = BaseHelper.toBigDecimal(maxCpc, MAX_CPC_SCALE);
    }

    public BigDecimal getMaxCpc() {
        return maxCpc;
    }

    public void setMaxCpm(Double maxCpm) {
        this.maxCpm = BaseHelper.toBigDecimal(maxCpm, MAX_CPM_SCALE);
    }

    public BigDecimal getMaxCpm() {
        return maxCpm;
    }

    public void setNsCampaignId(long nsCampaignId) {
        this.nsCampaignId = nsCampaignId;
    }

    public long getNsCampaignId() {
        return nsCampaignId;
    }

    public void setTargetConversionRate(double targetConversionRate) {
        this.targetConversionRate = targetConversionRate;
    }

    public double getTargetConversionRate() {
        return targetConversionRate;
    }

    public void setVendorAdGroupId(Long vendorAdGroupId) {
        setVendorEntityId(vendorAdGroupId);
    }

    public Long getVendorAdGroupId() {
        return getVendorEntityId();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof NsAdGroup)) {
            return false;
        }
        return equals(this, (NsAdGroup)o);
    }
}
