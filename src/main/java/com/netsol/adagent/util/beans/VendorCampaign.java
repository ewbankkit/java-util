/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.beans;

/**
 * Represents a vendor campaign.
 */
public class VendorCampaign extends VendorEntityWithShareOfVoice {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:11 VendorCampaign.java NSI";

    public void setNsCampaignId(long nsCampaignId) {
        setNsEntityId(nsCampaignId);
    }

    public long getNsCampaignId() {
        return getNsEntityId();
    }
}
