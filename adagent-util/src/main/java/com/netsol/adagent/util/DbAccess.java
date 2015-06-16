/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util;

import static com.netsol.adagent.util.beans.BaseData.collectionIsNotEmpty;
import static com.netsol.adagent.util.beans.BaseData.stringIsBlank;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;

import com.netsol.adagent.util.beans.Pair;
import com.netsol.adagent.util.cache.DataCache;
import com.netsol.adagent.util.dbhelpers.BaseHelper;
import com.netsol.adagent.util.dbhelpers.DataSourceFactory;
import com.netsol.adagent.util.dbhelpers.SimpleDataSourceFactory;
import com.netsol.adagent.util.log.BaseLoggable;

/**
 * DCAF DB Access.
 */
public class DbAccess extends BaseLoggable {
    public final static String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:40 DbAccess.java NSI";

    /**
     * Parallel task invocation strategy.
     */
    private final F2<String, List<Callable<Void>>, Void> parallelInvoker = new F2<String, List<Callable<Void>>, Void>() {
        /**
         * Invoke the specified tasks.
         */
        public Void apply(String logTag, List<Callable<Void>> tasks) throws Exception {
            // Invoke all but one of the tasks on the thread pool.
            int nTasks = tasks.size();
            List<Future<Void>> futures = new ArrayList<Future<Void>>(nTasks - 1);
            if (nTasks > 1) {
                for (int i = 1; i < nTasks; i++) {
                    futures.add(threadPool.submit(tasks.get(i)));
                }
            }

            Void v = null;
            // Call the first task on this thread.
            try {
                v = tasks.get(0).call();
            }
            catch (Exception ex) {
                logError(logTag, ex);
            }

            if (!futures.isEmpty()) {
                for (Future<Void> future : futures) {
                    try {
                        v = future.get();
                    }
                    catch (CancellationException ex) {
                        logError(logTag, ex.getCause());
                    }
                    catch (Exception ex) {
                        logError(logTag, ex);
                    }
                }
            }

            return v;
        }};

    /**
     * Serial task invocation strategy.
     */
    private final F2<String, List<Callable<Void>>, Void> serialInvoker = new F2<String, List<Callable<Void>>, Void>() {
        /**
         * Invoke the specified tasks.
         */
        public Void apply(String logTag, List<Callable<Void>> tasks) throws Exception {
            Void v = null;
            for (Callable<Void> task : tasks) {
                v = task.call();
            }
            return v;
        }};

    private boolean closed;
    private final DataSource controllerDbDataSource;
    private final DataSourceFactory dataSourceFactory;
    private final Map<Integer, DataSource> dataSourceMap = Collections.synchronizedMap(new HashMap<Integer, DataSource>());
    private final DbAccessHelper dbAccessHelper;
    private final DataCache<Pair<String, String>, Integer> dbIdCache;
    private final Map<Integer, List<DataSource>> slaveDataSourceMap = Collections.synchronizedMap(new HashMap<Integer, List<DataSource>>());
    private final ExecutorService threadPool = Executors.newCachedThreadPool();

    /**
     * Constructor.
     */
    public DbAccess(String logComponent, String controllerDbUrl) {
        this(logComponent, controllerDbUrl, SimpleDataSourceFactory.INSTANCE, SimpleDataSourceFactory.INSTANCE, null);
    }

    /**
     * Constructor.
     */
    public DbAccess(Log logger, String controllerDbUrl) {
        this(logger, controllerDbUrl, SimpleDataSourceFactory.INSTANCE, SimpleDataSourceFactory.INSTANCE, null);
    }

    /**
     * Constructor.
     */
    public DbAccess(String logComponent, String controllerDbUrl, DataSourceFactory controllerDbDataSourceFactory, DataSourceFactory dataSourceFactory, DataCache<Pair<String, String>, Integer> dbIdCache) {
        this(logComponent, null, controllerDbUrl, controllerDbDataSourceFactory, dataSourceFactory, dbIdCache);
    }

    /**
     * Constructor.
     */
    public DbAccess(Log logger, String controllerDbUrl, DataSourceFactory controllerDbDataSourceFactory, DataSourceFactory dataSourceFactory, DataCache<Pair<String, String>, Integer> dbIdCache) {
        this(null, logger, controllerDbUrl, controllerDbDataSourceFactory, dataSourceFactory, dbIdCache);
    }

