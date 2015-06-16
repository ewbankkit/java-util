/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.beans;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an NS campaign ad sitelinks extension.
 */
public class NsCampaignAdSitelinksExtension extends NsCampaignAdExtension {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:01 NsCampaignAdSitelinksExtension.java NSI";

    private List<Sitelink> sitelinks;

    public void setNsCampaignAdSitelinksExtensionId(long nsCampaignAdSitelinksExtensionId) {
        setNsEntityId(nsCampaignAdSitelinksExtensionId);
    }

    public long getNsCampaignAdSitelinksExtensionId() {
        return getNsEntityId();
    }

    public void setSitelinks(List<Sitelink> sitelinks) {
        this.sitelinks = sitelinks;
    }

    public List<Sitelink> getSitelinks() {
        return sitelinks;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof NsCampaignAdSitelinksExtension)) {
            return false;
        }
        return equals(this, (NsCampaignAdSitelinksExtension)o);
    }

    public static void copy(NsCampaignAdSitelinksExtension src, NsCampaignAdSitelinksExtension dest) {
        if ((src != null) && (dest != null)) {
            copy((NsCampaignAdExtension)src, (NsCampaignAdExtension)dest);
            List<Sitelink> srcSitelinks = src.getSitelinks();
            if (srcSitelinks != null) {
                List<Sitelink> destSitelinks = new ArrayList<Sitelink>(srcSitelinks.size());
                for (Sitelink srcSitelink : srcSitelinks) {
                    Sitelink destSitelink = new Sitelink();
                    destSitelink.setDestinationUrl(srcSitelink.getDestinationUrl());
                    destSitelink.setDisplayText(srcSitelink.getDisplayText());
                    destSitelinks.add(destSitelink);
                }
                dest.setSitelinks(destSitelinks);
            }
        }
    }

    public static class Sitelink extends BaseData {
        private String destinationUrl;
        private String displayText;

        public void setDestinationUrl(String destinationUrl) {
            this.destinationUrl = destinationUrl;
        }

        public String getDestinationUrl() {
            return destinationUrl;
        }

        public void setDisplayText(String displayText) {
            this.displayText = displayText;
        }

        public String getDisplayText() {
            return displayText;
        }
    }
}
