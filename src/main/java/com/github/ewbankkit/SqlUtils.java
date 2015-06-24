//
// Kit's Java Utils.
//

package com.github.ewbankkit;

import org.apache.commons.lang3.EnumUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Calendar;
import java.util.Objects;

/**
 * SQL/JDBC utilities.
 */
public final class SqlUtils {
    /**
     * Return any boolean value.
     */
    @SuppressWarnings("UnnecessaryBoxing")
    public static Boolean getBooleanValue(ResultSet resultSet, int columnIndex) throws SQLException {
        boolean value = resultSet.getBoolean(columnIndex);
        if (resultSet.wasNull()) {
            return null;
        }
        return Boolean.valueOf(value);
    }

    /**
     * Return any boolean value.
     */
    @SuppressWarnings("UnnecessaryBoxing")
    public static Boolean getBooleanValue(ResultSet resultSet, String columnName) throws SQLException {
        boolean value = resultSet.getBoolean(columnName);
        if (resultSet.wasNull()) {
            return null;
        }
        return Boolean.valueOf(value);
    }

    /**
     * Return any double value.
     */
    @SuppressWarnings("UnnecessaryBoxing")
    public static Double getDoubleValue(ResultSet resultSet, int columnIndex) throws SQLException {
        double value = resultSet.getDouble(columnIndex);
        if (resultSet.wasNull()) {
            return null;
        }
        return Double.valueOf(value);
    }

    /**
     * Return any double value.
     */
    @SuppressWarnings("UnnecessaryBoxing")
    public static Double getDoubleValue(ResultSet resultSet, String columnName) throws SQLException {
        double value = resultSet.getDouble(columnName);
        if (resultSet.wasNull()) {
            return null;
        }
        return Double.valueOf(value);
    }

    /**
     * Return any enum value.
     */
    public static <T extends Enum<T>> T getEnumValue(ResultSet resultSet, int columnIndex, Class<T> classOfT) throws SQLException {
        String s = resultSet.getString(columnIndex);
        if (s == null) {
            return null;
        }
        return EnumUtils.getEnum(classOfT, s);
    }

    /**
     * Return any enum value.
     */
    public static <T extends Enum<T>> T getEnumValue(ResultSet resultSet, String columnName, Class<T> classOfT) throws SQLException {
        String s = resultSet.getString(columnName);
        if (s == null) {
            return null;
        }
        return EnumUtils.getEnum(classOfT, s);
    }

    /**
     * Return any float value.
     */
    @SuppressWarnings("UnnecessaryBoxing")
    public static Float getFloatValue(ResultSet resultSet, int columnIndex) throws SQLException {
        float value = resultSet.getFloat(columnIndex);
        if (resultSet.wasNull()) {
            return null;
        }
        return Float.valueOf(value);
    }

    /**
     * Return any float value.
     */
    @SuppressWarnings("UnnecessaryBoxing")
    public static Float getFloatValue(ResultSet resultSet, String columnName) throws SQLException {
        float value = resultSet.getFloat(columnName);
        if (resultSet.wasNull()) {
            return null;
        }
        return Float.valueOf(value);
    }

    /**
     * Return any integer value.
     */
    @SuppressWarnings("UnnecessaryBoxing")
    public static Integer getIntegerValue(ResultSet resultSet, String columnName) throws SQLException {
        int value = resultSet.getInt(columnName);
        if (resultSet.wasNull()) {
            return null;
        }
        return Integer.valueOf(value);
    }

    /**
     * Return any long value.
     */
    @SuppressWarnings("UnnecessaryBoxing")
    public static Long getLongValue(ResultSet resultSet, int columnIndex) throws SQLException {
        long value = resultSet.getLong(columnIndex);
        if (resultSet.wasNull()) {
            return null;
        }
        return Long.valueOf(value);
    }

    /**
     * Return any long value.
     */
    @SuppressWarnings("UnnecessaryBoxing")
    public static Long getLongValue(ResultSet resultSet, String columnName) throws SQLException {
        long value = resultSet.getLong(columnName);
        if (resultSet.wasNull()) {
            return null;
        }
        return Long.valueOf(value);
    }

    /**
     * Return any short value.
     */
    @SuppressWarnings("UnnecessaryBoxing")
    public static Short getShortValue(ResultSet resultSet, int columnIndex) throws SQLException {
        short value = resultSet.getShort(columnIndex);
        if (resultSet.wasNull()) {
            return null;
        }
        return Short.valueOf(value);
    }

    /**
     * Return any short value.
     */
    @SuppressWarnings("UnnecessaryBoxing")
    public static Short getShortValue(ResultSet resultSet, String columnName) throws SQLException {
        short value = resultSet.getShort(columnName);
        if (resultSet.wasNull()) {
            return null;
        }
        return Short.valueOf(value);
    }

    /**
     * Is the specified exception a PostgreSQL unique violation.
     */
    public static boolean isPSQLUniqueViolation(SQLException ex) {
        return "23505".equals(Objects.requireNonNull(ex).getSQLState());
    }

    public static java.sql.Date toSqlDate(Calendar calendar) {
        if (calendar == null) {
            return null;
        }
        return new java.sql.Date(calendar.getTimeInMillis());
    }

    public static java.sql.Date toSqlDate(java.util.Date date) {
        if (date == null) {
            return null;
        }
        return new java.sql.Date(date.getTime());
    }

    public static java.sql.Timestamp toSqlTimestamp(java.util.Date date) {
        if (date == null) {
            return null;
        }
        return toSqlTimestamp(date.getTime());
    }

    public static java.sql.Timestamp toSqlTimestamp(long time) {
        return new java.sql.Timestamp(time);
    }

    public static java.sql.Timestamp toSqlTimestamp(Instant instant) {
        if (instant == null) {
            return null;
        }
        return toSqlTimestamp(instant.toEpochMilli());
    }
}
