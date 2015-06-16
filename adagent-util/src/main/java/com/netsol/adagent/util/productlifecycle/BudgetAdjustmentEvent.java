/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.productlifecycle;

import static com.netsol.adagent.util.productlifecycle.ProductLifeCycleEventData.GENERIC_DECIMAL_SCALE;

import java.util.Date;

import com.netsol.adagent.util.dbhelpers.BaseHelper;

/**
 * Represents a budget adjustment event.
 */
public class BudgetAdjustmentEvent extends ProductLifeCycleEvent {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:14 BudgetAdjustmentEvent.java NSI";

    /**
     * Constructor.
     */
    /* package-private */ BudgetAdjustmentEvent(ProductLifeCycleEventData productLifeCycleEventData) {
        super(productLifeCycleEventData);
    }

    public void setAdjustmentDate(Date adjustmentDate) {
        productLifeCycleEventData.setGenericDate1(BaseHelper.toSqlDate(adjustmentDate));
    }

    public Date getAdjustmentDate() {
        return productLifeCycleEventData.getGenericDate1();
    }

    public void setNewDailyBudgetRemaining(double newDailyBudgetRemaining) {
        productLifeCycleEventData.setGenericDecimal2(BaseHelper.toBigDecimal(newDailyBudgetRemaining, GENERIC_DECIMAL_SCALE));
    }

    public void setNewDailyBudgetRemaining(Double newDailyBudgetRemaining) {
        productLifeCycleEventData.setGenericDecimal2(BaseHelper.toBigDecimal(newDailyBudgetRemaining, GENERIC_DECIMAL_SCALE));
    }

    public Double getNewDailyBudgetRemaining() {
        return BaseHelper.toDouble(productLifeCycleEventData.getGenericDecimal2());
    }

    public void setOldDailyBudgetRemaining(double oldDailyBudgetRemaining) {
        productLifeCycleEventData.setGenericDecimal1(BaseHelper.toBigDecimal(oldDailyBudgetRemaining, GENERIC_DECIMAL_SCALE));
    }

    public void setOldDailyBudgetRemaining(Double oldDailyBudgetRemaining) {
        productLifeCycleEventData.setGenericDecimal1(BaseHelper.toBigDecimal(oldDailyBudgetRemaining, GENERIC_DECIMAL_SCALE));
    }

    public Double getOldDailyBudgetRemaining() {
        return BaseHelper.toDouble(productLifeCycleEventData.getGenericDecimal1());
    }

    public void setNewMonthlyBudgetRemaining(double newMonthlyBudgetRemaining) {
        productLifeCycleEventData.setGenericDecimal4(BaseHelper.toBigDecimal(newMonthlyBudgetRemaining, GENERIC_DECIMAL_SCALE));
    }

    public void setNewMonthlyBudgetRemaining(Double newMonthlyBudgetRemaining) {
        productLifeCycleEventData.setGenericDecimal4(BaseHelper.toBigDecimal(newMonthlyBudgetRemaining, GENERIC_DECIMAL_SCALE));
    }

    public Double getNewMonthlyBudgetRemaining() {
        return BaseHelper.toDouble(productLifeCycleEventData.getGenericDecimal4());
    }

    public void setOldMonthlyBudgetRemaining(double oldMonthlyBudgetRemaining) {
        productLifeCycleEventData.setGenericDecimal3(BaseHelper.toBigDecimal(oldMonthlyBudgetRemaining, GENERIC_DECIMAL_SCALE));
    }

    public void setOldMonthlyBudgetRemaining(Double oldMonthlyBudgetRemaining) {
        productLifeCycleEventData.setGenericDecimal3(BaseHelper.toBigDecimal(oldMonthlyBudgetRemaining, GENERIC_DECIMAL_SCALE));
    }

    public Double getOldMonthlyBudgetRemaining() {
        return BaseHelper.toDouble(productLifeCycleEventData.getGenericDecimal3());
    }

    public void setMigration(boolean migration) {
        productLifeCycleEventData.setGenericBoolean1(Boolean.valueOf(migration));
    }

    public void setMigration(Boolean migration) {
        productLifeCycleEventData.setGenericBoolean1(migration);
    }

    public Boolean isMigration() {
        return productLifeCycleEventData.getGenericBoolean1();
    }

    public void setRenewal(boolean renewal) {
        productLifeCycleEventData.setGenericBoolean2(Boolean.valueOf(renewal));
    }

    public void setRenewal(Boolean renewal) {
        productLifeCycleEventData.setGenericBoolean2(renewal);
    }

    public Boolean isRenewal() {
        return productLifeCycleEventData.getGenericBoolean2();
    }
}
