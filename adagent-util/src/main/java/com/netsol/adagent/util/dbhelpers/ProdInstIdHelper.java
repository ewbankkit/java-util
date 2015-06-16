/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.dbhelpers;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.logging.Log;

import com.netsol.adagent.util.log.BaseLoggable;

public class ProdInstIdHelper extends BaseHelper {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:53 ProdInstIdHelper.java NSI";

    /**
     * Constructor.
     */
    public ProdInstIdHelper(String logComponent) {
        super(logComponent);
    }

    /**
     * Constructor.
     */
    public ProdInstIdHelper(String logComponent, boolean logSqlStatements) {
        super(logComponent, logSqlStatements);
    }

    /**
     * Constructor.
     */
    public ProdInstIdHelper(Log logger) {
        super(logger);
    }

    /**
     * Constructor.
     */
    public ProdInstIdHelper(BaseLoggable baseLoggable) {
        super(baseLoggable);
    }

    /**
     * Generate a new, unique prodInstId.
     * Works by inserting a new row into the pid_sequence table, which has only one auto_increment column. 
     */
    public String generateProdInstId(String logTag, Connection connection, String prefix) throws SQLException {
        Statement statement = null;
        /*
         This was causing some issue, so we are commenting it for now.
         The auto_increment value is getting reset to zero - seems to be happening after a db restart.
        try {
            statement = connection.createStatement();
            statement.executeUpdate("DELETE FROM pid_sequence");
        }
        finally {
            close(statement);
        }
        */
        try {
            statement = connection.createStatement();
            logInfo(logTag, "Running SQL: INSERT INTO pid_sequence VALUES ();");
            statement.executeUpdate("INSERT INTO pid_sequence VALUES ();", Statement.RETURN_GENERATED_KEYS);
            Long id = getAutoIncrementId(statement);
            logInfo(logTag, "Generated a new prodInstId: "+prefix + id);
            
            return (id == null) ? null : prefix + id.toString();
        }
        finally {
            close(statement);
        }
    }
}
