/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.dbhelpers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.logging.Log;

import com.netsol.adagent.util.beans.SeoProductDetail;

/**
 * DB helpers for SEO product details.
 */
public class SeoProductDetailHelper extends BaseHelper {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:55 SeoProductDetailHelper.java NSI";

    /**
     * Constructor.
     */
    public SeoProductDetailHelper(String logComponent) {
        super(logComponent);
    }

    /**
     * Constructor.
     */
    public SeoProductDetailHelper(Log logger) {
        super(logger);
    }

    /**
     * Return any SEO product details for the specified product instance ID.
     */
    public SeoProductDetail getSeoProductDetail(String logTag, Connection connection, String prodInstId) throws SQLException {
        final String SQL =
            "SELECT" +
            "  prod_inst_id, " +
            "  js_check_result," +
            "  js_check_date," +
            "  account_manager_user_name," +
            "  qa_copywriter_user_name," +
            "  copywriter_user_name " +
            "FROM" +
            "  seo_product_detail " +
            "WHERE" +
            "  prod_inst_id = ?;";

        return singleValueFromProdInstId(logTag, connection, prodInstId, SQL, SeoProductDetailFactory.INSTANCE);
    }

    /**
     * Insert (or update) the specified SEO product details.
     */
    public void insertOrUpdateSeoProductDetail(String logTag, Connection connection, SeoProductDetail seoProductDetail) throws SQLException {
        final String SQL =
            "INSERT INTO seo_product_detail" +
            " (prod_inst_id," +
            "  js_check_result," +
            "  js_check_date," +
            "  account_manager_user_name," +
            "  qa_copywriter_user_name," +
            "  copywriter_user_name," +
            "  updated_by_user," +
            "  updated_by_system," +
            "  created_date) " +
            "VALUES" +
            "  (?, ?, ?, ?, ?, ?, ?, ?, NOW()) " +
            "ON DUPLICATE KEY UPDATE" +
            "  js_check_result = VALUES(js_check_result)," +
            "  js_check_date = VALUES(js_check_date)," +
            "  account_manager_user_name = VALUES(account_manager_user_name)," +
            "  qa_copywriter_user_name = VALUES(qa_copywriter_user_name)," +
            "  copywriter_user_name = VALUES(copywriter_user_name)," +
            "  updated_by_user = VALUES(updated_by_user)," +
            "  updated_by_system = VALUES(updated_by_system);";

        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(SQL);
            int parameterIndex = 1;
            statement.setString(parameterIndex++, seoProductDetail.getProdInstId());
            statement.setObject(parameterIndex++, seoProductDetail.getJsCheckResult());
            statement.setTimestamp(parameterIndex++, toSqlTimestamp(seoProductDetail.getJsCheckDate()));
            statement.setString(parameterIndex++, seoProductDetail.getAccountManagerUserName());
            statement.setString(parameterIndex++, seoProductDetail.getQaCopywriterUserName());
            statement.setString(parameterIndex++, seoProductDetail.getCopywriterUserName());
            statement.setString(parameterIndex++, seoProductDetail.getUpdatedByUser());
            statement.setString(parameterIndex++, seoProductDetail.getUpdatedBySystem());
            logSqlStatement(logTag, statement);
            statement.executeUpdate();
        }
        finally {
            close(statement);
        }
    }

    /**
     * Factory class used to create SeoProductDetail objects from a result set.
     */
    private static class SeoProductDetailFactory implements Factory<SeoProductDetail> {
        public static final SeoProductDetailFactory INSTANCE = new SeoProductDetailFactory();

        /**
         * Constructor.
         */
        private SeoProductDetailFactory() {}

        /**
         * Return a new instance with values from the result set.
         */
        public SeoProductDetail newInstance(ResultSet resultSet) throws SQLException {
            SeoProductDetail seoProductDetail = new SeoProductDetail();
            seoProductDetail.setAccountManagerUserName(resultSet.getString("account_manager_user_name"));
            seoProductDetail.setJsCheckDate(resultSet.getTimestamp("js_check_date"));
            seoProductDetail.setJsCheckResult(BaseHelper.getBooleanValue(resultSet, "js_check_result"));
            seoProductDetail.setProdInstId(resultSet.getString("prod_inst_id"));
            seoProductDetail.setQaCopywriterUserName(resultSet.getString("qa_copywriter_user_name"));
            seoProductDetail.setCopywriterUserName(resultSet.getString("copywriter_user_name"));
            return seoProductDetail;
        }
    }
}
