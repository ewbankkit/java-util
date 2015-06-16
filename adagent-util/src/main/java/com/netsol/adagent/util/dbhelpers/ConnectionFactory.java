/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.dbhelpers;

import java.sql.Connection;
import java.sql.SQLException;

public interface ConnectionFactory {
    public final static String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:43 ConnectionFactory.java NSI";

    /**
     * Create a GDB connection.
     */
    public abstract Connection createGdbConnection(String logTag) throws SQLException;

    /**
     * Create a PDB connection.
     */
    public abstract Connection createPdbConnection(String logTag, String prodInstId) throws SQLException;
}
