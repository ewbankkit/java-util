/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.productlifecycle;

import com.netsol.adagent.util.dbhelpers.BaseHelper;

/**
 * Represents a ad account creation event.
 */
public class AdAccountCreationEvent extends ProductLifeCycleEvent {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:13 AdAccountCreationEvent.java NSI";

    /**
     * Constructor.
     */
    /* package-private */ AdAccountCreationEvent(ProductLifeCycleEventData productLifeCycleEventData) {
        super(productLifeCycleEventData);
    }

    public void setAccountId1(String accountId1) {
        productLifeCycleEventData.setGenericString1(accountId1);
    }

    public String getAccountId1() {
        return productLifeCycleEventData.getGenericString1();
    }

    public void setAccountId2(String accountId2) {
        productLifeCycleEventData.setGenericString3(accountId2);
    }

    public String getAccountId2() {
        return productLifeCycleEventData.getGenericString3();
    }

    public void setAccountName(String accountName) {
        productLifeCycleEventData.setGenericString2(accountName);
    }

    public String getAccountName() {
        return productLifeCycleEventData.getGenericString2();
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
