/**
 * Kit's Java Utils.
 */

package com.github.ewbankkit.util.pool;

import static com.github.ewbankkit.util.beans.BaseData.stringIsNotBlank;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import javax.sql.DataSource;

import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.pool.KeyedObjectPoolFactory;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.impl.GenericKeyedObjectPool;
import org.apache.commons.pool.impl.GenericKeyedObjectPoolFactory;
import org.apache.commons.pool.impl.GenericObjectPool;

import com.github.ewbankkit.util.dbhelpers.BaseHelper;
import com.github.ewbankkit.util.dbhelpers.DataSourceFactory;

/**
 * Pooled data source factory.
 */
public class PooledDataSourceFactory implements DataSourceFactory {
    public final static String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:12 PooledDataSourceFactory.java NSI";

    private boolean closed;
    private final DbConnectionPoolParams dbConnectionPoolParams;
    private final Collection<ObjectPool> objectPools = Collections.synchronizedCollection(new ArrayList<ObjectPool>());

    /**
     * Constructor.
     */
    public PooledDataSourceFactory() {
        this(new DbConnectionPoolParams());
    }

    /**
     * Constructor.
     */
    public PooledDataSourceFactory(DbConnectionPoolParams dbConnectionPoolParams) {
        this.dbConnectionPoolParams = dbConnectionPoolParams.clone();
    }

    /**
     * Closes this factory and releases any system resources associated with it.
     */
    public void close() {
        if (closed) {
            return;
        }

        for (ObjectPool objectPool : objectPools) {
            try {
                objectPool.close();
            }
            catch (Exception ex) {}
        }
        closed = true;
    }

    /**
     * Return a new data source.
     */
    public DataSource newDataSource(String url) {
        if (closed) {
            throw new IllegalStateException();
        }

        try {
            BaseHelper.registerMySqlDriver();
        }
        catch (SQLException ex) {
            throw new RuntimeException(ex.getMessage());
        }

        GenericObjectPool genericObjectPool = new GenericObjectPool(null);
        genericObjectPool.setMaxActive(dbConnectionPoolParams.getMaxActive());
        genericObjectPool.setMaxIdle(dbConnectionPoolParams.getMaxIdle());
        Long maxWait = dbConnectionPoolParams.getMaxWait();
        if (maxWait == null) {
            genericObjectPool.setWhenExhaustedAction(GenericObjectPool.WHEN_EXHAUSTED_GROW);
        }
        else {
            if (maxWait.longValue() == 0L) {
                genericObjectPool.setWhenExhaustedAction(GenericObjectPool.WHEN_EXHAUSTED_FAIL);
            }
            else {
                genericObjectPool.setWhenExhaustedAction(GenericObjectPool.WHEN_EXHAUSTED_BLOCK);
                genericObjectPool.setMaxWait(maxWait.longValue());
            }
        }

        String validationQuery = dbConnectionPoolParams.getValidationQuery();
        if (stringIsNotBlank(validationQuery)) {
            genericObjectPool.setTestOnBorrow(true);
        }
        else {
            validationQuery = null;
        }
        objectPools.add(genericObjectPool);

        KeyedObjectPoolFactory preparedStatementPoolFactory = null;
        if (dbConnectionPoolParams.isCachePreparedStatements()) {
            preparedStatementPoolFactory =
                // Unlimited size prepared statement pool.
                new GenericKeyedObjectPoolFactory(null, -1, GenericKeyedObjectPool.WHEN_EXHAUSTED_FAIL, 0L, 1, -1);
        }

        PoolableConnectionFactory poolableConnectionFactory =
            new PoolableConnectionFactory(new DriverManagerConnectionFactory(url, null), genericObjectPool, preparedStatementPoolFactory, null, false, true);
        if (validationQuery != null) {
            poolableConnectionFactory.setValidationQuery(validationQuery);
        }

        return new PoolingDataSource(genericObjectPool);
    }
}
