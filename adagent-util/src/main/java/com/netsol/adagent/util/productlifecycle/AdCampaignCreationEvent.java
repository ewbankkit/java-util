/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.productlifecycle;

import static com.netsol.adagent.util.productlifecycle.ProductLifeCycleEventData.GENERIC_DECIMAL_SCALE;

import com.netsol.adagent.util.dbhelpers.BaseHelper;

/**
 * Represents an ad campaign creation event.
 */
public class AdCampaignCreationEvent extends BaseAdCampaignEvent {
    /**
     * Constructor.
     */
    /* package-private */ AdCampaignCreationEvent(ProductLifeCycleEventData productLifeCycleEventData) {
        super(productLifeCycleEventData);
    }

    public void setCampaignName(String campaignName) {
        productLifeCycleEventData.setGenericString1(campaignName);
    }

    public String getCampaignName() {
        return productLifeCycleEventData.getGenericString1();
    }

    public void setCampaignStatus(String campaignStatus) {
        productLifeCycleEventData.setGenericString2(campaignStatus);
    }

    public String getCampaignStatus() {
        return productLifeCycleEventData.getGenericString2();
    }

    public void setDailyBudget(double dailyBudget) {
        productLifeCycleEventData.setGenericDecimal1(BaseHelper.toBigDecimal(dailyBudget, GENERIC_DECIMAL_SCALE));
    }

    public void setDailyBudget(Number dailyBudget) {
        productLifeCycleEventData.setGenericDecimal1(BaseHelper.toBigDecimal(dailyBudget, GENERIC_DECIMAL_SCALE));
    }

    public Double getDailyBudget() {
        return BaseHelper.toDouble(productLifeCycleEventData.getGenericDecimal1());
    }
}
