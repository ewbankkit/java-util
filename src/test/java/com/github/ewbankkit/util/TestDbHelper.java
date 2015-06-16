package com.github.ewbankkit.util;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import com.github.ewbankkit.util.dbhelpers.BaseHelper;

/**
 * Unit test database helper methods.
 *
 * @author Adam Vernon
 */
public class TestDbHelper {
    static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:06 TestDbHelper.java NSI";

	private static final String INSERT_UPDATE_PRODUCT_SUM =
		"insert into product_sum (prod_inst_id, update_date, daily_budget_remaining, monthly_budget_remaining) "
		+ "values (?, now(), ?, ?) "
		+ "on duplicate key update daily_budget_remaining=?, monthly_budget_remaining=?, vendor_click_cost=0, ns_click_cost=0, click_count=0, "
		+ "total_lead_cost=0, total_lead_count=0, "
		+ "phone_lead_cost=0, email_lead_cost=0, form_submit_lead_cost=0, page_load_lead_cost=0, shop_cart_lead_cost=0, "
		+ "phone_lead_count=0, email_lead_count=0, form_submit_lead_count=0, page_load_lead_count=0, shop_cart_lead_count=0";

	private static final String INSERT_UPDATE_NS_CAMPAIGN_SUM =
		"insert into ns_campaign_sum (prod_inst_id, ns_campaign_id, update_date) values (?, ?, now()) "
		+ "on duplicate key update vendor_click_cost=0, ns_click_cost=0, click_count=0, total_lead_cost=0, total_lead_count=0, "
		+ "phone_lead_cost=0, email_lead_cost=0, form_submit_lead_cost=0, page_load_lead_cost=0, shop_cart_lead_cost=0, "
		+ "phone_lead_count=0, email_lead_count=0, form_submit_lead_count=0, page_load_lead_count=0, shop_cart_lead_count=0";

	private static final String GET_SUM_DATA =
		"select p.daily_budget_remaining, p.monthly_budget_remaining, p.vendor_click_cost, p.ns_click_cost, p.click_count, "
		+ "p.total_lead_cost, p.total_lead_count, "
		+ "p.phone_lead_cost, p.email_lead_cost, p.form_submit_lead_cost, p.page_load_lead_cost, p.shop_cart_lead_cost, "
		+ "p.phone_lead_count, p.email_lead_count, p.form_submit_lead_count, p.page_load_lead_count, p.shop_cart_lead_count, "
		+ "c.vendor_click_cost, c.ns_click_cost, c.click_count, c.total_lead_cost, c.total_lead_count, "
		+ "c.phone_lead_cost, c.email_lead_cost, c.form_submit_lead_cost, c.page_load_lead_cost, c.shop_cart_lead_cost, "
		+ "c.phone_lead_count, c.email_lead_count, c.form_submit_lead_count, c.page_load_lead_count, c.shop_cart_lead_count,"
		+ "p.unanswered_phone_lead_cost, p.unanswered_phone_lead_count, c.unanswered_phone_lead_cost, c.unanswered_phone_lead_count "
		+ "from product_sum p, ns_campaign_sum as c "
		+ "where p.prod_inst_id=? and p.update_date=? and p.prod_inst_id = c.prod_inst_id and p.update_date=c.update_date "
		+ "and c.ns_campaign_id=? "
		+ "group by p.update_date";

	private static final String INSERT_LEAD =
		"insert into leads (prod_inst_id, lead_type_id, lead_date, ns_keyword_id, ns_ad_id, ns_ad_group_id, ns_campaign_id, "
		+ "created_by, created_date) "
		+ "values (?, ?, now(), ?, ?, ?, ?, 'BudgetManagerTest', now())";

	private static final String UPDATE_PHONE_LEAD =
		"update leads set generic_vchar4=?, generic_number1=? where lead_id=?";

	private static final String GET_CAMPAIGN_PERCENT_OF_BUDGET_SUM = "select sum(percent_of_budget) "
		+ "from ns_campaign where prod_inst_id=? and status in ('ACTIVE', 'SYSTEM_PAUSE')";

	/**
	 * Get a connection to GDB in dev. Auto-commit will be false.
	 */
	public static Connection getGdbConnection() throws Exception {
		Connection conn = BaseHelper.createDevGdbConnection();
		conn.setAutoCommit(false);
		return conn;
	}

	/**
	 * Get a connection to PDB1 in dev. Auto-commit will be false.
	 */
	public static Connection getPdbConnection() throws Exception {
		Connection conn = BaseHelper.createDevPdb1Connection();
		conn.setAutoCommit(false);
		return conn;
	}

	/**
	 * Initialize the product_sum record for this test.
	 */
	public static void setUpProductSum(Connection pdbConn, String prodInstId, double dailyBudgetRemaining, double monthlyBudgetRemaining)
		throws Exception {

		PreparedStatement stmt = pdbConn.prepareStatement(INSERT_UPDATE_PRODUCT_SUM);
		stmt.setString(1, prodInstId);
		stmt.setDouble(2, dailyBudgetRemaining);
		stmt.setDouble(3, monthlyBudgetRemaining);
		stmt.setDouble(4, dailyBudgetRemaining);
		stmt.setDouble(5, monthlyBudgetRemaining);

		stmt.execute();
		close(stmt, null);
	}

