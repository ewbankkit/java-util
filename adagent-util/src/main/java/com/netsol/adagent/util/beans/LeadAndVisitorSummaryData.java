/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.beans;

/**
 * Represents lead and visitor summary data.
 */
public class LeadAndVisitorSummaryData extends BaseData {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:53:58 LeadAndVisitorSummaryData.java NSI";

    private float averageDailyLeadCount;
    private float averageDailyVisitCount;
    private long emailLeadCount;
    private long formLeadCount;
    private long highValuePageLeadCount;
    private long phoneLeadCount;
    private long shoppingCartLeadCount;
    private long totalLeadCount;
    private long totalVisitCount;

    /**
     * Constructor.
     */
    public LeadAndVisitorSummaryData() {
        super();

        return;
    }

    public void setAverageDailyLeadCount(float averageDailyLeadCount) {
        this.averageDailyLeadCount = averageDailyLeadCount;
    }

    public float getAverageDailyLeadCount() {
        return this.averageDailyLeadCount;
    }

    public void setAverageDailyVisitCount(float averageDailyVisitCount) {
        this.averageDailyVisitCount = averageDailyVisitCount;
    }

    public float getAverageDailyVisitCount() {
        return this.averageDailyVisitCount;
    }

    public void setEmailLeadCount(long emailLeadCount) {
        this.emailLeadCount = emailLeadCount;
    }

    public long getEmailLeadCount() {
        return this.emailLeadCount;
    }

    public void setFormLeadCount(long formLeadCount) {
        this.formLeadCount = formLeadCount;
    }

    public long getFormLeadCount() {
        return this.formLeadCount;
    }

    public void setHighValuePageLeadCount(long highValuePageLeadCount) {
        this.highValuePageLeadCount = highValuePageLeadCount;
    }

    public long getHighValuePageLeadCount() {
        return this.highValuePageLeadCount;
    }

    public void setPhoneLeadCount(long phoneLeadCount) {
        this.phoneLeadCount = phoneLeadCount;
    }

    public long getPhoneLeadCount() {
        return this.phoneLeadCount;
    }

    public void setShoppingCartLeadCount(long shoppingCartLeadCount) {
        this.shoppingCartLeadCount = shoppingCartLeadCount;
    }

    public long getShoppingCartLeadCount() {
        return this.shoppingCartLeadCount;
    }

    public void setTotalLeadCount(long totalLeadCount) {
        this.totalLeadCount = totalLeadCount;
    }

    public long getTotalLeadCount() {
        return this.totalLeadCount;
    }

    public void setTotalVisitCount(long totalVisitCount) {
        this.totalVisitCount = totalVisitCount;
    }

    public long getTotalVisitCount() {
        return this.totalVisitCount;
    }
}
