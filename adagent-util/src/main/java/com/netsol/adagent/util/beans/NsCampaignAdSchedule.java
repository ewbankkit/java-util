/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.beans;

/**
 * Represents an NS campaign ad schedule.
 */
public class NsCampaignAdSchedule extends NsCampaignCriterion {
    private double bidModifier;
    private String dayOfWeek;
    private int endHour;
    private int startHour;

    public void setBidModifier(double bidModifier) {
        this.bidModifier = bidModifier;
    }

    public double getBidModifier() {
        return bidModifier;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setEndHour(int endHour) {
        this.endHour = endHour;
    }

    public int getEndHour() {
        return endHour;
    }

    public void setStartHour(int startHour) {
        this.startHour = startHour;
    }

    public int getStartHour() {
        return startHour;
    }
}
