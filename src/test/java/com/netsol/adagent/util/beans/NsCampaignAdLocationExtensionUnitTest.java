/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.beans;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.netsol.adagent.util.beans.NsCampaignAdLocationExtension;

public class NsCampaignAdLocationExtensionUnitTest {
    public final static String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:53:55 NsCampaignAdLocationExtensionUnitTest.java NSI";

    @Test
    public void copyNsCampaignAdLocationExtensionTest1() {
        NsCampaignAdLocationExtension nsCampaignAdLocationExtension1 = new NsCampaignAdLocationExtension();
        nsCampaignAdLocationExtension1.setEditorialStatus("BEEP");
        nsCampaignAdLocationExtension1.setNsStatus("BOOP");
        nsCampaignAdLocationExtension1.setState("BB");
        NsCampaignAdLocationExtension nsCampaignAdLocationExtension2 = new NsCampaignAdLocationExtension();
        nsCampaignAdLocationExtension2.setEditorialStatus("HEEP");
        nsCampaignAdLocationExtension2.setNsStatus("HOOP");
        nsCampaignAdLocationExtension2.setState("HH");
        NsCampaignAdLocationExtension.copy(nsCampaignAdLocationExtension1, nsCampaignAdLocationExtension2);
        assertEquals("BEEP", nsCampaignAdLocationExtension2.getEditorialStatus());
        assertEquals("BOOP", nsCampaignAdLocationExtension2.getNsStatus());
        assertEquals("BB", nsCampaignAdLocationExtension2.getState());
    }
    
    @Test
    public void copyNsCampaignAdLocationExtensionTest2() {
        NsCampaignAdLocationExtension nsCampaignAdLocationExtension1 = new NsCampaignAdLocationExtension();
        nsCampaignAdLocationExtension1.setEditorialStatus("BEEP");
        nsCampaignAdLocationExtension1.setNsStatus("BOOP");
        NsCampaignAdLocationExtension nsCampaignAdLocationExtension2 = new NsCampaignAdLocationExtension();
        nsCampaignAdLocationExtension2.setEditorialStatus("HEEP");
        nsCampaignAdLocationExtension2.setNsStatus("HOOP");
        nsCampaignAdLocationExtension2.setState("HH");
        NsCampaignAdLocationExtension.copy(nsCampaignAdLocationExtension1, nsCampaignAdLocationExtension2);
        assertEquals("BEEP", nsCampaignAdLocationExtension2.getEditorialStatus());
        assertEquals("BOOP", nsCampaignAdLocationExtension2.getNsStatus());
        assertEquals("HH", nsCampaignAdLocationExtension2.getState());
    }
}
