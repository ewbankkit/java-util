/**
 * Kit's Java Utils.
 */

package com.github.ewbankkit.util.productlifecycle;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.sql.Connection;
import java.sql.SQLException;

import com.github.ewbankkit.util.dbhelpers.BaseHelper;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.netsol.adagent.util.productlifecycle.BudgetRenewalEvent;
import com.netsol.adagent.util.productlifecycle.ProductLifeCycleEventFactory;
import com.netsol.adagent.util.productlifecycle.ProductLifeCycleEventHelper;
import com.netsol.adagent.util.productlifecycle.ProvisioningRenewalEvent;

public class ProductLifecycleEventUnitTest {
    public final static String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:15 ProductLifecycleEventUnitTest.java NSI";

    private static Connection connection;
    private static final String logTag = null;
    private static final String prodInstId = "WN.TEST.20091201121825";

    @BeforeClass
    public static void setup() throws SQLException {
        connection = BaseHelper.createDevPdb1Connection();
    }

    @AfterClass
    public static void teardown() throws SQLException {
        BaseHelper.close(connection);
    }

    @Test
    public void getLatestBudgetRenewalEventTest1() throws SQLException {
        ProductLifeCycleEventHelper productLifeCycleEventHelper = new ProductLifeCycleEventHelper("");
        assertNull(productLifeCycleEventHelper.getLatestBudgetRenewalEvent(logTag, connection, null));
    }

    @Test
    public void getLatestBudgetRenewalEventTest2() throws SQLException {
        ProductLifeCycleEventHelper productLifeCycleEventHelper = new ProductLifeCycleEventHelper("");
        BudgetRenewalEvent budgetRenewalEvent = ProductLifeCycleEventFactory.createBudgetRenewalEvent();
        budgetRenewalEvent.setProdInstId(prodInstId);
        budgetRenewalEvent.setNewCurrentTarget(99.99D);
        productLifeCycleEventHelper.insertProductLifeCycleEvent(logTag, connection, budgetRenewalEvent);
        assertEquals(99.99D, productLifeCycleEventHelper.getLatestBudgetRenewalEvent(logTag, connection, prodInstId).getNewCurrentTarget(), 0D);
    }

    @Test
    public void getLatestProvisioningRenewalEventTest1() throws SQLException {
        ProductLifeCycleEventHelper productLifeCycleEventHelper = new ProductLifeCycleEventHelper("");
        assertNull(productLifeCycleEventHelper.getLatestProvisioningRenewalEvent(logTag, connection, null));
    }

    @Test
    public void getLatestProvisioningRenewalEventTest2() throws SQLException {
        ProductLifeCycleEventHelper productLifeCycleEventHelper = new ProductLifeCycleEventHelper("");
        ProvisioningRenewalEvent provisioningRenewalEvent = ProductLifeCycleEventFactory.createProvisioningRenewalEvent();
        provisioningRenewalEvent.setProdInstId(prodInstId);
        provisioningRenewalEvent.setNewBaseTarget(77.77D);
        productLifeCycleEventHelper.insertProductLifeCycleEvent(logTag, connection, provisioningRenewalEvent);
        assertEquals(77.77D, productLifeCycleEventHelper.getLatestProvisioningRenewalEvent(logTag, connection, prodInstId).getNewBaseTarget(), 0D);
    }

    @Test
    public void getLatestProvisioningRenewalEventTest3() throws SQLException {
        ProductLifeCycleEventHelper productLifeCycleEventHelper = new ProductLifeCycleEventHelper("");
        ProvisioningRenewalEvent provisioningRenewalEvent = ProductLifeCycleEventFactory.createProvisioningRenewalEvent();
        provisioningRenewalEvent.setProdInstId(prodInstId);
        provisioningRenewalEvent.setNewBaseTarget(44.44D);
        provisioningRenewalEvent.setNewCurrentTarget(88.88D);
        productLifeCycleEventHelper.insertProductLifeCycleEvent(logTag, connection, provisioningRenewalEvent);
        assertEquals(88.88D, productLifeCycleEventHelper.getLatestProvisioningRenewalEvent(logTag, connection, prodInstId).getNewCurrentTarget(), 0D);
    }
}
