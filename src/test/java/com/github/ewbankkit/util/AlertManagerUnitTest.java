/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.github.ewbankkit.util;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.SQLException;

import com.github.ewbankkit.util.dbhelpers.BaseHelper;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.netsol.adagent.util.AlertManager;
import com.netsol.adagent.util.beans.Alert;
import com.netsol.adagent.util.beans.Alert.AlertType;

public class AlertManagerUnitTest {
    public final static String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:01 AlertManagerUnitTest.java NSI";

    private static Connection connection;
    private static String logTag = null;
    private static String prodInstId = "WN.PP.33344444";
    private static String updatedByUser = "Unit test";

    @BeforeClass
    public static void setup() throws SQLException {
        connection = BaseHelper.createDevGdbConnection();
    }

    @AfterClass
    public static void teardown() throws SQLException {
        BaseHelper.close(connection);
    }

    @Test
    public void addAlertTest1() throws SQLException {
        AlertManager.deleteAllAlerts(logTag, connection, prodInstId, updatedByUser);

        Alert alert = new Alert();
        alert.setDetails("SPLOOGE");
        alert.setEntityType(Alert.ENTITY_PRODUCT);
        alert.setOrigin("TEST");
        alert.setPriority(Alert.PRIORITY_HIGH);
        alert.setProdInstId(prodInstId);
        alert.setStatus(Alert.STATUS_UNREAD);
        alert.setType(Alert.GENERIC_ALERT);
        Long alertId = AlertManager.addAlert(logTag, connection, alert, updatedByUser);
        assertNotNull(alertId);
    }

    @Test
    public void addAlertTest2() throws SQLException {
        AlertManager.deleteAllAlerts(logTag, connection, prodInstId, updatedByUser);

        Alert alert = new Alert();
        alert.setDetails("SPLOOGE");
        alert.setEntityId(123L);
        alert.setEntityType(Alert.ENTITY_CAMPAIGN);
        alert.setOrigin("TEST");
        alert.setPriority(Alert.PRIORITY_MED);
        alert.setProdInstId(prodInstId);
        alert.setStatus(Alert.STATUS_UNREAD);
        alert.setType(Alert.PERFORMANCE_ALERT);
        Long alertId = AlertManager.addAlert(logTag, connection, alert, updatedByUser);
        assertNotNull(alertId);
    }

    @Test
    public void addAlertTest3() throws SQLException {
        AlertManager.deleteAllAlerts(logTag, connection, prodInstId, updatedByUser);

        Alert alert = new Alert();
        alert.setDetails("SPLOOGE");
        alert.setEntityType(Alert.ENTITY_PRODUCT);
        alert.setOrigin("TEST");
        alert.setPriority(Alert.PRIORITY_HIGH);
        alert.setProdInstId(prodInstId);
        alert.setStatus(Alert.STATUS_UNREAD);
        alert.setType(Alert.GENERIC_ALERT);
        Long alertId = AlertManager.addAlert(logTag, connection, alert, updatedByUser);
        assertNotNull(alertId);

        alert = new Alert();
        alert.setDetails("SPLOOGE");
        alert.setEntityType(Alert.ENTITY_PRODUCT);
        alert.setOrigin("TEST");
        alert.setPriority(Alert.PRIORITY_HIGH);
        alert.setProdInstId(prodInstId);
        alert.setStatus(Alert.STATUS_UNREAD);
        alert.setType(Alert.GENERIC_ALERT);
        alertId = AlertManager.addAlert(logTag, connection, alert, updatedByUser);
        assertNull(alertId);
    }

    @Test
    public void addAlertTest4() throws SQLException {
        AlertManager.deleteAllAlerts(logTag, connection, prodInstId, updatedByUser);

        Alert alert = new Alert();
        alert.setDetails("SPLOOGE");
        alert.setEntityId(123L);
        alert.setEntityType(Alert.ENTITY_CAMPAIGN);
        alert.setOrigin("TEST");
        alert.setPriority(Alert.PRIORITY_MED);
        alert.setProdInstId(prodInstId);
        alert.setStatus(Alert.STATUS_UNREAD);
        alert.setType(Alert.PERFORMANCE_ALERT);
        Long alertId = AlertManager.addAlert(logTag, connection, alert, updatedByUser);
        assertNotNull(alertId);

        alert = new Alert();
        alert.setDetails("SPLOOGE");
        alert.setEntityId(123L);
        alert.setEntityType(Alert.ENTITY_CAMPAIGN);
        alert.setOrigin("TEST");
        alert.setPriority(Alert.PRIORITY_MED);
        alert.setProdInstId(prodInstId);
        alert.setStatus(Alert.STATUS_UNREAD);
        alert.setType(Alert.PERFORMANCE_ALERT);
        alertId = AlertManager.addAlert(logTag, connection, alert, updatedByUser);
        assertNull(alertId);
    }

    @Test
    public void addLandingPageAlertTest1() throws SQLException {
        AlertManager.deleteAllAlerts(logTag, connection, prodInstId, AlertType.LANDING_PAGE, updatedByUser);

        Alert alert = new Alert();
        alert.setDetails("http://www.example.com/index.html");
        alert.setEntityType(Alert.ENTITY_PRODUCT);
        alert.setOrigin("TEST");
        alert.setPriority(Alert.PRIORITY_MED);
        alert.setProdInstId(prodInstId);
        alert.setStatus(Alert.STATUS_UNREAD);
        alert.setType(Alert.LANDING_PAGE_ALERT);
        Long alertId = AlertManager.addAlert(logTag, connection, alert, updatedByUser);
        assertNotNull(alertId);

        alert = new Alert();
        alert.setDetails("http://www.example.com/info.php");
        alert.setEntityType(Alert.ENTITY_PRODUCT);
        alert.setOrigin("TEST");
        alert.setPriority(Alert.PRIORITY_MED);
        alert.setProdInstId(prodInstId);
        alert.setStatus(Alert.STATUS_UNREAD);
        alert.setType(Alert.LANDING_PAGE_ALERT);
        alertId = AlertManager.addAlert(logTag, connection, alert, updatedByUser);
        assertNotNull(alertId);
    }
}
