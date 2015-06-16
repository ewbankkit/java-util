/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.productlifecycle;

import static com.netsol.adagent.util.productlifecycle.ProductLifeCycleEventData.GENERIC_DECIMAL_SCALE;

import com.netsol.adagent.util.dbhelpers.BaseHelper;

/**
 * Represents an ad campaign status change event.
 */
public class AdCampaignStatusChangeEvent extends BaseAdCampaignEvent {
    /**
     * Constructor.
     */
    /* package-private */ AdCampaignStatusChangeEvent(ProductLifeCycleEventData productLifeCycleEventData) {
        super(productLifeCycleEventData);
    }

    public void setNewCampaignStatus(String newCampaignStatus) {
        productLifeCycleEventData.setGenericString2(newCampaignStatus);
    }

    public String getNewCampaignStatus() {
        return productLifeCycleEventData.getGenericString2();
    }

    public void setOldCampaignStatus(String oldCampaignStatus) {
        productLifeCycleEventData.setGenericString1(oldCampaignStatus);
    }

    public String getOldCampaignStatus() {
        return productLifeCycleEventData.getGenericString1();
    }

    public void setNewDailyBudget(double newDailyBudget) {
        productLifeCycleEventData.setGenericDecimal2(BaseHelper.toBigDecimal(newDailyBudget, GENERIC_DECIMAL_SCALE));
    }

    public void setNewDailyBudget(Number newDailyBudget) {
        productLifeCycleEventData.setGenericDecimal2(BaseHelper.toBigDecimal(newDailyBudget, GENERIC_DECIMAL_SCALE));
    }

    public Double getNewDailyBudget() {
        return BaseHelper.toDouble(productLifeCycleEventData.getGenericDecimal2());
    }

    public void setOldDailyBudget(Number oldDailyBudget) {
        productLifeCycleEventData.setGenericDecimal1(BaseHelper.toBigDecimal(oldDailyBudget, GENERIC_DECIMAL_SCALE));
    }

    public void setOldDailyBudget(double oldDailyBudget) {
        productLifeCycleEventData.setGenericDecimal1(BaseHelper.toBigDecimal(oldDailyBudget, GENERIC_DECIMAL_SCALE));
    }

    public Double getOldDailyBudget() {
        return BaseHelper.toDouble(productLifeCycleEventData.getGenericDecimal1());
    }
}
