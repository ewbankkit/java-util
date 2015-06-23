//
// Copyright (C) Capital One Labs.
//

package com.capitalone.cardcompanion.common.data

import com.google.common.base.Function
import com.google.common.base.Optional
import com.google.common.base.Preconditions
import groovy.sql.GroovyResultSet
import groovy.sql.Sql
import groovy.util.logging.Slf4j

import java.sql.Connection
import java.sql.SQLException

/**
 * Abstract base class for SQL DAOs.
 */
@Slf4j
abstract class AbstractSqlDAO {
    protected final String entityName

    private final Connection connection

    /**
     * Constructor.
     */
    protected AbstractSqlDAO(String entityName, Connection connection) {
        Preconditions.checkNotNull entityName
        Preconditions.checkNotNull connection

        this.entityName = entityName
        this.connection = connection
    }

    /**
     * Execute the specified SQL update/insert/delete statement.
     * Returns the number of rows updated.
     */
    protected int executeUpdate(String sqlStatement, List params) throws SQLException {
        Sql sql = new Sql(connection)
        boolean isRs = sql.execute sqlStatement, params
        isRs ? 0 : sql.updateCount
    }

    /**
     * Finds multiple entities.
     * The closure creates an entity from a result set.
     */
    protected List find(String selectSql, List params, Closure closure) {
        List data = []
        Sql sql = new Sql(connection)
        try {
            sql.eachRow(selectSql, params) {
                final GroovyResultSet rs = it as GroovyResultSet

                data << closure.call(rs.toRowResult())
            }
        }
        catch (SQLException ex) {
            log.error("Unable to find ${entityName}", ex)
        }

        data.asImmutable()
    }

    /**
     * Finds multiple entities.
     * The function creates an entity from a result map.
     */
    protected <T> List<T> find(final String selectSql, final List params, final Function<Map<String, Object>, T> function) {
        find(selectSql, params) {
            // Closure parameter is a GroovyRowResult.
            function.apply(it as Map)
        }
    }

    /**
     * Finds a single entity.
     * The closure creates an entity from the result set.
     */
    protected Optional findOne(String selectSql, List params, Closure closure) {
        Sql sql = new Sql(connection)
        try {
            def result = sql.firstRow selectSql, params
            if (result) {
                return Optional.of(closure.call(result))
            }
        }
        catch (SQLException ex) {
            log.error("Unable to find ${entityName}", ex)
        }

        Optional.absent()
    }

    /**
     * Finds a single entity.
     * The function creates an entity from the result map.
     */
    protected <T> Optional<T> findOne(final String selectSql, final List params, final Function<Map<String, Object>, T> function) {
        findOne(selectSql, params) {
            // Closure parameter is a GroovyRowResult.
            function.apply(it as Map)
        }
    }

    /**
     * Removes an entity by ID.
     */
    protected int removeById(String sqlStatement, Object id) throws SQLException {
        executeUpdate sqlStatement, [id]
    }

    /**
     * Returns the Boolean corresponding to the specified Oracle CHAR value.
     */
    protected static Boolean fromOracleChar(String s) {
        if (s == null) {
            return null
        }
        else if (s == 'Y') {
            return Boolean.TRUE
        }
        Boolean.FALSE
    }

    /**
     * Returns the Oracle CHAR value corresponding to the specified Boolean.
     */
    protected static String toOracleChar(Boolean b) {
        if (b == null) {
            return null
        }
        else if (b == Boolean.TRUE) {
            return 'Y'
        }
        'N'
    }

    /**
     * Returns SQL with a WHERE clause.
     */
    protected static String whereSql(String selectSql, String... columnNames) {
        if (columnNames.length > 0) {
            selectSql = selectSql + " WHERE ${columnNames[0]} = ?"
        }
        if (columnNames.length > 1) {
            selectSql = selectSql + " AND ${columnNames[1]} = ?"
        }
        selectSql
    }
}
