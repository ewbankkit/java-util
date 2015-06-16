/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.productlifecycle;

import static com.netsol.adagent.util.dbhelpers.BaseHelper.toBigDecimal;
import static com.netsol.adagent.util.dbhelpers.BaseHelper.toDouble;
import static com.netsol.adagent.util.dbhelpers.BaseHelper.toSqlDate;
import static com.netsol.adagent.util.productlifecycle.ProductLifeCycleEventData.GENERIC_DECIMAL_SCALE;

import java.util.Date;

/**
 * Represents a target creation event.
 */
public class TargetCreationEvent extends BaseTargetEvent {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:19 TargetCreationEvent.java NSI";

    /**
     * Constructor.
     */
    /* package-private */ TargetCreationEvent(ProductLifeCycleEventData productLifeCycleEventData) {
        super(productLifeCycleEventData);
    }

    public void setBudget(double budget) {
        productLifeCycleEventData.setGenericDecimal1(toBigDecimal(budget, GENERIC_DECIMAL_SCALE));
    }

    public void setBudget(Double budget) {
        productLifeCycleEventData.setGenericDecimal1(toBigDecimal(budget, GENERIC_DECIMAL_SCALE));
    }

    public Double getBudget() {
        return toDouble(productLifeCycleEventData.getGenericDecimal1());
    }

    public void setEndDate(Date endDate) {
        productLifeCycleEventData.setGenericDate1(toSqlDate(endDate));
    }

    public Date getEndDate() {
        return productLifeCycleEventData.getGenericDate1();
    }

    public void setMarketGeographyId(long marketGeographyId) {
        productLifeCycleEventData.setGenericNumber2(Long.valueOf(marketGeographyId));
    }

    public void setMarketGeographyId(Long marketGeographyId) {
        productLifeCycleEventData.setGenericNumber2(marketGeographyId);
    }

    public Long getMarketGeographyId() {
        return productLifeCycleEventData.getGenericNumber2();
    }

    public void setMarketSubCategoryId(long marketSubCategoryId) {
        productLifeCycleEventData.setGenericNumber3(Long.valueOf(marketSubCategoryId));
    }

    public void setMarketSubCategoryId(Long marketSubCategoryId) {
        productLifeCycleEventData.setGenericNumber3(marketSubCategoryId);
    }

    public Long getMarketSubCategoryId() {
        return productLifeCycleEventData.getGenericNumber3();
    }

    public void setMargin(double margin) {
        productLifeCycleEventData.setGenericDecimal3(toBigDecimal(margin, GENERIC_DECIMAL_SCALE));
    }

    public void setMargin(Double margin) {
        productLifeCycleEventData.setGenericDecimal3(toBigDecimal(margin, GENERIC_DECIMAL_SCALE));
    }

    public Double getMargin() {
        return toDouble(productLifeCycleEventData.getGenericDecimal3());
    }

    public void setName(String name) {
        productLifeCycleEventData.setGenericString1(name);
    }

    public String getName() {
        return productLifeCycleEventData.getGenericString1();
    }

    public void setSeoBudget(double seoBudget) {
        productLifeCycleEventData.setGenericDecimal2(toBigDecimal(seoBudget, GENERIC_DECIMAL_SCALE));
    }

    public void setSeoBudget(Double seoBudget) {
        productLifeCycleEventData.setGenericDecimal2(toBigDecimal(seoBudget, GENERIC_DECIMAL_SCALE));
    }

    public Double getSeoBudget() {
        return toDouble(productLifeCycleEventData.getGenericDecimal2());
    }

    public void setStartDate(Date startDate) {
        productLifeCycleEventData.setGenericDate2(toSqlDate(startDate));
    }

    public Date getStartDate() {
        return productLifeCycleEventData.getGenericDate2();
    }

    public void setTargetType(String targetType) {
        productLifeCycleEventData.setGenericString2(targetType);
    }

    public String getTargetType() {
        return productLifeCycleEventData.getGenericString2();
    }
}
