/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.beans;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;

import com.netsol.adagent.util.BudgetManagerException;
import com.netsol.adagent.util.TrackingUtil;
import com.netsol.adagent.util.budgetadj.BudgetAdjustment;
import com.netsol.adagent.util.budgetadj.BudgetAdjustmentFactory;
import com.netsol.adagent.util.codes.LeadType;
import com.netsol.adagent.util.codes.LeadTypePrefix;
import com.netsol.adagent.util.dbhelpers.BaseHelper;
import com.netsol.adagent.util.dbhelpers.PpcProductDetailHelper;
import com.netsol.adagent.util.log.BaseLoggable;

public class PPCLead extends PPCDebitableItem {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:03 PPCLead.java NSI";

    private long leadId;
    private int rating;
    private double cost;
    private double tollCost;
    private LeadTypePrefix prefix;
    private boolean duplicate;

    private Map<String, String> dirtyFields;

    private final DbHelper dbHelper;

    /**
     * Constructor.
     */
    public PPCLead(String logComponent) {
        super(logComponent);
        dbHelper = new DbHelper(logComponent);
    }

    /**
     * Constructor.
     */
    public PPCLead(Log logger) {
        super(logger);
        dbHelper = new DbHelper(logger);
    }

    /**
     * Constructor.
     */
    public PPCLead(BaseLoggable baseLoggable) {
        super(baseLoggable);
        dbHelper = new DbHelper(baseLoggable);
    }

    public void init(Connection conn, Integer channelId, String prodInstId, long leadId, double tollCost) throws BudgetManagerException {
        logTag = getLogTag(prodInstId);
        logInfo(logTag, "PPC-LEAD -> init(): ENTER");

        try {
            LeadInfo leadInfo = dbHelper.getLeadInfo(logTag, conn, prodInstId, leadId);
            if (leadInfo == null) {
                logError(logTag, "PPC-LEAD -> init(): ERROR retrieving lead data: No matches found!");
                throw new BudgetManagerException(BudgetManagerException.DATABASE_ERROR, "ERROR retrieving lead data: No matches found!");
            }

            this.leadId = leadId;
            this.tollCost = tollCost;
            setNsAdGroupId(leadInfo.nsAdGroupId);
            setNsAdId(leadInfo.nsAdId);
            setNsKeywordId(leadInfo.nsKeywordId);
            setNsCampaignId(leadInfo.nsCampaignId);
            setProdInstId(prodInstId);

            switch (leadInfo.leadTypeId) {
            case LeadType.PHONE_LEAD:            prefix = LeadTypePrefix.PHONE_LEAD;            break;
            case LeadType.FORM_LEAD:             prefix = LeadTypePrefix.FORM_LEAD;             break;
            case LeadType.EMAIL_LEAD:            prefix = LeadTypePrefix.EMAIL_LEAD;            break;
            case LeadType.HIGH_VALUE_PAGE_LEAD:  prefix = LeadTypePrefix.HIGH_VALUE_PAGE_LEAD;  break;
            case LeadType.SHOPPING_CART_LEAD:    prefix = LeadTypePrefix.SHOPPING_CART_LEAD;    break;
            case LeadType.UNANSWERED_PHONE_LEAD: prefix = LeadTypePrefix.UNANSWERED_PHONE_LEAD; break;
            }

            // Adjust the lead date to the time zone of any associated vendor.
            int vendorId = 0;
            if ((leadInfo.leadTypeId == LeadType.PHONE_LEAD) || (leadInfo.leadTypeId == LeadType.UNANSWERED_PHONE_LEAD)) {
                // For call leads use the time zone of the call tracking vendor.
                vendorId = dbHelper.getCallTrackingVendorId(prodInstId, conn, prodInstId, leadId);
            }
            else if (leadInfo.nsCampaignId > 0L) {
                vendorId = budgetManagerHelper.getSingleCampaignInfo(conn, prodInstId, leadInfo.nsCampaignId).getCampaigns().get(0).getVendorId();
            }
            if (vendorId > 0) {
                leadInfo.leadDate = budgetManagerHelper.shiftClickOrLeadDate(conn, prodInstId, vendorId, leadInfo.leadDate);
            }
            setDate(leadInfo.leadDate);

            logInfo(logTag, "PPC-LEAD -> init(): Successfully retrieved lead data!");

            PricingInfo pricingInfo = dbHelper.getPricingInfo(prodInstId, conn, prodInstId, prefix.getId(), channelId);
            if (pricingInfo == null) {
                logError(logTag, "PPC-LEAD -> init(): ERROR retrieving pricing data: No matches found!");
                throw new BudgetManagerException(BudgetManagerException.DATABASE_ERROR, "ERROR retrieving pricing data: No matches found!");
            }

            cost = pricingInfo.cost;
            rating = pricingInfo.rating;

            logInfo(logTag, "PPC-LEAD -> init(): Successfully retrieved pricing data!");

            dirtyFields = new HashMap<String, String>();

            logInfo(logTag, "PPC-LEAD -> init(): COMPLETE!");
        }
        catch (SQLException ex) {
            logError(logTag, ex);
            throw new BudgetManagerException(BudgetManagerException.DATABASE_ERROR, "Generic DB Error", ex.getMessage(), ex);
        }
    }

