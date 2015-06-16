/**
 * Kit's Java Utils.
 */

package com.github.ewbankkit.util.beans;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.netsol.adagent.util.BudgetManagerException;
import com.github.ewbankkit.util.CalendarUtil;
import com.github.ewbankkit.util.DateUtil;
import com.netsol.adagent.util.beans.Product;
import com.github.ewbankkit.util.dbhelpers.BaseHelper;

public class ProductUnitTest {
    public final static String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:53:56 ProductUnitTest.java NSI";

    private static Connection gdbConnection;
    private static Connection pdbConnection;
    private static final DbHelper dbHelper = new DbHelper();
    private static final String prodInstId = "WN.TEST.20091201121825";

    @BeforeClass
    public static void setup() throws SQLException {
        gdbConnection = BaseHelper.createDevGdbConnection();
        pdbConnection = BaseHelper.createDevPdb1Connection();
    }

    @AfterClass
    public static void teardown() throws SQLException {
        BaseHelper.close(gdbConnection);
        BaseHelper.close(pdbConnection);
    }

    @Test(expected = BudgetManagerException.class)
    public void productTest1() throws Exception {
        Product product = new Product("");
        product.init(gdbConnection, pdbConnection, null);
    }

    @Test
    public void productTest2() throws Exception {
        Product product = new Product("");
        dbHelper.updateOneProductValue(gdbConnection, "expiration_date", prodInstId, DateUtil.stringToDate("2011-10-30"));
        product.init(gdbConnection, pdbConnection, prodInstId);
        assertNotNull(product.getExpirationDate());
    }

    @Test
    public void productTest3() throws Exception {
        Product product = new Product("");
        dbHelper.updateOneProductValue(gdbConnection, "start_date", prodInstId, DateUtil.stringToDate("2011-08-30"));
        dbHelper.updateOneProductValue(gdbConnection, "expiration_date", prodInstId, DateUtil.stringToDate("2011-09-30"));
        product.init(gdbConnection, pdbConnection, prodInstId);
        assertEquals(32D, product.getNumberOfDaysInCurrentCycle(), 0D);
    }

    @Test
    public void productTest4() throws Exception {
        Product product = new Product("");
        dbHelper.updateOneProductValue(gdbConnection, "start_date", prodInstId, DateUtil.stringToDate("2011-09-30"));
        dbHelper.updateOneProductValue(gdbConnection, "expiration_date", prodInstId, DateUtil.stringToDate("2011-10-30"));
        product.init(gdbConnection, pdbConnection, prodInstId);
        assertEquals(31D, product.getNumberOfDaysInCurrentCycle(), 0D);
    }

    @Test
    public void productTest5() throws Exception {
        Product product = new Product("");
        dbHelper.updateOneProductValue(gdbConnection, "start_date", prodInstId, DateUtil.stringToDate("2011-08-30"));
        dbHelper.updateOneProductValue(gdbConnection, "expiration_date", prodInstId, DateUtil.stringToDate("2011-09-30"));
        product.init(gdbConnection, pdbConnection, prodInstId);
        assertEquals(3D, product.getDaysUntilExpiration(CalendarUtil.stringToCalendar("2011-09-28")), 0D); // Includes the from day.
    }

    @Test
    public void productTest6() throws Exception {
        Product product = new Product("");
        dbHelper.updateOneProductValue(gdbConnection, "start_date", prodInstId, DateUtil.stringToDate("2011-09-30"));
        dbHelper.updateOneProductValue(gdbConnection, "expiration_date", prodInstId, DateUtil.stringToDate("2011-10-30"));
        product.init(gdbConnection, pdbConnection, prodInstId);
        assertEquals(21D, product.getDaysUntilExpiration(CalendarUtil.stringToCalendar("2011-10-10")), 0D); // Includes the from day.
    }

    private static class DbHelper extends BaseHelper {
        public DbHelper() {
            super("");
        }

        /**
         * Update the value of one product column.
         */
        private void updateOneProductValue(Connection connection, String columnName, String prodInstId, Object value) throws SQLException {
            final String SQL =
                "UPDATE" +
                "  product " +
                "SET" +
                "  %1$s = ?," +
                "  updated_date = NOW()," +
                "  updated_by_user = ?," +
                "  updated_by_system = ? " +
                "WHERE" +
                "  prod_inst_id = ?;";

            PreparedStatement statement = null;
            try {
                statement = connection.prepareStatement(String.format(SQL, columnName));
                statement.setObject(1, value);
                statement.setString(2, "Unit test");
                statement.setString(3, "Unit test");
                statement.setString(4, prodInstId);
                logSqlStatement(null, statement);
                statement.executeUpdate();
            }
            finally {
                close(statement);
            }
        }
    }
}
