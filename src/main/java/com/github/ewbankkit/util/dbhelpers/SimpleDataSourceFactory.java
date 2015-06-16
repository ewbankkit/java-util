/**
 * Kit's Java Utils.
 */

package com.github.ewbankkit.util.dbhelpers;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

import javax.sql.DataSource;

/**
 * A simple data source factory.
 */
public class SimpleDataSourceFactory implements DataSourceFactory {
    public final static String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:55 SimpleDataSourceFactory.java NSI";

    public final static SimpleDataSourceFactory INSTANCE = new SimpleDataSourceFactory();

    /**
     * Return a new data source.
     */
    public DataSource newDataSource(String url) {
        return new SimpleDataSource(url);
    }

    /**
     * Constructor.
     */
    private SimpleDataSourceFactory() {}

    /**
     * A simple data source.
     */
    private static class SimpleDataSource implements DataSource {
        private final String url;

        /**
         * Constructor.
         */
        public SimpleDataSource(String url) {
            this.url = url;
        }

        /**
         * Attempt to establish a connection with the data source that this DataSource object represents.
         */
        public Connection getConnection() throws SQLException {
            return BaseHelper.createConnection(url);
        }

        /**
         * Attempt to establish a connection with the data source that this DataSource object represents.
         */
        public Connection getConnection(String username, String password) throws SQLException {
            throw new UnsupportedOperationException();
        }

        /**
         * Returns the log writer for this DataSource  object.
         */
        public PrintWriter getLogWriter() throws SQLException {
            throw new UnsupportedOperationException();
        }

        /**
         * Returns the maximum time in seconds that this data source can wait while attempting to connect to a database.
         */
        public int getLoginTimeout() throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public Logger getParentLogger() throws SQLFeatureNotSupportedException {
            return null;
        }

        /**
         * Set the log writer for this DataSource  object to the given PrintWriter object.
         */
        public void setLogWriter(PrintWriter out) throws SQLException {
            throw new UnsupportedOperationException();
        }

        /**
         * Set the maximum time in seconds that this data source will wait while attempting to connect to a database.
         */
        public void setLoginTimeout(int seconds) throws SQLException {
            throw new UnsupportedOperationException();
        }

        @Override
        public <T> T unwrap(Class<T> iface) throws SQLException {
            return null;
        }

        @Override
        public boolean isWrapperFor(Class<?> iface) throws SQLException {
            return false;
        }
    }
}
