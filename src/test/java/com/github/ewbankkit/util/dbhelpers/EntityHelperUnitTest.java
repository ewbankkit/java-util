/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.github.ewbankkit.util.dbhelpers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import com.github.ewbankkit.util.codes.GoogleKeywordStatus;
import org.junit.Test;

import com.github.ewbankkit.util.codes.AdGroupStatus;
import com.github.ewbankkit.util.codes.AdStatus;
import com.github.ewbankkit.util.codes.CampaignStatus;
import com.github.ewbankkit.util.codes.GoogleAdStatus;
import com.github.ewbankkit.util.codes.GoogleCampaignStatus;
import com.github.ewbankkit.util.codes.KeywordStatus;
import com.github.ewbankkit.util.codes.MicrosoftAdGroupStatus;
import com.github.ewbankkit.util.codes.MicrosoftAdStatus;
import com.github.ewbankkit.util.codes.MicrosoftCampaignStatus;
import com.github.ewbankkit.util.codes.VendorId;
import com.github.ewbankkit.util.codes.YahooAdGroupStatus;
import com.github.ewbankkit.util.codes.YahooKeywordStatus;
import com.netsol.adagent.util.dbhelpers.EntityHelper;
import com.netsol.adagent.util.dbhelpers.EntityHelper.EntityType;

public class EntityHelperUnitTest {
    public final static String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:46 EntityHelperUnitTest.java NSI";

    @Test
    public void nsStatusToVendorStatusTest1() {
        assertNull(EntityHelper.nsStatusToVendorStatus(0, EntityType.CAMPAIGN, null));
    }

    @Test
    public void nsStatusToVendorStatusTest2() {
        assertEquals(GoogleCampaignStatus.DELETED, EntityHelper.nsStatusToVendorStatus(VendorId.GOOGLE, EntityType.CAMPAIGN, CampaignStatus.DELETED));
    }

    @Test
    public void nsStatusToVendorStatusTest3() {
        assertEquals(YahooAdGroupStatus.PAUSED, EntityHelper.nsStatusToVendorStatus(VendorId.YAHOO, EntityType.ADGROUP, AdGroupStatus.MANUAL_PAUSE));
    }

    @Test
    public void nsStatusToVendorStatusTest4() {
        assertEquals(MicrosoftAdStatus.ACTIVE, EntityHelper.nsStatusToVendorStatus(VendorId.MICROSOFT, EntityType.AD, AdStatus.ACTIVE));
    }

    @Test
    public void nsStatusToVendorStatusTest5() {
        assertEquals(GoogleKeywordStatus.PAUSED, EntityHelper.nsStatusToVendorStatus(VendorId.GOOGLE, EntityType.KEYWORD, KeywordStatus.MANUAL_PAUSE));
    }

    @Test
    public void vendorStatusToNsStatusTest1() {
        assertEquals(CampaignStatus.UNKNOWN, EntityHelper.vendorStatusToNsStatus(0, EntityType.CAMPAIGN, null));
    }

    @Test
    public void vendorStatusToNsStatusTest2() {
        assertEquals(AdStatus.ACTIVE, EntityHelper.vendorStatusToNsStatus(VendorId.GOOGLE, EntityType.AD, GoogleAdStatus.ACTIVE));
    }

    @Test
    public void vendorStatusToNsStatusTest3() {
        assertEquals(KeywordStatus.MANUAL_PAUSE, EntityHelper.vendorStatusToNsStatus(VendorId.YAHOO, EntityType.KEYWORD, YahooKeywordStatus.PAUSED));
    }

    @Test
    public void vendorStatusToNsStatusTest4() {
        assertEquals(CampaignStatus.SYSTEM_PAUSE, EntityHelper.vendorStatusToNsStatus(VendorId.MICROSOFT, EntityType.CAMPAIGN, MicrosoftCampaignStatus.PAUSED));
    }

    @Test
    public void vendorStatusToNsStatusTest5() {
        assertEquals(AdGroupStatus.MANUAL_PAUSE, EntityHelper.vendorStatusToNsStatus(VendorId.MICROSOFT, EntityType.ADGROUP, MicrosoftAdGroupStatus.PAUSED));
    }

    @Test
    public void truncateDestinationUrlTest1() {
        assertEquals("http://www.example.com/?adGroup=77", EntityHelper.truncateDestinationUrl("http://www.example.com/?adGroup=77"));
    }

    @Test
    public void truncateDestinationUrlTest2() {
        assertNull(EntityHelper.truncateDestinationUrl(null));
    }

    @Test
    public void truncateDestinationUrlTest3() {
        StringBuilder sb = new StringBuilder();
        sb.append("http://www.example.com/");
        for (int i = 0; i < 1024; i++) {
            sb.append('a');
        }
        String destinationUrl = sb.toString();
        sb = new StringBuilder();
        sb.append("http://www.example.com/");
        for (int i = 0; i < 1024 - ("http://www.example.com/".length() + 3); i++) {
            sb.append('a');
        }
        sb.append("...");
        String expectedUrl = sb.toString();
        assertEquals(expectedUrl, EntityHelper.truncateDestinationUrl(destinationUrl));
    }
}