    /**
     * Constructor.
     */
    private DbAccess(String logComponent, Log logger, String controllerDbUrl, DataSourceFactory controllerDbDataSourceFactory, DataSourceFactory dataSourceFactory, DataCache<Pair<String, String>, Integer> dbIdCache) {
        super(logComponent, logger);
        if ((controllerDbUrl == null) || (controllerDbDataSourceFactory == null) || (dataSourceFactory == null)) {
            throw new IllegalArgumentException();
        }
        controllerDbDataSource = controllerDbDataSourceFactory.newDataSource(controllerDbUrl);
        this.dataSourceFactory = dataSourceFactory;
        dbAccessHelper = new DbAccessHelper(this);
        this.dbIdCache = dbIdCache;
    }

    /**
     * Closes this instance and releases any system resources associated with it.
     */
    public void close() {
        if (closed) {
            return;
        }

        threadPool.shutdownNow();
        closed = true;
    }

    /**
     * Return a connection for the specified application ID and key.
     * The connection should be closed after use.
     */
    public Connection getConnection(String logTag, String applicationId, String uniqueKeyId) throws SQLException {
        if (closed) {
            throw new IllegalStateException();
        }

        Pair<String, String> cacheKey = Pair.from(applicationId, uniqueKeyId);
        // Look first in the cache.
        if (dbIdCache != null) {
            Integer dbId = dbIdCache.get(cacheKey);
            if (dbId != null) {
                // Don't worry about synchronizing here.
                // The worst that could happen is extra DB activity below.
                DataSource dataSource = dataSourceMap.get(dbId);
                if (dataSource != null) {
                    return dataSource.getConnection();
                }
            }
        }

        ConnectionData connectionData = getConnectionData(logTag, applicationId, uniqueKeyId);
        if (dbIdCache != null) {
            dbIdCache.put(cacheKey, Integer.valueOf(connectionData.dbId));
        }
        return getDataSource(connectionData).getConnection();
    }

    /**
     * Return a read connection for the specified application ID and key.
     * The connection will be to a slave instance if possible.
     * The connection should be closed after use.
     */
    public Connection getReadConnection(String logTag, String applicationId, String uniqueKeyId) throws SQLException {
        if (closed) {
            throw new IllegalStateException();
        }

        Pair<String, String> cacheKey = Pair.from(applicationId, uniqueKeyId);
        // Look first in the cache for the master DB ID.
        if (dbIdCache != null) {
            Integer masterDbId = dbIdCache.get(cacheKey);
            if (masterDbId != null) {
                List<DataSource> slaveDataSources = slaveDataSourceMap.get(masterDbId);
                Connection connection = getSlaveConnection(slaveDataSources);
                if (connection != null) {
                    return connection;
                }
            }
        }

        int masterDbId = getConnectionData(logTag, applicationId, uniqueKeyId).dbId;
        if (dbIdCache != null) {
            dbIdCache.put(cacheKey, Integer.valueOf(masterDbId));
        }

        return createReadConnection(logTag, masterDbId);
    }

    /**
     * Return a connection for the specified DB ID.
     * The connection should be closed after use.
     */
    public Connection createConnection(String logTag, int dbId) throws SQLException {
        if (closed) {
            throw new IllegalStateException();
        }

        synchronized(dataSourceMap) {
            DataSource dataSource = dataSourceMap.get(Integer.valueOf(dbId));
            if (dataSource != null) {
                return dataSource.getConnection();
            }
        }

        Connection controllerDbConnection = null;
        try {
            controllerDbConnection = createControllerDbConnection();
            ConnectionData connectionData = dbAccessHelper.getConnectionDataForDbId(logTag, controllerDbConnection, dbId);
            if (connectionData == null) {
                return null;
            }

            return getDataSource(connectionData).getConnection();
        }
        finally {
            BaseHelper.close(controllerDbConnection);
        }
    }

    /**
     * Return a connection to the controller DB.
     * The connection should be closed after use.
     */
    public Connection createControllerDbConnection() throws SQLException {
        if (closed) {
            throw new IllegalStateException();
        }

        return controllerDbDataSource.getConnection();
    }

