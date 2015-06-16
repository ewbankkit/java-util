/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.productlifecycle;

import static com.netsol.adagent.util.productlifecycle.ProductLifeCycleEventData.GENERIC_DECIMAL_SCALE;

import java.util.Date;

import com.netsol.adagent.util.dbhelpers.BaseHelper;

/**
 * Represents a provisioning product creation event.
 */
public class ProvisioningCreationEvent extends ProductLifeCycleEvent {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:17 ProvisioningCreationEvent.java NSI";

    /**
     * Constructor.
     */
    /* package-private */ ProvisioningCreationEvent(ProductLifeCycleEventData productLifeCycleEventData) {
        super(productLifeCycleEventData);
    }

    public void setBaseTarget(double baseTarget) {
        productLifeCycleEventData.setGenericDecimal1(BaseHelper.toBigDecimal(baseTarget, GENERIC_DECIMAL_SCALE));
    }

    public void setBaseTarget(Double baseTarget) {
        productLifeCycleEventData.setGenericDecimal1(BaseHelper.toBigDecimal(baseTarget, GENERIC_DECIMAL_SCALE));
    }

    public Double getBaseTarget() {
        return BaseHelper.toDouble(productLifeCycleEventData.getGenericDecimal1());
    }

    public void setChannelId(long channelId) {
        productLifeCycleEventData.setGenericNumber2(Long.valueOf(channelId));
    }

    public void setChannelId(Long channelId) {
        productLifeCycleEventData.setGenericNumber2(channelId);
    }

    public Long getChannelId() {
        return productLifeCycleEventData.getGenericNumber2();
    }

    public void setCurrentTarget(double currentTarget) {
        productLifeCycleEventData.setGenericDecimal2(BaseHelper.toBigDecimal(currentTarget, GENERIC_DECIMAL_SCALE));
    }

    public void setCurrentTarget(Double currentTarget) {
        productLifeCycleEventData.setGenericDecimal2(BaseHelper.toBigDecimal(currentTarget, GENERIC_DECIMAL_SCALE));
    }

    public Double getCurrentTarget() {
        return BaseHelper.toDouble(productLifeCycleEventData.getGenericDecimal2());
    }

    public void setExpirationDate(Date expirationDate) {
        productLifeCycleEventData.setGenericDate1(BaseHelper.toSqlDate(expirationDate));
    }

    public Date getExpirationDate() {
        return productLifeCycleEventData.getGenericDate1();
    }

    public void setMaxBudget(double maxBudget) {
        productLifeCycleEventData.setGenericDecimal3(BaseHelper.toBigDecimal(maxBudget, GENERIC_DECIMAL_SCALE));
    }

    public void setMaxBudget(Double maxBudget) {
        productLifeCycleEventData.setGenericDecimal3(BaseHelper.toBigDecimal(maxBudget, GENERIC_DECIMAL_SCALE));
    }

    public Double getMaxBudget() {
        return BaseHelper.toDouble(productLifeCycleEventData.getGenericDecimal3());
    }

    public void setProductId(long productId) {
        productLifeCycleEventData.setGenericNumber1(Long.valueOf(productId));
    }

    public void setProductId(Long productId) {
        productLifeCycleEventData.setGenericNumber1(productId);
    }

    public Long getProductId() {
        return productLifeCycleEventData.getGenericNumber1();
    }

    public void setStartDate(Date startDate) {
        productLifeCycleEventData.setGenericDate2(BaseHelper.toSqlDate(startDate));
    }

    public Date getStartDate() {
        return productLifeCycleEventData.getGenericDate2();
    }

    public void setSubscriptionFee(double subscriptionFee) {
        productLifeCycleEventData.setGenericDecimal4(BaseHelper.toBigDecimal(subscriptionFee, GENERIC_DECIMAL_SCALE));
    }

    public void setSubscriptionFee(Double subscriptionFee) {
        productLifeCycleEventData.setGenericDecimal4(BaseHelper.toBigDecimal(subscriptionFee, GENERIC_DECIMAL_SCALE));
    }

    public Double getSubscriptionFee() {
        return BaseHelper.toDouble(productLifeCycleEventData.getGenericDecimal4());
    }

    public void setTermQuantity(int termQuantity) {
        productLifeCycleEventData.setGenericNumber3(Long.valueOf(termQuantity));
    }

    public void setTermQuantity(Integer termQuantity) {
        productLifeCycleEventData.setGenericNumber3(BaseHelper.toLong(termQuantity));
    }

    public Integer getTermQuantity() {
        return BaseHelper.toInteger(productLifeCycleEventData.getGenericNumber3());
    }

    public void setTermUnit(String termUnit) {
        productLifeCycleEventData.setGenericString2(termUnit);
    }

    public String getTermUnit() {
        return productLifeCycleEventData.getGenericString2();
    }

    public void setUrl(String url) {
        productLifeCycleEventData.setGenericString1(url);
    }

    public String getUrl() {
        return productLifeCycleEventData.getGenericString1();
    }
}
