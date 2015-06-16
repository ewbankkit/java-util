/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.beans;

import java.math.BigDecimal;
import java.util.Date;

import com.netsol.adagent.util.codes.AdRotation;
import com.netsol.adagent.util.codes.NetworkTarget;
import com.netsol.adagent.util.dbhelpers.BaseHelper;

/**
 * Represents an NS campaign.
 */
@ColumnNameOverride({"vendorEntityId/vendor_campaign_id"})
public class NsCampaign extends NsEntity {
    private static final int DAILY_BUDGET_SCALE = TWO_DECIMAL_PLACES;
    private static final int MONTHLY_BUDGET_SCALE = TWO_DECIMAL_PLACES;

    @ColumnName("ad_rotation")
    private String adRotation = AdRotation.OPTIMIZE;
    @ColumnName("daily_budget")
    private BigDecimal dailyBudget = BigDecimal.ZERO;
    @ColumnName("end_date")
    private Date endDate;
    @ColumnName("monbthly_budget")
    private BigDecimal monthlyBudget;
    @ColumnName("network_target")
    private String networkTarget = NetworkTarget.SPONSORED;
    @ColumnName("percent_of_budget")
    private float percentOfBudget;
    @ColumnName("search_engine_id")
    private Integer searchEngineId;
    @ColumnName("spend_aggressiveness")
    private float spendAggressiveness;
    @ColumnName("start_date")
    private Date startDate;
    @ColumnName("target_id")
    private Long targetId;

    public void setAdRotation(String adRotation) {
    	setTrackedField("adRotation", adRotation);
    }

    public String getAdRotation() {
        return adRotation;
    }

    public void setDailyBudget(double dailyBudget) {
        this.dailyBudget = BaseHelper.toBigDecimal(dailyBudget, DAILY_BUDGET_SCALE);
    }

    public void setDailyBudget(BigDecimal dailyBudget) {
    	setTrackedField("dailyBudget", dailyBudget);
    }

    public BigDecimal getDailyBudget() {
        return dailyBudget;
    }

    public void setEndDate(Date endDate) {
    	setTrackedField("endDate", endDate);
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setMonthlyBudget(double monthlyBudget) {
    	setMonthlyBudget(BaseHelper.toBigDecimal(monthlyBudget, MONTHLY_BUDGET_SCALE));
    }

    public void setMonthlyBudget(Number monthlyBudget) {
    	setMonthlyBudget(BaseHelper.toBigDecimal(monthlyBudget, MONTHLY_BUDGET_SCALE));
    }

    public void setMonthlyBudget(BigDecimal monthlyBudget) {
    	setTrackedField("monthlyBudget", monthlyBudget);
    }

    public BigDecimal getMonthlyBudget() {
        return monthlyBudget;
    }

    public void setNetworkTarget(String networkTarget) {
    	setTrackedField("networkTarget", networkTarget);
    }

    public String getNetworkTarget() {
        return networkTarget;
    }

    public void setNsCampaignId(long nsCampaignId) {
        setNsEntityId(nsCampaignId);
    }

    public long getNsCampaignId() {
        return getNsEntityId();
    }

    public void setPercentOfBudget(float percentOfBudget) {
    	setTrackedField("percentOfBudget", percentOfBudget);
    }

    public float getPercentOfBudget() {
        return percentOfBudget;
    }

    public void setSearchEngineId(Integer searchEngineId) {
    	setTrackedField("searchEngineId", searchEngineId);
    }

    public Integer getSearchEngineId() {
        return searchEngineId;
    }

    public void setSpendAggressiveness(float spendAggressiveness) {
    	setTrackedField("spendAggressiveness", spendAggressiveness);
    }

    public float getSpendAggressiveness() {
        return spendAggressiveness;
    }

    public void setStartDate(Date startDate) {
    	setTrackedField("startDate", startDate);
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setTargetId(Long targetId) {
    	setTrackedField("targetId", targetId);
    }

    public Long getTargetId() {
        return targetId;
    }

    public void setVendorCampaignId(Long vendorCampaignId) {
        setVendorEntityId(vendorCampaignId);
    }

    public Long getVendorCampaignId() {
        return getVendorEntityId();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof NsCampaign)) {
            return false;
        }
        return equals(this, (NsCampaign)o);
    }

    public static void copy(NsCampaign src, NsCampaign dest) {
        if ((src != null) && (dest != null)) {
            copy((NsEntity)src, (NsEntity)dest);
            dest.setAdRotation(src.getAdRotation());
            dest.setDailyBudget(src.getDailyBudget());
            dest.setEndDate(src.getEndDate());
            dest.setNetworkTarget(src.getNetworkTarget());
            // Don't overwrite any non-null start date with a null value.
            Date srcStartDate = src.getStartDate();
            if (srcStartDate != null) {
            	dest.setStartDate(srcStartDate);
            }
        }
    }
}
