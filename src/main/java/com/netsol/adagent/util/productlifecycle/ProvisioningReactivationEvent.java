/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.productlifecycle;

/**
 * Represents a provisioning product reactivation event.
 */
public class ProvisioningReactivationEvent extends ProductLifeCycleEvent {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:18 ProvisioningReactivationEvent.java NSI";

    /**
     * Constructor.
     */
    /* package-private */ ProvisioningReactivationEvent(ProductLifeCycleEventData productLifeCycleEventData) {
        super(productLifeCycleEventData);
    }
}
