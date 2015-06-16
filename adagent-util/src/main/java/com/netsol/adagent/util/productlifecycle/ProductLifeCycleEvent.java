/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.productlifecycle;

import java.util.Date;

import com.netsol.adagent.util.beans.BaseData;

/**
 * Represents a product life cycle event.
 */
public abstract class ProductLifeCycleEvent extends BaseData {
    /* package-private */ final ProductLifeCycleEventData productLifeCycleEventData;

    /**
     * Constructor.
     */
    /* package-private */ ProductLifeCycleEvent(ProductLifeCycleEventData productLifeCycleEventData) {
        if (productLifeCycleEventData == null) {
            throw new IllegalArgumentException();
        }

        this.productLifeCycleEventData = productLifeCycleEventData;
    }

    public Date getEventDate() {
        return productLifeCycleEventData.getEventDate();
    }

    public String getEventType() {
        return productLifeCycleEventData.getEventType();
    }

    public void setProdInstId(String prodInstId) {
        productLifeCycleEventData.setProdInstId(prodInstId);
    }

    public String getProdInstId() {
        return productLifeCycleEventData.getProdInstId();
    }
}
