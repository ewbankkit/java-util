/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.beans;

import java.math.BigDecimal;
import java.util.Date;

import com.netsol.adagent.util.dbhelpers.BaseHelper;

/**
 * Represents a vendor entity.
 */
public abstract class VendorEntity extends BaseData {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:11 VendorEntity.java NSI";

    private static final int TWO_DECIMAL_PLACES = 2;
    private static final int AVERAGE_POSITION_SCALE = TWO_DECIMAL_PLACES;
    private static final int COST_SCALE = TWO_DECIMAL_PLACES;

    private BigDecimal averagePosition = BigDecimal.ZERO;
    private int clicks;
    private BigDecimal cost = BigDecimal.ZERO;
    private int impressions;
    private long nsEntityId;
    private String prodInstId;
    private Date updateDate;

    public void setAveragePosition(double averagePosition) {
        this.averagePosition = BaseHelper.toBigDecimal(averagePosition, AVERAGE_POSITION_SCALE);
    }

    public BigDecimal getAveragePosition() {
        return averagePosition;
    }

    public void setClicks(int clicks) {
        this.clicks = clicks;
    }

    public void setClicks(long clicks) {
        setClicks((int)clicks);
    }

    public int getClicks() {
        return clicks;
    }

    public void setCost(double cost) {
        this.cost = BaseHelper.toBigDecimal(cost, COST_SCALE);
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setImpressions(int impressions) {
        this.impressions = impressions;
    }

    public void setImpressions(long impressions) {
        setImpressions((int)impressions);
    }

    public int getImpressions() {
        return impressions;
    }

    public void setProdInstId(String prodInstId) {
        this.prodInstId = prodInstId;
    }

    public String getProdInstId() {
        return prodInstId;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    protected void setNsEntityId(long nsEntityId) {
        this.nsEntityId = nsEntityId;
    }

    protected long getNsEntityId() {
        return nsEntityId;
    }
}
