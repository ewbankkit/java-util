/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.productlifecycle;

import static com.netsol.adagent.util.productlifecycle.ProductLifeCycleEventData.GENERIC_DECIMAL_SCALE;

import com.netsol.adagent.util.dbhelpers.BaseHelper;

/**
 * Represents a provisioning product upgrade event.
 */
public class ProvisioningUpgradeEvent extends ProductLifeCycleEvent {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:19 ProvisioningUpgradeEvent.java NSI";

    /**
     * Constructor.
     */
    /* package-private */ ProvisioningUpgradeEvent(ProductLifeCycleEventData productLifeCycleEventData) {
        super(productLifeCycleEventData);
    }

    public void setNewBaseTarget(double newBaseTarget) {
        productLifeCycleEventData.setGenericDecimal2(BaseHelper.toBigDecimal(newBaseTarget, GENERIC_DECIMAL_SCALE));
    }

    public void setNewBaseTarget(Double newBaseTarget) {
        productLifeCycleEventData.setGenericDecimal2(BaseHelper.toBigDecimal(newBaseTarget, GENERIC_DECIMAL_SCALE));
    }

    public Double getNewBaseTarget() {
        return BaseHelper.toDouble(productLifeCycleEventData.getGenericDecimal2());
    }

    public void setOldBaseTarget(double oldBaseTarget) {
        productLifeCycleEventData.setGenericDecimal1(BaseHelper.toBigDecimal(oldBaseTarget, GENERIC_DECIMAL_SCALE));
    }

    public void setOldBaseTarget(Double oldBaseTarget) {
        productLifeCycleEventData.setGenericDecimal1(BaseHelper.toBigDecimal(oldBaseTarget, GENERIC_DECIMAL_SCALE));
    }

    public Double getOldBaseTarget() {
        return BaseHelper.toDouble(productLifeCycleEventData.getGenericDecimal1());
    }

    public void setNewProductId(long newProductId) {
        productLifeCycleEventData.setGenericNumber2(Long.valueOf(newProductId));
    }

    public void setNewProductId(Long newProductId) {
        productLifeCycleEventData.setGenericNumber2(newProductId);
    }

    public Long getNewProductId() {
        return productLifeCycleEventData.getGenericNumber2();
    }

    public void setOldProductId(long oldProductId) {
        productLifeCycleEventData.setGenericNumber1(Long.valueOf(oldProductId));
    }

    public void setOldProductId(Long oldProductId) {
        productLifeCycleEventData.setGenericNumber1(oldProductId);
    }

    public Long getOldProductId() {
        return productLifeCycleEventData.getGenericNumber1();
    }

    public void setNewTermQuantity(int newTermQuantity) {
        productLifeCycleEventData.setGenericNumber4(Long.valueOf(newTermQuantity));
    }

    public void setNewTermQuantity(Integer newTermQuantity) {
        productLifeCycleEventData.setGenericNumber4(BaseHelper.toLong(newTermQuantity));
    }

    public Integer getNewTermQuantity() {
        return BaseHelper.toInteger(productLifeCycleEventData.getGenericNumber4());
    }

    public void setOldTermQuantity(int oldTermQuantity) {
        productLifeCycleEventData.setGenericNumber3(Long.valueOf(oldTermQuantity));
    }

    public void setOldTermQuantity(Integer oldTermQuantity) {
        productLifeCycleEventData.setGenericNumber3(BaseHelper.toLong(oldTermQuantity));
    }

    public Integer getOldTermQuantity() {
        return BaseHelper.toInteger(productLifeCycleEventData.getGenericNumber3());
    }
}
