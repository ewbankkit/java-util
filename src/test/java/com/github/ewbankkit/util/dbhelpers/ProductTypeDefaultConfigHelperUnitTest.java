/**
 * Kit's Java Utils.
 */

package com.github.ewbankkit.util.dbhelpers;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.github.ewbankkit.util.codes.ProdId;
import com.netsol.adagent.util.dbhelpers.ProductTypeDefaultConfigHelper;

public class ProductTypeDefaultConfigHelperUnitTest {
    public final static String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:49 ProductTypeDefaultConfigHelperUnitTest.java NSI";

    private static Connection connection;
    private static final String logTag = null;

    @BeforeClass
    public static void setup() throws SQLException {
        connection = BaseHelper.createDevGdbConnection();
    }

    @AfterClass
    public static void teardown() throws SQLException {
        BaseHelper.close(connection);
    }

    @Test
    public void getDefaultConfigTest1() throws SQLException {
        ProductTypeDefaultConfigHelper helper = new ProductTypeDefaultConfigHelper("");
        assertTrue(helper.getDefaultConfig(logTag, connection, 0L).isEmpty());
    }

    @Test
    public void getDefaultConfigTest2() throws SQLException {
        ProductTypeDefaultConfigHelper helper = new ProductTypeDefaultConfigHelper("");
        assertFalse(helper.getDefaultConfig(logTag, connection, ProdId.DIFM_PPC).isEmpty());
    }
}
