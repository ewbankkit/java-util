/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.dbhelpers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.logging.Log;

import com.netsol.adagent.util.beans.PpcProductDetail;
import com.netsol.adagent.util.log.BaseLoggable;

public class PpcProductDetailHelper extends BaseHelper {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:52 PpcProductDetailHelper.java NSI";

    /**
     * Constructor.
     */
    public PpcProductDetailHelper(String logComponent) {
        super(logComponent);
    }

    /**
     * Constructor.
     */
    public PpcProductDetailHelper(String logComponent, boolean logSqlStatements) {
        super(logComponent, logSqlStatements);
    }

    /**
     * Constructor.
     */
    public PpcProductDetailHelper(Log logger) {
        super(logger);
    }

    /**
     * Constructor.
     */
    public PpcProductDetailHelper(BaseLoggable baseLoggable) {
        super(baseLoggable);
    }

    /**
     * Return any PPC product details for the specified product instance ID.
     */
    public PpcProductDetail getPpcProductDetail(String logTag, Connection connection, String prodInstId) throws SQLException {
        final String SQL =
            "SELECT" +
            "  prod_inst_id," +
            "  max_budget," +
            "  subscription_fee," +
            "  debit_cpc_markup," +
            "  debit_phone_lead_cost," +
            "  debit_email_lead_cost," +
            "  debit_form_submit_lead_cost," +
            "  debit_page_load_lead_cost," +
            "  debit_shop_cart_lead_cost," +
            "  debit_unanswered_phone_lead_cost," +
            "  cpc_markup," +
            "  phone_lead_cost," +
            "  email_lead_cost," +
            "  form_submit_lead_cost," +
            "  page_load_lead_cost," +
            "  shop_cart_lead_cost," +
            "  unanswered_phone_lead_cost," +
            "  click_threshold," +
            "  lead_threshold," +
            "  optimize_budget," +
            "  optimize_ad_groups," +
            "  optimize_keywords," +
            "  optimize_ads," +
            "  optimize_bids," +
            "  optimize_quality_score " +
            "FROM" +
            "  ppc_product_detail " +
            "WHERE" +
            "  prod_inst_id = ?;";

        return singleValueFromProdInstId(logTag, connection, prodInstId, SQL, PpcProductDetailFactory.INSTANCE);
    }

