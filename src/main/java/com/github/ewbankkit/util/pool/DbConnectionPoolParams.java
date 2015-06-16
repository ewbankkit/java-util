/**
 * Kit's Java Utils.
 */

package com.github.ewbankkit.util.pool;

import com.github.ewbankkit.util.beans.BaseData;

/**
 * Database connection pool parameters.
 */
public final class DbConnectionPoolParams extends BaseData implements Cloneable {
    public final static String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:12 DbConnectionPoolParams.java NSI";

    public final static boolean DEFAULT_CACHE_PREPARED_STATEMENTS = true;
    public final static int DEFAULT_MAX_ACTIVE = 25;
    public final static int DEFAULT_MAX_IDLE = 5;
    public final static Long DEFAULT_MAX_WAIT = null;
    public final static String DEFAULT_VALIDATION_QUERY = "SELECT 1;";

    private boolean cachePreparedStatements = DEFAULT_CACHE_PREPARED_STATEMENTS;
    private int maxActive = DEFAULT_MAX_ACTIVE;
    private int maxIdle = DEFAULT_MAX_IDLE;
    private Long maxWait = DEFAULT_MAX_WAIT;
    private String validationQuery = DEFAULT_VALIDATION_QUERY;

    public void setCachePreparedStatements(boolean cachePreparedStatements) {
        this.cachePreparedStatements = cachePreparedStatements;
    }

    public boolean isCachePreparedStatements() {
        return cachePreparedStatements;
    }

    public void setMaxActive(int maxActive) {
        this.maxActive = maxActive;
    }

    public int getMaxActive() {
        return maxActive;
    }

    public void setMaxIdle(int maxIdle) {
        this.maxIdle = maxIdle;
    }

    public int getMaxIdle() {
        return maxIdle;
    }

    public void setMaxWait(Long maxWait) {
        this.maxWait = maxWait;
    }

    public Long getMaxWait() {
        return maxWait;
    }

    public void setValidationQuery(String validationQuery) {
        this.validationQuery = validationQuery;
    }

    public String getValidationQuery() {
        return validationQuery;
    }

    /**
     * Create and return a copy of this object.
     */
    @Override
    public DbConnectionPoolParams clone() {
        try {
            return (DbConnectionPoolParams)super.clone();
        }
        catch (CloneNotSupportedException ex) {
            throw new AssertionError();
        }
    }
}
