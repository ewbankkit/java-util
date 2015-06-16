/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.productlifecycle;

import com.netsol.adagent.util.dbhelpers.BaseHelper;

/**
 * Represents an ad campaign event.
 */
public abstract class BaseAdCampaignEvent extends ProductLifeCycleEvent {
    /**
     * Constructor.
     */
    /* package-private */ BaseAdCampaignEvent(ProductLifeCycleEventData productLifeCycleEventData) {
        super(productLifeCycleEventData);
    }

    public void setNsCampaignId(long nsCampaignId) {
        setNsCampaignId(Long.valueOf(nsCampaignId));
    }

    public void setNsCampaignId(Long nsCampaignId) {
        productLifeCycleEventData.setGenericNumber3(nsCampaignId);
    }

    public Long getNsCampaignId() {
        return productLifeCycleEventData.getGenericNumber3();
    }

    public void setUpdatedByUser(String updatedByUser) {
        productLifeCycleEventData.setGenericString3(updatedByUser);
    }

    public String getUpdatedByUser() {
        return productLifeCycleEventData.getGenericString3();
    }

    public void setVendorCampaignId(long vendorCampaignId) {
        setVendorCampaignId(Long.valueOf(vendorCampaignId));
    }

    public void setVendorCampaignId(Long vendorCampaignId) {
        productLifeCycleEventData.setGenericNumber2(vendorCampaignId);
    }

    public Long getVendorCampaignId() {
        return productLifeCycleEventData.getGenericNumber2();
    }

    public void setVendorId(int vendorId) {
        setVendorId(Integer.valueOf(vendorId));
    }

    public void setVendorId(Integer vendorId) {
        productLifeCycleEventData.setGenericNumber1(BaseHelper.toLong(vendorId));
    }

    public Integer getVendorId() {
        return BaseHelper.toInteger(productLifeCycleEventData.getGenericNumber1());
    }
}