    /**
     * Return a read connection for the specified master DB ID.
     * The connection will be to a slave instance if possible.
     * The connection should be closed after use.
     */
    public Connection createReadConnection(String logTag, int masterDbId) throws SQLException {
        if (closed) {
            throw new IllegalStateException();
        }

        List<DataSource> slaveDataSources = slaveDataSourceMap.get(Integer.valueOf(masterDbId));
        if (slaveDataSources == null) {
            Connection controllerDbConnection = null;
            try {
                controllerDbConnection = createControllerDbConnection();
                slaveDataSources = new ArrayList<DataSource>();
                for (ConnectionData slaveConnectionData : dbAccessHelper.getSlaveConnectionDatasForMasterDbId(logTag, controllerDbConnection, masterDbId)) {
                    slaveDataSources.add(newDataSource(slaveConnectionData));
                }
                slaveDataSources = Collections.unmodifiableList(slaveDataSources);
                slaveDataSourceMap.put(Integer.valueOf(masterDbId), slaveDataSources);
            }
            finally {
                BaseHelper.close(controllerDbConnection);
            }
        }

        Connection connection = getSlaveConnection(slaveDataSources);
        if (connection != null) {
            return connection;
        }

        // Fall back to returning a master DB connection.
        return createConnection(logTag, masterDbId);
    }

    /**
     * Invoke (serially) the specified action for each DB for the specified application ID.
     */
    public void forEachDb(String logTag, String applicationId, DbAction dbAction) throws Exception {
        if (closed) {
            throw new IllegalStateException();
        }

        forEachDb(logTag, applicationId, dbAction, serialInvoker, false);
    }

    /**
     * Invoke (in parallel) the specified action for each DB for the specified application ID.
     * The action can only invoke read-only actions on the supplied connection.
     */
    public void parallelForEachDb(String logTag, String applicationId, DbAction dbAction) throws Exception {
        if (closed) {
            throw new IllegalStateException();
        }

        forEachDb(logTag, applicationId, dbAction, parallelInvoker, true);
    }

    /**
     * Return the DB IDs for the specified application ID.
     */
    public List<Integer> getDbIds(String logTag, String applicationId) throws SQLException {
        if (closed) {
            throw new IllegalStateException();
        }

        Connection controllerDbConnection = null;
        try {
            controllerDbConnection = createControllerDbConnection();

            return dbAccessHelper.getDbIds(logTag, controllerDbConnection, applicationId);
        }
        finally {
            BaseHelper.close(controllerDbConnection);
        }
    }

    /**
     * Return the unique key IDs for the specified DB ID and application ID.
     */
    public List<String> getUniqueKeyIds(String logTag, String applicationId, int dbId) throws SQLException {
        if (closed) {
            throw new IllegalStateException();
        }

        Connection controllerDbConnection = null;
        try {
            controllerDbConnection = createControllerDbConnection();

            return dbAccessHelper.getUniqueKeyIds(logTag, controllerDbConnection, applicationId, dbId);
        }
        finally {
            BaseHelper.close(controllerDbConnection);
        }
    }

    /**
     * Invoke (using the specified invoker) the specified action for each DB for the specified application ID.
     */
    private void forEachDb(String logTag, String applicationId, DbAction dbAction, F2<String, List<Callable<Void>>, Void> invoker, boolean readOnlyConnection) throws Exception {
        List<Integer> dbIds = getDbIds(logTag, applicationId);
        if (dbIds.isEmpty()) {
            return;
        }

        List<Callable<Void>> tasks = new ArrayList<Callable<Void>>(dbIds.size());
        for (Integer dbId : dbIds) {
            tasks.add(newTask(logTag, dbId.intValue(), dbAction, readOnlyConnection));
        }
        invoker.apply(logTag, tasks);
    }

