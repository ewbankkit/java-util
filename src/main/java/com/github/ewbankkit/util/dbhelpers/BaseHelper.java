/**
 * Kit's Java Utils.
 */

package com.github.ewbankkit.util.dbhelpers;

import static com.github.ewbankkit.util.beans.BaseData.arrayIsNotEmpty;
import static com.github.ewbankkit.util.beans.BaseData.stringIsBlank;
import static com.github.ewbankkit.util.beans.BaseData.stringIsEmpty;
import static com.github.ewbankkit.util.beans.BaseData.toHexString;
import static com.github.ewbankkit.util.beans.BaseData.toIterable;
import static java.sql.Statement.EXECUTE_FAILED;
import static java.sql.Statement.NO_GENERATED_KEYS;
import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Statement.SUCCESS_NO_INFO;

import java.io.PrintWriter;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.github.ewbankkit.util.F1;
import com.github.ewbankkit.util.beans.BaseDataWithUpdateTracking;
import com.github.ewbankkit.util.beans.Pair;
import com.github.ewbankkit.util.CalendarUtil;
import com.github.ewbankkit.util.DateUtil;
import com.github.ewbankkit.util.Factories;
import com.github.ewbankkit.util.MapBuilder;
import com.github.ewbankkit.util.beans.BaseData;
import com.github.ewbankkit.util.beans.Option;
import com.github.ewbankkit.util.log.BaseLoggable;

/**
 * Base class for DB helpers.
 */
public abstract class BaseHelper extends BaseLoggable {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:40 BaseHelper.java NSI";

    protected static final ParametersSetter<String> SINGLE_STRING_VALUE_PARAMETERS_SETTER = new ParametersSetter<String>() {
        public void setParameters(PreparedStatement statement, String string) throws SQLException {
            statement.setString(1, string);
        }};

    private static final SimpleTypeValues<BigDecimal> BIG_DECIMAL_VALUES = new SimpleTypeValues<BigDecimal>() {
        public BigDecimal value(ResultSet resultSet, int columnIndex) throws SQLException {
            return resultSet.getBigDecimal(columnIndex);
        }

        public BigDecimal value(ResultSet resultSet, String columnName) throws SQLException {
            return resultSet.getBigDecimal(columnName);
        }};
    private static final SimpleTypeValues<Boolean> BOOLEAN_VALUES = new SimpleTypeValues<Boolean>() {
        public Boolean value(ResultSet resultSet, int columnIndex) throws SQLException {
            return getBooleanValue(resultSet, columnIndex);
        }

        public Boolean value(ResultSet resultSet, String columnName) throws SQLException {
            return getBooleanValue(resultSet, columnName);
        }};
    private static final SimpleTypeValues<java.util.Date> DATE_VALUES = new SimpleTypeValues<java.util.Date>() {
        public java.util.Date value(ResultSet resultSet, int columnIndex) throws SQLException {
            return resultSet.getTimestamp(columnIndex);
        }

        public java.util.Date value(ResultSet resultSet, String columnName) throws SQLException {
            return resultSet.getTimestamp(columnName);
        }};
    private static final SimpleTypeValues<Double> DOUBLE_VALUES = new SimpleTypeValues<Double>() {
        public Double value(ResultSet resultSet, int columnIndex) throws SQLException {
            return getDoubleValue(resultSet, columnIndex);
        }

        public Double value(ResultSet resultSet, String columnName) throws SQLException {
            return getDoubleValue(resultSet, columnName);
        }};
    private static final SimpleTypeValues<Float> FLOAT_VALUES = new SimpleTypeValues<Float>() {
        public Float value(ResultSet resultSet, int columnIndex) throws SQLException {
            return getFloatValue(resultSet, columnIndex);
        }

        public Float value(ResultSet resultSet, String columnName) throws SQLException {
            return getFloatValue(resultSet, columnName);
        }};
    private static final SimpleTypeValues<Integer> INTEGER_VALUES = new SimpleTypeValues<Integer>() {
        public Integer value(ResultSet resultSet, int columnIndex) throws SQLException {
            return getIntegerValue(resultSet, columnIndex);
        }

        public Integer value(ResultSet resultSet, String columnName) throws SQLException {
            return getIntegerValue(resultSet, columnName);
        }};
    private static final SimpleTypeValues<Long> LONG_VALUES = new SimpleTypeValues<Long>() {
        public Long value(ResultSet resultSet, int columnIndex) throws SQLException {
            return getLongValue(resultSet, columnIndex);
        }

        public Long value(ResultSet resultSet, String columnName) throws SQLException {
            return getLongValue(resultSet, columnName);
        }};
    private static final SimpleTypeValues<String> STRING_VALUES = new SimpleTypeValues<String>() {
        public String value(ResultSet resultSet, int columnIndex) throws SQLException {
            return resultSet.getString(columnIndex);
        }

        public String value(ResultSet resultSet, String columnName) throws SQLException {
            return resultSet.getString(columnName);
        }};

    private static final Map<Class<?>, SimpleTypeValues<?>> SIMPLE_TYPE_TO_VALUES_MAP = new MapBuilder<Class<?>, SimpleTypeValues<?>>().
            put(BigDecimal.class,     BIG_DECIMAL_VALUES).
            put(Boolean.class,        BOOLEAN_VALUES).
            put(java.util.Date.class, DATE_VALUES).
            put(Double.class,         DOUBLE_VALUES).
            put(Float.class,          FLOAT_VALUES).
            put(Integer.class,        INTEGER_VALUES).
            put(Long.class,           LONG_VALUES).
            put(String.class,         STRING_VALUES).unmodifiableMap();

    private static volatile boolean mySqlDriverLoaded;
    private static volatile boolean oracleDriverLoaded;

    private final boolean logSqlStatements;

    /**
     * Constructor.
     */
    protected BaseHelper(String logComponent) {
        this(logComponent, REGISTRAR_UTILS_LOG.checkSeverity(DEBUG));
    }

    /**
     * Constructor.
     */
    protected BaseHelper(String logComponent, boolean logSqlStatements) {
        super(logComponent);
        this.logSqlStatements = logSqlStatements;
    }

    /**
     * Constructor.
     */
    protected BaseHelper(org.apache.commons.logging.Log logger) {
        this(logger, (logger == null) ? false : logger.isDebugEnabled());
    }

    /**
     * Constructor.
     */
    protected BaseHelper(org.apache.commons.logging.Log logger, boolean logSqlStatements) {
        super(logger);
        this.logSqlStatements = logSqlStatements;
    }

    /**
     * Constructor.
     */
    protected BaseHelper(BaseLoggable baseLoggable) {
        this(baseLoggable, (baseLoggable == null) ? false : baseLoggable.isDebugEnabled());
    }

    /**
     * Constructor.
     */
    protected BaseHelper(BaseLoggable baseLoggable, boolean logSqlStatements) {
        super(baseLoggable);
        this.logSqlStatements = logSqlStatements;
    }

