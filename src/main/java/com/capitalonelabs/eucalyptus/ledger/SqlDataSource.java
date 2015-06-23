//
// Kit's Java Utils.
//

package com.capitalonelabs.eucalyptus.ledger;

import org.apache.commons.lang3.mutable.MutableBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

/**
 * Represents a SQL data source.
 */
public final class SqlDataSource {
    private static final Logger LOGGER = LoggerFactory.getLogger(SqlDataSource.class);

    private final DataSource jdbcDataSource;

    public SqlDataSource(DataSource jdbcDataSource) {
        this.jdbcDataSource = Objects.requireNonNull(jdbcDataSource);
    }

    /**
     * Return whether or not the data source is valid.
     */
    public boolean isValid(final int timeout) {
        final MutableBoolean valid = new MutableBoolean();
        ConnectionConsumer cc = connection -> {
            valid.setValue(connection.isValid(timeout));
        };

        try {
            withConnection(cc);
        }
        catch (SQLException ignore) {}

        return valid.booleanValue();
    }

    /**
     * Invoke the specified function with a connection.
     */
    public void withConnection(final ConnectionConsumer consumer) throws SQLException {
        try (Connection connection = getConnection()) {
            try {
                consumer.accept(connection);
            }
            catch (Throwable t) {
                throw sqlException(t);
            }
        }
    }

    /**
     * Invoke the specified function with a transaction.
     */
    public void withTransaction(final ConnectionConsumer consumer) throws SQLException {
        try (Connection connection = getConnection()) {
            try {
                consumer.accept(connection);
                if (!connection.getAutoCommit()) {
                    connection.commit();
                }
            }
            catch (Throwable t) {
                if (!connection.getAutoCommit()) {
                    try {
                        connection.rollback();
                    }
                    catch (SQLException ex) {
                        LOGGER.error("Could not rollback transaction.", ex);
                    }
                }

                throw sqlException(t);
            }
        }
    }

    private Connection getConnection() throws SQLException {
        return jdbcDataSource.getConnection();
    }

    private static SQLException sqlException(Throwable t) {
        if (t instanceof  SQLException) {
            return (SQLException)t;
        }
        Throwable cause = t.getCause();
        return (cause instanceof SQLException) ? (SQLException)cause : new SQLException(t);
    }

    @FunctionalInterface
    public interface ConnectionConsumer {
        void accept(Connection connection) throws SQLException;
    }
}
