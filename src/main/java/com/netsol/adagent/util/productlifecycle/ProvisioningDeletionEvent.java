/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.productlifecycle;

/**
 * Represents a provisioning product deletion event.
 */
public class ProvisioningDeletionEvent extends ProductLifeCycleEvent {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:18 ProvisioningDeletionEvent.java NSI";

    /**
     * Constructor.
     */
    /* package-private */ ProvisioningDeletionEvent(ProductLifeCycleEventData productLifeCycleEventData) {
        super(productLifeCycleEventData);
    }

    public void setDelayedDelete(Boolean delayedDelete) {
        productLifeCycleEventData.setGenericBoolean1(delayedDelete);
    }

    public Boolean getDelayedDelete() {
        return productLifeCycleEventData.getGenericBoolean1();
    }
}
