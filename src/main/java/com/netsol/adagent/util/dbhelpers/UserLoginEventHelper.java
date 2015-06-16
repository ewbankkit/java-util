/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.dbhelpers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.commons.logging.Log;

import com.netsol.adagent.util.beans.BaseUserLoginEvent;

/**
 * DB helpers for user login events.
 */
public class UserLoginEventHelper extends BaseHelper {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:56 UserLoginEventHelper.java NSI";

    public UserLoginEventHelper(String logComponent) {
        super(logComponent, false);
    }

    public UserLoginEventHelper(Log logger) {
        super(logger, false);
    }

    /**
     * Insert the specified user login event.
     */
    public void insertUserLoginEvent(String logTag, Connection connection, BaseUserLoginEvent userLoginEvent) throws SQLException {
        final String SQL =
            "INSERT INTO user_login_event" +
            "  (event_type, user_name, user_login_domain, ip, details, created_by_system) " +
            "VALUES" +
            "  (?, SUBSTRING(?, 1, 64), ?, ?, ?, ?);";

        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(SQL);
            statement.setString(1, userLoginEvent.getEventType());
            statement.setString(2, userLoginEvent.getUserName());
            statement.setString(3, userLoginEvent.getUserLoginDomain());
            statement.setString(4, userLoginEvent.getIpAddress());
            statement.setString(5, userLoginEvent.getDetails());
            statement.setString(6, userLoginEvent.getCreatedBySystem());
            logSqlStatement(logTag, statement);
            statement.executeUpdate();
        }
        finally {
            close(statement);
        }
    }
}
