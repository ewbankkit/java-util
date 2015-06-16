/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.productlifecycle;

import static com.netsol.adagent.util.productlifecycle.ProductLifeCycleEventData.GENERIC_DECIMAL_SCALE;

import java.util.Date;

import com.netsol.adagent.util.dbhelpers.BaseHelper;

/**
 * Represents a provisioning product renewal event.
 */
public class ProvisioningRenewalEvent extends ProductLifeCycleEvent {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:19 ProvisioningRenewalEvent.java NSI";

    /**
     * Constructor.
     */
    /* package-private */ ProvisioningRenewalEvent(ProductLifeCycleEventData productLifeCycleEventData) {
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

    public void setNewCurrentTarget(double newCurrentTarget) {
        productLifeCycleEventData.setGenericDecimal4(BaseHelper.toBigDecimal(newCurrentTarget, GENERIC_DECIMAL_SCALE));
    }

    public void setNewCurrentTarget(Double newCurrentTarget) {
        productLifeCycleEventData.setGenericDecimal4(BaseHelper.toBigDecimal(newCurrentTarget, GENERIC_DECIMAL_SCALE));
    }

    public Double getNewCurrentTarget() {
        return BaseHelper.toDouble(productLifeCycleEventData.getGenericDecimal4());
    }

    public void setOldCurrentTarget(double oldCurrentTarget) {
        productLifeCycleEventData.setGenericDecimal3(BaseHelper.toBigDecimal(oldCurrentTarget, GENERIC_DECIMAL_SCALE));
    }

    public void setOldCurrentTarget(Double oldCurrentTarget) {
        productLifeCycleEventData.setGenericDecimal3(BaseHelper.toBigDecimal(oldCurrentTarget, GENERIC_DECIMAL_SCALE));
    }

    public Double getOldCurrentTarget() {
        return BaseHelper.toDouble(productLifeCycleEventData.getGenericDecimal3());
    }

    public void setNewExpirationDate(Date newExpirationDate) {
        productLifeCycleEventData.setGenericDate2(BaseHelper.toSqlDate(newExpirationDate));
    }

    public Date getNewExpirationDate() {
        return productLifeCycleEventData.getGenericDate2();
    }

    public void setOldExpirationDate(Date oldExpirationDate) {
        productLifeCycleEventData.setGenericDate1(BaseHelper.toSqlDate(oldExpirationDate));
    }

    public Date getOldExpirationDate() {
        return productLifeCycleEventData.getGenericDate1();
    }

    public void setNewStartDate(Date newStartDate) {
        productLifeCycleEventData.setGenericDate4(BaseHelper.toSqlDate(newStartDate));
    }

    public Date getNewStartDate() {
        return productLifeCycleEventData.getGenericDate4();
    }

    public void setOldStartDate(Date oldStartDate) {
        productLifeCycleEventData.setGenericDate3(BaseHelper.toSqlDate(oldStartDate));
    }

    public Date getOldStartDate() {
        return productLifeCycleEventData.getGenericDate3();
    }
}
