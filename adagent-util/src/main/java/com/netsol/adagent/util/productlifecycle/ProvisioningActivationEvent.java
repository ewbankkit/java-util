/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.productlifecycle;

import static com.netsol.adagent.util.productlifecycle.ProductLifeCycleEventData.GENERIC_DECIMAL_SCALE;

import java.util.Date;

import com.netsol.adagent.util.dbhelpers.BaseHelper;

/**
 * Represents a provisioning product activation event.
 */
public class ProvisioningActivationEvent extends ProductLifeCycleEvent {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:17 ProvisioningActivationEvent.java NSI";

    /**
     * Constructor.
     */
    /* package-private */ ProvisioningActivationEvent(ProductLifeCycleEventData productLifeCycleEventData) {
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

    public void setNewTermUnit(String newTermUnit) {
        productLifeCycleEventData.setGenericString2(newTermUnit);
    }

    public String getNewTermUnit() {
        return productLifeCycleEventData.getGenericString2();
    }

    public void setOldTermUnit(String oldTermUnit) {
        productLifeCycleEventData.setGenericString1(oldTermUnit);
    }

    public String getOldTermUnit() {
        return productLifeCycleEventData.getGenericString1();
    }

    public void setNewUrl(String newUrl) {
        productLifeCycleEventData.setGenericString4(newUrl);
    }

    public String getNewUrl() {
        return productLifeCycleEventData.getGenericString4();
    }

    public void setOldUrl(String oldUrl) {
        productLifeCycleEventData.setGenericString3(oldUrl);
    }

    public String getOldUrl() {
        return productLifeCycleEventData.getGenericString3();
    }
}