    /**
     * Insert a PPC product detail record. Update the record if it already exists.
     */
    public void insertOrUpdatePpcProductDetail(String logTag, Connection connection, PpcProductDetail ppcProductDetail) throws SQLException {
        final String SQL =
            "INSERT INTO ppc_product_detail (" +            
            "  prod_inst_id," +
            "  max_budget," +
            "  subscription_fee," +
            "  debit_cpc_markup," +
            "  debit_phone_lead_cost," +
            "  debit_email_lead_cost," +
            "  debit_form_submit_lead_cost," +
            "  debit_page_load_lead_cost," +
            "  debit_shop_cart_lead_cost," +
            "  debit_unanswered_phone_lead_cost," +
            "  cpc_markup," +
            "  phone_lead_cost," +
            "  email_lead_cost," +
            "  form_submit_lead_cost," +
            "  page_load_lead_cost," +
            "  shop_cart_lead_cost," +
            "  unanswered_phone_lead_cost," +
            "  click_threshold," +
            "  lead_threshold," +
            "  optimize_budget," +
            "  optimize_ad_groups," +
            "  optimize_keywords," +
            "  optimize_ads," +
            "  optimize_bids," +
            "  optimize_quality_score, " +
            "  created_date, updated_date, updated_by_user, updated_by_system) " +
            "VALUES" +
            "  (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,  NOW(), NOW(), ?, ?) " +
            "ON DUPLICATE KEY UPDATE" +
            "  max_budget = VALUES(max_budget)," +
            "  subscription_fee = VALUES(subscription_fee)," +
            "  debit_cpc_markup = VALUES(debit_cpc_markup)," +
            "  debit_phone_lead_cost = VALUES(debit_phone_lead_cost)," +
            "  debit_email_lead_cost = VALUES(debit_email_lead_cost)," +
            "  debit_form_submit_lead_cost = VALUES(debit_form_submit_lead_cost)," +
            "  debit_page_load_lead_cost = VALUES(debit_page_load_lead_cost)," +
            "  debit_shop_cart_lead_cost = VALUES(debit_shop_cart_lead_cost)," +
            "  debit_unanswered_phone_lead_cost = VALUES(debit_unanswered_phone_lead_cost)," +
            "  cpc_markup = VALUES(cpc_markup )," +
            "  phone_lead_cost = VALUES(phone_lead_cost )," +
            "  email_lead_cost = VALUES(email_lead_cost )," +
            "  form_submit_lead_cost = VALUES(form_submit_lead_cost )," +
            "  page_load_lead_cost = VALUES(page_load_lead_cost )," +
            "  shop_cart_lead_cost = VALUES(shop_cart_lead_cost )," +
            "  unanswered_phone_lead_cost = VALUES(unanswered_phone_lead_cost)," +
            "  click_threshold = VALUES(click_threshold)," +
            "  lead_threshold = VALUES(lead_threshold)," +
            "  optimize_budget = VALUES(optimize_budget)," +
            "  optimize_ad_groups = VALUES(optimize_ad_groups)," +
            "  optimize_keywords = VALUES(optimize_keywords)," +
            "  optimize_ads = VALUES(optimize_ads)," +
            "  optimize_bids = VALUES(optimize_bids)," +
            "  optimize_quality_score = VALUES(optimize_quality_score)," +
            "  updated_date = NOW()," +
            "  updated_by_user = VALUES(updated_by_user)," +
            "  updated_by_system = VALUES(updated_by_system);";

        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(SQL);
            int parameterIndex = 1;
            statement.setString(parameterIndex++, ppcProductDetail.getProdInstId());
            statement.setObject(parameterIndex++, ppcProductDetail.getMaxBudget());
            statement.setObject(parameterIndex++, ppcProductDetail.getSubscriptionFee());
            statement.setBoolean(parameterIndex++, ppcProductDetail.isDebitCpcMarkup());
            statement.setBoolean(parameterIndex++, ppcProductDetail.isDebitPhoneLeadCost());
            statement.setBoolean(parameterIndex++, ppcProductDetail.isDebitEmailLeadCost());
            statement.setBoolean(parameterIndex++, ppcProductDetail.isDebitFormLeadCost());
            statement.setBoolean(parameterIndex++, ppcProductDetail.isDebitHighValuePageLeadCost());
            statement.setBoolean(parameterIndex++, ppcProductDetail.isDebitShoppingCartLeadCost());
            statement.setBoolean(parameterIndex++, ppcProductDetail.isDebitUnansweredPhoneLeadCost());

            statement.setDouble(parameterIndex++, ppcProductDetail.getCpcMarkup());
            statement.setDouble(parameterIndex++, ppcProductDetail.getPhoneLeadCost());
            statement.setDouble(parameterIndex++, ppcProductDetail.getEmailLeadCost());
            statement.setDouble(parameterIndex++, ppcProductDetail.getFormLeadCost());
            statement.setDouble(parameterIndex++, ppcProductDetail.getHighValuePageLeadCost());
            statement.setDouble(parameterIndex++, ppcProductDetail.getShoppingCartLeadCost());
            statement.setDouble(parameterIndex++, ppcProductDetail.getUnansweredPhoneLeadCost());
            
            statement.setObject(parameterIndex++, ppcProductDetail.getClickThreshold());
            statement.setObject(parameterIndex++, ppcProductDetail.getLeadThreshold());
            statement.setBoolean(parameterIndex++, ppcProductDetail.isOptimizeBudget());
            statement.setBoolean(parameterIndex++, ppcProductDetail.isOptimizeAdGroups());
            statement.setBoolean(parameterIndex++, ppcProductDetail.isOptimizeKeywords());
            statement.setBoolean(parameterIndex++, ppcProductDetail.isOptimizeAds());
            statement.setBoolean(parameterIndex++, ppcProductDetail.isOptimizeBids());
            statement.setBoolean(parameterIndex++, ppcProductDetail.isOptimizeQualityScore());
            statement.setString(parameterIndex++, ppcProductDetail.getUpdatedByUser());
            statement.setString(parameterIndex++, ppcProductDetail.getUpdatedBySystem());
            logSqlStatement(logTag, statement);
            statement.executeUpdate();
        }
        finally {
            close(statement);
        }
    }

    /**
     * Factory class used to create PpcProductDetail objects from a result set.
     */
    private static class PpcProductDetailFactory implements Factory<PpcProductDetail> {
        public static final PpcProductDetailFactory INSTANCE = new PpcProductDetailFactory();

        /**
         * Constructor.
         */
        private PpcProductDetailFactory() {}

        /**
         * Return a new instance with values from the result set.
         */
        public PpcProductDetail newInstance(ResultSet resultSet) throws SQLException {
            PpcProductDetail ppcProductDetail= new PpcProductDetail();
            ppcProductDetail.setClickThreshold(getLongValue(resultSet, "click_threshold"));
            ppcProductDetail.setDebitCpcMarkup(resultSet.getBoolean("debit_cpc_markup"));
            ppcProductDetail.setDebitEmailLeadCost(resultSet.getBoolean("debit_email_lead_cost"));
            ppcProductDetail.setDebitFormLeadCost(resultSet.getBoolean("debit_form_submit_lead_cost"));
            ppcProductDetail.setDebitHighValuePageLeadCost(resultSet.getBoolean("debit_page_load_lead_cost"));
            ppcProductDetail.setDebitPhoneLeadCost(resultSet.getBoolean("debit_phone_lead_cost"));
            ppcProductDetail.setDebitShoppingCartLeadCost(resultSet.getBoolean("debit_shop_cart_lead_cost"));
            ppcProductDetail.setDebitUnansweredPhoneLeadCost(resultSet.getBoolean("debit_unanswered_phone_lead_cost"));
            
            ppcProductDetail.setCpcMarkup(resultSet.getDouble("cpc_markup"));
            ppcProductDetail.setEmailLeadCost(resultSet.getDouble("email_lead_cost"));
            ppcProductDetail.setFormLeadCost(resultSet.getDouble("form_submit_lead_cost"));
            ppcProductDetail.setHighValuePageLeadCost(resultSet.getDouble("page_load_lead_cost"));
            ppcProductDetail.setPhoneLeadCost(resultSet.getDouble("phone_lead_cost"));
            ppcProductDetail.setShoppingCartLeadCost(resultSet.getDouble("shop_cart_lead_cost"));
            ppcProductDetail.setUnansweredPhoneLeadCost(resultSet.getDouble("unanswered_phone_lead_cost"));
            
            ppcProductDetail.setLeadThreshold(getLongValue(resultSet, "lead_threshold"));
            ppcProductDetail.setOptimizeAdGroups(resultSet.getBoolean("optimize_ad_groups"));
            ppcProductDetail.setOptimizeAds(resultSet.getBoolean("optimize_ads"));
            ppcProductDetail.setOptimizeBids(resultSet.getBoolean("optimize_bids"));
            ppcProductDetail.setOptimizeBudget(resultSet.getBoolean("optimize_budget"));
            ppcProductDetail.setOptimizeKeywords(resultSet.getBoolean("optimize_keywords"));
            ppcProductDetail.setOptimizeQualityScore(resultSet.getBoolean("optimize_quality_score"));
            ppcProductDetail.setMaxBudget(getDoubleValue(resultSet, "max_budget"));
            ppcProductDetail.setProdInstId(resultSet.getString("prod_inst_id"));
            ppcProductDetail.setSubscriptionFee(getDoubleValue(resultSet, "subscription_fee"));
            return ppcProductDetail;
        }
    }
}
