/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.budgetadj;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.netsol.adagent.util.dbhelpers.BaseHelper;

/**
 * Budget adjustment data.
 *
 * Note: The amounts are doubles even though floats would have been more than sufficient because the values are
 * already doubles everywhere in the applicOation.
 *
 * @author Adam S. Vernon
 */
public class BudgetAdjustment {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:13 BudgetAdjustment.java NSI";

	/**
	 * An enumeration of budget adjustment type values.
	 */
	public enum AdjustmentType {
		DEBIT_CLICK, DEBIT_LEAD, RECONCILE_CLICKS, RECONCILE_LEAD, RENEW, ADD_ON, UPDGRADE_DOWNGRADE, LEAD_REFUND, MANUAL_ADJ
	};

	/**
	 * An enumeration of budget adjustment system values.
	 */
	public enum System {
		AGGREGATOR, RECONCILER_BATCH, CALL_BATCH, DAILY_BUDGET_BATCH, DATA_SERVICES, WS_ADAGENT, MANUAL
	};

	private static final Log logger = LogFactory.getLog(BudgetAdjustment.class);
	private static final DbHelper dbHelper = new DbHelper(BudgetAdjustment.logger);

	private String prodInstId;
	private Date updateDate;
	private System system;
	private String hostname;
	private String user;
	private AdjustmentType adjustmentType;
	private Date adjustmentTimestamp;
	private Double adjustmentAmount;
	private Long hitId;
	private Date hitDate;
	private Double vendorClickAmount;
	private Double nsClickAmount;
	private Integer clickCountDiff;
	private Long leadId;
	private Integer leadTypeId;
	private Double leadAmount;
	private Double leadTollAmount;
	private Long nsCampaignId;
	private Long nsAdGroupId;
	private Long nsAdId;
	private Long nsKeywordId;
	private Double monthlyBudgetRemaining;
	private Double dailyBudgetRemaining;
	private Double campaignDailyBudget;
	private Integer vendorId;

	/**
	 * Constructor. Accessible only by classes in this package.
	 */
	BudgetAdjustment(String prodInstId) {
		this.prodInstId = prodInstId;
	}

	/**
	 * @return the prodInstId
	 */
	public String getProdInstId() {
		return prodInstId;
	}

	/**
	 * @param prodInstId the prodInstId to set
	 */
	public void setProdInstId(String prodInstId) {
		this.prodInstId = prodInstId;
	}

	/**
	 * @return the updateDate
	 */
	public Date getUpdateDate() {
		return updateDate;
	}

	/**
	 * @param updateDate the updateDate to set
	 */
	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	/**
	 * @return the system
	 */
	public System getSystem() {
		return system;
	}

	/**
	 * @param system the system to set
	 */
	public void setSystem(System system) {
		this.system = system;
	}

	/**
	 * @return the hostname
	 */
	public String getHostname() {
		return hostname;
	}

	/**
	 * @param hostname the hostname to set
	 */
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	/**
	 * @return the user
	 */
	public String getUser() {
		return user;
	}

	/**
	 * @param user the user to set
	 */
	public void setUser(String user) {
		this.user = user;
	}

	/**
	 * @return the adjustmentType
	 */
	public AdjustmentType getAdjustmentType() {
		return adjustmentType;
	}

	/**
	 * @param adjustmentType the adjustmentType to set
	 */
	public void setAdjustmentType(AdjustmentType adjustmentType) {
		this.adjustmentType = adjustmentType;
	}

	/**
	 * @return the adjustmentTimestamp
	 */
	public Date getAdjustmentTimestamp() {
		return adjustmentTimestamp;
	}

	/**
	 * @param adjustmentTimestamp the adjustmentTimestamp to set
	 */
	public void setAdjustmentTimestamp(Date adjustmentTimestamp) {
		this.adjustmentTimestamp = adjustmentTimestamp;
	}

