/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.beans;

import org.apache.commons.logging.Log;

import com.netsol.adagent.util.dbhelpers.BudgetManagerHelper;
import com.netsol.adagent.util.log.BaseLoggable;

/**
 * Abstract base class for data used by the Budget Manager.
 */
public abstract class BaseBudgetManagerData extends BaseLoggable {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:53:49 BaseBudgetManagerData.java NSI";

    protected final BudgetManagerHelper budgetManagerHelper;
    protected String logTag;
    private String prodInstId;

    /**
     * Constructor.
     */
    protected BaseBudgetManagerData(String logComponent) {
        super(logComponent);
        budgetManagerHelper = new BudgetManagerHelper(this);
    }

    /**
     * Constructor.
     */
    protected BaseBudgetManagerData(Log logger) {
        super(logger);
        budgetManagerHelper = new BudgetManagerHelper(this);
    }

    /**
     * Constructor.
     */
    protected BaseBudgetManagerData(BaseLoggable baseLoggable) {
        super(baseLoggable);
        budgetManagerHelper = new BudgetManagerHelper(this);
    }

    public void setProdInstId(String prodInstId) {
        this.prodInstId = prodInstId;
        logTag = getLogTag(prodInstId);
    }

    public String getProdInstId() {
        return prodInstId;
    }

    @Override
    public String toString() {
        return BaseData.toString(this);
    }

    /**
     * Return the log tag for the specified product instance ID.
     */
    public static String getLogTag(String prodInstId) {
        return prodInstId + "|BudgetManager";
    }
}
