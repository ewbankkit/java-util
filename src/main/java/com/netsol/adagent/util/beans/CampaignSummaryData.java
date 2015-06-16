/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.beans;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.commons.logging.Log;

import com.netsol.adagent.util.BudgetManagerException;
import com.netsol.adagent.util.dbhelpers.BaseHelper;
import com.netsol.adagent.util.log.BaseLoggable;

public class CampaignSummaryData extends PPCSummaryData {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:53:52 CampaignSummaryData.java NSI";

    private static final String PERSIST_CAMPAIGN_SUMMARY_DATA =
        "INSERT INTO ns_campaign_sum (prod_inst_id, ns_campaign_id, update_date, percent_of_budget) VALUES (?, ?, ?, ?) "
        + "ON DUPLICATE KEY UPDATE percent_of_budget = ?";

    private Connection connection;
    private CampaignList campaigns;

    /**
     * Constructor.
     */
    public CampaignSummaryData(String logComponent) {
        super(logComponent);
    }

    /**
     * Constructor.
     */
    public CampaignSummaryData(Log logger) {
        super(logger);
    }

    /**
     * Constructor.
     */
    public CampaignSummaryData(BaseLoggable baseLoggable) {
        super(baseLoggable);
    }

    public CampaignList getCampaigns() {
        return campaigns;
    }

    public void setCampaigns(CampaignList campaigns) {
        this.campaigns = campaigns;
    }

    @Override
    public void init(Connection conn, String prodInstId) throws BudgetManagerException {
        logTag = getLogTag(prodInstId);
        try {
            if (conn == null || conn.isClosed()) {
                logError(logTag, "Error initializing CAMPAIGN SUMMARY data: Provided connection is NULL or CLOSED!");
                throw new BudgetManagerException(BudgetManagerException.DATABASE_ERROR, "Error initializing CAMPAIGN SUMMARY data: Provided connection is NULL or CLOSED!");
            }

            this.connection = conn;
            setProdInstId(prodInstId);
        } catch (SQLException sqle) {
            logError(logTag, sqle);
            throw new BudgetManagerException(BudgetManagerException.DATABASE_ERROR, "Error occurred accessing DB in CAMPAIGN SUMMARY BEAN!", ""+sqle, sqle);
        }
    }

    @Override
    public void persist() throws BudgetManagerException {
        PreparedStatement pstmt = null;

        try {
            if (connection == null || connection.isClosed()) {
                logError(logTag, "Error persisting CAMPAIGN SUMMARY data: Provided connection is NULL or CLOSED!");
                throw new BudgetManagerException(BudgetManagerException.DATABASE_ERROR, "Error persisting CAMPAIGN SUMMARY data: Provided connection is NULL or CLOSED!");
            }

            pstmt = connection.prepareStatement(PERSIST_CAMPAIGN_SUMMARY_DATA);
            for (Campaign campaign : campaigns.getCampaigns()) {
                // INSERT values
                pstmt.setString(1, getProdInstId());
                pstmt.setLong(2, campaign.getNsCampaignId());
                pstmt.setDate(3, updateDate);
                pstmt.setDouble(4, campaign.getPercentOfBudget());

                // ON DUPLICATE KEY UPDATE values
                pstmt.setDouble(5, campaign.getPercentOfBudget());
                pstmt.addBatch();
            }
            logDebug(logTag, "CAMPAIGN SUMMARY -> persist(): About to execute PERSIST_CAMPAIGN_SUMMARY_DATA: " + pstmt);
            pstmt.executeBatch();
        } catch (SQLException sqle) {
            logError(logTag, sqle);
            throw new BudgetManagerException(BudgetManagerException.DATABASE_ERROR, "Error occurred accessing DB in CAMPAIGN SUMMARY BEAN!", ""+sqle, sqle);
        } catch (Exception e) {
            logError(logTag, e);
            throw new BudgetManagerException(BudgetManagerException.UNKNOWN_ERROR, "Unexpected Error occurred in CAMPAIGN SUMMARY BEAN!", ""+e, e);
         } finally {
            BaseHelper.close(pstmt);
        }
    }
}
