/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.beans;

/**
 * Represents a cross-reference between product instance ID, NS campaign and NS ad group. 
 */
public class ProductCampaignAdGroupXRef extends BaseData {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:04 ProductCampaignAdGroupXRef.java NSI";

    private long nsAdGroupId;
    private long nsCampaignId;
    private String prodInstId;
    
    /**
     * Constructor.
     */
    public ProductCampaignAdGroupXRef() {
        super();
        
        return;
    }

    public void setNsAdGroupId(long nsAdGroupId) {
        this.nsAdGroupId = nsAdGroupId;
    }

    public long getNsAdGroupId() {
        return this.nsAdGroupId;
    }

    public void setNsCampaignId(long nsCampaignId) {
        this.nsCampaignId = nsCampaignId;
    }

    public long getNsCampaignId() {
        return this.nsCampaignId;
    }

    public void setProdInstId(String prodInstId) {
        this.prodInstId = prodInstId;
    }

    public String getProdInstId() {
        return this.prodInstId;
    }
}
