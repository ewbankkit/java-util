/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.beans;

import java.util.List;


/**
 * Represents NS campaign ad extensions.
 */
public class NsCampaignAdExtensions extends BaseData {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:00 NsCampaignAdExtensions.java NSI";

    private List<NsCampaignAdCallExtension> nsCampaignAdCallExtensions;
    private List<NsCampaignAdLocationExtension> nsCampaignAdLocationExtensions;
    private List<NsCampaignAdSitelinksExtension> nsCampaignAdSitelinksExtensions;

    public void setNsCampaignAdCallExtensions(List<NsCampaignAdCallExtension> nsCampaignAdCallExtensions) {
        this.nsCampaignAdCallExtensions = nsCampaignAdCallExtensions;
    }

    public List<NsCampaignAdCallExtension> getNsCampaignAdCallExtensions() {
        return nsCampaignAdCallExtensions;
    }

    public void setNsCampaignAdLocationExtensions(List<NsCampaignAdLocationExtension> nsCampaignAdLocationExtensions) {
        this.nsCampaignAdLocationExtensions = nsCampaignAdLocationExtensions;
    }

    public List<NsCampaignAdLocationExtension> getNsCampaignAdLocationExtensions() {
        return nsCampaignAdLocationExtensions;
    }

    public void setNsCampaignAdSitelinksExtensions(List<NsCampaignAdSitelinksExtension> nsCampaignAdSitelinksExtensions) {
        this.nsCampaignAdSitelinksExtensions = nsCampaignAdSitelinksExtensions;
    }

    public List<NsCampaignAdSitelinksExtension> getNsCampaignAdSitelinksExtensions() {
        return nsCampaignAdSitelinksExtensions;
    }

    public boolean isEmpty() {
        return collectionIsEmpty(nsCampaignAdCallExtensions) &&
            collectionIsEmpty(nsCampaignAdLocationExtensions) &&
            collectionIsEmpty(nsCampaignAdSitelinksExtensions);
    }
}