    @Override
    public boolean validate(Connection conn) throws BudgetManagerException {
        return true;
    }

    @Override
    public void calculateCosts(Connection pdbConn, Product product) throws BudgetManagerException {
        // now that we have a lead_type_id, verify that this is not a duplicate lead
        if (budgetManagerHelper.isDuplicateLead(pdbConn, this)) {
            // since it is a dupe, set to zero values
            setRating(0);
            setCost(0D);
            setTollCost(0D);
            setDuplicate(true);

            logInfo(logTag, "debitLead -> Duplicate lead detected! Skipping pricing...");
            logInfo(logTag, "debitLead -> rating: "+rating);
            logInfo(logTag, "debitLead -> price: "+cost);
        } 
        else {
    		// Get the PpcProductDetail.
    		String prodInstId = product.getProdInstId();
    		PpcProductDetail ppcProductDetail = null;
    		try {
    			ppcProductDetail = new PpcProductDetailHelper(this).getPpcProductDetail(getLogTag(prodInstId), pdbConn, prodInstId);
    		}
    		catch(Exception e) {
    			this.logError(logTag, e);
    			throw new BudgetManagerException(BudgetManagerException.UNKNOWN_ERROR, "Error initializing ppcProductDetail", e.getMessage(), e);
    		}

            //  set the rating
            setRating(rating);

            if (ppcProductDetail == null || !ppcProductDetail.isLeadTypeDebited(prefix.getId())) {
                setCost(0D);
                setTollCost(0D);
            }
            else {
                setCost(ppcProductDetail.getLeadTypeCost(prefix.getId()));
                setTollCost(0D); // TODO remove all references to toll cost
                logInfo(logTag, "debitLead -> new price: "+cost);
                
                // now check if this is a phone call lead
                if (getPrefix().getId() == LeadType.PHONE_LEAD) {
                    TrackingUtil trackingUtil = new TrackingUtil(this);
                    boolean callTrackingEnabled = false;
                    try {
                        callTrackingEnabled = trackingUtil.isCallTrackingEnabled(getLogTag(prodInstId), pdbConn, prodInstId);
                    }
                    catch (SQLException ex) {}
                    if (!callTrackingEnabled) {
                        setCost(0D);
                        setTollCost(0D);
                        setRating(0);
                        logInfo(logTag, "debitLead -> phone lead tracking is off, not charging for lead: "+cost);
                    }

                    logInfo(logTag, "debitLead -> Phone info processed. New price: "+cost);
                }
            }
        }
    }

