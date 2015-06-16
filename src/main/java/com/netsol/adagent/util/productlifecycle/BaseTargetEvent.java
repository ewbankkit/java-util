/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.productlifecycle;

/**
 * Represents a target event.
 */
public abstract class BaseTargetEvent extends ProductLifeCycleEvent {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:14 BaseTargetEvent.java NSI";

    /**
     * Constructor.
     */
    /* package-private */ BaseTargetEvent(ProductLifeCycleEventData productLifeCycleEventData) {
        super(productLifeCycleEventData);
    }

    public void setTargetId(long targetId) {
        productLifeCycleEventData.setGenericNumber1(Long.valueOf(targetId));
    }

    public void setTargetId(Long targetId) {
        productLifeCycleEventData.setGenericNumber1(targetId);
    }

    public Long getTargetId() {
        return productLifeCycleEventData.getGenericNumber1();
    }

    public void setUpdatedByUser(String updatedByUser) {
        productLifeCycleEventData.setGenericString10(updatedByUser);
    }

    public String getUpdatedByUser() {
        return productLifeCycleEventData.getGenericString10();
    }}