    /**
     * Get connection data for the specified application ID and key.
     */
    private ConnectionData getConnectionData(final String logTag, final String applicationId, final String uniqueKeyId) throws SQLException {
        Connection controllerDbConnection = null;
        try {
            controllerDbConnection = createControllerDbConnection();
            controllerDbConnection.setAutoCommit(false);

            // Start a transaction.
            boolean success = false;
            try {
                ConnectionData connectionData = null;

                if (uniqueKeyId == null) {
                    // Return global data.
                    List<ConnectionData> connectionDataList =
                        dbAccessHelper.getConnectionDatasForApplicationId(logTag, controllerDbConnection, applicationId);
                    int connectionDataListSize = connectionDataList.size();
                    switch (connectionDataListSize) {
                    case 0:
                        connectionData = null;
                        break;
                    case 1:
                        connectionData = connectionDataList.get(0);
                        break;
                    default:
                        throw new SQLException(String.format("%1$s returned %2$d rows", applicationId, connectionDataListSize));
                    }
                }
                else if (stringIsBlank(uniqueKeyId)) {
                    throw new SQLException("Unique key ID cannot be a blank string");
                }
                else {
                    Integer dbId = dbAccessHelper.getDbId(logTag, controllerDbConnection, applicationId, uniqueKeyId);
                    if (dbId == null) {
                        // New unique key ID.
                        connectionData = BaseHelper.attemptInRestartableTransaction(controllerDbConnection, new F1<Connection, ConnectionData>() {
                            public ConnectionData apply(Connection connection) throws SQLException {
                                ConnectionData connectionData =
                                    dbAccessHelper.getConnectionDataForApplicationIdWithNewUniqueKey(logTag, connection, applicationId);
                                dbAccessHelper.insertNewUniqueKey(
                                        logTag,
                                        connection,
                                        applicationId,
                                        uniqueKeyId,
                                        connectionData.dbId);
                                dbAccessHelper.updateKeyCount(logTag, connection, connectionData.dbId);
                                return connectionData;
                            }
                        }, 2);
                    }
                    else {
                        // Existing unique key ID.
                        connectionData = dbAccessHelper.getConnectionDataForDbId(logTag, controllerDbConnection, dbId.intValue());
                    }
                }

                success = true;
                return connectionData;
            }
            finally {
                BaseHelper.endTransaction(controllerDbConnection, success);
            }
        }
        finally {
            BaseHelper.close(controllerDbConnection);
        }
    }

    /**
     * Return the data source corresponding to the specified connection data.
     */
    private DataSource getDataSource(ConnectionData connectionData) {
        Integer dbId = Integer.valueOf(connectionData.dbId);
        synchronized(dataSourceMap) {
            DataSource dataSource = dataSourceMap.get(dbId);
            if (dataSource == null) {
                dataSource = newDataSource(connectionData);
                dataSourceMap.put(dbId, dataSource);
            }
            return dataSource;
        }
    }

    /**
     * Return a slave connection (or null).
     */
    private Connection getSlaveConnection(List<DataSource> slaveDataSources) {
        if (collectionIsNotEmpty(slaveDataSources)) {
            slaveDataSources = new ArrayList<DataSource>(slaveDataSources);
            // Return one at random.
            Collections.shuffle(slaveDataSources);
            for (DataSource slaveDataSource : slaveDataSources) {
                try {
                    Connection connection = slaveDataSource.getConnection();
                    if (BaseHelper.ping(connection)) {
                        return connection;
                    }
                }
                catch (SQLException ex) {}
            }
        }
        return null;
    }

    private DataSource newDataSource(ConnectionData connectionData) {
        String url =
            // e.g. jdbc:mysql://eng2.dev.netsol.com:4310/adagent?user=adagent&password=adagent
            String.format("jdbc:mysql://%1$s:%2$d/%3$s?user=%4$s&password=%5$s%6$s",
                    connectionData.hostName,
                    connectionData.port,
                    connectionData.databaseName,
                    connectionData.userName,
                    connectionData.password,
                    stringIsBlank(connectionData.properties) ? "" : "&" + connectionData.properties);
        return dataSourceFactory.newDataSource(url);
    }

    private Callable<Void> newTask(final String logTag, final int dbId, final DbAction dbAction, final boolean readOnlyConnection) {
        return new Callable<Void>() {
            /**
             * Computes a result, or throws an exception if unable to do so.
             */
            public Void call() throws Exception {
                Connection connection = null;
                try {
                    connection = createConnection(logTag, dbId);
                    boolean readOnlyConnectionAfterCreation = connection.isReadOnly();
                    try {
                        connection.setReadOnly(readOnlyConnection);
                        dbAction.invoke(dbId, connection);
                    }
                    finally {
                        connection.setReadOnly(readOnlyConnectionAfterCreation);
                    }
                }
                finally {
                    BaseHelper.close(connection);
                }
                return null;
            }};
    }

    /**
     * Action to be invoked.
     */
    public static interface DbAction {
        public abstract void invoke(int dbId, Connection connection) throws Exception;
    }

    /**
     * Represents connection data.
     */
    private static class ConnectionData {
        public String databaseName;
        public int dbId;
        public String hostName;
        public String password;
        public int port;
        public String properties;
        public String userName;
    }