	/**
	 * @return the adjustmentAmount
	 */
	public Double getAdjustmentAmount() {
		return adjustmentAmount;
	}

	/**
	 * @param adjustmentAmount the adjustmentAmount to set
	 */
	public void setAdjustmentAmount(Double adjustmentAmount) {
		this.adjustmentAmount = adjustmentAmount;
	}

	/**
	 * @return the hitId
	 */
	public Long getHitId() {
		return hitId;
	}

	/**
	 * @param hitId the hitId to set
	 */
	public void setHitId(Long hitId) {
		this.hitId = hitId;
	}

	/**
	 * @return the hitDate
	 */
	public Date getHitDate() {
		return hitDate;
	}

	/**
	 * @param hitDate the hitDate to set
	 */
	public void setHitDate(Date hitDate) {
		this.hitDate = hitDate;
	}

	/**
	 * @return the vendorClickAmount
	 */
	public Double getVendorClickAmount() {
		return vendorClickAmount;
	}

	/**
	 * @param vendorClickAmount the vendorClickAmount to set
	 */
	public void setVendorClickAmount(Double vendorClickAmount) {
		this.vendorClickAmount = vendorClickAmount;
	}

	/**
	 * @return the nsClickAmount
	 */
	public Double getNsClickAmount() {
		return nsClickAmount;
	}

	/**
	 * @param nsClickAmount the nsClickAmount to set
	 */
	public void setNsClickAmount(Double nsClickAmount) {
		this.nsClickAmount = nsClickAmount;
	}

	/**
	 * @return the clickCountDiff
	 */
	public Integer getClickCountDiff() {
		return clickCountDiff;
	}

	/**
	 * @param clickCountDiff the clickCountDiff to set
	 */
	public void setClickCountDiff(Integer clickCountDiff) {
		this.clickCountDiff = clickCountDiff;
	}

	/**
	 * @return the leadId
	 */
	public Long getLeadId() {
		return leadId;
	}

	/**
	 * @param leadId the leadId to set
	 */
	public void setLeadId(Long leadId) {
		this.leadId = leadId;
	}

	/**
	 * @return the leadTypeId
	 */
	public Integer getLeadTypeId() {
		return leadTypeId;
	}

	/**
	 * @param leadTypeId the leadTypeId to set
	 */
	public void setLeadTypeId(Integer leadTypeId) {
		this.leadTypeId = leadTypeId;
	}

	/**
	 * @return the leadAmount
	 */
	public Double getLeadAmount() {
		return leadAmount;
	}

	/**
	 * @param leadAmount the leadAmount to set
	 */
	public void setLeadAmount(Double leadAmount) {
		this.leadAmount = leadAmount;
	}

	/**
	 * @return the leadTollAmount
	 */
	public Double getLeadTollAmount() {
		return leadTollAmount;
	}

	/**
	 * @param leadTollAmount the leadTollAmount to set
	 */
	public void setLeadTollAmount(Double leadTollAmount) {
		this.leadTollAmount = leadTollAmount;
	}

	/**
	 * @return the nsCampaignId
	 */
	public Long getNsCampaignId() {
		return nsCampaignId;
	}

	/**
	 * @param nsCampaignId the nsCampaignId to set
	 */
	public void setNsCampaignId(Long nsCampaignId) {
		this.nsCampaignId = nsCampaignId;
	}

	/**
	 * @return the nsAdGroupId
	 */
	public Long getNsAdGroupId() {
		return nsAdGroupId;
	}

	/**
	 * @param nsAdGroupId the nsAdGroupId to set
	 */
	public void setNsAdGroupId(Long nsAdGroupId) {
		this.nsAdGroupId = nsAdGroupId;
	}

	/**
	 * @return the nsAdId
	 */
	public Long getNsAdId() {
		return nsAdId;
	}

	/**
	 * @param nsAdId the nsAdId to set
	 */
	public void setNsAdId(Long nsAdId) {
		this.nsAdId = nsAdId;
	}

