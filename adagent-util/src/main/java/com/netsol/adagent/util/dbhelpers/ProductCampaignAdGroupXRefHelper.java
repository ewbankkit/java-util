/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.dbhelpers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

import org.apache.commons.logging.Log;

import com.netsol.adagent.util.beans.ProductCampaignAdGroupXRef;
import com.netsol.adagent.util.codes.AdGroupStatus;
import com.netsol.adagent.util.codes.CampaignStatus;
import com.netsol.adagent.util.codes.ProductStatus;
import com.netsol.adagent.util.codes.VendorId;

/**
 * DB helpers for product instance ID, NS campaign, NS ad group cross-references.
 */
public class ProductCampaignAdGroupXRefHelper extends BaseHelper {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:53 ProductCampaignAdGroupXRefHelper.java NSI";

    /**
     * Constructor.
     */
    public ProductCampaignAdGroupXRefHelper(String logComponent) {
        super(logComponent);
    }

    /**
     * Constructor.
     */
    public ProductCampaignAdGroupXRefHelper(Log logger, boolean logSqlStatements) {
        super(logger, logSqlStatements);
    }

    /**
     * Delete all rows.
     */
    public void deleteAll(String logTag, Connection connection) throws SQLException {
        final String SQL =
            "DELETE FROM" +
            "  product_campaign_ad_group_xref;";

        deleteForParameters(logTag, connection, SQL);
    }

    /**
     * Return all product instance ID, NS campaign, NS ad group cross-references from the PDB.
     */
    public Collection<ProductCampaignAdGroupXRef> getAllProductCampaignAdGroupXRefsFromPdb(String logTag, Connection connection) throws SQLException {
        final String SQL =
            "SELECT nsag.prod_inst_id   AS prod_inst_id," +
            "       nsag.ns_campaign_id AS ns_campaign_id," +
            "       nsag.ns_ad_group_id AS ns_ad_group_id " +
            "FROM   ns_ad_group AS nsag" +
            "       INNER JOIN ns_campaign AS nsc" +
            "         ON nsc.prod_inst_id = nsag.prod_inst_id" +
            "            AND nsc.ns_campaign_id = nsag.ns_campaign_id" +
            "       INNER JOIN product AS p" +
            "         ON p.prod_inst_id = nsc.prod_inst_id " +
            // SuperPages campaigns use a dummy DELETED ad group.
            "WHERE  IF(nsag.vendor_id = ?, TRUE, nsag.status <> ?)" +
            "       AND nsc.status <> ?" +
            "       AND p.status <> ?;";

        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(SQL);
            int parameterIndex = 1;
            statement.setInt(parameterIndex++, VendorId.SUPERPAGES);
            statement.setString(parameterIndex++, AdGroupStatus.DELETED);
            statement.setString(parameterIndex++, CampaignStatus.DELETED);
            statement.setString(parameterIndex++, ProductStatus.DELETED);
            logSqlStatement(logTag, statement);
            resultSet = statement.executeQuery();
            return newList(resultSet, ProductCampaignAdGroupXRefFactory.INSTANCE);
        }
        finally {
            close(statement, resultSet);
        }
    }

    /**
     * Insert data.
     */
    public int insertProductCampaignAdGroupXRefs(String logTag, Connection connection, Collection<ProductCampaignAdGroupXRef> productCampaignAdGroupXRefs) throws SQLException {
        final String SQL =
            "INSERT INTO product_campaign_ad_group_xref" +
            "  (prod_inst_id, ns_campaign_id, ns_ad_group_id) " +
            "VALUES" +
            "  (?, ?, ?);";

        return insertAll(logTag, connection, SQL, productCampaignAdGroupXRefs, new ParametersSetter<ProductCampaignAdGroupXRef>() {
            public void setParameters(PreparedStatement statement, ProductCampaignAdGroupXRef productCampaignAdGroupXRef) throws SQLException {
                statement.setString(1, productCampaignAdGroupXRef.getProdInstId());
                statement.setLong(2, productCampaignAdGroupXRef.getNsCampaignId());
                statement.setLong(3, productCampaignAdGroupXRef.getNsAdGroupId());
            }});
    }

    /**
     * Factory class used to create an object from a result set.
     */
    private static class ProductCampaignAdGroupXRefFactory implements Factory<ProductCampaignAdGroupXRef> {
        public static final ProductCampaignAdGroupXRefFactory INSTANCE = new ProductCampaignAdGroupXRefFactory();

        /**
         * Constructor.
         */
        private ProductCampaignAdGroupXRefFactory() {}

        /**
         * Return a new instance with values from the result set.
         */
        public ProductCampaignAdGroupXRef newInstance(ResultSet resultSet) throws SQLException {
            ProductCampaignAdGroupXRef productCampaignAdGroupXRef= new ProductCampaignAdGroupXRef();
            productCampaignAdGroupXRef.setNsAdGroupId(resultSet.getLong("ns_ad_group_id"));
            productCampaignAdGroupXRef.setNsCampaignId(resultSet.getLong("ns_campaign_id"));
            productCampaignAdGroupXRef.setProdInstId(resultSet.getString("prod_inst_id"));
            return productCampaignAdGroupXRef;
        }
    }
}