    @Override
    public void debit(Connection conn) throws BudgetManagerException {
        logInfo(logTag, "PPC-LEAD -> persist(): *** ENTER ***");
        logInfo(logTag, "debitLead -> persisting lead of type: "+getPrefix().getPrefix());

        // first, persist to the summaries
        // check to see if ad group is tagged
        if (getNsAdGroupId() != 0L) {
            // only do keyword if it exists
            if (getNsKeywordId() != 0L) {
                if (!budgetManagerHelper.debitLeadFromKeyword(conn, this)) { throw new BudgetManagerException(BudgetManagerException.DEBIT_ERROR, "Error debiting from keyword!"); }
            }

            // only do ad if it exists
            if (getNsAdId() != 0L) {
                if (!budgetManagerHelper.debitLeadFromAd(conn, this)) { throw new BudgetManagerException(BudgetManagerException.DEBIT_ERROR, "Error debiting from ad!"); }
            }

            // now do ad group and campaign (assume campaign exists since ad group exists)
            if (!budgetManagerHelper.debitLeadFromAdGroup(conn, this)) { throw new BudgetManagerException(BudgetManagerException.DEBIT_ERROR, "Error debiting from ad group!"); }
            if (!budgetManagerHelper.debitLeadFromCampaign(conn, this)) { throw new BudgetManagerException(BudgetManagerException.DEBIT_ERROR, "Error debiting from campaign!"); }
        }
        else if (getNsCampaignId() != 0L) {
            // Ensure that phone leads tracked at the campaign level are debited.
            if (!budgetManagerHelper.debitLeadFromCampaign(conn, this)) { throw new BudgetManagerException(BudgetManagerException.DEBIT_ERROR, "Error debiting from campaign!"); }
        }
        // regardless, persist to product level summary
        if (!budgetManagerHelper.debitLeadFromProduct(conn, this)) { throw new BudgetManagerException(BudgetManagerException.DEBIT_ERROR, "Error debiting from account!"); }

        // now, do the leads table (bean persistence)
        StringBuilder sb = new StringBuilder("UPDATE leads SET ");
        for (String field : dirtyFields.keySet()) {
            sb.append(dirtyFields.get(field)).append(" = ?").append(", ");
        }
        sb.append(" updated_by = 'BM.debit', updated_date = NOW() ");
        sb.append(" WHERE prod_inst_id = '").append(getProdInstId()).append("'");
        sb.append(" AND lead_id = ").append(leadId);

        PreparedStatement pstmt = null;

        try {
            if (conn == null || conn.isClosed()) {
                logError(logTag, "Error persisting PPC-LEAD data: Provided connection is NULL or CLOSED!");
                throw new BudgetManagerException(BudgetManagerException.DATABASE_ERROR, "Error persisting PPC-LEAD data: Provided connection is NULL or CLOSED!");
            }

            pstmt = conn.prepareStatement(sb.toString());
            int parameterIndex = 1;
            for (String fieldName : dirtyFields.keySet()) {
                pstmt.setObject(parameterIndex, getClass().getDeclaredField(fieldName).get(this));
                parameterIndex++;
            }

            logDebug(logTag, "PPC-LEAD -> persist(): About to execute PPC_LEAD_UPDATE: " + pstmt);
            if (pstmt.executeUpdate() <= 0) {
                logError(logTag, "PPC-LEAD -> persist(): Error persisting PRODUCT data: No rows updated! Product not found or concurrency conflict detected!");
                throw new Exception("Error persisting PRODUCT data: No rows updated! Product not found or concurrency conflict detected!");
            }

            logInfo(logTag, "PPC-LEAD -> persist(): *** COMPLETE ***");
        } catch (SQLException sqle) {
            logError(logTag, sqle);
            throw new BudgetManagerException(BudgetManagerException.DATABASE_ERROR, "Error occurred accessing DB in PRODUCT BEAN!", ""+sqle, sqle);
        } catch (Exception e) {
            logError(logTag, e);
            throw new BudgetManagerException(BudgetManagerException.UNKNOWN_ERROR, "Unexpected Error occurred in PRODUCT BEAN!", ""+e, e);
         } finally {
            BaseHelper.close(pstmt);
        }
    }

