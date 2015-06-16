/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.dbhelpers;

import java.sql.Connection;
import java.sql.SQLException;

import com.netsol.adagent.util.DbAccess;
import com.netsol.adagent.util.codes.DBAlias;

public final class ConnectionFactories {
    public final static String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:42 ConnectionFactories.java NSI";

    public static ConnectionFactory newDbAccessConnectionFactory(final DbAccess dbAccess) {
        return new ConnectionFactory() {
            public Connection createGdbConnection(String logTag) throws SQLException {
                return dbAccess.getConnection(logTag, DBAlias.GDB, null);
            }

            public Connection createPdbConnection(String logTag, String prodInstId) throws SQLException {
                return dbAccess.getConnection(logTag, DBAlias.PDB, prodInstId);
            }};
    }
}
