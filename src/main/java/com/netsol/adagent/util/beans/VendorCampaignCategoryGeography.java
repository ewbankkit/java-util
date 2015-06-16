/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.beans;

public class VendorCampaignCategoryGeography extends VendorEntity {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:11 VendorCampaignCategoryGeography.java NSI";

    private long nsCampaignGeographyId;


    public long getNsCampaignCategoryId() {
        return getNsEntityId();
    }

    public void setNsCampaignCategoryId(long nsCampaignCategoryId) {
        setNsEntityId(nsCampaignCategoryId);
    }

    public long getNsCampaignGeographyId() {
        return nsCampaignGeographyId;
    }

    public void setNsCampaignGeographyId(long nsCampaignGeographyId) {
        this.nsCampaignGeographyId = nsCampaignGeographyId;
    }
}