    @Override
    public void insertBudgetAdjustment(Connection connection) {
        try {
            String prodInstId = getProdInstId();
            ProductSummaryData productSum = new ProductSummaryData(this);
            productSum.init(connection, prodInstId);

            // Phone leads can have a NS campaign ID of 0.
            Long nsCampaignId = null; // The ns_campaign_id can be null for call leads using the default number.
            Double campaignDailyBudget = null; // The daily budget will be null if the campaign is null.
            if (getNsCampaignId() > 0) {
                nsCampaignId = getNsCampaignId();
                CampaignList campaignList = budgetManagerHelper.getSingleCampaignInfo(connection, prodInstId, nsCampaignId);
                Campaign campaign = campaignList.getCampaigns().get(0);
                campaignDailyBudget = campaign.getDailyBudget();
            }

            Double tollCost = null; // The toll_amount should be null for non-call leads.
            Long nsAdGroupId = (getNsAdGroupId() == 0 ? null : getNsAdGroupId()); // The ns_ad_group_id can be null for call leads using the default number.
            Long nsAdId = null; // The ns_ad_id should be null for call leads.
            Long nsKeywordId = null; // The ns_keyword_id should be null for call leads.
            if (prefix.getId() == LeadType.PHONE_LEAD) {
                tollCost = getTollCost();
            }
            else if (prefix.getId() != LeadType.UNANSWERED_PHONE_LEAD) {
                nsAdId = getNsAdId();
                nsKeywordId = getNsKeywordId();
            }

            BudgetAdjustmentFactory factory = BudgetAdjustmentFactory.getInstance();
            BudgetAdjustment budgetAdjustment = factory.getDebitLeadBudgetAdjustment(prodInstId, getDate(), getSystem(),
                    getLeadId(), prefix.getId(), getFullCost(), tollCost, nsCampaignId, nsAdGroupId,
                    nsAdId, nsKeywordId, productSum.getMonthlyBudgetRemaining(), productSum.getDailyBudgetRemaining(),
                    campaignDailyBudget);
            budgetAdjustment.insert(connection);
        } catch (Exception e) {
            // Log the error, but don't throw an exception since we would not want a debit to fail because of this error.
            logError(logTag, e);
        }
    }

    @Override
    public double getFullCost() {
        return cost;
    }

    public int getRating() {
        return rating;
    }

    public long getLeadId() {
        return leadId;
    }

    public LeadTypePrefix getPrefix() {
        return prefix;
    }

    public double getCost() {
        return cost;
    }

    public void setRating(int rating) {
        this.rating = rating;
        dirtyFields.put("rating", "lead_value");
    }

    public void setCost(double cost) {
        this.cost = cost;
        dirtyFields.put("cost", "generic_decimal2");
    }

    public double getTollCost() {
        return tollCost;
    }

    public void setTollCost(double tollCost) {
        this.tollCost = tollCost;
    }

    public void setDuplicate(boolean duplicate) {
        this.duplicate = duplicate;
        dirtyFields.put("duplicate", "is_duplicate");
    }

    public boolean isDuplicate() {
        return duplicate;
    }

    private static class DbHelper extends BaseHelper {
        /**
         * Constructor.
         */
        public DbHelper(String logComponent) {
            super(logComponent);
        }

        /**
         * Constructor.
         */
        public DbHelper(Log logger) {
            super(logger);
        }

        /**
         * Constructor.
         */
        public DbHelper(BaseLoggable baseLoggable) {
            super(baseLoggable);
        }

