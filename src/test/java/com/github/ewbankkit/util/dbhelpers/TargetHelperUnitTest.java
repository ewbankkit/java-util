/**
 * Kit's Java Utils.
 */

package com.github.ewbankkit.util.dbhelpers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;

import com.github.ewbankkit.util.DateUtil;
import com.github.ewbankkit.util.codes.TargetType;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import com.netsol.adagent.util.beans.Target;
import com.netsol.adagent.util.beans.TargetVendor;
import com.netsol.adagent.util.dbhelpers.TargetHelper;

public class TargetHelperUnitTest {
    public final static String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:50 TargetHelperUnitTest.java NSI";

    private static Connection connection;
    private static final String logTag = null;
    private static final String prodInstId = "WN.TEST.20091201121825";
    private static final String updatedBySystem = "UNIT_TEST";
    private static final String updatedByUser = "UNIT_TEST";
    private static Long marketSubCategoryId = 4199l;
    private static Long marketGeographyId = 1l;

    @BeforeClass
    public static void setup() throws SQLException {
        connection = BaseHelper.createDevPdb1Connection();
        SimpleJdbcTemplate tpl = new SimpleJdbcTemplate(new SingleConnectionDataSource(connection, true));
        try{
            marketSubCategoryId =  tpl.queryForLong("select max(market_sub_category_id) from market_sub_category");
            marketGeographyId = tpl.queryForLong("select max(market_geography_id) from market_geography");
        }catch(Exception e){
            e.printStackTrace();
            //continue with default values
        }

    }

    @AfterClass
    public static void teardown() throws SQLException {
        BaseHelper.close(connection);
    }

    @Test
    public void getTargetTest1() throws SQLException {
        TargetHelper helper = new TargetHelper("");
        assertNull(helper.getTarget(logTag, connection, null, 0L));
    }

    @Test
    public void getTargetsTest1() throws SQLException {
        TargetHelper helper = new TargetHelper("");
        assertTrue(helper.getTargetsExcludingStatuses(logTag, connection, null).isEmpty());
    }

    @Test
    public void getTargetTest2() throws SQLException, ParseException {
        TargetHelper helper = new TargetHelper("");
        Target target = new Target();
        target.setBudget(100.10D);
        target.setEndDate(DateUtil.stringToDate("2020-02-02"));
        target.setMargin(10.1F);
        target.setMarketGeographyId(marketGeographyId);
        target.setMarketSubCategoryId(marketSubCategoryId);
        target.setName("UNIT TEST TARGET");
        target.setProdInstId(prodInstId);
        target.setSeoBudget(42.24D);
        target.setSpendAggressiveness(1F);
        target.setStartDate(DateUtil.stringToDate("2012-02-02"));
        target.setStatus(Target.Status.ACTIVE);
        target.setTargetType(TargetType.DEFAULT);
        target.setUpdatedBySystem(updatedBySystem);
        target.setUpdatedByUser(updatedByUser);

        TargetVendor targetVendor = new TargetVendor();
        targetVendor.setProdInstId(prodInstId);
        targetVendor.setVendorId(1l);
        targetVendor.setBudget(50.00d);
        targetVendor.setSpendAggressiveness(1.5f);
        targetVendor.setUpdatedBySystem(updatedBySystem);
        targetVendor.setUpdatedByUser(updatedByUser);
        target.setTargetVendors(new TargetVendor[] {targetVendor});

        System.out.println("BEFORE INSERT: " + target);
        helper.insertTarget(logTag, connection, target);

        target = helper.getTarget(logTag, connection, prodInstId, target.getTargetId());
        System.out.println("AFTER INSERT: " + target);
        assertEquals(100.10D, target.getBudget(), 0D);
        assertEquals("2020-02-02", DateUtil.dateToString(target.getEndDate()));
        assertEquals(10.1F, target.getMargin(), 0F);
        assertEquals(marketGeographyId, Long.valueOf(target.getMarketGeographyId()));
        assertEquals(marketSubCategoryId, Long.valueOf(target.getMarketSubCategoryId()));
        assertEquals("UNIT TEST TARGET", target.getName());
        assertEquals(42.24D, target.getSeoBudget(), 0D);
        assertEquals(1F, target.getSpendAggressiveness(), 0F);
        assertEquals("2012-02-02", DateUtil.dateToString(target.getStartDate()));
        assertEquals(Target.Status.ACTIVE, target.getStatus());
        assertEquals(TargetType.DEFAULT, target.getTargetType());

        targetVendor = target.getTargetVendors()[0];
        assertEquals(1l, targetVendor.getVendorId());
        assertEquals(50.00d, targetVendor.getBudget(), 0d);
        assertEquals(1.5f, targetVendor.getSpendAggressiveness(), 0d);

        TargetVendor targetVendor2 = new TargetVendor();
        targetVendor2.setProdInstId(prodInstId);
        targetVendor2.setVendorId(1l);
        targetVendor2.setBudget(40.00d);
        targetVendor2.setSpendAggressiveness(1f);
        targetVendor2.setUpdatedBySystem(updatedBySystem);
        targetVendor2.setUpdatedByUser(updatedByUser);
        target.setTargetVendors(new TargetVendor[] {targetVendor, targetVendor2});

        assertFalse(helper.getTargetsExcludingStatuses(logTag, connection, prodInstId, Target.Status.DELETED).isEmpty());

        target.setBudget(200.20D);
        target.setEndDate(DateUtil.stringToDate("2030-03-03"));
        target.setSeoBudget(88.88D);
        target.setUpdatedBySystem(updatedBySystem);
        target.setUpdatedByUser(updatedByUser);

        targetVendor = target.getTargetVendors()[0];
        targetVendor.setBudget(40.00d);
        targetVendor.setSpendAggressiveness(1f);

        System.out.println("BEFORE UPDATE: " + target);
        helper.updateTarget(logTag, connection, target);

        target = helper.getTarget(logTag, connection, prodInstId, target.getTargetId());
        System.out.println("AFTER UPDATE: " + target);
        assertEquals(200.20D, target.getBudget(), 0D);
        assertEquals("2030-03-03", DateUtil.dateToString(target.getEndDate()));
        assertEquals(88.88D, target.getSeoBudget(), 0D);

        targetVendor = target.getTargetVendors()[0];
        assertEquals(40.00d, targetVendor.getBudget(), 0d);
        assertEquals(1f, targetVendor.getSpendAggressiveness(), 0d);

        targetVendor = target.getTargetVendors()[0];
        assertEquals(40.00d, targetVendor.getBudget(), 0d);
        assertEquals(1f, targetVendor.getSpendAggressiveness(), 0d);

        target.setStatus(Target.Status.DELETED);
        target.setUpdatedBySystem(updatedBySystem);
        target.setUpdatedByUser(updatedByUser);
        helper.updateTarget(logTag, connection, target);
    }
}