	/**
	 * @return the nsKeywordId
	 */
	public Long getNsKeywordId() {
		return nsKeywordId;
	}

	/**
	 * @param nsKeywordId the nsKeywordId to set
	 */
	public void setNsKeywordId(Long nsKeywordId) {
		this.nsKeywordId = nsKeywordId;
	}

	/**
	 * @return the monthlyBudgetRemaining
	 */
	public Double getMonthlyBudgetRemaining() {
		return monthlyBudgetRemaining;
	}

	/**
	 * @param monthlyBudgetRemaining the monthlyBudgetRemaining to set
	 */
	public void setMonthlyBudgetRemaining(Double monthlyBudgetRemaining) {
		this.monthlyBudgetRemaining = monthlyBudgetRemaining;
	}

	/**
	 * @return the dailyBudgetRemaining
	 */
	public Double getDailyBudgetRemaining() {
		return dailyBudgetRemaining;
	}

	/**
	 * @param dailyBudgetRemaining the dailyBudgetRemaining to set
	 */
	public void setDailyBudgetRemaining(Double dailyBudgetRemaining) {
		this.dailyBudgetRemaining = dailyBudgetRemaining;
	}

	/**
	 * @return the campaignDailyBudget
	 */
	public Double getCampaignDailyBudget() {
		return campaignDailyBudget;
	}

	/**
	 * @param campaignDailyBudget the campaignDailyBudget to set
	 */
	public void setCampaignDailyBudget(Double campaignDailyBudget) {
		this.campaignDailyBudget = campaignDailyBudget;
	}

	/**
	 * @return the vendorId
	 */
	public Integer getVendorId() {
		return vendorId;
	}

	/**
	 * @param vendorId the vendorId to set
	 */
	public void setVendorId(int vendorId) {
		this.vendorId = Integer.valueOf(vendorId);
	}

	/**
	 * Override toString for logging.
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("prodInstId=" + prodInstId);
		sb.append(",updateDate=" + updateDate);
		sb.append(",system=" + system);
		sb.append(",user=" + user);
		sb.append(",hostname=" + hostname);
		sb.append(",adjustmentType=" + adjustmentType);
		sb.append(",adjustmentTimestamp=" + adjustmentTimestamp);
		sb.append(",adjustmentAmount=" + adjustmentAmount);
		sb.append(",hitId=" + hitId);
		sb.append(",hitDate=" + hitDate);
		sb.append(",vendorClickAmount=" + vendorClickAmount);
		sb.append(",nsClickAmount=" + nsClickAmount);
		sb.append(",clickCountDiff=" + clickCountDiff);
		sb.append(",leadId=" + leadId);
		sb.append(",leadTypeId=" + leadTypeId);
		sb.append(",leadAmount=" + leadAmount);
		sb.append(",leadTollAmount=" + leadTollAmount);
		sb.append(",nsCampaignId=" + nsCampaignId);
		sb.append(",nsAdGroupId=" + nsAdGroupId);
		sb.append(",nsAdId=" + nsAdId);
		sb.append(",nsKeywordId=" + nsKeywordId);
		sb.append(",monthlyBudgetRemaining=" + monthlyBudgetRemaining);
		sb.append(",dailyBudgetRemaining=" + dailyBudgetRemaining);
		sb.append(",campaignDailyBudget=" + campaignDailyBudget);
		sb.append(",vendorId=" + vendorId);

		return sb.toString();

	}

	//
	// Database
	//

	/**
	 * Insert a budget_adj record.
	 *
	 * @param connection a connection to PDB
	 */
	public void insert(Connection connection) throws SQLException {
        if (connection == null || connection.isClosed()) {
            throw new RuntimeException("database connection was null or closed");
        }

        String logTag = BudgetAdjustment.getLogTag(this.getProdInstId());
        try {
            BudgetAdjustment.dbHelper.insert(logTag, connection, this);
        }
        catch (SQLException e) {
            BudgetAdjustment.logger.error(null, e);
            throw e;
        }

        return;
	}

