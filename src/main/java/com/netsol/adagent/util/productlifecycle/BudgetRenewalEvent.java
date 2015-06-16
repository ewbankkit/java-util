/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.productlifecycle;

import static com.netsol.adagent.util.productlifecycle.ProductLifeCycleEventData.GENERIC_DECIMAL_SCALE;

import java.util.Date;

import com.netsol.adagent.util.dbhelpers.BaseHelper;

/**
 * Represents a budget renewal event.
 */
public class BudgetRenewalEvent extends ProductLifeCycleEvent {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:14 BudgetRenewalEvent.java NSI";

    /**
     * Constructor.
     */
    /* package-private */ BudgetRenewalEvent(ProductLifeCycleEventData productLifeCycleEventData) {
        super(productLifeCycleEventData);
    }

    public void setNewCurrentTarget(double newCurrentTarget) {
        productLifeCycleEventData.setGenericDecimal2(BaseHelper.toBigDecimal(newCurrentTarget, GENERIC_DECIMAL_SCALE));
    }

    public void setNewCurrentTarget(Double newCurrentTarget) {
        productLifeCycleEventData.setGenericDecimal2(BaseHelper.toBigDecimal(newCurrentTarget, GENERIC_DECIMAL_SCALE));
    }

    public Double getNewCurrentTarget() {
        return BaseHelper.toDouble(productLifeCycleEventData.getGenericDecimal2());
    }

    public void setOldCurrentTarget(double oldCurrentTarget) {
        productLifeCycleEventData.setGenericDecimal1(BaseHelper.toBigDecimal(oldCurrentTarget, GENERIC_DECIMAL_SCALE));
    }

    public void setOldCurrentTarget(Double oldCurrentTarget) {
        productLifeCycleEventData.setGenericDecimal1(BaseHelper.toBigDecimal(oldCurrentTarget, GENERIC_DECIMAL_SCALE));
    }

    public Double getOldCurrentTarget() {
        return BaseHelper.toDouble(productLifeCycleEventData.getGenericDecimal1());
    }

    public void setNewStartDate(Date newStartDate) {
        productLifeCycleEventData.setGenericDate2(BaseHelper.toSqlDate(newStartDate));
    }

    public Date getNewStartDate() {
        return productLifeCycleEventData.getGenericDate2();
    }

    public void setOldStartDate(Date oldStartDate) {
        productLifeCycleEventData.setGenericDate1(BaseHelper.toSqlDate(oldStartDate));
    }

    public Date getOldStartDate() {
        return productLifeCycleEventData.getGenericDate1();
    }
}
