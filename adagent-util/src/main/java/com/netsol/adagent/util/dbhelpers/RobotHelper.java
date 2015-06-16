/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.dbhelpers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

import org.apache.commons.logging.Log;

import com.netsol.adagent.util.beans.Robot;

/**
 * DB helpers for web robots.
 */
public class RobotHelper extends BaseHelper {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:54 RobotHelper.java NSI";

    /**
     * Constructor.
     */
    public RobotHelper(String logComponent) {
        super(logComponent);

        return;
    }

    /**
     * Constructor.
     */
    public RobotHelper(String logComponent, boolean logSqlStatements) {
        super(logComponent, logSqlStatements);

        return;
    }

    /**
     * Constructor.
     */
    public RobotHelper(Log logger) {
        super(logger);

        return;
    }

    /**
     * Constructor.
     */
    public RobotHelper(Log logger, boolean logSqlStatements) {
        super(logger, logSqlStatements);

        return;
    }

    /**
     * Return all robots.
     */
    public Collection<Robot> getAllRobots(String logTag, Connection connection) throws SQLException {
        final String SQL =
            "SELECT" +
            "  robot_name," +
            "  http_user_agent " +
            "FROM" +
            "  robots;";

        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            statement = connection.prepareStatement(SQL);
            this.logSqlStatement(null, statement);
            resultSet = statement.executeQuery();
            return BaseHelper.newList(resultSet, RobotFactory.INSTANCE);
        }
        finally {
            BaseHelper.close(statement, resultSet);
        }
    }

    /**
     * Factory class used to create Robot objects from a result set.
     */
    private static class RobotFactory implements Factory<Robot> {
        public static final RobotFactory INSTANCE = new RobotFactory();

        /**
         * Constructor.
         */
        private RobotFactory() {
            return;
        }

        /**
         * Return a new instance with values from the result set.
         */
        public Robot newInstance(ResultSet resultSet) throws SQLException {
            Robot robot = new Robot();
            robot.setHttpUserAgent(resultSet.getString("http_user_agent"));
            robot.setRobotName(resultSet.getString("robot_name"));

            return robot;
        }
    }

    // Test harness.
    public static void main(String[] args) {
        try {
            Connection connection = null;
            try {
                String logTag = null;
                connection = BaseHelper.createDevGdbConnection();
                RobotHelper robotHelper = new RobotHelper("");
                System.out.println(robotHelper.getAllRobots(logTag, connection));
            }
            finally {
                BaseHelper.close(connection);
            }
        }
        catch (Throwable t) {
            t.printStackTrace();
        }

        return;
    }
}