    /**
     * Attempt the specified operation in a restartable transaction.
     */
    public static <T> T attemptInRestartableTransaction(Connection connection, F1<Connection, T> f1, int maxAttempts) throws SQLException {
        int attempt = 0;
        while (true) {
            try {
                return f1.apply(connection);
            }
            catch (SQLException ex) {
                if (attempt < (maxAttempts - 1)) {
                    attempt++;
                    int errorCode = ex.getErrorCode();
                    // ER_LOCK_WAIT_TIMEOUT 1205
                    // "Lock wait timeout exceeded; try restarting transaction"
                    // ER_LOCK_DEADLOCK 1213
                    // "Deadlock found when trying to get lock; try restarting transaction"
                    if ((errorCode == 1205) || (errorCode == 1213)) {
                        rollback(connection);
                        continue;
                    }
                }
                throw ex;
            }
            catch (Exception ex) {
                throw new SQLException(ex.getMessage());
            }
        }
    }

    /**
     * Close a connection.
     */
    public static void close(Connection connection) {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                }
            }
            catch (SQLException ex) {}
        }
    }

    /**
     * Close connections.
     */
    public static void close(Connection... connections) {
        if (connections != null) {
            for (Connection connection : connections) {
                close(connection);
            }
        }
    }

    /**
     * Close a result set.
     */
    public static void close(ResultSet resultSet) {
        if (resultSet != null) {
            try {
                resultSet.close();
            }
            catch (SQLException ex) {}
        }
    }

    /**
     * Close result sets.
     */
    public static void close(ResultSet... resultSets) {
        if (resultSets != null) {
            for (ResultSet resultSet : resultSets) {
                close(resultSet);
            }
        }
    }

    /**
     * Close a statement.
     */
    public static void close(Statement statement) {
        if (statement != null) {
            try {
                statement.close();
            }
            catch (SQLException ex) {}
        }
    }

    /**
     * Close statements.
     */
    public static void close(Statement... statements) {
        if (statements != null) {
            for (Statement statement : statements) {
                close(statement);
            }
        }
    }

    /**
     * Close a statement and associated result set.
     */
    public static void close(Statement statement, ResultSet resultSet) {
        close(resultSet);
        close(statement);
    }

    /**
     * Make all changes made since the previous commit/rollback permanent and
     * release any database locks currently held by the connection.
     */
    public static void commit(Connection connection) {
        if (connection != null) {
            try {
                if (!connection.getAutoCommit()) {
                    connection.commit();
                }
            }
            catch (SQLException ex) {}
        }
    }

    /**
     * Return a new connection to the DEV GDB.
     */
    public static Connection createConnection(String url) throws SQLException {
        registerMySqlDriver();
        return DriverManager.getConnection(url);
    }

    /**
     * Return the current time as a SQL time stamp.
     */
    public static java.sql.Timestamp currentTimestamp() {
        return new java.sql.Timestamp(System.currentTimeMillis());
    }

    /**
     * End the current transaction.
     * On success commit the transaction, otherwise roll it back.
     */
    public static void endTransaction(Connection connection, boolean success) {
        if (success) {
            commit(connection);
        }
        else {
            rollback(connection);
        }
    }

    /**
     * Escape the specified string for the LIKE operator.
     */
    public static String escapeForLikeOperator(String s) {
        if (stringIsEmpty(s)) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (Character character : toIterable(s)) {
            char ch = character.charValue();
            switch (ch) {
            case '%':
            case '_':
                sb.append('\\');
                sb.append(ch);
                break;
            default:
                sb.append(ch);
                break;
            }
        }
        return sb.toString();
    }

    /**
     * Return any generated auto-increment ID.
     */
    public static Long getAutoIncrementId(Statement statement) throws SQLException {
        List<Long> autoIncrementIds = getAutoIncrementIds(statement);
        if (autoIncrementIds.isEmpty()) {
            return null;
        }
        return autoIncrementIds.get(0);
    }

    /**
     * Return any generated auto-increment IDs.
     */
    public static List<Long> getAutoIncrementIds(Statement statement) throws SQLException {
        ResultSet resultSet = null;
        try {
            resultSet = statement.getGeneratedKeys();
            return newList(resultSet, AutoIncrementIdFactory.INSTANCE);
        }
        finally {
            close(resultSet);
        }
    }

    /**
     * Return any boolean value.
     */
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
        return BaseData.getEnumValue(classOfT, s);
    }

    /**
     * Return any enum value.
     */
    public static <T extends Enum<T>> T getEnumValue(ResultSet resultSet, String columnName, Class<T> classOfT) throws SQLException {
        String s = resultSet.getString(columnName);
        if (s == null) {
            return null;
        }
        return BaseData.getEnumValue(classOfT, s);
    }

    /**
     * Return any float value.
     */
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
    public static Float getFloatValue(ResultSet resultSet, String columnName) throws SQLException {
        float value = resultSet.getFloat(columnName);
        if (resultSet.wasNull()) {
            return null;
        }
        return Float.valueOf(value);
    }

    /**
     * Return the SQL statement snippet for IN clause values.
     */
    public static CharSequence getInClauseValuesSnippet(int count) {
        if (count < 0) {
            throw new IllegalArgumentException();
        }
        if (count == 0) {
            // Special case.
            return "?";
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append("?, ");
        }
        // Remove trailing ", ".
        int length = sb.length();
        sb.setLength(length - 2);

        return sb;
    }

    /**
     * Return any integer value.
     */
    public static Integer getIntegerValue(ResultSet resultSet, int columnIndex) throws SQLException {
        int value = resultSet.getInt(columnIndex);
        if (resultSet.wasNull()) {
            return null;
        }
        return Integer.valueOf(value);
    }

    /**
     * Return any integer value.
     */
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
    public static Long getLongValue(ResultSet resultSet, String columnName) throws SQLException {
        long value = resultSet.getLong(columnName);
        if (resultSet.wasNull()) {
            return null;
        }
        return Long.valueOf(value);
    }

    /**
     * Get the MySQL MD5 checksum of the specified string.
     */
    public static String getMD5Checksum(String s) throws SQLException {
        return getChecksum(s, "MD5");
    }

    /**
     * Get the MySQL SHA-1 checksum of the specified string.
     */
    public static String getSHA1Checksum(String s) throws SQLException {
        return getChecksum(s, "SHA-1");
    }

    /**
     * Return any short value.
     */
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
    public static Short getShortValue(ResultSet resultSet, String columnName) throws SQLException {
        short value = resultSet.getShort(columnName);
        if (resultSet.wasNull()) {
            return null;
        }
        return Short.valueOf(value);
    }

    /**
     * Return the numbers of rows a SELECT statement including a LIMIT clause would
     * have returned if the LIMIT clause was not present. The SELECT statement must
     * include the SQL_CALC_FOUND_ROWS option and this method must be called
     * immediately after the SELECT statement and on the same connection.
     */
    public static Long getUnlimitedRowCount(Statement statementWithLimit) throws SQLException {
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            statement = statementWithLimit.getConnection().createStatement();
            resultSet = statement.executeQuery("SELECT FOUND_ROWS();");
            return singleValue(resultSet, CountFactory.INSTANCE);
        }
        finally {
            close(statement, resultSet);
        }
    }

    /**
     * Return the SQL statement snippet for an UPDATE values guard.
     */
    public static CharSequence getUpdateValuesGuardSnippet(Iterable<String> columnNames) {
        StringBuilder sb = new StringBuilder();
        for (String columnName : columnNames) {
            if (sb.length() == 0) {
                sb.append("(NOT (");
            }
            else {
                sb.append(" AND ");
            }
            sb.append(columnName).append(" <=> ?");
        }
        if (sb.length() > 0) {
            sb.append("))");
        }
        return sb;
    }

    /**
     * Return the SQL statement snippet for an UPDATE values guard.
     */
    public static CharSequence getUpdateValuesGuardSnippet(String[] columnNames) {
        return getUpdateValuesGuardSnippet(toIterable(columnNames));
    }

    /**
     * Return the SQL statement snippet for UPDATE values.
     */
    public static CharSequence getUpdateValuesSnippet(Iterable<String> columnNames) {
        StringBuilder sb = new StringBuilder();
        for (String columnName : columnNames) {
            if (sb.length() > 0) {
                sb.append(' ');
            }
            sb.append(columnName).append(" = ?,");
        }
        // Remove any trailing ','.
        int length = sb.length();
        if (length > 0) {
            sb.setLength(length - 1);
        }
        return sb;
    }

    /**
     * Return the SQL statement snippet for UPDATE values.
     */
    public static CharSequence getUpdateValuesSnippet(String[] columnNames) {
        return getUpdateValuesSnippet(toIterable(columnNames));
    }

    /**
     * Ping the specified connection.
     * Return whether or not the connection is alive.
     */
    public static boolean ping(Connection connection) {
        try {
            Statement statement = null;
            ResultSet resultSet = null;
            try {
                statement = connection.createStatement();
                resultSet = statement.executeQuery("SELECT 1 FROM DUAL;");
                return singleValue(resultSet, 1, 0) == 1;
            }
            finally {
                close(statement, resultSet);
            }
        }
        catch (SQLException ex) {
            return false;
        }
    }

    /**
     * Register the MySQL JDBC driver.
     */
    public static void registerMySqlDriver() throws SQLException {
        // Don't worry about synchronizing here.
        // The worst that could happen is the driver is loaded more than once.
        if (!mySqlDriverLoaded) {
            registerJdbcDriver("com.mysql.jdbc.Driver");
            mySqlDriverLoaded = true;
        }
    }

    /**
     * Register the Oracle JDBC driver.
     */
    public static void registerOracleDriver() throws SQLException {
        // Don't worry about synchronizing here.
        // The worst that could happen is the driver is loaded more than once.
        if (!oracleDriverLoaded) {
            registerJdbcDriver("oracle.jdbc.driver.OracleDriver");
            oracleDriverLoaded = true;
        }
    }

    /**
     * Undo all changes made in the current transaction and
     * release any database locks currently held by the connection.
     */
    public static void rollback(Connection connection) {
        if (connection != null) {
            try {
                if (!connection.getAutoCommit()) {
                    connection.rollback();
                }
            }
            catch (SQLException ex) {}
        }
    }

    /**
     * Set IN clause parameters.
     * Return the next parameter index.
     */
    public static int setInClauseParameters(PreparedStatement statement, int initialParameterIndex, Iterable<?> values) throws SQLException {
        int parameterIndex = setParameters(statement, initialParameterIndex, values);
        if (parameterIndex == initialParameterIndex) {
            // Special case.
            statement.setNull(initialParameterIndex, Types.NULL);
            parameterIndex++;
        }
        return parameterIndex;
    }

    /**
     * Set IN clause parameters.
     * Return the next parameter index.
     */
    public static int setInClauseParameters(PreparedStatement statement, int initialParameterIndex, int[] values) throws SQLException {
        return setInClauseParameters(statement, initialParameterIndex, toIterable(values));
    }

    /**
     * Set IN clause parameters.
     * Return the next parameter index.
     */
    public static int setInClauseParameters(PreparedStatement statement, int initialParameterIndex, long[] values) throws SQLException {
        return setInClauseParameters(statement, initialParameterIndex, toIterable(values));
    }

    /**
     * Set IN clause parameters.
     * Return the next parameter index.
     */
    public static int setInClauseParameters(PreparedStatement statement, int initialParameterIndex, Object[] values) throws SQLException {
        return setInClauseParameters(statement, initialParameterIndex, toIterable(values));
    }

    /**
     * Set UPDATE parameters.
     * Return the next parameter index.
     */
    public static int setUpdateParameters(PreparedStatement statement, int initialParameterIndex, Iterable<Object> values) throws SQLException {
        return setParameters(statement, initialParameterIndex, values);
    }

    /**
     * Set UPDATE parameters.
     * Return the next parameter index.
     */
    public static int setUpdateParameters(PreparedStatement statement, int initialParameterIndex, Object[] values) throws SQLException {
        return setUpdateParameters(statement, initialParameterIndex, toIterable(values));
    }

    public static BigDecimal toBigDecimal(double d, int scale) {
        return BaseData.toBigDecimal(d, scale);
    }

    public static BigDecimal toBigDecimal(Number number, int scale) {
        return BaseData.toBigDecimal(number, scale);
    }

    public static Calendar toCalendar(java.util.Date date) {
        return CalendarUtil.dateToCalendar(date);
    }

    public static Double toDouble(Number number) {
        return BaseData.toDouble(number);
    }

    public static Integer toInteger(long l) {
        return BaseData.toInteger(l);
    }

    public static Integer toInteger(Number number) {
        return BaseData.toInteger(number);
    }

    public static Long toLong(Number number) {
        return BaseData.toLong(number);
    }

    public static java.sql.Date toSqlDate(Calendar calendar) {
        return CalendarUtil.calendarToSqlDate(calendar);
    }

    public static java.sql.Date toSqlDate(java.util.Date date) {
        return DateUtil.toSqlDate(date);
    }

    public static java.sql.Timestamp toSqlTimestamp(java.util.Date date) {
        return DateUtil.toSqlTimestamp(date);
    }

    public static java.sql.Timestamp toSqlTimestamp(long time) {
        return DateUtil.toSqlTimestamp(time);
    }

    public static java.sql.Timestamp toSqlTimestamp(Long time) {
        return (time == null) ? null : toSqlTimestamp(time.longValue());
    }

    /**
     * Return whether or not a count value is greater than zero.
     */
    protected static boolean countGreaterThanZero(ResultSet resultSet) throws SQLException {
        return greaterThanZero(singleValue(resultSet, CountFactory.INSTANCE));
    }

    /**
     * Return whether or not a count value is greater than zero for the given parameters.
     */
    protected boolean countGreaterThanZeroFromParameters(String logTag, Connection connection, String sql, Object... parameters) throws SQLException {
        return greaterThanZero(singleValueFromParameters(logTag, connection, sql, CountFactory.INSTANCE, parameters));
    }

    /**
     * Return whether or not a count value is greater than zero for the given product instance ID.
     */
    protected boolean countGreaterThanZeroFromProdInstId(String logTag, Connection connection, String prodInstId, String sql) throws SQLException {
        return greaterThanZero(singleValueFromProdInstId(logTag, connection, prodInstId, sql, CountFactory.INSTANCE));
    }

    /**
     * Return whether or not a count value is greater than zero for the given single parameter.
     */
    protected <T> boolean countGreaterThanZeroFromSingleParameter(String logTag, Connection connection, T parameter, String sql) throws SQLException {
        return greaterThanZero(singleValueFromSingleParameter(logTag, connection, parameter, sql, CountFactory.INSTANCE));
    }

    /**
     * Delete data for the given parameters.
     * Return the number of rows updated.
     */
    protected int deleteForParameters(String logTag, Connection connection, String sql, Object... parameters) throws SQLException {
        return updateForParameters(logTag, connection, sql, parameters);
    }

    /**
     * Delete data for the given product instance ID.
     * Return the number of rows deleted.
     */
    protected int deleteForProdInstId(String logTag, Connection connection, String prodInstId, String sql) throws SQLException {
        return deleteForSingleParameter(logTag, connection, prodInstId, sql);
    }

    /**
     * Delete data for the given single parameter.
     * Return the number of rows deleted.
     */
    protected <T> int deleteForSingleParameter(String logTag, Connection connection, T parameter, String sql) throws SQLException {
        return deleteForParameters(logTag, connection, sql, parameter);
    }

    /**
     * Return the first value.
     */
    protected static <T> T firstValue(ResultSet resultSet, Factory<T> factory) throws SQLException {
        return valueOrNull(firstValueMaybe(resultSet, factory));
    }

    /**
     * Return the first value.
     */
    protected static boolean firstValue(ResultSet resultSet, int columnIndex, boolean defaultValue) throws SQLException {
        return firstValue(resultSet, columnIndex, Boolean.class, Boolean.valueOf(defaultValue)).booleanValue();
    }

    /**
     * Return the first value.
     */
    protected static <T> T firstValue(ResultSet resultSet, int columnIndex, Class<T> clazz) throws SQLException {
        return firstValue(resultSet, columnIndex, clazz, null);
    }

    /**
     * Return the first value.
     */
    protected static <T> T firstValue(ResultSet resultSet, int columnIndex, Class<T> clazz, T defaultValue) throws SQLException {
        return firstValue(resultSet, new ColumnIdentifier(columnIndex), clazz, defaultValue);
    }

    /**
     * Return the first value.
     */
    protected static double firstValue(ResultSet resultSet, int columnIndex, double defaultValue) throws SQLException {
        return firstValue(resultSet, columnIndex, Double.class, Double.valueOf(defaultValue)).doubleValue();
    }

    /**
     * Return the first value.
     */
    protected static float firstValue(ResultSet resultSet, int columnIndex, float defaultValue) throws SQLException {
        return firstValue(resultSet, columnIndex, Float.class, Float.valueOf(defaultValue)).floatValue();
    }

    /**
     * Return the first value.
     */
    protected static int firstValue(ResultSet resultSet, int columnIndex, int defaultValue) throws SQLException {
        return firstValue(resultSet, columnIndex, Integer.class, Integer.valueOf(defaultValue)).intValue();
    }

    /**
     * Return the first value.
     */
    protected static long firstValue(ResultSet resultSet, int columnIndex, long defaultValue) throws SQLException {
        return firstValue(resultSet, columnIndex, Long.class, Long.valueOf(defaultValue)).longValue();
    }

    /**
     * Return the first value.
     */
    protected static boolean firstValue(ResultSet resultSet, String columnName, boolean defaultValue) throws SQLException {
        return firstValue(resultSet, columnName, Boolean.class, Boolean.valueOf(defaultValue)).booleanValue();
    }

    /**
     * Return the first value.
     */
    protected static <T> T firstValue(ResultSet resultSet, String columnName, Class<T> clazz) throws SQLException {
        return firstValue(resultSet, columnName, clazz, null);
    }

    /**
     * Return the first value.
     */
    protected static <T> T firstValue(ResultSet resultSet, String columnName, Class<T> clazz, T defaultValue) throws SQLException {
        return firstValue(resultSet, new ColumnIdentifier(columnName), clazz, defaultValue);
    }

    /**
     * Return the first value.
     */
    protected static double firstValue(ResultSet resultSet, String columnName, double defaultValue) throws SQLException {
        return firstValue(resultSet, columnName, Double.class, Double.valueOf(defaultValue)).doubleValue();
    }

    /**
     * Return the first value.
     */
    protected static float firstValue(ResultSet resultSet, String columnName, float defaultValue) throws SQLException {
        return firstValue(resultSet, columnName, Float.class, Float.valueOf(defaultValue)).floatValue();
    }

    /**
     * Return the first value.
     */
    protected static int firstValue(ResultSet resultSet, String columnName, int defaultValue) throws SQLException {
        return firstValue(resultSet, columnName, Integer.class, Integer.valueOf(defaultValue)).intValue();
    }

    /**
     * Return the first value.
     */
    protected static long firstValue(ResultSet resultSet, String columnName, long defaultValue) throws SQLException {
        return firstValue(resultSet, columnName, Long.class, Long.valueOf(defaultValue)).longValue();
    }

    /**
     * Return data for the given parameters.
     */
    protected <T> T firstValueFromParameters(String logTag, Connection connection, String sql, final Factory<T> factory, Object... parameters) throws SQLException {
        return fromParameters(logTag, connection, sql, new F1<ResultSet, T>() {
            @Override
            public T apply(ResultSet resultSet) throws Exception {
                return firstValue(resultSet, factory);
            }}, parameters);
    }

    /**
     * Return data for the given product instance ID.
     */
    protected <T> T firstValueFromProdInstId(String logTag, Connection connection, String prodInstId, String sql, final Factory<T> factory) throws SQLException {
        return firstValueFromSingleParameter(logTag, connection, prodInstId, sql, factory);
    }

    /**
     * Return data for the given single parameter.
     */
    protected <T, U> U firstValueFromSingleParameter(String logTag, Connection connection, T parameter, String sql, final Factory<U> factory) throws SQLException {
        return firstValueFromParameters(logTag, connection, sql, factory, parameter);
    }

    /**
     * Return the first value, maybe.
     */
    protected static <T> Option<T> firstValueMaybe(ResultSet resultSet, Factory<T> factory) throws SQLException {
        return firstValueStrategy(factory).value(resultSet);
    }

    protected <T> int insertAll(String logTag, Connection connection, String sql, Iterable<T> ts, ParametersSetter<T> parametersSetter) throws SQLException {
        return insertAll(logTag, connection, sql, ts, parametersSetter, false).getFirst().intValue();
    }

    protected <T> List<Long> insertAllReturningIds(String logTag, Connection connection, String sql, Iterable<T> ts, ParametersSetter<T> parametersSetter) throws SQLException {
        return insertAll(logTag, connection, sql, ts, parametersSetter, true).getSecond();
    }

    /**
     * Insert data for the given parameters.
     * Return any auto-generated key.
     */
    protected Long insertForParameters(String logTag, Connection connection, String sql, Object... parameters) throws SQLException {
        return forParameters(logTag, connection, sql, true, new F1<PreparedStatement, Long>() {
            @Override
            public Long apply(PreparedStatement statement) throws Exception {
                statement.executeUpdate();
                return getAutoIncrementId(statement);
            }}, parameters);
    }

    /**
     * Insert data for the given parameters.
     * Return any auto-generated keys.
     */
    protected <T> List<Long> insertAllForParameters(String logTag, Connection connection, String sql, Iterable<T> ts, final F1<T, Object[]> f1) throws SQLException {
        return insertAllReturningIds(logTag, connection, sql, ts, new ParametersSetter<T>() {
            public void setParameters(PreparedStatement statement, T t) throws SQLException {
                try {
                    BaseHelper.setParameters(statement, 1, f1.apply(t));
                }
                catch (Exception ex) {
                    throw new SQLException(ex.getMessage());
                }
            }
        });
    }

    /**
     * Log a SQL exception.
     */
    protected void logSqlException(String logTag, SQLException sqlException) {
        logError(logTag, sqlException);
    }

    /**
     * Log a SQL statement.
     */
    protected void logSqlStatement(String logTag, Statement statement) {
        if (logSqlStatements) {
            logInfo(logTag, statement.toString());
        }
    }

    /**
     * Return a new list with values from the result set.
     */
    protected static <T> List<T> newList(ResultSet resultSet, Factory<T> factory) throws SQLException {
        com.github.ewbankkit.util.Factory<List<T>> listFactory = Factories.newArrayListFactory();
        return newList(resultSet, factory, listFactory);
    }

    /**
     * Return a new list with values from the result set.
     */
    protected static <T> List<T> newList(ResultSet resultSet, Factory<T> factory, com.github.ewbankkit.util.Factory<List<T>> listFactory) throws SQLException {
        if ((resultSet == null) || !resultSet.next()) {
            return Collections.emptyList();
        }

        List<T> list = listFactory.newInstance();
        do {
            list.add(newInstance(resultSet, factory));
        } while (resultSet.next());

        return Collections.unmodifiableList(list);
    }

    /**
     * Return data for the given parameters.
     */
    protected <T> List<T> newListFromParameters(String logTag, Connection connection, String sql, final Factory<T> factory, Object... parameters) throws SQLException {
        return fromParameters(logTag, connection, sql, new F1<ResultSet, List<T>>() {
            @Override
            public List<T> apply(ResultSet resultSet) throws Exception {
                return newList(resultSet, factory);
            }}, parameters);
    }

    /**
     * Return data for the given product instance ID.
     */
    protected <T> List<T> newListFromProdInstId(String logTag, Connection connection, String prodInstId, String sql, final Factory<T> factory) throws SQLException {
        return newListFromSingleParameter(logTag, connection, prodInstId, sql, factory);
    }

    /**
     * Return data for the given single parameter.
     */
    protected <T, U> List<U> newListFromSingleParameter(String logTag, Connection connection, T parameter, String sql, final Factory<U> factory) throws SQLException {
        return newListFromParameters(logTag, connection, sql, factory, parameter);
    }

    /**
     * Return a new map with values from the result set.
     */
    protected static <K, V> Map<K, V> newMap(ResultSet resultSet, Factory<Pair<K, V>> factory) throws SQLException {
        com.github.ewbankkit.util.Factory<Map<K, V>> mapFactory = Factories.newHashMapFactory();
        return newMap(resultSet, factory, mapFactory);
    }

    /**
     * Return a new map with values from the result set.
     */
    protected static <K, V> Map<K, V> newMap(ResultSet resultSet, Factory<Pair<K, V>> factory, com.github.ewbankkit.util.Factory<Map<K, V>> mapFactory) throws SQLException {
        if ((resultSet == null) || !resultSet.next()) {
            return Collections.emptyMap();
        }

        Map<K, V> map = mapFactory.newInstance();
        do {
            Pair<K, V> pair = newInstance(resultSet, factory);
            map.put(pair.getFirst(), pair.getSecond());
        } while (resultSet.next());

        return Collections.unmodifiableMap(map);
    }

    /**
     * Return data for the given parameters.
     */
    protected <K, V> Map<K, V> newMapFromParameters(String logTag, Connection connection, String sql, final Factory<Pair<K, V>> factory, Object... parameters) throws SQLException {
        return fromParameters(logTag, connection, sql, new F1<ResultSet, Map<K, V>>() {
            @Override
            public Map<K, V> apply(ResultSet resultSet) throws Exception {
                return newMap(resultSet, factory);
            }}, parameters);
    }

    /**
     * Return data for the given product instance ID.
     */
    protected <K, V> Map<K, V> newMapFromProdInstId(String logTag, Connection connection, String prodInstId, String sql, final Factory<Pair<K, V>> factory) throws SQLException {
        return newMapFromSingleParameter(logTag, connection, prodInstId, sql, factory);
    }

    /**
     * Return data for the given single parameter.
     */
    protected <T, K, V> Map<K, V> newMapFromSingleParameter(String logTag, Connection connection, T parameter, String sql, final Factory<Pair<K, V>> factory) throws SQLException {
        return newMapFromParameters(logTag, connection, sql, factory, parameter);
    }

    /**
     * Return a new map of lists with values from the result set.
     */
    protected static <K, V> Map<K, List<V>> newMapOfLists(ResultSet resultSet, Factory<Pair<K, V>> factory) throws SQLException {
        com.github.ewbankkit.util.Factory<Map<K, List<V>>> mapFactory = Factories.newHashMapFactory();
        com.github.ewbankkit.util.Factory<List<V>> listFactory = Factories.newArrayListFactory();
        return newMapOfLists(resultSet, factory, mapFactory, listFactory);
    }

    /**
     * Return a new map of lists with values from the result set.
     */
    protected static <K, V> Map<K, List<V>> newMapOfLists(ResultSet resultSet, Factory<Pair<K, V>> factory, com.github.ewbankkit.util.Factory<Map<K, List<V>>> mapFactory, com.github.ewbankkit.util.Factory<List<V>> listFactory) throws SQLException {
        if ((resultSet == null) || !resultSet.next()) {
            return Collections.emptyMap();
        }

        Map<K, List<V>> map = mapFactory.newInstance();
        do {
            Pair<K, V> pair = newInstance(resultSet, factory);
            K key = pair.getFirst();
            V value = pair.getSecond();
            List<V> list = map.get(key);
            if (list == null) {
                list = listFactory.newInstance();
                map.put(key, list);
            }
            list.add(value);
        } while (resultSet.next());

        return Collections.unmodifiableMap(map);
    }

    /**
     * Return data for the given parameters.
     */
    protected <K, V> Map<K, List<V>> newMapOfListsFromParameters(String logTag, Connection connection, String sql, final Factory<Pair<K, V>> factory, Object... parameters) throws SQLException {
        return fromParameters(logTag, connection, sql, new F1<ResultSet, Map<K, List<V>>>() {
            @Override
            public Map<K, List<V>> apply(ResultSet resultSet) throws Exception {
                return newMapOfLists(resultSet, factory);
            }}, parameters);
    }

    /**
     * Return data for the given product instance ID.
     */
    protected <K, V> Map<K, List<V>> newMapOfListsFromProdInstId(String logTag, Connection connection, String prodInstId, String sql, final Factory<Pair<K, V>> factory) throws SQLException {
        return newMapOfListsFromSingleParameter(logTag, connection, prodInstId, sql, factory);
    }

    /**
     * Return data for the given single parameter.
     */
    protected <T, K, V> Map<K, List<V>> newMapOfListsFromSingleParameter(String logTag, Connection connection, T parameter, String sql, final Factory<Pair<K, V>> factory) throws SQLException {
        return newMapOfListsFromParameters(logTag, connection, sql, factory, parameter);
    }

    /**
     * Set parameters.
     * Return the next parameter index.
     */
    protected static int setParameters(PreparedStatement statement, int initialParameterIndex, Iterable<?> values) throws SQLException {
        int parameterIndex = initialParameterIndex;
        for (Object value : values) {
            if (value instanceof InClauseParameters) {
                parameterIndex = setInClauseParameters(statement, parameterIndex, ((InClauseParameters)value).parameters);
            }
            else if (value instanceof UpdateParameters) {
                parameterIndex = ((UpdateParameters)value).object.setUpdateParameters(statement, parameterIndex);
            }
            else {
                if (value instanceof Enum<?>) {
                    value = value.toString();
                }
                statement.setObject(parameterIndex++, value);
            }
        }
        return parameterIndex;
    }

    /**
     * Set parameters.
     * Return the next parameter index.
     */
    protected static int setParameters(PreparedStatement statement, int initialParameterIndex, Object[] values) throws SQLException {
        return setParameters(statement, initialParameterIndex, toIterable(values));
    }

    /**
     * Return the single value.
     */
    protected static <T> T singleValue(ResultSet resultSet, Factory<T> factory) throws SQLException {
        return valueOrNull(singleValueMaybe(resultSet, factory));
    }

    /**
     * Return the first value.
     */
    protected static boolean singleValue(ResultSet resultSet, int columnIndex, boolean defaultValue) throws SQLException {
        return singleValue(resultSet, columnIndex, Boolean.class, Boolean.valueOf(defaultValue)).booleanValue();
    }

    /**
     * Return the single value.
     */
    protected static <T> T singleValue(ResultSet resultSet, int columnIndex, Class<T> clazz) throws SQLException {
        return singleValue(resultSet, columnIndex, clazz, null);
    }

    /**
     * Return the single value.
     */
    protected static <T> T singleValue(ResultSet resultSet, int columnIndex, Class<T> clazz, T defaultValue) throws SQLException {
        return singleValue(resultSet, new ColumnIdentifier(columnIndex), clazz, defaultValue);
    }

    /**
     * Return the first value.
     */
    protected static double singleValue(ResultSet resultSet, int columnIndex, double defaultValue) throws SQLException {
        return singleValue(resultSet, columnIndex, Double.class, Double.valueOf(defaultValue)).doubleValue();
    }

    /**
     * Return the first value.
     */
    protected static float singleValue(ResultSet resultSet, int columnIndex, float defaultValue) throws SQLException {
        return singleValue(resultSet, columnIndex, Float.class, Float.valueOf(defaultValue)).floatValue();
    }

    /**
     * Return the first value.
     */
    protected static int singleValue(ResultSet resultSet, int columnIndex, int defaultValue) throws SQLException {
        return singleValue(resultSet, columnIndex, Integer.class, Integer.valueOf(defaultValue)).intValue();
    }

    /**
     * Return the first value.
     */
    protected static long singleValue(ResultSet resultSet, int columnIndex, long defaultValue) throws SQLException {
        return singleValue(resultSet, columnIndex, Long.class, Long.valueOf(defaultValue)).longValue();
    }

    /**
     * Return the first value.
     */
    protected static boolean singleValue(ResultSet resultSet, String columnName, boolean defaultValue) throws SQLException {
        return singleValue(resultSet, columnName, Boolean.class, Boolean.valueOf(defaultValue)).booleanValue();
    }

    /**
     * Return the single value.
     */
    protected static <T> T singleValue(ResultSet resultSet, String columnName, Class<T> clazz) throws SQLException {
        return singleValue(resultSet, columnName, clazz, null);
    }

    /**
     * Return the single value.
     */
    protected static <T> T singleValue(ResultSet resultSet, String columnName, Class<T> clazz, T defaultValue) throws SQLException {
        return singleValue(resultSet, new ColumnIdentifier(columnName), clazz, defaultValue);
    }

    /**
     * Return the first value.
     */
    protected static double singleValue(ResultSet resultSet, String columnName, double defaultValue) throws SQLException {
        return singleValue(resultSet, columnName, Double.class, Double.valueOf(defaultValue)).doubleValue();
    }

    /**
     * Return the first value.
     */
    protected static float singleValue(ResultSet resultSet, String columnName, float defaultValue) throws SQLException {
        return singleValue(resultSet, columnName, Float.class, Float.valueOf(defaultValue)).floatValue();
    }

    /**
     * Return the first value.
     */
    protected static int singleValue(ResultSet resultSet, String columnName, int defaultValue) throws SQLException {
        return singleValue(resultSet, columnName, Integer.class, Integer.valueOf(defaultValue)).intValue();
    }

    /**
     * Return the first value.
     */
    protected static long singleValue(ResultSet resultSet, String columnName, long defaultValue) throws SQLException {
        return singleValue(resultSet, columnName, Long.class, Long.valueOf(defaultValue)).longValue();
    }

    /**
     * Return data for the given parameters.
     */
    protected <T> T singleValueFromParameters(String logTag, Connection connection, String sql, final Factory<T> factory, Object... parameters) throws SQLException {
        return fromParameters(logTag, connection, sql, new F1<ResultSet, T>() {
            @Override
            public T apply(ResultSet resultSet) throws Exception {
                return singleValue(resultSet, factory);
            }}, parameters);
    }

    /**
     * Return data for the given product instance ID.
     */
    protected <T> T singleValueFromProdInstId(String logTag, Connection connection, String prodInstId, String sql, final Factory<T> factory) throws SQLException {
        return singleValueFromSingleParameter(logTag, connection, prodInstId, sql, factory);
    }

    /**
     * Return data for the given single parameter.
     */
    protected <T, U> U singleValueFromSingleParameter(String logTag, Connection connection, T parameter, String sql, final Factory<U> factory) throws SQLException {
        return singleValueFromParameters(logTag, connection, sql, factory, parameter);
    }

    /**
     * Return the single value.
     */
    protected static <T> Option<T> singleValueMaybe(ResultSet resultSet, Factory<T> factory) throws SQLException {
        return singleValueStrategy(factory).value(resultSet);
    }

    /**
     * Update data for the given parameters.
     * Return the number of rows updated.
     */
    protected int updateForParameters(String logTag, Connection connection, String sql, Object... parameters) throws SQLException {
        return forParameters(logTag, connection, sql, false, new F1<PreparedStatement, Integer>() {
            @Override
            public Integer apply(PreparedStatement statement) throws Exception {
                return Integer.valueOf(statement.executeUpdate());
            }}, parameters).intValue();
    }

    /**
     * Return the first non-null value or null if there are no non-null values.
     */
    private static <T> T coalesce(T t1, T t2) {
        return (t1 == null) ? t2 : t1;
    }

    /**
     * Return the first value.
     */
    private static <T> T firstValue(ResultSet resultSet, ColumnIdentifier columnIdentifier, Class<T> clazz, T defaultValue) throws SQLException {
        @SuppressWarnings("unchecked")
        SimpleTypeValues<T> simpleTypeValues = (SimpleTypeValues<T>)SIMPLE_TYPE_TO_VALUES_MAP.get(clazz);
        if (simpleTypeValues == null) {
            throw new IllegalArgumentException();
        }
        return coalesce(valueOrNull(firstValueMaybe(resultSet, new BaseSimpleTypeFactory<T>(columnIdentifier, simpleTypeValues) {})), defaultValue);
    }

    /**
     * Return a value getting strategyValue getting strategy that returns the first value.
     */
    private static <T> ValueStrategy<T> firstValueStrategy(final Factory<T> factory) {
        return new ValueStrategy<T>() {
            public Option<T> value(ResultSet resultSet) throws SQLException {
                if ((resultSet == null) || !resultSet.next()) {
                    return Option.none();
                }
                return Option.some(newInstance(resultSet, factory));
            }};
    }

    /**
     * Execute the specified function for the specified parameters.
     */
    private <T> T forParameters(String logTag, Connection connection, String sql, boolean returnAutoGeneratedKeys, F1<PreparedStatement, T> f1, Object... parameters) throws SQLException {
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(sql, returnAutoGeneratedKeys ? RETURN_GENERATED_KEYS : NO_GENERATED_KEYS);
            setParameters(statement, 1, parameters);
            logSqlStatement(logTag, statement);
            return f1.apply(statement);
        }
        catch (SQLException ex) {
            throw ex;
        }
        catch (Exception ex) {
            throw new SQLException(ex.getMessage());
        }
        finally {
            close(statement);
        }
    }

    /**
     * Return data for the given parameters.
     */
    private <T> T fromParameters(String logTag, Connection connection, String sql, final F1<ResultSet, T> f1, Object... parameters) throws SQLException {
        return forParameters(logTag, connection, sql, false, new F1<PreparedStatement, T>() {
            @Override
            public T apply(PreparedStatement statement) throws Exception {
                ResultSet resultSet = null;
                try {
                    resultSet = statement.executeQuery();
                    return f1.apply(resultSet);
                }
                finally {
                    close(resultSet);
                }
            }}, parameters);
    }

    private static String getChecksum(String s, String algorithm) throws SQLException {
        if (s == null) {
            return null;
        }
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
            messageDigest.reset();
            messageDigest.update(s.getBytes());
            return toHexString(messageDigest.digest());
        }
        catch (NoSuchAlgorithmException ex) {
            throw new SQLException(ex.toString());
        }
    }

    private static boolean greaterThanZero(Number number) {
        return (number != null) && (number.longValue() > 0L);
    }

    private <T> Pair<Integer, List<Long>> insertAll(String logTag, Connection connection, String sql, Iterable<T> ts, ParametersSetter<T> parametersSetter, boolean returnAutoGeneratedKeys) throws SQLException {
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(sql, returnAutoGeneratedKeys ? RETURN_GENERATED_KEYS : NO_GENERATED_KEYS);
            for (T t : ts) {
                statement.clearParameters();
                parametersSetter.setParameters(statement, t);
                logSqlStatement(logTag, statement);
                statement.addBatch();
            }
            int[] updateCounts = statement.executeBatch();

            int rowsInserted = 0;
            if (arrayIsNotEmpty(updateCounts)) {
                for (int updateCount : updateCounts) {
                    switch (updateCount) {
                    case SUCCESS_NO_INFO: break;
                    case EXECUTE_FAILED: break;
                    default: rowsInserted += updateCount; break;
                    }
                }
            }
            return returnAutoGeneratedKeys ?
                        Pair.from(Integer.valueOf(rowsInserted), getAutoIncrementIds(statement)) :
                        Pair.<Integer, List<Long>>from(Integer.valueOf(rowsInserted), null);
        }
        finally {
            close(statement);
        }
    }

    private static <T> T newInstance(ResultSet resultSet, Factory<T> factory) throws SQLException {
        T t = factory.newInstance(resultSet);
        if (t instanceof BaseDataWithUpdateTracking) {
            ((BaseDataWithUpdateTracking)t).clearTrackedUpdates();
        }
        return t;
    }

    private static void registerJdbcDriver(String className) throws SQLException {
        try {
            Class.forName(className);
        }
        catch (Throwable t) {
            PrintWriter logWriter = DriverManager.getLogWriter();
            if (logWriter != null) {
                t.printStackTrace(logWriter);
            }
            throw new SQLException(t.toString());
        }
    }

    /**
     * Return the single value.
     */
    private static <T> T singleValue(ResultSet resultSet, ColumnIdentifier columnIdentifier, Class<T> clazz, T defaultValue) throws SQLException {
        @SuppressWarnings("unchecked")
        SimpleTypeValues<T> simpleTypeValues = (SimpleTypeValues<T>)SIMPLE_TYPE_TO_VALUES_MAP.get(clazz);
        if (simpleTypeValues == null) {
            throw new IllegalArgumentException();
        }
        return coalesce(valueOrNull(singleValueMaybe(resultSet, new BaseSimpleTypeFactory<T>(columnIdentifier, simpleTypeValues) {})), defaultValue);
    }

    /**
     * Return a value getting strategyValue getting strategy that returns the first value
     * and throws an exception if there is more than value.
     */
    private static <T> ValueStrategy<T> singleValueStrategy(final Factory<T> factory) {
        return new ValueStrategy<T>() {
            public Option<T> value(ResultSet resultSet) throws SQLException {
                List<T> list = newList(resultSet, factory);
                int size = list.size();
                switch (size) {
                case 0:
                    return Option.none();
                case 1:
                    return Option.some(list.get(0));
                default:
                    throw new SQLException(String.format("%1$s returned %2$d rows", String.valueOf(resultSet.getStatement()), size));
                }
            }};
    }

    /**
     * Return the value of an optional value or null if it has none.
     */
    private static <T> T valueOrNull(Option<T> option) {
        return option.isNone() ? null : option.getValue();
    }

    /**
     * Factory interface.
     */
    protected static interface Factory<T> {
        /**
         * Return a new instance with values from the result set.
         */
        public abstract T newInstance(ResultSet resultSet) throws SQLException;
    }

    /**
     * Parameters setter interface.
     */
    protected static interface ParametersSetter<T> {
        /**
         * Set statement parameters.
         */
        public abstract void setParameters(PreparedStatement statement, T t) throws SQLException;
    }

    /**
     * Factory class used to create a BigDecimal from a result set.
     */
    protected static abstract class BigDecimalFactory extends BaseSimpleTypeFactory<BigDecimal> {
        /**
         * Constructor.
         */
        protected BigDecimalFactory(int columnIndex) {
            super(columnIndex, BIG_DECIMAL_VALUES);
        }

        /**
         * Constructor.
         */
        protected BigDecimalFactory(String columnName) {
            super(columnName, BIG_DECIMAL_VALUES);
        }
    }

    /**
     * Factory class used to create a boolean from a result set.
     */
    protected static abstract class BooleanFactory extends BaseSimpleTypeFactory<Boolean> {
        /**
         * Constructor.
         */
        protected BooleanFactory(int columnIndex) {
            super(columnIndex, BOOLEAN_VALUES);
        }

        /**
         * Constructor.
         */
        protected BooleanFactory(String columnName) {
            super(columnName, BOOLEAN_VALUES);
        }
    }

    /**
     * Factory class used to create a Date from a result set.
     */
    protected static abstract class DateFactory extends BaseSimpleTypeFactory<java.util.Date> {
        /**
         * Constructor.
         */
        protected DateFactory(int columnIndex) {
            super(columnIndex, DATE_VALUES);
        }

        /**
         * Constructor.
         */
        protected DateFactory(String columnName) {
            super(columnName, DATE_VALUES);
        }
    }

    /**
     * Factory class used to create a double from a result set.
     */
    protected static abstract class DoubleFactory extends BaseSimpleTypeFactory<Double> {
        /**
         * Constructor.
         */
        protected DoubleFactory(int columnIndex) {
            super(columnIndex, DOUBLE_VALUES);
        }

        /**
         * Constructor.
         */
        protected DoubleFactory(String columnName) {
            super(columnName, DOUBLE_VALUES);
        }
    }

    /**
     * Factory class used to create a float from a result set.
     */
    protected static abstract class FloatFactory extends BaseSimpleTypeFactory<Float> {
        /**
         * Constructor.
         */
        protected FloatFactory(int columnIndex) {
            super(columnIndex, FLOAT_VALUES);
        }

        /**
         * Constructor.
         */
        protected FloatFactory(String columnName) {
            super(columnName, FLOAT_VALUES);
        }
    }

    /**
     * Factory class used to create an integer from a result set.
     */
    protected static abstract class IntegerFactory extends BaseSimpleTypeFactory<Integer> {
        /**
         * Constructor.
         */
        protected IntegerFactory(int columnIndex) {
            super(columnIndex, INTEGER_VALUES);
        }

        /**
         * Constructor.
         */
        protected IntegerFactory(String columnName) {
            super(columnName, INTEGER_VALUES);
        }
    }

    /**
     * Factory class used to create an integer ID from a result set.
     */
    @Deprecated
    protected static abstract class IntegerIdFactory extends IntegerFactory {
        /**
         * Constructor.
         */
        protected IntegerIdFactory(int columnIndex) {
            super(columnIndex);
        }

        /**
         * Constructor.
         */
        protected IntegerIdFactory(String columnName) {
            super(columnName);
        }
    }

    /**
     * Factory class used to create a long from a result set.
     */
    protected static abstract class LongFactory extends BaseSimpleTypeFactory<Long> {
        /**
         * Constructor.
         */
        protected LongFactory(int columnIndex) {
            super(columnIndex, LONG_VALUES);
        }

        /**
         * Constructor.
         */
        protected LongFactory(String columnName) {
            super(columnName, LONG_VALUES);
        }
    }

    /**
     * Factory class used to create a long ID from a result set.
     */
    @Deprecated
    protected static abstract class LongIdFactory extends LongFactory {
        /**
         * Constructor.
         */
        protected LongIdFactory(int columnIndex) {
            super(columnIndex);
        }

        /**
         * Constructor.
         */
        protected LongIdFactory(String columnName) {
            super(columnName);
        }
    }

    /**
     * Factory class used to create a string from a result set.
     */
    protected static abstract class StringFactory extends BaseSimpleTypeFactory<String> {
        /**
         * Constructor.
         */
        protected StringFactory(int columnIndex) {
            super(columnIndex, STRING_VALUES);
        }

        /**
         * Constructor.
         */
        protected StringFactory(String columnName) {
            super(columnName, STRING_VALUES);
        }
    }

    /**
     * Base factory class used to create a 'simple' type from a result set.
     */
    protected abstract static class BaseSimpleTypeFactory<T> implements Factory<T> {
        private final ColumnIdentifier columnIdentifier;
        private final SimpleTypeValues<T> simpleTypeValues;

        /**
         * Constructor.
         */
        protected BaseSimpleTypeFactory(int columnIndex, SimpleTypeValues<T> simpleTypeValues) {
            this(new ColumnIdentifier(columnIndex), simpleTypeValues);
        }

        /**
         * Constructor.
         */
        protected BaseSimpleTypeFactory(String columnName, SimpleTypeValues<T> simpleTypeValues) {
            this(new ColumnIdentifier(columnName), simpleTypeValues);
        }

        /**
         * Constructor.
         */
        private BaseSimpleTypeFactory(ColumnIdentifier columnIdentifier, SimpleTypeValues<T> simpleTypeValues) {
            this.columnIdentifier = columnIdentifier;
            this.simpleTypeValues = simpleTypeValues;
        }

        /**
         * Return a new instance with values from the result set.
         */
        public final T newInstance(ResultSet resultSet) throws SQLException {
            return (columnIdentifier.index == 0) ? simpleTypeValues.value(resultSet, columnIdentifier.name) : simpleTypeValues.value(resultSet, columnIdentifier.index);
        }
    }

    /**
     * Factory class used to create a count from a result set.
     * The count is assumed to be the first column in the result set.
     */
    protected static class CountFactory extends LongFactory {
        public static final CountFactory INSTANCE = new CountFactory();

        /**
         * Constructor.
         */
        private CountFactory() {
            super(1);
        }
    }

    /**
     * Factory class used to create a product instance ID from a result set.
     */
    protected static class ProdInstIdFactory extends StringFactory {
        public static final ProdInstIdFactory INSTANCE = new ProdInstIdFactory();

        /**
         * Constructor.
         */
        private ProdInstIdFactory() {
            super("prod_inst_id");
        }
    }

    /**
     * Represents IN clause parameters.
     */
    protected static class InClauseParameters {
        private final Object[] parameters;

        /**
         * Constructor.
         */
        public InClauseParameters(Object[] parameters) {
            this.parameters = parameters;
        }

        /**
         * Constructor.
         */
        public InClauseParameters(Collection<?> parameters) {
            this(parameters.toArray());
        }
    }

    /**
     * Represents UPDATE parameters.
     */
    protected static class UpdateParameters {
        private final BaseDataWithUpdateTracking object;

        /**
         * Constructor.
         */
        public UpdateParameters(BaseDataWithUpdateTracking object) {
            this.object = object;
        }
    }

    /**
     * Factory class used to create an auto-increment ID from a result set.
     */
    private static class AutoIncrementIdFactory implements Factory<Long> {
        public static final AutoIncrementIdFactory INSTANCE = new AutoIncrementIdFactory();

        /**
         * Constructor.
         */
        private AutoIncrementIdFactory() {}

        /**
         * Return a new instance with values from the result set.
         */
        public Long newInstance(ResultSet resultSet) throws SQLException {
            Object value = resultSet.getObject(1);
            if (value == null) {
                return null;
            }
            else if (value instanceof Number) {
                return toLong((Number)value);
            }
            else {
                throw new SQLException(value.getClass().getName() + " is non-numeric");
            }
        }
    }

    private static class ColumnIdentifier {
        public final int index;
        public final String name;

        public ColumnIdentifier(int index) {
            this(index, null);
            if (index <= 0) {
                throw new IllegalArgumentException();
            }
        }

        public ColumnIdentifier(String name) {
            this(0, name);
            if (stringIsBlank(name)) {
                throw new IllegalArgumentException();
            }
        }

        private ColumnIdentifier(int index, String name) {
            this.index = index;
            this.name = name;
        }
    }

    /**
     * Getters for 'simple' types.
     */
    private static interface SimpleTypeValues<T> {
        /**
         * Return any value.
         */
        public abstract T value(ResultSet resultSet, int columnIndex) throws SQLException;

        /**
         * Return any value.
         */
        public abstract T value(ResultSet resultSet, String columnName) throws SQLException;
    }

    /**
     * Value getting strategy interface.
     */
    private static interface ValueStrategy<T> {
        /**
         * Return any value.
         */
        public abstract Option<T> value(ResultSet resultSet) throws SQLException;
    }
}