        public int getCallTrackingVendorId(String logTag, Connection connection, String prodInstId, long leadId) throws SQLException {
            final String SQL = "SELECT vendor_id FROM call_leads_v WHERE prod_inst_id = ? AND lead_id = ?;";

            PreparedStatement statement = null;
            ResultSet resultSet = null;
            try {
                statement = connection.prepareStatement(SQL);
                int parameterIndex = 1;
                statement.setString(parameterIndex++, prodInstId);
                statement.setLong(parameterIndex++, leadId);
                logSqlStatement(logTag, statement);
                resultSet = statement.executeQuery();

                return singleValue(resultSet, "vendor_id", 0);
            }
            finally {
                close(statement, resultSet);
            }
        }

        public LeadInfo getLeadInfo(String logTag, Connection connection, String prodInstId, long leadId) throws SQLException {
            final String SQL =
                "SELECT lead_date," +
                "       lead_type_id," +
                "       ns_campaign_id," +
                "       ns_ad_group_id," +
                "       ns_keyword_id," +
                "       ns_ad_id " +
                "FROM   leads " +
                "WHERE  prod_inst_id = ?" +
                "       AND lead_id = ?;";

            PreparedStatement statement = null;
            ResultSet resultSet = null;
            try {
                statement = connection.prepareStatement(SQL);
                int parameterIndex = 1;
                statement.setString(parameterIndex++, prodInstId);
                statement.setLong(parameterIndex++, leadId);
                logSqlStatement(logTag, statement);
                resultSet = statement.executeQuery();

                return singleValue(resultSet, new Factory<LeadInfo>() {
                    public LeadInfo newInstance(ResultSet resultSet) throws SQLException {
                        LeadInfo leadInfo = new LeadInfo();
                        leadInfo.leadDate = resultSet.getTimestamp("lead_date");
                        leadInfo.leadTypeId = resultSet.getInt("lead_type_id");
                        leadInfo.nsAdGroupId = resultSet.getLong("ns_ad_group_id");
                        leadInfo.nsAdId = resultSet.getLong("ns_ad_id");
                        leadInfo.nsCampaignId = resultSet.getLong("ns_campaign_id");
                        leadInfo.nsKeywordId = resultSet.getLong("ns_keyword_id");
                        return leadInfo;
                    }});
            }
            finally {
                close(statement, resultSet);
            }
        }

        public PricingInfo getPricingInfo(String logTag, Connection connection, String prodInstId, int leadTypeId, Integer channelId) throws SQLException {
            final String SQL =
                "SELECT pdr.rating AS rating," +
                "       bt.price   AS cost " +
                "FROM   product_lead_type_default_rating AS pdr" +
                "       INNER JOIN base_tier AS bt" +
                "         ON bt.lead_type_id = pdr.lead_type_id " +
                "WHERE  pdr.prod_inst_id = ?" +
                "       AND pdr.lead_type_id = ?" +
                "       AND bt.channel_id = ?;";

            PreparedStatement statement = null;
            ResultSet resultSet = null;
            try {
                statement = connection.prepareStatement(SQL);
                int parameterIndex = 1;
                statement.setString(parameterIndex++, prodInstId);
                statement.setInt(parameterIndex++, leadTypeId);
                statement.setObject(parameterIndex++, channelId);
                logSqlStatement(logTag, statement);
                resultSet = statement.executeQuery();

                return singleValue(resultSet, new Factory<PricingInfo>() {
                    public PricingInfo newInstance(ResultSet resultSet) throws SQLException {
                        PricingInfo pricingInfo = new PricingInfo();
                        pricingInfo.cost = resultSet.getDouble("cost");
                        pricingInfo.rating = resultSet.getInt("rating");
                        return pricingInfo;
                    }});
            }
            finally {
                close(statement, resultSet);
            }
        }
    }

    private static class LeadInfo {
        public Date leadDate;
        public int leadTypeId;
        public long nsAdGroupId;
        public long nsAdId;
        public long nsCampaignId;
        public long nsKeywordId;
    }

    private static class PricingInfo {
        public double cost;
        public int rating;
    }
}
