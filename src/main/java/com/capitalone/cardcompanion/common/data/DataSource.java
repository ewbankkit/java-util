//
// Copyright (C) Capital One Labs.
//

package com.capitalone.cardcompanion.common.data;

import com.capitalone.cardcompanion.common.base.Closures;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import groovy.lang.Closure;
import net.sf.log4jdbc.log.SpyLogFactory;
import net.sf.log4jdbc.sql.jdbcapi.ConnectionSpy;
import net.sf.log4jdbc.sql.rdbmsspecifics.OracleRdbmsSpecifics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Represents a data source.
 */
public final class DataSource {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataSource.class);

    private final javax.sql.DataSource jdbcDataSource;

    /**
     * Constructor.
     */
    public DataSource(javax.sql.DataSource jdbcDataSource) {
        Preconditions.checkNotNull(jdbcDataSource);

        this.jdbcDataSource = jdbcDataSource;
    }

    /**
     * Return whether or not the data source is valid.
     */
    @SuppressWarnings("UnnecessaryUnboxing")
    public boolean isValid(final int timeout) {
        try {
            return withConnection(new Function<Connection, Boolean>() {
                /**
                 * Returns the result of applying this function to input.
                 */
                @SuppressWarnings("UnnecessaryBoxing")
                @Nullable
                @Override
                public Boolean apply(@Nullable Connection connection) {
                    assert connection != null;
                    try {
                        return Boolean.valueOf(connection.isValid(timeout));
                    }
                    catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }).booleanValue();
        }
        catch (SQLException ignore) {
            return false;
        }
    }

    /**
     * Invoke the specified closure with a connection.
     */
    public <T> T withConnection(final Closure<T> closure) throws SQLException {
        Preconditions.checkNotNull(closure);

        return withConnection(Closures.<Connection, T>toFunction(closure));
    }

    /**
     * Invoke the specified function with a connection.
     */
    public <T> T withConnection(final Function<Connection, T> function) throws SQLException {
        Preconditions.checkNotNull(function);

        try (Connection connection = getConnection()) {
            try {
                return function.apply(connection);
            }
            catch (Throwable t) {
                throw sqlException(t);
            }
        }
    }

    /**
     * Invoke the specified closure with a transaction.
     */
    public <T> T withTransaction(final Closure<T> closure) throws SQLException {
        Preconditions.checkNotNull(closure);

        return withTransaction(Closures.<Connection, T>toFunction(closure));
    }

    /**
     * Invoke the specified function with a transaction.
     */
    public <T> T withTransaction(final Function<Connection, T> function) throws SQLException {
        Preconditions.checkNotNull(function);

        try (Connection connection = getConnection()) {
            try {
                T t = function.apply(connection);
                connection.commit();
                return t;
            }
            catch (Throwable t) {
                try {
                    connection.rollback();
                }
                catch (SQLException ex) {
                    LOGGER.error("Could not rollback back transaction.", ex);
                }

                throw sqlException(t);
            }
        }
    }

    private Connection getConnection() throws SQLException {
        Connection delegate = jdbcDataSource.getConnection();

        try {
            ConnectionSpy connection = new ConnectionSpy(
                delegate,
                new DatabaseSqlFormatter(new OracleRdbmsSpecifics()),
                SpyLogFactory.getSpyLogDelegator()
            );
            LOGGER.trace("Using logging JDBC connection.");
            return connection;
        }
        catch(Exception ex) {
            LOGGER.warn("Could not load logging JDBC connection. Using direct JDBC connection instead.", ex);
            return delegate;
        }
    }

    private static SQLException sqlException(Throwable t) {
        if (t instanceof  SQLException) {
            return (SQLException)t;
        }
        Throwable cause = t.getCause();
        return (cause instanceof SQLException) ? (SQLException)cause : new SQLException(t);
    }
}
