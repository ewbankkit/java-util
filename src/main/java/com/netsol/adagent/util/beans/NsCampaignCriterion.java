/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.beans;

/**
 * Represents an NS campaign criterion.
 */
public abstract class NsCampaignCriterion extends BaseData {
    private long nsCampaignId;
    private String prodInstId;

	public long getNsCampaignId() {
		return nsCampaignId;
	}

	public void setNsCampaignId(long nsCampaignId) {
		this.nsCampaignId = nsCampaignId;
	}

	public String getProdInstId() {
		return prodInstId;
	}

	public void setProdInstId(String prodInstId) {
		this.prodInstId = prodInstId;
	}
}
