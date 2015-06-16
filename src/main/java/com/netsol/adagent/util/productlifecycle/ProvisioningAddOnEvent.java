/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.productlifecycle;

import static com.netsol.adagent.util.productlifecycle.ProductLifeCycleEventData.GENERIC_DECIMAL_SCALE;

import com.netsol.adagent.util.dbhelpers.BaseHelper;

/**
 * Represents a provisioning product add-on event.
 */
public class ProvisioningAddOnEvent extends ProductLifeCycleEvent {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:17 ProvisioningAddOnEvent.java NSI";

    /**
     * Constructor.
     */
    /* package-private */ ProvisioningAddOnEvent(ProductLifeCycleEventData productLifeCycleEventData) {
        super(productLifeCycleEventData);
    }

    public void setAmountToAdd(double amountToAdd) {
        productLifeCycleEventData.setGenericDecimal1(BaseHelper.toBigDecimal(amountToAdd, GENERIC_DECIMAL_SCALE));
    }

    public void setAmountToAdd(Double amountToAdd) {
        productLifeCycleEventData.setGenericDecimal1(BaseHelper.toBigDecimal(amountToAdd, GENERIC_DECIMAL_SCALE));
    }

    public Double getAmountToAdd() {
        return BaseHelper.toDouble(productLifeCycleEventData.getGenericDecimal1());
    }

    public void setAmountToRemove(double amountToRemove) {
        productLifeCycleEventData.setGenericDecimal2(BaseHelper.toBigDecimal(amountToRemove, GENERIC_DECIMAL_SCALE));
    }

    public void setAmountToRemove(Double amountToRemove) {
        productLifeCycleEventData.setGenericDecimal2(BaseHelper.toBigDecimal(amountToRemove, GENERIC_DECIMAL_SCALE));
    }

    public Double getAmountToRemove() {
        return BaseHelper.toDouble(productLifeCycleEventData.getGenericDecimal2());
    }
}
