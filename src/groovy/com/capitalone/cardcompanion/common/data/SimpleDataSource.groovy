//
// Copyright (C) Capital One Labs.
//

package com.capitalone.cardcompanion.common.data

import javax.sql.DataSource
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import java.sql.SQLFeatureNotSupportedException
import java.util.logging.Logger

class SimpleDataSource implements DataSource {
    SimpleDataSource() {
        System.setProperty("oracle.jdbc.J2EE13Compliant", "true"); // Return java.sql.Timestamp instead of oracle.sql.TIMESTAMP.
        Class.forName('oracle.jdbc.OracleDriver')
    }

    @Override
    Connection getConnection() throws SQLException {
        Connection connection = DriverManager.getConnection 'jdbc:oracle:thin:ORCHESTRATOR_APP/nCxb6bkGIAPoOw1vW0CwYk0o@localhost:1521:xe'
        connection.autoCommit = false
        connection
    }

    @Override
    Connection getConnection(String username, String password) throws SQLException {
        null
    }

    @Override
    PrintWriter getLogWriter() throws SQLException {
        null
    }

    @Override
    void setLogWriter(PrintWriter out) throws SQLException {}

    @Override
    void setLoginTimeout(int seconds) throws SQLException {}

    @Override
    int getLoginTimeout() throws SQLException {
        0
    }

    @Override
    Logger getParentLogger() throws SQLFeatureNotSupportedException {
        null
    }

    @Override
    def <T> T unwrap(Class<T> iface) throws SQLException {
        null
    }

    @Override
    boolean isWrapperFor(Class<?> iface) throws SQLException {
        false
    }
}
