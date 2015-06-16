/**
 * Kit's Java Utils.
 */

package com.github.ewbankkit.util.lsv;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.github.ewbankkit.util.dbhelpers.BaseHelper;
import com.netsol.adagent.util.lsv.BusinessCategoryDBHelper;
import com.netsol.adagent.util.lsv.BusinessCategoryTypeCd;

public class BusinessCategoryDBHelperUnitTest {
    public final static String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:10 BusinessCategoryDBHelperUnitTest.java NSI";

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
    public void queryCategoriesTest1() throws SQLException {
        BusinessCategoryDBHelper helper = new BusinessCategoryDBHelper("", logTag);
        assertTrue(helper.queryCategories(connection, 0L).isEmpty());
    }

    @Test
    public void queryCategoriesTest2() throws SQLException {
        BusinessCategoryDBHelper helper = new BusinessCategoryDBHelper("", logTag);
        assertFalse(helper.queryCategories(connection, BusinessCategoryTypeCd.NETSOL_CATEGORY_TYPE_CD).isEmpty());
    }

    @Test
    public void queryParentCategoriesTest1() throws SQLException {
        BusinessCategoryDBHelper helper = new BusinessCategoryDBHelper("", logTag);
        assertTrue(helper.queryParentCategories(connection, 0L, 1L).isEmpty());
    }

    @Test
    public void queryParentCategoriesTest2() throws SQLException {
        BusinessCategoryDBHelper helper = new BusinessCategoryDBHelper("", logTag);
        assertFalse(helper.queryParentCategories(connection, BusinessCategoryTypeCd.NETSOL_CATEGORY_TYPE_CD, 250910L).isEmpty());
    }
}
