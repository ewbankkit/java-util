/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.dbhelpers;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import com.netsol.adagent.util.dbhelpers.BaseHelper;
import com.netsol.adagent.util.dbhelpers.CallTrackingNumberHelper;

public class CallTrackingNumberHelperUnitTest {
    public final static String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:45 CallTrackingNumberHelperUnitTest.java NSI";

    private static Connection connection;
    private static final String logTag = null;
    private static final String prodInstId = "WN.DEV.BING.003";

    @BeforeClass
    public static void setup() throws SQLException {
        connection = BaseHelper.createDevPdb1Connection();
    }

    @AfterClass
    public static void teardown() throws SQLException {
        BaseHelper.close(connection);
    }

   
    public void hasActiveTrackingNumbersTest1() throws SQLException {
        CallTrackingNumberHelper callTrackingNumberHelper = new CallTrackingNumberHelper("");
      //  assertFalse(callTrackingNumberHelper.hasActiveTrackingNumbers(logTag, connection, null));
    }

  
}
