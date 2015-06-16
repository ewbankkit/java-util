/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.productlifecycle;

import static com.netsol.adagent.util.dbhelpers.BaseHelper.toBigDecimal;
import static com.netsol.adagent.util.dbhelpers.BaseHelper.toDouble;
import static com.netsol.adagent.util.dbhelpers.BaseHelper.toSqlDate;
import static com.netsol.adagent.util.productlifecycle.ProductLifeCycleEventData.GENERIC_DECIMAL_SCALE;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Represents a target modification event.
 */
public class TargetModificationEvent extends BaseTargetEvent {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:19 TargetModificationEvent.java NSI";

    /**
     * Constructor.
     */
    /* package-private */ TargetModificationEvent(ProductLifeCycleEventData productLifeCycleEventData) {
        super(productLifeCycleEventData);
    }

    public void setNewBudget(double budget) {
        productLifeCycleEventData.setGenericDecimal2(toBigDecimal(budget, GENERIC_DECIMAL_SCALE));
    }

    public void setNewBudget(Double budget) {
        productLifeCycleEventData.setGenericDecimal2(toBigDecimal(budget, GENERIC_DECIMAL_SCALE));
    }

    public Double getNewBudget() {
        return toDouble(productLifeCycleEventData.getGenericDecimal2());
    }

    public void setOldBudget(double budget) {
        productLifeCycleEventData.setGenericDecimal1(toBigDecimal(budget, GENERIC_DECIMAL_SCALE));
    }

    public void setOldBudget(Double budget) {
        productLifeCycleEventData.setGenericDecimal1(toBigDecimal(budget, GENERIC_DECIMAL_SCALE));
    }

    public Double getOldBudget() {
        return toDouble(productLifeCycleEventData.getGenericDecimal1());
    }

    public void setNewEndDate(Date endDate) {
        productLifeCycleEventData.setGenericDate2(toSqlDate(endDate));
    }

    public Date getNewEndDate() {
        return productLifeCycleEventData.getGenericDate2();
    }

    public void setOldEndDate(Date endDate) {
        productLifeCycleEventData.setGenericDate1(toSqlDate(endDate));
    }

    public Date getOldEndDate() {
        return productLifeCycleEventData.getGenericDate1();
    }

    public void setNewMargin(double margin) {
        productLifeCycleEventData.setGenericNumber3(toStoredMargin(margin));
    }

    public void setNewMargin(Double margin) {
        productLifeCycleEventData.setGenericNumber3(toStoredMargin(margin));
    }

    public Double getNewMargin() {
        return fromStoredMargin(productLifeCycleEventData.getGenericNumber3());
    }

    public void setOldMargin(double margin) {
        productLifeCycleEventData.setGenericNumber2(toStoredMargin(margin));
    }

    public void setOldMargin(Double margin) {
        productLifeCycleEventData.setGenericNumber2(toStoredMargin(margin));
    }

    public Double getOldMargin() {
        return fromStoredMargin(productLifeCycleEventData.getGenericNumber2());
    }

    public void setNewName(String name) {
        productLifeCycleEventData.setGenericString2(name);
    }

    public String getNewName() {
        return productLifeCycleEventData.getGenericString2();
    }

    public void setOldName(String name) {
        productLifeCycleEventData.setGenericString1(name);
    }

    public String getOldName() {
        return productLifeCycleEventData.getGenericString1();
    }

    public void setNewSeoBudget(double seoBudget) {
        productLifeCycleEventData.setGenericDecimal4(toBigDecimal(seoBudget, GENERIC_DECIMAL_SCALE));
    }

    public void setNewSeoBudget(Double seoBudget) {
        productLifeCycleEventData.setGenericDecimal4(toBigDecimal(seoBudget, GENERIC_DECIMAL_SCALE));
    }

    public Double getNewSeoBudget() {
        return toDouble(productLifeCycleEventData.getGenericDecimal4());
    }

    public void setOldSeoBudget(double seoBudget) {
        productLifeCycleEventData.setGenericDecimal3(toBigDecimal(seoBudget, GENERIC_DECIMAL_SCALE));
    }

    public void setOldSeoBudget(Double seoBudget) {
        productLifeCycleEventData.setGenericDecimal3(toBigDecimal(seoBudget, GENERIC_DECIMAL_SCALE));
    }

    public Double getOldSeoBudget() {
        return toDouble(productLifeCycleEventData.getGenericDecimal3());
    }

    public void setNewStartDate(Date startDate) {
        productLifeCycleEventData.setGenericDate4(toSqlDate(startDate));
    }

    public Date getNewStartDate() {
        return productLifeCycleEventData.getGenericDate4();
    }

    public void setOldStartDate(Date startDate) {
        productLifeCycleEventData.setGenericDate3(toSqlDate(startDate));
    }

    public Date getOldStartDate() {
        return productLifeCycleEventData.getGenericDate3();
    }

    public void setNewStatus(String status) {
        productLifeCycleEventData.setGenericString4(status);
    }

    public String getNewStatus() {
        return productLifeCycleEventData.getGenericString4();
    }

    public void setOldStatus(String status) {
        productLifeCycleEventData.setGenericString3(status);
    }

    public String getOldStatus() {
        return productLifeCycleEventData.getGenericString3();
    }

    // Margin values have to be stored as integers because we don't have sufficient decimal fields.

    private static final BigDecimal MARGIN_MULTIPLIER = toBigDecimal(Long.valueOf(1000L), GENERIC_DECIMAL_SCALE);

    private static Double fromStoredMargin(Long margin) {
        if (margin == null) {
            return null;
        }
        return toDouble(toBigDecimal(margin, GENERIC_DECIMAL_SCALE).divide(MARGIN_MULTIPLIER));
    }

    private static long toStoredMargin(double margin) {
        return toBigDecimal(margin, GENERIC_DECIMAL_SCALE).multiply(MARGIN_MULTIPLIER).longValue();
    }

    private static Long toStoredMargin(Double margin) {
        if (margin == null) {
            return null;
        }
        return Long.valueOf(toStoredMargin(margin.doubleValue()));
    }
}
