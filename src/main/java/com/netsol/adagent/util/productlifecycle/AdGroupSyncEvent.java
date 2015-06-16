/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.productlifecycle;

/**
 * Represents an ad group sync event.
 */
public class AdGroupSyncEvent extends BaseSyncEvent {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:13 AdGroupSyncEvent.java NSI";

    /**
     * Constructor.
     */
    /* package-private */ AdGroupSyncEvent(ProductLifeCycleEventData productLifeCycleEventData) {
        super(productLifeCycleEventData);
    }

    public void setNsAdGroupId(long nsAdGroupId) {
        productLifeCycleEventData.setGenericNumber1(Long.valueOf(nsAdGroupId));
    }

    public Long getNsAdGroupId() {
        return productLifeCycleEventData.getGenericNumber1();
    }
}
