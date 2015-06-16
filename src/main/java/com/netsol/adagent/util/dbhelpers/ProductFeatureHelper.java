/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.dbhelpers;

import static com.netsol.adagent.util.beans.BaseData.arrayIsEmpty;
import static com.netsol.adagent.util.beans.BaseData.coalesce;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.logging.Log;

import com.netsol.adagent.util.beans.ProductFeature;
import com.netsol.adagent.util.log.BaseLoggable;

public class ProductFeatureHelper extends BaseHelper {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:53 ProductFeatureHelper.java NSI";

    /**
     * Constructor.
     */
    public ProductFeatureHelper(String logComponent) {
        super(logComponent);
    }

    /**
     * Constructor.
     */
    public ProductFeatureHelper(String logComponent, boolean logSqlStatements) {
        super(logComponent, logSqlStatements);
    }

    /**
     * Constructor.
     */
    public ProductFeatureHelper(Log logger) {
        super(logger);
    }

    /**
     * Constructor.
     */
    public ProductFeatureHelper(BaseLoggable baseLoggable) {
        super(baseLoggable);
    }

    public int[] getFeatureIdsExcludingStatuses(String logTag, Connection connection, String prodInstId, String... excludedProductFeatureStatuses) throws SQLException {
        List<ProductFeature> productFeatures = getProductFeaturesExcludingStatuses(logTag, connection, prodInstId, excludedProductFeatureStatuses);
        int length = productFeatures.size();
        int[] featuresIds = new int[length];
        for (int i = 0; i < length; i++) {
            featuresIds[i] = productFeatures.get(i).getFeatureId();
        }
        return featuresIds;
    }

    public List<ProductFeature> getProductFeaturesExcludingStatuses(String logTag, Connection connection, String prodInstId, String... excludedProductFeatureStatuses) throws SQLException {
        final String SQL =
            "SELECT prod_inst_id," +
            "       feature_id," +
            "       status " +
            "FROM   product_feature " +
            "WHERE  prod_inst_id = ?" +
            "       AND IF(?, TRUE, status NOT IN (%1$s));";

        excludedProductFeatureStatuses = coalesce(excludedProductFeatureStatuses, new String[0]);

        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(String.format(SQL, getInClauseValuesSnippet(excludedProductFeatureStatuses.length)));
            int parameterIndex = 1;
            statement.setString(parameterIndex++, prodInstId);
            statement.setBoolean(parameterIndex++, arrayIsEmpty(excludedProductFeatureStatuses));
            parameterIndex = setInClauseParameters(statement, parameterIndex, excludedProductFeatureStatuses);
            logSqlStatement(logTag, statement);
            resultSet = statement.executeQuery();
            return newList(resultSet, new Factory<ProductFeature>() {
                public ProductFeature newInstance(ResultSet resultSet) throws SQLException {
                    ProductFeature productFeature = new ProductFeature();
                    productFeature.setFeatureId(resultSet.getInt("feature_id"));
                    productFeature.setProdInstId(resultSet.getString("prod_inst_id"));
                    productFeature.setStatus(resultSet.getString("status"));
                    return productFeature;
                }});
        }
        finally {
            close(statement, resultSet);
        }
    }

    public void insertOrUpdateProductFeature(String logTag, Connection connection, ProductFeature productFeature) throws SQLException {
        final String SQL =
            "INSERT INTO product_feature" +
            " (prod_inst_id," +
            "  feature_id," +
            "  status) " +
            "VALUES" +
            " (?, ?, ?) " +
            "ON DUPLICATE KEY UPDATE" +
            "  status = VALUES(status);";

        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(SQL);
            int parameterIndex = 1;
            statement.setString(parameterIndex++, productFeature.getProdInstId());
            statement.setInt(parameterIndex++, productFeature.getFeatureId());
            statement.setString(parameterIndex++, productFeature.getStatus());
            logSqlStatement(logTag, statement);
            statement.executeUpdate();
        }
        finally {
            BaseHelper.close(statement);
        }
    }
}