	/**
	 * Initialize the ns_campaign_sum record for this test.
	 */
	public static void setUpCampaignSum(Connection pdbConn, String prodInstId, long nsCampaignId)
		throws Exception {

		PreparedStatement stmt = pdbConn.prepareStatement(INSERT_UPDATE_NS_CAMPAIGN_SUM);
		stmt.setString(1, prodInstId);
		stmt.setLong(2, nsCampaignId);
		stmt.execute();
		close(stmt, null);
	}

	/**
	 * Get product_sum and ns_ampaign_sum data for a prod_inst_id on a particular day.
	 */
	public static SummaryData getSummaryData(Connection pdbConn, String prodInstId, Date day, long nsCampaignId)
		throws Exception {

		PreparedStatement stmt = pdbConn.prepareStatement(GET_SUM_DATA);
		stmt.setString(1, prodInstId);
		stmt.setDate(2, day);
		stmt.setLong(3, nsCampaignId);
		System.out.println("getSummaryData: " + stmt);
		ResultSet results = stmt.executeQuery();

		results.next();
		SummaryData data = new SummaryData();
		data.setDailyBudgetRemaining(results.getDouble(1));
		data.setMonthlyBudgetRemaining(results.getDouble(2));

		data.setProductVendorClickCost(results.getDouble(3));
		data.setProductNsClickCost(results.getDouble(4));
		data.setProductClickCount(results.getInt(5));

		data.setProductTotalLeadCost(results.getDouble(6));
		data.setProductTotalLeadCount(results.getInt(7));

		data.setProductPhoneLeadCost(results.getDouble(8));
		data.setProductEmailLeadCost(results.getDouble(9));
		data.setProductFormLeadCost(results.getDouble(10));
		data.setProductPageLeadCost(results.getDouble(11));
		data.setProductCartLeadCost(results.getDouble(12));

		data.setProductPhoneLeadCount(results.getInt(13));
		data.setProductEmailLeadCount(results.getInt(14));
		data.setProductFormLeadCount(results.getInt(15));

		data.setProductPageLeadCount(results.getInt(16));
		data.setProductCartLeadCount(results.getInt(17));

		data.setCampaignVendorClickCost(results.getDouble(18));
		data.setCampaignNsClickCost(results.getDouble(19));
		data.setCampaignClickCount(results.getInt(20));

		data.setCampaignTotalLeadCost(results.getDouble(21));
		data.setCampaignTotalLeadCount(results.getInt(22));

		data.setCampaignPhoneLeadCost(results.getDouble(23));
		data.setCampaignEmailLeadCost(results.getDouble(24));
		data.setCampaignFormLeadCost(results.getDouble(25));
		data.setCampaignPageLeadCost(results.getDouble(26));
		data.setCampaignCartLeadCost(results.getDouble(27));

		data.setCampaignPhoneLeadCount(results.getInt(28));
		data.setCampaignEmailLeadCount(results.getInt(29));
		data.setCampaignFormLeadCount(results.getInt(30));
		data.setCampaignPageLeadCount(results.getInt(31));
		data.setCampaignCartLeadCount(results.getInt(32));

		data.setProductUnansweredPhoneLeadCost(results.getDouble(33));
		data.setProductUnansweredPhoneLeadCount(results.getInt(34));
		data.setCampaignUnansweredPhoneLeadCost(results.getDouble(35));
		data.setCampaignUnansweredPhoneLeadCount(results.getInt(36));

		close(stmt, results);
		return data;
	}

	/**
	 * Insert a lead for testing
	 *
	 * @return the lead_id.
	 */
	public static int insertLead(Connection pdbConn, String prodInstId, int leadTypeId, long nsKeywordId, long nsAdId,
			long nsAdGroupId, long nsCampaignId)
		throws Exception {

		PreparedStatement stmt = pdbConn.prepareStatement(INSERT_LEAD, Statement.RETURN_GENERATED_KEYS);
		stmt.setString(1, prodInstId);
		stmt.setInt(2, leadTypeId);
		stmt.setLong(3, nsKeywordId);
		stmt.setLong(4, nsAdId);
		stmt.setLong(5, nsAdGroupId);
		stmt.setLong(6, nsCampaignId);
		stmt.executeUpdate();

		ResultSet results = stmt.getGeneratedKeys();
		int id;
	    if (results.next()) {
	        id = results.getInt(1);
	    } else {
	    	throw new RuntimeException("auto-increment lead_id not returned");
	    }

	    close(stmt, results);
	    return id;
	}

	public static void updatePhoneLeadData(Connection pdbConn, String connectedStatus, int duration, int leadId) throws Exception {
		PreparedStatement stmt = pdbConn.prepareStatement(UPDATE_PHONE_LEAD);
		stmt.setString(1, connectedStatus);
		stmt.setInt(2, duration);
		stmt.setInt(3, leadId);
		stmt.executeUpdate();
	}

	/**
	 * Get the sum of the percent_of_budget for all ACTIVE and SYSTEM_PAUSE campaigns within a product.
	 */
	public static float getCampaignPercentOfBudgetSum(Connection pdbConn, String prodInstId) throws Exception {
		PreparedStatement stmt = pdbConn.prepareStatement(GET_CAMPAIGN_PERCENT_OF_BUDGET_SUM);
		stmt.setString(1, prodInstId);
		System.out.println("getCampaignPercentOfBudgetSum: " + stmt);
		ResultSet results = stmt.executeQuery();

		results.next();
		return results.getFloat(1);
	}

	/** Close database resources. */
	public static void close(Statement stmt, ResultSet results) {
	    BaseHelper.close(stmt, results);
	}
}
