/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.dbhelpers;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

import org.apache.commons.logging.Log;

import com.netsol.adagent.util.beans.InterceptorLeadPage;

/**
 * DB helpers for Interceptor lead pages.
 */
public class InterceptorLeadPageHelper extends BaseHelper {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:44 InterceptorLeadPageHelper.java NSI";

    /**
     * Constructor.
     */
    public InterceptorLeadPageHelper(String logComponent) {
        super(logComponent);
    }

    /**
     * Constructor.
     */
    public InterceptorLeadPageHelper(String logComponent, boolean logSqlStatements) {
        super(logComponent, logSqlStatements);
    }

    /**
     * Constructor.
     */
    public InterceptorLeadPageHelper(Log logger) {
        super(logger);
    }

    /**
     * Constructor.
     */
    public InterceptorLeadPageHelper(Log logger, boolean logSqlStatements) {
        super(logger, logSqlStatements);
    }

    /**
     * Delete all lead pages for the specified product instance ID.
     */
    public void deleteLeadPages(String logTag, Connection connection, String prodInstId) throws SQLException {
        final String SQL =
            "DELETE FROM interceptor_lead_pages " +
            "WHERE  prod_inst_id = ?;";

        deleteForProdInstId(logTag, connection, prodInstId, SQL);
    }

    /**
     * Return the form pages for the specified product instance ID.
     */
    public Collection<String> getFormPages(String logTag, Connection connection, String prodInstId) throws SQLException {
        final String SQL =
            "SELECT path " +
            "FROM   interceptor_lead_pages " +
            "WHERE  prod_inst_id = ?" +
            "       AND form = TRUE;";

        return getPages(logTag, connection, SQL, prodInstId);
    }

    /**
     * Return the high value pages for the specified product instance ID.
     */
    public Collection<String> getHighValuePages(String logTag, Connection connection, String prodInstId) throws SQLException {
        final String SQL =
            "SELECT path " +
            "FROM   interceptor_lead_pages " +
            "WHERE  prod_inst_id = ?" +
            "       AND high_value = TRUE;";

        return getPages(logTag, connection, SQL, prodInstId);
    }

    /**
     * Return the shopping cart pages for the specified product instance ID.
     */
    public Collection<String> getShoppingCartPages(String logTag, Connection connection, String prodInstId) throws SQLException {
        final String SQL =
            "SELECT path " +
            "FROM   interceptor_lead_pages " +
            "WHERE  prod_inst_id = ?" +
            "       AND shopping_cart = TRUE;";

        return getPages(logTag, connection, SQL, prodInstId);
    }

    /**
     * Return the Interceptor lead pages for the specified product instance ID.
     */
    public Collection<InterceptorLeadPage> getLeadPages(String logTag, Connection connection, String prodInstId) throws SQLException {
        final String SQL =
            "SELECT prod_inst_id," +
            "       path," +
            "       description," +
            "       form," +
            "       high_value," +
            "       shopping_cart " +
            "FROM   interceptor_lead_pages " +
            "WHERE  prod_inst_id = ?;";

        return newListFromProdInstId(logTag, connection, prodInstId, SQL, new Factory<InterceptorLeadPage>() {
            public InterceptorLeadPage newInstance(ResultSet resultSet) throws SQLException {
                InterceptorLeadPage interceptorLeadPage = new InterceptorLeadPage();
                interceptorLeadPage.setDescription(resultSet.getString("description"));
                interceptorLeadPage.setFormPage(resultSet.getBoolean("form"));
                interceptorLeadPage.setHighValuePage(resultSet.getBoolean("high_value"));
                interceptorLeadPage.setPath(resultSet.getString("path"));
                interceptorLeadPage.setProdInstId(resultSet.getString("prod_inst_id"));
                interceptorLeadPage.setShoppingCartPage(resultSet.getBoolean("shopping_cart"));
                return interceptorLeadPage;
            }});
    }

    /**
     * Insert the specified Interceptor lead page.
     */
    public void insertLeadPage(String logTag, Connection connection, InterceptorLeadPage interceptorLeadPage) throws SQLException {
        final String SQL =
            "INSERT IGNORE INTO interceptor_lead_pages" +
            "  (prod_inst_id, path, description, form, high_value, shopping_cart) " +
            "VALUES" +
            "  (?, ?, ?, ?, ?, ?);";

        insertForParameters(logTag, connection, SQL,
                interceptorLeadPage.getProdInstId(),
                interceptorLeadPage.getPath(),
                interceptorLeadPage.getDescription(),
                Boolean.valueOf(interceptorLeadPage.isFormPage()),
                Boolean.valueOf(interceptorLeadPage.isHighValuePage()),
                Boolean.valueOf(interceptorLeadPage.isShoppingCartPage()));
    }

    /**
     * Return pages.
     */
    private Collection<String> getPages(String logTag, Connection connection, String sql, String prodInstId) throws SQLException {
        return newListFromProdInstId(logTag, connection, prodInstId, sql, new StringFactory("path") {});
    }
}