    /**
     * Database helper.
     */
    private static class DbAccessHelper extends BaseHelper {
        private static final IntegerFactory DB_ID_FACTORY = new IntegerFactory("db_id") {};
        private static final StringFactory UNIQUE_KEY_ID_FACTORY = new StringFactory("unique_key_id") {};

        /**
         * Constructor.
         */
        public DbAccessHelper(BaseLoggable baseLoggable) {
            super(baseLoggable, Boolean.parseBoolean(System.getProperty("com.netsol.adagent.util.DbAccess.logSqlStatements")));
        }

        /**
         * Get connection data for the specified application ID.
         */
        public List<ConnectionData> getConnectionDatasForApplicationId(String logTag, Connection connection, String applicationId) throws SQLException {
            final String SQL =
                "SELECT" +
                "  db_id," +
                "  host," +
                "  port," +
                "  username," +
                "  password," +
                "  database_name," +
                "  connection_properties " +
                "FROM" +
                "  master " +
                "WHERE" +
                "  application_id = ?;";

            PreparedStatement statement = null;
            ResultSet resultSet = null;
            try {
                statement = connection.prepareStatement(SQL);
                statement.setString(1, applicationId);
                logSqlStatement(logTag, statement);
                resultSet = statement.executeQuery();
                return newList(resultSet, ConnectionDataFactory.INSTANCE);
            }
            finally {
                close(statement, resultSet);
            }
        }

        /**
         * Get connection data for the specified application ID.
         */
        public ConnectionData getConnectionDataForApplicationIdWithNewUniqueKey(String logTag, Connection connection, String applicationId) throws SQLException {
            final String SQL =
                "SELECT" +
                "  db_id," +
                "  host," +
                "  port," +
                "  username," +
                "  password," +
                "  database_name," +
                "  connection_properties " +
                "FROM" +
                "  master " +
                "WHERE" +
                "  application_id = ? " +
                "ORDER BY" +
                "  num_keys ASC " +
                "LIMIT 1 " +
                // SELECT ... FOR UPDATE so as to block any concurrent SELECTs on `master`.
                // This reduces the chance of deadlock.
                "FOR UPDATE;";

            PreparedStatement statement = null;
            ResultSet resultSet = null;
            try {
                statement = connection.prepareStatement(SQL);
                statement.setString(1, applicationId);
                logSqlStatement(logTag, statement);
                resultSet = statement.executeQuery();
                return singleValue(resultSet, ConnectionDataFactory.INSTANCE);
            }
            finally {
                close(statement, resultSet);
            }
        }

        /**
         * Get connection data for the specified DB ID.
         */
        public ConnectionData getConnectionDataForDbId(String logTag, Connection connection, int dbId) throws SQLException {
            final String SQL =
                "SELECT" +
                "  db_id," +
                "  host," +
                "  port," +
                "  username," +
                "  password," +
                "  database_name," +
                "  connection_properties " +
                "FROM" +
                "  master " +
                "WHERE" +
                "  db_id = ?;";

            PreparedStatement statement = null;
            ResultSet resultSet = null;
            try {
                statement = connection.prepareStatement(SQL);
                statement.setInt(1, dbId);
                logSqlStatement(logTag, statement);
                resultSet = statement.executeQuery();
                return singleValue(resultSet, ConnectionDataFactory.INSTANCE);
            }
            finally {
                close(statement, resultSet);
            }
        }

        /**
         * Get DB ID.
         */
        public Integer getDbId(String logTag, Connection connection, String applicationId, String uniqueKeyId) throws SQLException {
            final String SQL =
                "SELECT" +
                "  db_id " +
                "FROM" +
                "  unique_key_lookup " +
                "WHERE" +
                "  application_id = ? AND" +
                "  unique_key_id = ?;";

            PreparedStatement statement = null;
            ResultSet resultSet = null;
            try {
                statement = connection.prepareStatement(SQL);
                statement.setString(1, applicationId);
                statement.setString(2, uniqueKeyId);
                logSqlStatement(logTag, statement);
                resultSet = statement.executeQuery();
                return singleValue(resultSet, DB_ID_FACTORY);
            }
            finally {
                close(statement, resultSet);
            }
        }

