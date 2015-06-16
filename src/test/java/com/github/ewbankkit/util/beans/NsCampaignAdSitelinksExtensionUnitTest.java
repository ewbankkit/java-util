/**
 * Kit's Java Utils.
 */

package com.github.ewbankkit.util.beans;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Collections;

import org.junit.Test;

import com.netsol.adagent.util.beans.NsCampaignAdSitelinksExtension;
import com.netsol.adagent.util.beans.NsCampaignAdSitelinksExtension.Sitelink;

public class NsCampaignAdSitelinksExtensionUnitTest {
    public final static String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:53:56 NsCampaignAdSitelinksExtensionUnitTest.java NSI";

    @Test
    public void copyNsCampaignAdSitelinksExtensionTest1() {
        NsCampaignAdSitelinksExtension nsCampaignAdSitelinksExtension1 = new NsCampaignAdSitelinksExtension();
        nsCampaignAdSitelinksExtension1.setEditorialStatus("BEEP");
        nsCampaignAdSitelinksExtension1.setNsStatus("BOOP");
        NsCampaignAdSitelinksExtension nsCampaignAdSitelinksExtension2 = new NsCampaignAdSitelinksExtension();
        NsCampaignAdSitelinksExtension.copy(nsCampaignAdSitelinksExtension1, nsCampaignAdSitelinksExtension2);
        assertEquals("BEEP", nsCampaignAdSitelinksExtension2.getEditorialStatus());
        assertEquals("BOOP", nsCampaignAdSitelinksExtension2.getNsStatus());
        assertNull(nsCampaignAdSitelinksExtension2.getSitelinks());
    }

    @Test
    public void copyNsCampaignAdSitelinksExtensionTest2() {
        NsCampaignAdSitelinksExtension nsCampaignAdSitelinksExtension1 = new NsCampaignAdSitelinksExtension();
        Sitelink sitelink = new Sitelink();
        sitelink.setDestinationUrl("DEST");
        sitelink.setDisplayText("DISPLAY");
        nsCampaignAdSitelinksExtension1.setSitelinks(Collections.singletonList(sitelink));
        NsCampaignAdSitelinksExtension nsCampaignAdSitelinksExtension2 = new NsCampaignAdSitelinksExtension();
        NsCampaignAdSitelinksExtension.copy(nsCampaignAdSitelinksExtension1, nsCampaignAdSitelinksExtension2);
        assertNotNull(nsCampaignAdSitelinksExtension2.getSitelinks());
        assertEquals(1, nsCampaignAdSitelinksExtension2.getSitelinks().size());
        assertEquals("DEST", nsCampaignAdSitelinksExtension2.getSitelinks().get(0).getDestinationUrl());
        assertEquals("DISPLAY", nsCampaignAdSitelinksExtension2.getSitelinks().get(0).getDisplayText());
    }
}
