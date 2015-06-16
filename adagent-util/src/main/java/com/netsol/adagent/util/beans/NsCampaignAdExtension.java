/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.beans;

/**
 * Represents an NS campaign ad extension.
 */
@ColumnNameOverride({"name/", "vendorEntityId/vendor_ad_extension_id"})
public abstract class NsCampaignAdExtension extends NsEntity {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:00 NsCampaignAdExtension.java NSI";

    @ColumnName("editorial_status")
    private String editorialStatus;
    @ColumnName("ns_campaign_id")
    private long nsCampaignId;

    public void setEditorialStatus(String editorialStatus) {
        setTrackedField("editorialStatus", editorialStatus);
    }

    public String getEditorialStatus() {
        return editorialStatus;
    }

    public void setNsCampaignId(long nsCampaignId) {
        setTrackedField("nsCampaignId", nsCampaignId);
    }

    public long getNsCampaignId() {
        return nsCampaignId;
    }

    public void setVendorAdExtensionId(Long vendorAdExtensionId) {
        setVendorEntityId(vendorAdExtensionId);
    }

    public Long getVendorAdExtensionId() {
        return getVendorEntityId();
    }

    protected static void copy(NsCampaignAdExtension src, NsCampaignAdExtension dest) {
        if ((src != null) && (dest != null)) {
            copy((NsEntity)src, (NsEntity)dest);
            dest.setEditorialStatus(src.getEditorialStatus());
            // Don't overwrite the NS campaign ID in the destination.
        }
    }
}
