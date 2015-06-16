/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.dbhelpers;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.netsol.adagent.util.beans.UserLoginFailureEvent;
import com.netsol.adagent.util.beans.UserLoginSuccessEvent;
import com.netsol.adagent.util.dbhelpers.BaseHelper;
import com.netsol.adagent.util.dbhelpers.UserLoginEventHelper;

public class UserLoginEventHelperUnitTest {
    public final static String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:50 UserLoginEventHelperUnitTest.java NSI";

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
    public void insertUserLoginFailureEventTest1() throws SQLException {
        UserLoginEventHelper userLoginEventHelper = new UserLoginEventHelper("");
        UserLoginFailureEvent userLoginEvent = new UserLoginFailureEvent();
        userLoginEvent.setCreatedBySystem("UNIT_TEST");
        userLoginEvent.setDetails("Some details");
        userLoginEvent.setIpAddress("1.2.3.4");
        userLoginEvent.setUserName("fred");
        userLoginEventHelper.insertUserLoginEvent(logTag, connection, userLoginEvent);
    }

    @Test
    public void insertUserLoginSuccessEventTest1() throws SQLException {
        UserLoginEventHelper userLoginEventHelper = new UserLoginEventHelper("");
        UserLoginSuccessEvent userLoginEvent = new UserLoginSuccessEvent();
        userLoginEvent.setCreatedBySystem("UNIT_TEST");
        userLoginEvent.setDetails("Some details");
        userLoginEvent.setIpAddress("1.2.3.4");
        userLoginEvent.setUserName("fred");
        userLoginEventHelper.insertUserLoginEvent(logTag, connection, userLoginEvent);
    }
}
