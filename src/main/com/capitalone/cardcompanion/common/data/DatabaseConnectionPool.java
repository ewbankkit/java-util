//
// Copyright (C) Capital One Labs.
//

package com.capitalone.cardcompanion.common.data;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Map;

import javax.management.JMException;

import org.apache.tomcat.jdbc.pool.PoolConfiguration;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.capitalone.cardcompanion.common.Config;
import com.capitalone.cardcompanion.common.InitializationException;
import com.capitalone.cardcompanion.common.jmx.MBeanRegistry;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;

/**
 * Represents the database connection pool.
 */
public final class DatabaseConnectionPool {
    private static final int    DATABASE_HEALTH_CHECK_TIMEOUT_SECS = 5;
    private static final Logger LOGGER                             = LoggerFactory.getLogger(DatabaseConnectionPool.class);

    private org.apache.tomcat.jdbc.pool.DataSource tomcatDataSource;
    
    private org.apache.tomcat.jdbc.pool.DataSource tomcatTransactionMetadataDataSource;

    /**
     * Constructor.
     */
    private DatabaseConnectionPool() {}

    /**
     * Returns the single instance.
     */
    public static DatabaseConnectionPool getInstance() {
        return LazyHolder.INSTANCE;
    }

    /**
     * Returns the data source.
     */
    public DataSource getDataSource() {
        Preconditions.checkState(tomcatDataSource != null);

        return new DataSource(tomcatDataSource);
    }
    
    /**
     * Returns the transaction meta data data source.
     */
    public DataSource getTransactionMetadataDataSource() {
        Preconditions.checkState(tomcatTransactionMetadataDataSource != null);

        return new DataSource(tomcatTransactionMetadataDataSource);
    }

    /**
     * Create the connection pool.
     */
    @SuppressWarnings("UnnecessaryUnboxing")
    public void create() throws InitializationException {
    	
        tomcatDataSource = createDataSource("databaseConnectionPool", "OracleConnectionPool");
        tomcatTransactionMetadataDataSource = createDataSource("transactionMetadataDatabaseConnectionPool", "TransactionMetadataOracleConnectionPool");
    }

    /**
     * Destroy the connection pool.
     */
    public void destroy() {
        if (tomcatDataSource != null) {
            tomcatDataSource.close();
        }
        
        if (tomcatTransactionMetadataDataSource != null) {
        	tomcatTransactionMetadataDataSource.close();
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
        return getDataSource().isValid(DATABASE_HEALTH_CHECK_TIMEOUT_SECS) && getTransactionMetadataDataSource().isValid(DATABASE_HEALTH_CHECK_TIMEOUT_SECS);
    }
        
    private org.apache.tomcat.jdbc.pool.DataSource createDataSource(String databaseConnectionPoolPropertyPrefix, String connectionPoolJmxNamePrefix) throws InitializationException {
    	Config config = Config.getInstance();
        Optional<String> optionalUrl = config.getString(databaseConnectionPoolPropertyPrefix + ".url");
        if (!optionalUrl.isPresent()) {
            throw new InitializationException("Missing database connection pool URL configuration value");
        }
        Optional<String> optionalDriverClassName = config.getString(databaseConnectionPoolPropertyPrefix + ".driverClassName");
        if (!optionalDriverClassName.isPresent()) {
            throw new InitializationException("Missing database connection pool driver class name configuration value");
        }
        Optional<Integer> optionalInitialSize = config.getInteger(databaseConnectionPoolPropertyPrefix + ".initialSize");
        if (!optionalInitialSize.isPresent()) {
            throw new InitializationException("Missing database connection pool initial size configuration value");
        }
        Optional<Integer> optionalMaxActive = config.getInteger(databaseConnectionPoolPropertyPrefix + ".maxActive");
        if (!optionalMaxActive.isPresent()) {
            throw new InitializationException("Missing database connection pool max active configuration value");
        }
        Optional<Integer> optionalMaxIdle = config.getInteger(databaseConnectionPoolPropertyPrefix + ".maxIdle");
        if (!optionalMaxIdle.isPresent()) {
            throw new InitializationException("Missing database connection pool max idle configuration value");
        }
        Optional<Integer> optionalMinIdle = config.getInteger(databaseConnectionPoolPropertyPrefix + ".minIdle");
        if (!optionalMinIdle.isPresent()) {
            throw new InitializationException("Missing database connection pool min idle configuration value");
        }
        boolean jmxEnabled = MBeanRegistry.isJmxEnabled();

        PoolConfiguration poolConfiguration = new PoolProperties();
        poolConfiguration.setDefaultAutoCommit(Boolean.FALSE);
        poolConfiguration.setDriverClassName(optionalDriverClassName.get());
        poolConfiguration.setInitialSize(optionalInitialSize.get().intValue());
        poolConfiguration.setJmxEnabled(jmxEnabled);
        poolConfiguration.setLogAbandoned(true);
        poolConfiguration.setLogValidationErrors(true);
        poolConfiguration.setMaxActive(optionalMaxActive.get().intValue());
        poolConfiguration.setMaxIdle(optionalMaxIdle.get().intValue());
        poolConfiguration.setMinIdle(optionalMinIdle.get().intValue());
        poolConfiguration.setRemoveAbandoned(true);
        poolConfiguration.setTestOnBorrow(true);
        poolConfiguration.setTestWhileIdle(true);
        poolConfiguration.setUrl(optionalUrl.get());
        poolConfiguration.setValidationQuery("SELECT 1 FROM DUAL");
        LOGGER.info("Creating JDBC Connection Pool with configuration {}", poolConfiguration);

        Optional<Map<String, String>> optionalSystemProperties = config.getMap(databaseConnectionPoolPropertyPrefix + ".systemProperties");
        if (optionalSystemProperties.isPresent()) {
            for (Map.Entry<String, String> entry : optionalSystemProperties.get().entrySet()) {
                System.setProperty(entry.getKey(), entry.getValue());
            }
        }

        org.apache.tomcat.jdbc.pool.DataSource dataSource = new org.apache.tomcat.jdbc.pool.DataSource(poolConfiguration);
        try {
            dataSource.createPool();
        }
        catch (SQLException ex) {
            throw new InitializationException("Unable to create database connection pool", ex);
        }

        if (jmxEnabled) {
            try {
                String jmxBeanName = new StringBuilder()
                                            .append(connectionPoolJmxNamePrefix)
                                            .append(".")
                                            .append(Config.getName())
                                            .toString();
                
                MBeanRegistry.getInstance().register(jmxBeanName, dataSource.getPool().getJmxPool());
            }
            catch (JMException ex) {
                LOGGER.warn("Unable to register database connection pool MBean", ex);
            }
        }
        
        return dataSource;
    }

    /**
     * Initialization on-demand holder.
     */
    private static final class LazyHolder {
        public static final DatabaseConnectionPool INSTANCE = new DatabaseConnectionPool();
    }
}
