/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.productlifecycle;

/**
 * Represents a campaign sync event.
 */
public class CampaignSyncEvent extends BaseSyncEvent {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:15 CampaignSyncEvent.java NSI";

    /**
     * Constructor.
     */
    /* package-private */ CampaignSyncEvent(ProductLifeCycleEventData productLifeCycleEventData) {
        super(productLifeCycleEventData);
    }

    public void setNsCampaignId(long nsCampaignId) {
        productLifeCycleEventData.setGenericNumber1(Long.valueOf(nsCampaignId));
    }

    public Long getNsCampaignId() {
        return productLifeCycleEventData.getGenericNumber1();
    }
}
