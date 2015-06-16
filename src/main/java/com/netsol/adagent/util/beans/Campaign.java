package com.netsol.adagent.util.beans;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.netsol.adagent.util.BudgetManagerException;
import com.netsol.adagent.util.dbhelpers.BaseHelper;

public class Campaign extends BaseData {
	public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:53:52 Campaign.java NSI";

	private double dailyBudget;
	private Double monthlyBudget;
	private long nsCampaignId;
	private double percentOfBudget;
	private String prodInstId;
	private double spendAggressiveness;
	private String status;
	private Long targetId;
	private int vendorId;

    public double getDailyBudget() {
        return dailyBudget;
    }

    public void setDailyBudget(double dailyBudget) {
        this.dailyBudget = dailyBudget;
    }

    public Double getMonthlyBudget() {
        return monthlyBudget;
    }

    public void setMonthlyBudget(Double monthlyBudget) {
        this.monthlyBudget = monthlyBudget;
    }

    public long getNsCampaignId() {
        return nsCampaignId;
    }

    public void setNsCampaignId(long nsCampaignId) {
        this.nsCampaignId = nsCampaignId;
    }

    public double getPercentOfBudget() {
        return percentOfBudget;
    }

    public void setPercentOfBudget(double percentOfBudget) {
        this.percentOfBudget = percentOfBudget;
    }

    public String getProdInstId() {
        return prodInstId;
    }

    public void setProdInstId(String prodInstId) {
        this.prodInstId = prodInstId;
    }

    public double getSpendAggressiveness() {
        return spendAggressiveness;
    }

    public void setSpendAggressiveness(double spendAggressiveness) {
        this.spendAggressiveness = spendAggressiveness;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getTargetId() {
        return targetId;
    }

    public void setTargetId(Long targetId) {
        this.targetId = targetId;
    }

    public int getVendorId() {
        return vendorId;
    }

    public void setVendorId(int vendorId) {
        this.vendorId = vendorId;
    }

    private static final String PERSIST_CAMPAIGN_DATA = "UPDATE ns_campaign SET daily_budget = ?, percent_of_budget = ?, monthly_budget = ?, updated_date = NOW(), updated_by_user = 'Budget Manager', updated_by_system = ? WHERE prod_inst_id = ? AND ns_campaign_id = ?;";

    public void persist(Connection conn, String updatedBySystem) throws BudgetManagerException {
        PreparedStatement pstmt = null;

        try {
            pstmt = conn.prepareStatement(PERSIST_CAMPAIGN_DATA);
            int i = 1;
            pstmt.setDouble(i++, getDailyBudget());
            pstmt.setDouble(i++, getPercentOfBudget());
            pstmt.setObject(i++, getMonthlyBudget());
            pstmt.setString(i++, updatedBySystem);
            pstmt.setString(i++, getProdInstId());
            pstmt.setLong(i++, getNsCampaignId());
            pstmt.executeUpdate();
        } catch (SQLException sqle) {
            throw new BudgetManagerException(BudgetManagerException.DATABASE_ERROR, "Error occurred accessing DB in Campaign.", ""+sqle, sqle);
        } catch (Exception e) {
            throw new BudgetManagerException(BudgetManagerException.UNKNOWN_ERROR, "Unexpected Error occurred in Campaign.", ""+e, e);
        } finally {
            BaseHelper.close(pstmt);
        }
    }
}