	/**
	 * Get all budget adjustment data for a product.
	 *
	 * @param connection a connection to PDB
	 * @param prodInstId the prod_inst_id
	 */
	public static BudgetAdjustment[] getBudgetAdjustments(Connection connection, String prodInstId) throws SQLException {
        if (connection == null || connection.isClosed()) {
            throw new RuntimeException("database connection was null or closed");
        }

        String logTag = BudgetAdjustment.getLogTag(prodInstId);
        try {
            return BudgetAdjustment.dbHelper.getBudgetAdjustments(logTag, connection, prodInstId);
        }
        catch (SQLException e) {
            BudgetAdjustment.logger.error(null, e);
            throw e;
        }
	}

	/**
	 * Get budget adjustment data for a product for a given date range.
	 *
	 * @param connection a connection to PDB
	 * @param prodInstId the prod_inst_id
	 * @param startDate the start date (inclusive
	 * @param endDate the end date (inclusive
	 */
	public static BudgetAdjustment[] getBudgetAdjustmentsByDate(Connection connection, String prodInstId,
			Date startDate, Date endDate) throws SQLException {
        if (connection == null || connection.isClosed()) {
            throw new RuntimeException("database connection was null or closed");
        }

        String logTag = BudgetAdjustment.getLogTag(prodInstId);
        try {
            return BudgetAdjustment.dbHelper.getBudgetAdjustmentsByDate(logTag, connection, prodInstId, startDate, endDate);
        }
        catch (SQLException e) {
            BudgetAdjustment.logger.error(null, e);
            throw e;
        }
	}

    /**
     * Return the log tag for the specified product instance ID.
     */
    private static String getLogTag(String prodInstId) {
        return prodInstId + "|BudgetAdjustment";
    }

	private static class DbHelper extends BaseHelper {
	    private static final String GET_BUDGET_ADJ_DATA = "select update_date, system, user, hostname, adj_type, "
	        + "adj_timestamp, adj_amount, hit_id, hit_date, vendor_click_amount, ns_click_amount, click_count_diff, lead_id, "
	        + "lead_type_id, lead_amount, lead_toll_amount, ns_campaign_id, ns_ad_group_id, ns_ad_id, ns_keyword_id, product_mbr, "
	        + "product_dbr, campaign_daily_budget, vendor_id from budget_adj where prod_inst_id=?";

	    /**
	     * Constructor.
	     */
	    public DbHelper(Log logger) {
	        super(logger);

	        return;
	    }

	    public BudgetAdjustment[] getBudgetAdjustments(String logTag, Connection connection, String prodInstId) throws SQLException {
	        final String SQL = DbHelper.GET_BUDGET_ADJ_DATA;

	        PreparedStatement statement = null;
	        ResultSet resultSet = null;

	        try {
	            statement = connection.prepareStatement(SQL);
	            statement.setString(1, prodInstId);
	            this.logSqlStatement(logTag, statement);
	            resultSet = statement.executeQuery();
	            return getBudgetAdjustmentsFromResultSet(prodInstId, resultSet);
	        }
	        finally {
	            BaseHelper.close(statement, resultSet);
	        }
	    }

	    public BudgetAdjustment[] getBudgetAdjustmentsByDate(String logTag, Connection connection, String prodInstId, Date startDate, Date endDate) throws SQLException {
            final String SQL = DbHelper.GET_BUDGET_ADJ_DATA + " and update_date between ? and ?";

            PreparedStatement statement = null;
            ResultSet resultSet = null;

	        try {
	            statement = connection.prepareStatement(SQL);
	            statement.setString(1, prodInstId);
	            statement.setTimestamp(2, BaseHelper.toSqlTimestamp(startDate));
                statement.setTimestamp(3, BaseHelper.toSqlTimestamp(endDate));
                this.logSqlStatement(logTag, statement);
                resultSet = statement.executeQuery();
	            return getBudgetAdjustmentsFromResultSet(prodInstId, resultSet);
	        }
	        finally {
	            BaseHelper.close(statement, resultSet);
	        }
	    }

