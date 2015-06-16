/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.productlifecycle;

/**
 * Represents a sync event.
 */
public abstract class BaseSyncEvent extends ProductLifeCycleEvent {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:14 BaseSyncEvent.java NSI";

    /**
     * Constructor.
     */
    /* package-private */ BaseSyncEvent(ProductLifeCycleEventData productLifeCycleEventData) {
        super(productLifeCycleEventData);
    }

    public void setUpdatedByUser(String updatedByUser) {
        productLifeCycleEventData.setGenericString1(updatedByUser);
    }

    public String getUpdatedByUser() {
        return productLifeCycleEventData.getGenericString1();
    }
}
