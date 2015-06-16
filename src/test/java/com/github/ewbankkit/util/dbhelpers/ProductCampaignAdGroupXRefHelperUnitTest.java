/**
 * Kit's Java Utils.
 */

package com.github.ewbankkit.util.dbhelpers;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.netsol.adagent.util.beans.ProductCampaignAdGroupXRef;
import com.netsol.adagent.util.dbhelpers.ProductCampaignAdGroupXRefHelper;

public class ProductCampaignAdGroupXRefHelperUnitTest {
    public final static String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:48 ProductCampaignAdGroupXRefHelperUnitTest.java NSI";

    private static Connection gdbConnection;
    private static Connection pdb1Connection;
    private static Connection pdb2Connection;
    private static final String logTag = null;

    @BeforeClass
    public static void setup() throws SQLException {
        gdbConnection = BaseHelper.createDevGdbConnection();
        pdb1Connection = BaseHelper.createDevPdb1Connection();
        pdb2Connection = BaseHelper.createDevPdb2Connection();
    }

    @AfterClass
    public static void teardown() throws SQLException {
        BaseHelper.close(gdbConnection);
        BaseHelper.close(pdb1Connection);
        BaseHelper.close(pdb2Connection);
    }

    @Test
    public void insertTest1() throws SQLException {
        ProductCampaignAdGroupXRefHelper productCampaignAdGroupXRefHelper = new ProductCampaignAdGroupXRefHelper("");
        productCampaignAdGroupXRefHelper.deleteAll(logTag, gdbConnection);
        Collection<ProductCampaignAdGroupXRef> productCampaignAdGroupXRefs =
            productCampaignAdGroupXRefHelper.getAllProductCampaignAdGroupXRefsFromPdb(logTag, pdb1Connection);
        productCampaignAdGroupXRefHelper.insertProductCampaignAdGroupXRefs(logTag, gdbConnection, productCampaignAdGroupXRefs);
        productCampaignAdGroupXRefs =
            productCampaignAdGroupXRefHelper.getAllProductCampaignAdGroupXRefsFromPdb(logTag, pdb2Connection);
        productCampaignAdGroupXRefHelper.insertProductCampaignAdGroupXRefs(logTag, gdbConnection, productCampaignAdGroupXRefs);
    }
}
