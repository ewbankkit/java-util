/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.beans;

/**
 * Represents an NS campaign business location target.
 */
public class NsCampaignBusinessLocationTarget extends NsCampaignCriterion {
    private long nsBusinessLocationId;
    private double radius;

    public void setNsBusinessLocationId(long nsBusinessLocationId) {
        this.nsBusinessLocationId = nsBusinessLocationId;
    }

    public long getNsBusinessLocationId() {
        return nsBusinessLocationId;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public double getRadius() {
        return radius;
    }
}
