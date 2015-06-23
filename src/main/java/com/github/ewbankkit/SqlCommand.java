//
// Kit's Java Utils.
//

package com.github.ewbankkit;

import org.apache.commons.lang3.mutable.Mutable;
import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.commons.lang3.mutable.MutableObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Currency;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Represents a SQL command.
 */
public final class SqlCommand {
    private final String sql;

    public SqlCommand(String sql) {
        this.sql = Objects.requireNonNull(sql);
    }

    public int executeForUpdateCount(final Object... params) throws SQLException {
        final MutableInt mutableInt = new MutableInt();
        SqlDatabaseConnectionPool.getInstance().getSqlDataSource().withTransaction(
            connection -> mutableInt.setValue(executeForUpdateCount(connection, params)));
        return mutableInt.intValue();
    }

    @SuppressWarnings("UnnecessaryUnboxing")
    public int executeForUpdateCount(
        final Connection connection,
        final Object... params) throws SQLException {
        Optional<Either<List<Object>, Integer>> optionalResult = executeSql(
            Objects.requireNonNull(connection),
            null,
            sql,
            params);
        if (!optionalResult.isPresent()) {
            throw new SQLException("No result");
        }
        return optionalResult.get().getRight().intValue();
    }

    /**
     * Execute the command, returning all values.
     */
    public <T> List<T> executeForValues(
        final ValueSupplier<T> f,
        final Object... params) throws SQLException {
        final Mutable<List<T>> mutableList = new MutableObject<>();
        SqlDatabaseConnectionPool.getInstance().getSqlDataSource().withTransaction(
            connection -> mutableList.setValue(executeForValues(connection, f, params)));
        return mutableList.getValue();
    }

    public <T> List<T> executeForValues(
        final Connection connection,
        final ValueSupplier<T> f,
        final Object... params) throws SQLException {
        Optional<Either<List<T>, Integer>> optionalResult = executeSql(
            Objects.requireNonNull(connection),
            f,
            sql,
            params);
        if (!optionalResult.isPresent()) {
            throw new SQLException("No result");
        }
        return optionalResult.get().getLeft();
    }

    /**
     * Execute the specified SQL with the specified parameters.
     */
    @SuppressWarnings("UnnecessaryBoxing")
    private static <T> Optional<Either<List<T>, Integer>> executeSql(
        final Connection connection,
        final ValueSupplier<T> f,
        final String sql,
        final Object[] params) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            setParameters(statement, 1, params);
            if (statement.execute()) {
                if (f == null) {
                    throw new SQLException("No update count");
                }
                try (ResultSet resultSet = statement.getResultSet()) {
                    return Optional.of(Either.left(newList(resultSet, f)));
                }
            }
            if (f != null) {
                throw new SQLException("No result set");
            }
            int updateCount = statement.getUpdateCount();
            return (updateCount > 0) ?
                Optional.of(Either.right(Integer.valueOf(updateCount))) :
                Optional.<Either<List<T>, Integer>>empty();
        }
    }

    /**
     * Return a new list with values from the result set.
     */
    private static <T> List<T> newList(
        ResultSet resultSet,
        ValueSupplier<T> f) throws SQLException {
        if ((resultSet == null) || !resultSet.next()) {
            return Collections.emptyList();
        }

        List<T> list = new ArrayList<>();
        do {
            list.add(f.get(resultSet));
        } while (resultSet.next());

        return Collections.unmodifiableList(list);
    }

    /**
     * Set parameters.
     * Return the next parameter index.
     */
    private static int setParameters(
        PreparedStatement statement,
        int initialParameterIndex,
        Object[] params) throws SQLException {
        int parameterIndex = initialParameterIndex;
        for (Object param : params) {
            if (param instanceof Enum<?>) {
                param = param.toString();
            }
            else if (param instanceof Instant) {
                param = SqlUtils.toSqlTimestamp((Instant)param);
            }
            else if (param instanceof Currency) {
                param = ((Currency)param).getCurrencyCode();
            }
            statement.setObject(parameterIndex++, param);
        }
        return parameterIndex;
    }

    @FunctionalInterface
    public interface ValueSupplier<T> {
        /**
         * Return any value.
         */
        T get(ResultSet resultSet) throws SQLException;
    }
}
