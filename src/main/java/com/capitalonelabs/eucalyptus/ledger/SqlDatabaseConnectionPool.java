//
// Kit's Java Utils.
//

package com.capitalonelabs.eucalyptus.ledger;

import org.apache.tomcat.jdbc.pool.PoolConfiguration;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Properties;

/**
 * Represents the SQL database connection pool.
 */
public final class SqlDatabaseConnectionPool {
    private static final int    DATABASE_HEALTH_CHECK_TIMEOUT_SECS = 5;
    private static final Logger LOGGER                             =
        LoggerFactory.getLogger(SqlDatabaseConnectionPool.class);

    private org.apache.tomcat.jdbc.pool.DataSource dataSource;

    private SqlDatabaseConnectionPool() {}

    /**
     * Returns the single instance.
     */
    public static SqlDatabaseConnectionPool getInstance() {
        return LazyHolder.INSTANCE;
    }

    /**
     * Returns the SQL data source.
     */
    public SqlDataSource getSqlDataSource() {
        return new SqlDataSource(dataSource);
    }

    /**
     * Create the connection pool.
     */
    public void create(String resourceName) throws Exception {
        Properties properties = new Properties();
        try (InputStream inStream = Thread.currentThread().getContextClassLoader().
            getResourceAsStream(Objects.requireNonNull(resourceName))) {
            properties.load(inStream);
        }

        PoolConfiguration poolConfiguration = new PoolProperties();
        poolConfiguration.setDefaultAutoCommit(Boolean.FALSE);
        poolConfiguration.setDriverClassName(
            getStringPropertyOrThrow(properties, "databaseConnectionPool.driverClassName"));
        poolConfiguration.setInitialSize(
            getIntPropertyOrThrow(properties, "databaseConnectionPool.initialSize"));
        poolConfiguration.setJmxEnabled(false);
        poolConfiguration.setLogAbandoned(true);
        poolConfiguration.setLogValidationErrors(true);
        poolConfiguration.setMaxActive(
            getIntPropertyOrThrow(properties, "databaseConnectionPool.maxActive"));
        poolConfiguration.setMaxIdle(
            getIntPropertyOrThrow(properties, "databaseConnectionPool.maxIdle"));
        poolConfiguration.setMinIdle(
            getIntPropertyOrThrow(properties, "databaseConnectionPool.minIdle"));
        poolConfiguration.setRemoveAbandoned(true);
        poolConfiguration.setTestOnBorrow(true);
        poolConfiguration.setTestWhileIdle(true);
        poolConfiguration.setValidationQuery(
            getStringPropertyOrThrow(properties, "databaseConnectionPool.validationQuery"));
        poolConfiguration.setUrl(
            getStringPropertyOrThrow(properties, "databaseConnectionPool.url"));
        poolConfiguration.setUsername(
            getStringPropertyOrThrow(properties, "databaseConnectionPool.username"));
        poolConfiguration.setPassword(
            getStringPropertyOrThrow(properties, "databaseConnectionPool.password"));
        LOGGER.info("Creating JDBC Connection Pool with configuration {}", poolConfiguration);

        org.apache.tomcat.jdbc.pool.DataSource dataSource = new org.apache.tomcat.jdbc.pool.DataSource(poolConfiguration);
        try {
            dataSource.createPool();
        }
        catch (SQLException ex) {
            throw new RuntimeException("Unable to create database connection pool", ex);
        }
        this.dataSource = dataSource;
    }

    /**
     * Destroy the connection pool.
     */
    public void destroy() {
        if (dataSource != null) {
            dataSource.close();
        }

        // Deregister JDBC drivers.
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            try {
                DriverManager.deregisterDriver(drivers.nextElement());
            }
            catch (SQLException ignored) {}
        }
    }

    public boolean isHealthy() {
        return getSqlDataSource().isValid(DATABASE_HEALTH_CHECK_TIMEOUT_SECS);
    }

    private String getStringPropertyOrThrow(
        final Properties properties,
        final String key) throws Exception {
        return Optional.ofNullable(properties.getProperty(key)).
            orElseThrow(() -> new Exception("Missing property: " + key));
    }

    private int getIntPropertyOrThrow(
        final Properties properties,
        final String key) throws Exception {
        String s = properties.getProperty(key);
        return ((s == null) ? OptionalInt.empty() : OptionalInt.of(Integer.parseInt(s))).
            orElseThrow(() -> new Exception("Missing property: " + key));
    }

    /**
     * Initialization on-demand holder.
     */
    private static final class LazyHolder {
        public static final SqlDatabaseConnectionPool INSTANCE = new SqlDatabaseConnectionPool();
    }
}
