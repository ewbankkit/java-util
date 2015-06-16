/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.productlifecycle;

/**
 * Represents a product sync event.
 */
public class ProductSyncEvent extends BaseSyncEvent {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:17 ProductSyncEvent.java NSI";

    /**
     * Constructor.
     */
    /* package-private */ ProductSyncEvent(ProductLifeCycleEventData productLifeCycleEventData) {
        super(productLifeCycleEventData);
    }
}
