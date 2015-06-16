/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.github.ewbankkit.util.dbhelpers;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.netsol.adagent.util.beans.ProductListing;
import com.netsol.adagent.util.dbhelpers.ProductListingHelper;

public class ProductListingHelperUnitTest {
    public final static String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:48 ProductListingHelperUnitTest.java NSI";

    private static Connection connection;
    private static final String logTag = null;
    private static final String prodInstId = "WN.TEST.20091201121825";
    private static final String updatedBySystem = "UNIT_TEST";
    private static final String updatedByUser = "UNIT_TEST";

    @BeforeClass
    public static void setup() throws SQLException {
        connection = BaseHelper.createDevGdbConnection();
    }

    @AfterClass
    public static void teardown() throws SQLException {
        BaseHelper.close(connection);
    }

    @Test
    public void getProductListingTest1() throws SQLException {
        ProductListingHelper helper = new ProductListingHelper("");
        assertNull(helper.getProductListingByProdInstId(logTag, connection, null));
    }

    @Test
    public void getProductListingTest2() throws SQLException {
        ProductListingHelper helper = new ProductListingHelper("");
        assertNull(helper.getProductListingByProductListingId(logTag, connection, 0L));
    }

    @Test
    public void getProductListingTest3() throws SQLException {
        ProductListingHelper helper = new ProductListingHelper("");
        assertNull(helper.getProductListingByFulfillmentId(logTag, connection, 0));
    }

    @Test
    public void getProductListingTest4() throws SQLException {
        ProductListingHelper helper = new ProductListingHelper("");
        helper.deleteProductListing(logTag, connection, prodInstId);

        ProductListing productListing = new ProductListing();
        productListing.setDescription("DESCRIPTION");
        productListing.setFulfillmentId(17L);
        productListing.setNotes("NOTES");
        productListing.setProdInstId(prodInstId);
        productListing.setTitle("TITLE");
        productListing.setUpdatedBySystem(updatedBySystem);
        productListing.setUpdatedByUser(updatedByUser);
        helper.insertOrUpdateProductListing(logTag, connection, productListing);

        productListing = helper.getProductListingByProdInstId(logTag, connection, prodInstId);
        assertTrue(productListing.getProductListingId() > 0L);
        assertEquals("DESCRIPTION", productListing.getDescription());
        assertNull(productListing.getCrmId());
        assertEquals(17L, productListing.getFulfillmentId());
        assertEquals("NOTES", productListing.getNotes());
        assertEquals("TITLE", productListing.getTitle());

        productListing.setDescription("ANOTHER DESCRIPTION");
        productListing.setCrmId("42");
        productListing.setNotes("SOME MORE NOTES");
        productListing.setProdInstId(prodInstId);
        productListing.setTitle("ANOTHER TITLE");
        productListing.setUpdatedBySystem(updatedBySystem);
        productListing.setUpdatedByUser(updatedByUser);
        helper.insertOrUpdateProductListing(logTag, connection, productListing);
        productListing = helper.getProductListingByProductListingId(logTag, connection, productListing.getProductListingId());
        assertEquals("ANOTHER DESCRIPTION", productListing.getDescription());
        assertEquals("42", productListing.getCrmId());
        assertEquals(17L, productListing.getFulfillmentId());
        assertEquals("SOME MORE NOTES", productListing.getNotes());
        assertEquals("ANOTHER TITLE", productListing.getTitle());

        productListing = helper.getProductListingByCrmId(logTag, connection, "42");
        assertEquals("ANOTHER DESCRIPTION", productListing.getDescription());
    }
}
