/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.beans;

import java.util.Calendar;
import java.util.Date;

import com.netsol.adagent.util.CalendarUtil;

/**
 * Represents a target.
 */
public class Target extends BaseDataWithUpdateTracking {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:08 Target.java NSI";

    public enum Status {ACTIVE, DELETED, PAUSED, UNKNOWN};

    @ColumnName("budget")
    private double budget;
    private String crmId;
    @ColumnName("end_date")
    private Date endDate;
    private Long fulfillmentId;
    @ColumnName("margin")
    private float margin;
    @ColumnName("market_geography_id")
    private long marketGeographyId;
    private String marketGeographyName;
    @ColumnName("market_sub_category_id")
    private long marketSubCategoryId;
    private String marketSubCategoryName;
    private long marketCategoryId;
    private String marketCategoryName;
    @ColumnName("name")
    private String name;
    private String prodInstId;
    @ColumnName("seo_budget")
    private double seoBudget;
    @ColumnName("spend_aggressiveness")
    private Float spendAggressiveness;
    @ColumnName("start_date")
    private Date startDate;
    @ColumnName("status")
    private Status status;
    private long targetId;
    @ColumnName("target_type")
    private String targetType;
    @ColumnName("updated_by_system")
    private String updatedBySystem;
    @ColumnName("updated_by_user")
    private String updatedByUser;

    private TargetVendor[] targetVendors;

    public void setBudget(double budget) {
        setTrackedField("budget", budget);
    }

    public double getBudget() {
        return budget;
    }

    public void setCrmId(String crmId) {
        this.crmId = crmId;
    }

    public String getCrmId() {
        return crmId;
    }

    public void setEndDate(Date endDate) {
        setTrackedField("endDate", endDate);
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setFulfillmentId(Long fulfillmentId) {
        this.fulfillmentId = fulfillmentId;
    }

    public Long getFulfillmentId() {
        return fulfillmentId;
    }

    public void setMargin(float margin) {
        setTrackedField("margin", margin);
    }

    public float getMargin() {
        return margin;
    }

    public long getMarketCategoryId() {
        return marketCategoryId;
    }

    public void setMarketCategoryId(long marketCategoryId) {
        this.marketCategoryId = marketCategoryId;
    }

    public String getMarketCategoryName() {
        return marketCategoryName;
    }

    public void setMarketCategoryName(String marketCategoryName) {
        this.marketCategoryName = marketCategoryName;
    }

    public void setMarketGeographyId(long marketGeographyId) {
        setTrackedField("marketGeographyId", marketGeographyId);
    }

    public long getMarketGeographyId() {
        return marketGeographyId;
    }

    public String getMarketGeographyName() {
        return marketGeographyName;
    }

    public void setMarketGeographyName(String marketGeographyName) {
        this.marketGeographyName = marketGeographyName;
    }

    public void setMarketSubCategoryId(long marketSubCategoryId) {
        setTrackedField("marketSubCategoryId", marketSubCategoryId);
    }

    public long getMarketSubCategoryId() {
        return marketSubCategoryId;
    }

    public String getMarketSubCategoryName() {
        return marketSubCategoryName;
    }

    public void setMarketSubCategoryName(String marketSubCategoryName) {
        this.marketSubCategoryName = marketSubCategoryName;
    }

    public void setName(String name) {
        setTrackedField("name", name);
    }

    public String getName() {
        return name;
    }

    public String getProdInstId() {
        return prodInstId;
    }

    public void setProdInstId(String prodInstId) {
        this.prodInstId = prodInstId;
    }

    public void setSeoBudget(double seoBudget) {
        setTrackedField("seoBudget", seoBudget);
    }

    public double getSeoBudget() {
        return seoBudget;
    }

    public void setSpendAggressiveness(Float spendAggressiveness) {
        setTrackedField("spendAggressiveness", spendAggressiveness);
    }

    public Float getSpendAggressiveness() {
        return spendAggressiveness;
    }

    public void setStartDate(Date startDate) {
        setTrackedField("startDate", startDate);
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStatus(Status status) {
        setTrackedField("status", status);
    }

    public Status getStatus() {
        return status;
    }

    public void setTargetId(long targetId) {
        this.targetId = targetId;
    }

    public long getTargetId() {
        return targetId;
    }

    public void setTargetType(String targetType) {
        setTrackedField("targetType", targetType);
    }

    public String getTargetType() {
        return targetType;
    }

    public TargetVendor[] getTargetVendors() {
        return targetVendors;
    }

    public void setTargetVendors(TargetVendor[] targetVendors) {
        this.targetVendors = targetVendors;
    }

    public void setUpdatedBySystem(String updatedBySystem) {
        setTrackedField("updatedBySystem", updatedBySystem);
    }

    public String getUpdatedBySystem() {
        return updatedBySystem;
    }

    public String getUpdatedByUser() {
        return updatedByUser;
    }

    public void setUpdatedByUser(String updatedByUser) {
        setTrackedField("updatedByUser", updatedByUser);
    }
    
    //
    // Useful target methods:
    //
    
    /**
     * Calculate the actual monthly margin amount.
     */
    public double calculateActualMonthlyMarginAmount() {
    	return budget * margin;
    }

    /**
     * Calculate the actual monthly PPC budget amount.
     */
    public double calculateActualMonthlyPPCBudget() {
    	return (budget * (1 - margin)) - seoBudget;
    }
    
    /**
     * Is the day in the target date range?
     * 
     * @param day the day to check.
     * @return true if the day is in the target date range.
     */
    public boolean isDayInTargetDateRange(Calendar day) {
		Calendar startDate = getStartDateAsCalendar();
		Calendar endDate = getEndDateAsCalendar();
		// End date is nullable, in which case the target is open-ended.
		return (CalendarUtil.isSameDay(day, startDate) || day.after(startDate)) && (endDate == null || CalendarUtil.isSameDay(day, endDate) || day.before(endDate));
    }
    
    /**
     * Does this target's date range overlap the product's current cycle?
     * 
     * target end date == null and (cycle start date >= target start date OR target start date <= cycle expiration date)
     * OR
     * cycle expiration date >= target start date AND target end date >= cycle start date.
     * 
     * @param startDate the product's start date.
     * @param expirationDate the product's expiration date.
     * @return
     */
    public boolean doesTargetDateRangeOverlapCurrentCycle(Calendar cycleStartDate, Calendar cycleExpirationDate) {
		Calendar targetStartDate = getStartDateAsCalendar();
		Calendar targetEndDate = getEndDateAsCalendar();
		// End date is nullable, in which case the target is open-ended.
		boolean returnValue = false;
		if (endDate == null) {
			returnValue = CalendarUtil.isSameDay(targetStartDate, cycleStartDate) || cycleStartDate.after(targetStartDate);
		}
		else {
			returnValue = (CalendarUtil.isSameDay(cycleExpirationDate, targetStartDate) || cycleExpirationDate.after(targetStartDate)) && 
			(CalendarUtil.isSameDay(targetEndDate, cycleStartDate) || targetEndDate.after(cycleStartDate));
		}
		return returnValue;
    }
    
    /**
     * Get the start date as a calendar.
     */
    public Calendar getStartDateAsCalendar() {
		return CalendarUtil.dateToCalendar(getStartDate());
    }
    
    /**
     * Get the end date as a calendar.
     */
    public Calendar getEndDateAsCalendar() {
		return CalendarUtil.dateToCalendar(getEndDate());
    }
}
