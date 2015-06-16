package com.netsol.adagent.util.beans;

import java.sql.Connection;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.logging.Log;

import com.netsol.adagent.util.BudgetManagerException;
import com.netsol.adagent.util.CalendarUtil;
import com.netsol.adagent.util.budgetadj.BudgetAdjustment;
import com.netsol.adagent.util.log.BaseLoggable;

public abstract class DebitableItem extends BaseBudgetManagerData {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:53:53 DebitableItem.java NSI";

    // only product and campaign ids here because there will be services with only campaign structures
    private long nsCampaignId;
    private Date date;

    private BudgetAdjustment.System system;

    /**
     * Constructor.
     */
    protected DebitableItem(String logComponent) {
        super(logComponent);
    }

    /**
     * Constructor.
     */
    protected DebitableItem(Log logger) {
        super(logger);
    }

    /**
     * Constructor.
     */
    protected DebitableItem(BaseLoggable baseLoggable) {
        super(baseLoggable);
    }

    public long getNsCampaignId() {
        return nsCampaignId;
    }

    public void setNsCampaignId(long nsCampaignId) {
        this.nsCampaignId = nsCampaignId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Calendar getDateAsCalendar() {
        return CalendarUtil.dateToCalendar(date);
    }

    /**
     * Validates the DebitableItem is ok to debit.
     *
     * @param conn
     * @return whether or not the DebitableItem is available for debiting.
     * @throws BudgetManagerException
     */
    public abstract boolean validate(Connection conn) throws BudgetManagerException;

    /**
     * Calculates all of the costs for the DebitableItem. This is public because there may be a need
     * in the future for a method to calculate the costs on the item only and use those values.
     *
     * @param conn
     * @throws BudgetManagerException
     */
    public abstract void calculateCosts(Connection conn, Product product) throws BudgetManagerException;

    /**
     * Persists all of the necessary information for debiting this item.
     *
     * @param conn
     * @throws BudgetManagerException
     */
    public abstract void debit(Connection conn) throws BudgetManagerException;

    /**
     * Inserts a budget_adj record for this debit.
     *
     * @param connection
     * @throws BudgetManagerException
     */
    public abstract void insertBudgetAdjustment(Connection connection) throws BudgetManagerException;

    /**
     * Returns the full cost of the DebitableItem.
     *
     * @return the full cost of the DebitableItem.
     */
    public abstract double getFullCost();

    /**
     * @return the system
     */
    public BudgetAdjustment.System getSystem() {
        return system;
    }

    /**
     * @param system the system to set
     */
    public void setSystem(BudgetAdjustment.System system) {
        this.system = system;
    }
}
