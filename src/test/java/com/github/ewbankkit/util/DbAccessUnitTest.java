/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.github.ewbankkit.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.SQLException;

import com.github.ewbankkit.util.beans.Pair;
import com.github.ewbankkit.util.codes.DBAlias;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.netsol.adagent.util.DbAccess;
import com.netsol.adagent.util.DbAccess.DbAction;
import com.netsol.adagent.util.cache.DataCache;
import com.netsol.adagent.util.cache.LocalDataCache;
import com.github.ewbankkit.util.dbhelpers.BaseHelper;
import com.github.ewbankkit.util.pool.PooledDataSourceFactory;

public class DbAccessUnitTest {
    public final static String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:03 DbAccessUnitTest.java NSI";

    private static final String                                   applicationId = DBAlias.PDB;
    private static       DataCache<Pair<String, String>, Integer> dataCache     = new LocalDataCache<Pair<String, String>, Integer>();
    private static PooledDataSourceFactory dataSourceFactory;
    private static DbAccess                dbAccess;
    private static final String logTag     = null;
    private static final String prodInstId = "WN.TEST.20091201121825";

    @BeforeClass
    public static void setup() {
        dataSourceFactory = new PooledDataSourceFactory();
        dbAccess = new DbAccess("", "jdbc:mysql://eng2.dev.netsol.com:3501/dcafafc?user=dcafafc&password=dcafafc", dataSourceFactory, dataSourceFactory, dataCache);
    }

    @AfterClass
    public static void teardown() {
        if (dbAccess != null) {
            dbAccess.close();
        }
        if (dataSourceFactory != null) {
            dataSourceFactory.close();
        }
    }

    @Test
    public void dbAccessTest1() throws SQLException {
        assertEquals(2, dbAccess.getDbIds(logTag, applicationId).size());
    }

    @Test
    public void dbAccessTest2() throws SQLException {
        Connection connection = null;
        try {
            connection = dbAccess.getConnection(logTag, applicationId, prodInstId);
            assertEquals("jdbc:mysql://eng2.dev.netsol.com:4300/adagent?user=adagent&password=adagent", connection.getMetaData().getURL());
        }
        finally {
            BaseHelper.close(connection);
        }
    }

    @Test
    public void dbAccessTest3() throws SQLException {
        Connection connection = null;
        try {
            connection = dbAccess.getConnection(logTag, applicationId, "PROD.INST.ID." + System.currentTimeMillis());
            assertEquals("jdbc:mysql://eng2.dev.netsol.com:4301/adagent?user=adagent&password=adagent", connection.getMetaData().getURL());
        }
        finally {
            BaseHelper.close(connection);
        }
    }

    @Test
    public void dbAccessTest4() throws Exception {
        dbAccess.forEachDb(logTag, applicationId, new DbAction() {
            public void invoke(int dbId, Connection connection) throws Exception {
                assertFalse(dbId == 0);
                assertNotNull(connection);
            }});
    }

    @Test
    public void dbAccessTest5() throws Exception {
        dbAccess.parallelForEachDb(logTag, applicationId, new DbAction() {
            public void invoke(int dbId, Connection connection) throws Exception {
                assertFalse(dbId == 0);
                assertNotNull(connection);
                assertTrue(connection.isReadOnly());
            }});
    }

    @Test
    public void dbAccessTest6() throws SQLException {
        Connection connection = null;
        try {
            connection = dbAccess.getReadConnection(logTag, applicationId, prodInstId);
            assertEquals("jdbc:mysql://eng2.dev.netsol.com:4300/adagent?user=adagent&password=adagent", connection.getMetaData().getURL());
        }
        finally {
            BaseHelper.close(connection);
        }
    }

    @Test
    public void dbAccessTest7() throws SQLException {
        Connection connection = null;
        try {
            connection = dbAccess.getReadConnection(logTag, applicationId, "PROD.INST.ID." + System.currentTimeMillis());
            assertEquals("jdbc:mysql://eng2.dev.netsol.com:4301/adagent?user=adagent&password=adagent", connection.getMetaData().getURL());
        }
        finally {
            BaseHelper.close(connection);
        }
    }
}
