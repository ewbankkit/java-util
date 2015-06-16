/**
 * Kit's Java Utils.
 */

package com.github.ewbankkit.util.vendor;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.sql.SQLException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.netsol.vendor.beans.NSCampaignCategoryData;
import com.netsol.vendor.client.CampaignCategoryClient;
import com.netsol.vendor.client.ClientFactories;

public class CampaignCategoryClientUnitTest extends BaseClientUnitTest {
    public final static String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:37 CampaignCategoryClientUnitTest.java NSI";

    @BeforeClass
    public static void setup() throws SQLException {
        setupBuiltInClient();
    }

    @AfterClass
    public static void teardown() throws SQLException {
        teardownBuiltInClient();
    }

    @Test
    public void getCategoriesByKeywordsTest1() throws Exception {
        CampaignCategoryClient client = ClientFactories.getClientFactory().getCampaignCategoryClient(getCredentials());
        NSCampaignCategoryData[] categoryData = client.getCategoriesByKeywords(new String[] {"fruit", "candy"});
        assertNotNull(categoryData);
        assertFalse(categoryData.length == 0);
    }
}