        /**
         * Get DB IDs.
         */
        public List<Integer> getDbIds(String logTag, Connection connection, String applicationId) throws SQLException {
            final String SQL =
                "SELECT" +
                "  db_id " +
                "FROM" +
                "  master " +
                "WHERE" +
                "  application_id = ? " +
                "ORDER BY" +
                "  db_id ASC;";

            PreparedStatement statement = null;
            ResultSet resultSet = null;
            try {
                statement = connection.prepareStatement(SQL);
                statement.setString(1, applicationId);
                logSqlStatement(logTag, statement);
                resultSet = statement.executeQuery();
                return newList(resultSet, DB_ID_FACTORY);
            }
            finally {
                close(statement, resultSet);
            }
        }

        /**
         * Get slave connection data for the specified master DB ID.
         */
        public List<ConnectionData> getSlaveConnectionDatasForMasterDbId(String logTag, Connection connection, int dbId) throws SQLException {
            final String SQL =
                "SELECT" +
                "  db_id," +
                "  host," +
                "  port," +
                "  username," +
                "  password," +
                "  database_name," +
                "  connection_properties " +
                "FROM" +
                "  master " +
                "WHERE" +
                "  master_db_id = ?;";

            PreparedStatement statement = null;
            ResultSet resultSet = null;
            try {
                statement = connection.prepareStatement(SQL);
                statement.setInt(1, dbId);
                logSqlStatement(logTag, statement);
                resultSet = statement.executeQuery();
                return newList(resultSet, ConnectionDataFactory.INSTANCE);
            }
            finally {
                close(statement, resultSet);
            }
        }

        /**
         * Get Unique key IDs.
         */
        public List<String> getUniqueKeyIds(String logTag, Connection connection, String applicationId, int dbId) throws SQLException {
            final String SQL =
                "SELECT" +
                "  unique_key_id " +
                "FROM" +
                "  unique_key_lookup " +
                "WHERE" +
                "  db_id = ? AND" +
                "  application_id = ?;";

            PreparedStatement statement = null;
            ResultSet resultSet = null;
            try {
                statement = connection.prepareStatement(SQL);
                statement.setInt(1, dbId);
                statement.setString(2, applicationId);
                logSqlStatement(logTag, statement);
                resultSet = statement.executeQuery();
                return newList(resultSet, UNIQUE_KEY_ID_FACTORY);
            }
            finally {
                close(statement, resultSet);
            }
        }

        /**
         * Insert a new unique key.
         */
        public void insertNewUniqueKey(String logTag, Connection connection, String applicationId, String uniqueKeyId, int dbId) throws SQLException {
            final String SQL =
                "INSERT INTO unique_key_lookup" +
                "  (db_id, application_id, unique_key_id) " +
                "VALUES" +
                "  (?, ?, ?);";

            PreparedStatement statement = null;
            try {
                statement = connection.prepareStatement(SQL);
                statement.setInt(1, dbId);
                statement.setString(2, applicationId);
                statement.setString(3, uniqueKeyId);
                logSqlStatement(logTag, statement);
                statement.executeUpdate();
            }
            finally {
                close(statement);
            }
        }

        /**
         * Update the key count.
         */
        public void updateKeyCount(String logTag, Connection connection, int dbId) throws SQLException {
            final String SQL =
                "UPDATE" +
                "  master " +
                "SET" +
                "  num_keys = num_keys + 1 " +
                "WHERE" +
                "  db_id = ?;";

            PreparedStatement statement = null;
            try {
                statement = connection.prepareStatement(SQL);
                statement.setInt(1, dbId);
                logSqlStatement(logTag, statement);
                statement.executeUpdate();
            }
            finally {
                close(statement);
            }
        }

        /**
         * Connection data factory.
         */
        private static class ConnectionDataFactory implements Factory<ConnectionData> {
            public static final ConnectionDataFactory INSTANCE = new ConnectionDataFactory();

            /**
             * Constructor.
             */
            private ConnectionDataFactory() {}

            /**
             * Return a new connection data instance with values from the result set.
             */
            public ConnectionData newInstance(ResultSet resultSet) throws SQLException {
                ConnectionData connectionData = new ConnectionData();
                connectionData.databaseName = resultSet.getString("database_name");
                connectionData.dbId = resultSet.getInt("db_id");
                connectionData.hostName = resultSet.getString("host");
                connectionData.password = resultSet.getString("password");
                connectionData.port = resultSet.getInt("port");
                connectionData.properties = resultSet.getString("connection_properties");
                connectionData.userName = resultSet.getString("username");
                return connectionData;
            }
        }
    }
}
