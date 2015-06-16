/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.dbhelpers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.logging.Log;

import com.netsol.adagent.util.beans.PctProductDetail;
import com.netsol.adagent.util.log.BaseLoggable;

public class PctProductDetailHelper extends BaseHelper {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:52 PctProductDetailHelper.java NSI";

    /**
     * Constructor.
     */
    public PctProductDetailHelper(String logComponent) {
        super(logComponent);
    }

    /**
     * Constructor.
     */
    public PctProductDetailHelper(String logComponent, boolean logSqlStatements) {
        super(logComponent, logSqlStatements);
    }

    /**
     * Constructor.
     */
    public PctProductDetailHelper(Log logger) {
        super(logger);
    }

    /**
     * Constructor.
     */
    public PctProductDetailHelper(BaseLoggable baseLoggable) {
        super(baseLoggable);
    }

    /**
     * Return any PCT product details for the specified product instance ID.
     */
    public PctProductDetail getPctProductDetail(String logTag, Connection connection, String prodInstId) throws SQLException {
        final String SQL =
            "SELECT" +
            "  prod_inst_id " +
            "FROM" +
            "  pct_product_detail " +
            "WHERE" +
            "  prod_inst_id = ?;";

        return singleValueFromProdInstId(logTag, connection, prodInstId, SQL, PctProductDetailFactory.INSTANCE);
    }

    /**
     * Insert a PCT product detail record. Update the record if it already exists.
     */
    public void insertOrUpdatePctProductDetail(String logTag, Connection connection, PctProductDetail pctProductDetail) throws SQLException {
        final String SQL =
            "INSERT INTO pct_product_detail" +
            "  (prod_inst_id," +
            "   created_date, updated_date, updated_by_user, updated_by_system) " +
            "VALUES" +
            "  (?," +
            "   NOW(), NOW(), ?, ?) " +
            "ON DUPLICATE KEY UPDATE" +
            "  updated_date = NOW()," +
            "  updated_by_user = VALUES(updated_by_user)," +
            "  updated_by_system = VALUES(updated_by_system);";

        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(SQL);
            int parameterIndex = 1;
            statement.setString(parameterIndex++, pctProductDetail.getProdInstId());
            statement.setString(parameterIndex++, pctProductDetail.getUpdatedByUser());
            statement.setString(parameterIndex++, pctProductDetail.getUpdatedBySystem());
            logSqlStatement(logTag, statement);
            statement.executeUpdate();
        }
        finally {
            close(statement);
        }
    }

    /**
     * Factory class used to create PctProductDetail objects from a result set.
     */
    private static class PctProductDetailFactory implements Factory<PctProductDetail> {
        public static final PctProductDetailFactory INSTANCE = new PctProductDetailFactory();

        /**
         * Constructor.
         */
        private PctProductDetailFactory() {}

        /**
         * Return a new instance with values from the result set.
         */
        public PctProductDetail newInstance(ResultSet resultSet) throws SQLException {
            PctProductDetail pctProductDetail= new PctProductDetail();
            pctProductDetail.setProdInstId(resultSet.getString("prod_inst_id"));
            return pctProductDetail;
        }
    }
}
