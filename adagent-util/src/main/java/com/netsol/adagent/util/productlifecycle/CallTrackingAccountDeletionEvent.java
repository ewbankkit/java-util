/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.productlifecycle;

import com.netsol.adagent.util.dbhelpers.BaseHelper;

/**
 * Represents a call tracking account deletion event.
 */
public class CallTrackingAccountDeletionEvent extends ProductLifeCycleEvent {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:15 CallTrackingAccountDeletionEvent.java NSI";

    /**
     * Constructor.
     */
    /* package-private */ CallTrackingAccountDeletionEvent(ProductLifeCycleEventData productLifeCycleEventData) {
        super(productLifeCycleEventData);
    }

    public void setAccountId(String accountId) {
        productLifeCycleEventData.setGenericString1(accountId);
    }

    public String getAccountId() {
        return productLifeCycleEventData.getGenericString1();
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
