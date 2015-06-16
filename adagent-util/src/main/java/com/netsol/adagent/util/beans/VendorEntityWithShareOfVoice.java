/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.beans;

/**
 * Represents a vendor entity with share of voice statistics.
 */
public abstract class VendorEntityWithShareOfVoice extends VendorEntity {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:12 VendorEntityWithShareOfVoice.java NSI";

    private Double exactMatchImpressionShare;
    private Double impressionShare;
    private Double lostImpressionShareBudget;
    private Double lostImpressionShareRank;

    public void setExactMatchImpressionShare(Double exactMatchImpressionShare) {
        this.exactMatchImpressionShare = exactMatchImpressionShare;
    }

    public Double getExactMatchImpressionShare() {
        return exactMatchImpressionShare;
    }

    public void setImpressionShare(Double impressionShare) {
        this.impressionShare = impressionShare;
    }

    public Double getImpressionShare() {
        return impressionShare;
    }

    public Double getLostImpressionShareBudget() {
        return lostImpressionShareBudget;
    }

    public void setLostImpressionShareBudget(Double lostImpressionShareBudget) {
        this.lostImpressionShareBudget = lostImpressionShareBudget;
    }

    public Double getLostImpressionShareRank() {
        return lostImpressionShareRank;
    }

    public void setLostImpressionShareRank(Double lostImpressionShareRank) {
        this.lostImpressionShareRank = lostImpressionShareRank;
    }
}
