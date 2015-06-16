/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.productlifecycle;

/**
 * Represents a provisioning product deactivation event.
 */
public class ProvisioningDeactivationEvent extends ProductLifeCycleEvent {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:18 ProvisioningDeactivationEvent.java NSI";

    /**
     * Constructor.
     */
    /* package-private */ ProvisioningDeactivationEvent(ProductLifeCycleEventData productLifeCycleEventData) {
        super(productLifeCycleEventData);
    }
}