	    public void insert(String logTag, Connection connection, BudgetAdjustment budgetAdjustment) throws SQLException {
	        final String SQL = "insert into budget_adj (prod_inst_id,update_date,system,user,hostname,adj_type,adj_amount, "
	            + "hit_id,hit_date,vendor_click_amount,ns_click_amount,click_count_diff,lead_id,lead_type_id,lead_amount,lead_toll_amount,ns_campaign_id, "
	            + "ns_ad_group_id,ns_ad_id,ns_keyword_id,product_mbr,product_dbr,campaign_daily_budget, vendor_id) "
	            + "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?, ?)";

	        PreparedStatement statement = null;

	        try {
	            statement = connection.prepareStatement(SQL);
	            statement.setString(1, budgetAdjustment.getProdInstId());
	            statement.setTimestamp(2, BaseHelper.toSqlTimestamp(budgetAdjustment.getUpdateDate()));
	            statement.setString(3, budgetAdjustment.getSystem().toString());
	            statement.setString(4, budgetAdjustment.getUser());
	            statement.setString(5, budgetAdjustment.getHostname());
	            statement.setString(6, budgetAdjustment.getAdjustmentType().toString());
	            statement.setObject(7, budgetAdjustment.getAdjustmentAmount());
	            statement.setObject(8, budgetAdjustment.getHitId());
	            statement.setTimestamp(9, BaseHelper.toSqlTimestamp(budgetAdjustment.getHitDate()));
	            statement.setObject(10, budgetAdjustment.getVendorClickAmount());
	            statement.setObject(11, budgetAdjustment.getNsClickAmount());
	            statement.setObject(12, budgetAdjustment.getClickCountDiff());
	            statement.setObject(13, budgetAdjustment.getLeadId());
	            statement.setObject(14, budgetAdjustment.getLeadTypeId());
	            statement.setObject(15, budgetAdjustment.getLeadAmount());
	            statement.setObject(16, budgetAdjustment.getLeadTollAmount());
	            statement.setObject(17, budgetAdjustment.getNsCampaignId());
	            statement.setObject(18, budgetAdjustment.getNsAdGroupId());
	            statement.setObject(19, budgetAdjustment.getNsAdId());
	            statement.setObject(20, budgetAdjustment.getNsKeywordId());
	            statement.setObject(21, budgetAdjustment.getMonthlyBudgetRemaining());
	            statement.setObject(22, budgetAdjustment.getDailyBudgetRemaining());
	            statement.setObject(23, budgetAdjustment.getCampaignDailyBudget());
	            statement.setObject(24, budgetAdjustment.getVendorId());
	            this.logSqlStatement(logTag, statement);
	            statement.execute();
	        }
	        finally {
	            BaseHelper.close(statement);
	        }

	        return;
	    }

	    private BudgetAdjustment[] getBudgetAdjustmentsFromResultSet(String prodInstId, ResultSet rs) throws SQLException {
	        Collection<BudgetAdjustment> budgetAdjustmentList = new ArrayList<BudgetAdjustment>();
	        while (rs.next()) {

	            BudgetAdjustment budgetAdjustment = new BudgetAdjustment(prodInstId);

	            // These fields are returned as object types so no null-checks are necessary.
	            budgetAdjustment.setUpdateDate(rs.getDate("update_date"));
	            budgetAdjustment.setUser(rs.getString("user"));
	            budgetAdjustment.setHostname(rs.getString("hostname"));
	            budgetAdjustment.setHitDate(rs.getDate("hit_date"));
	            budgetAdjustment.setAdjustmentTimestamp(rs.getDate("adj_timestamp"));

	            // Check the enum fields for null before performing a valueOf operation. I am doing this even though the fields are
	            // not nullable in the database. If someone changes the table definition in the future, this code will not throw a NPE.
	            String system = rs.getString("system");
	            if (system != null) {
	                budgetAdjustment.setSystem(BudgetAdjustment.System.valueOf(system));
	            }
	            String adjustmentType = rs.getString("adj_type");
	            if (adjustmentType != null) {
	                budgetAdjustment.setAdjustmentType(BudgetAdjustment.AdjustmentType.valueOf(adjustmentType));
	            }

	            // Numeric types are returned as primitives so null-checks are necessary.
	            Long hitId = rs.getLong("hit_id");
	            if (!rs.wasNull()) {
	                budgetAdjustment.setHitId(hitId);
	            }
	            Double vendorClickAmount = rs.getDouble("vendor_click_amount");
	            if (!rs.wasNull()) {
	                budgetAdjustment.setVendorClickAmount(vendorClickAmount);
	            }
	            Double nsClickAmount = rs.getDouble("ns_click_amount");
	            if (!rs.wasNull()) {
	                budgetAdjustment.setNsClickAmount(nsClickAmount);
	            }
	            Integer clickCountDiff = rs.getInt("click_count_diff");
	            if (!rs.wasNull()) {
	                budgetAdjustment.setClickCountDiff(clickCountDiff);
	            }
	            Long leadId = rs.getLong("lead_id");
	            if (!rs.wasNull()) {
	                budgetAdjustment.setLeadId(leadId);
	            }
	            Integer leadTypeId = rs.getInt("lead_type_id");
	            if (!rs.wasNull()) {
	                budgetAdjustment.setLeadTypeId(leadTypeId);
	            }
	            Double leadAmount = rs.getDouble("lead_amount");
	            if (!rs.wasNull()) {
	                budgetAdjustment.setLeadAmount(leadAmount);
	            }
	            Double leadTollAmount = rs.getDouble("lead_toll_amount");
	            if (!rs.wasNull()) {
	                budgetAdjustment.setLeadTollAmount(leadTollAmount);
	            }
	            Long nsCampaignId = rs.getLong("ns_campaign_id");
	            if (!rs.wasNull()) {
	                budgetAdjustment.setNsCampaignId(nsCampaignId);
	            }
	            Long nsAdGroupId = rs.getLong("ns_ad_group_id");
	            if (!rs.wasNull()) {
	                budgetAdjustment.setNsAdGroupId(nsAdGroupId);
	            }
	            Long nsAdId = rs.getLong("ns_ad_id");
	            if (!rs.wasNull()) {
	                budgetAdjustment.setNsAdId(nsAdId);
	            }
	            Long nsKeywordId = rs.getLong("ns_keyword_id");
	            if (!rs.wasNull()) {
	                budgetAdjustment.setNsKeywordId(nsKeywordId);
	            }
	            Double dbr = rs.getDouble("product_dbr");
	            if (!rs.wasNull()) {
	                budgetAdjustment.setDailyBudgetRemaining(dbr);
	            }
	            Double mbr = rs.getDouble("product_mbr");
	            if (!rs.wasNull()) {
	                budgetAdjustment.setMonthlyBudgetRemaining(mbr);
	            }
	            Double cdb = rs.getDouble("campaign_daily_budget");
	            if (!rs.wasNull()) {
	                budgetAdjustment.setCampaignDailyBudget(cdb);
	            }
	            Integer vendorId = rs.getInt("vendor_id");
	            if (!rs.wasNull()) {
	                budgetAdjustment.setVendorId(vendorId);
	            }

	            budgetAdjustmentList.add(budgetAdjustment);
	        }
	        return budgetAdjustmentList.toArray(new BudgetAdjustment[budgetAdjustmentList.size()]);
	    }
	}
}
