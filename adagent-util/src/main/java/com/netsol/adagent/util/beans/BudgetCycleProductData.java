package com.netsol.adagent.util.beans;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.netsol.adagent.util.CalendarUtil;
import com.netsol.adagent.util.codes.VendorId;
import com.netsol.adagent.util.dbhelpers.BaseHelper;
import com.netsol.adagent.util.dbhelpers.TargetHelper;

/**
 * Current budget cycle product data and aggregation structures.
 * 
 * @author Adam S. Vernon
 */
public class BudgetCycleProductData extends BaseData {
	public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:53:51 BudgetCycleProductData.java NSI";
    private static final Log log = LogFactory.getLog(BudgetCycleProductData.class);
    private static final String logTag = BudgetCycleProductData.class.getName();
    
	/** The prod_inst_id. */
	private String prodInstId;
	
	/** The start date of the cycle. */
	private Date startDate;
	
	/** The end date of the cycle. */
	private Date endDate;
	
	/** The base budget. This is the renewal amount and does not include addons. */
	private double baseBudget;
	
	/** The addon budget (positive or negative). */
	private double addonBudget;
	
	/** The last update date, which is most recent cycle date (presumably today) for which we have data. */
	private Date updateDate;
	
	/** The daily budget remaining for today. */
	private double dailyBudgetRemaining;
	
	/** The current monthly budget remaining. */
	private double monthlyBudgetRemaining;
	
	/** The product's budget cycle data. */
	private BudgetCycleData budgetCycleData;

    /** Total budget allocated to targets this month. */
	private double totalTargetBaseMonthlyBudget;

    /** Total target budget allocated to PPC spend this month (minus margin and SEO spend). */
	private double totalTargetActualPPCMonthlyBudget;

	/** Total current month target cost. */
	private double totalTargetCurrentCost;

    /** Total target margin amount for this month. */
	private double totalTargetMarginAmount;
    
    /** Total target Superpages monthly budget amount. */
	private double totalTargetSuperpagesMonthlyBudget;

    /** Total target SEO monthly budget amount. */
	private double totalTargetSEOMonthlyBudget;

    /** Total non-target monthly budget. */
	private double totalNonTargetMonthlyBudget;
	
	/** Available non-target campaign monthly budget. */
	private double availableNonTargetCampaignMonthlyBudget;
	
	/** A map of Long vendorId to an aggregation of BudgetCycleData for the vendor. */
	private Map<Long, BudgetCycleData> vendorBudgetCycleDataMap = new HashMap<Long, BudgetCycleData>();
	
	/** A map of Long ns_campaignId to an aggregation of BudgetCycleData for the campaign. */
	private Map<Long, BudgetCycleData> campaignBudgetCycleDataMap = new HashMap<Long, BudgetCycleData>();
	
	/** A map of Long target_id to an aggregation of BudgetCycleData for the target. */
	private Map<Long, BudgetCycleData> targetBudgetCycleDataMap = new HashMap<Long, BudgetCycleData>();
	
	/** A map of Long target_id to a map of Long vendor_id to an aggregation of BudgetCycleData for the target vendor. */
	private Map<Long, Map<Long, BudgetCycleData>> targetVendorBudgetCycleDataMap = new HashMap<Long, Map<Long, BudgetCycleData>>();

	/** An aggregation of campaign data. */
	private BudgetCycleData campaignBudgetCycleData = new BudgetCycleData();

	/** An aggregation of non-target campaign data. */
	private BudgetCycleData nonTargetCampaignBudgetCycleData = new BudgetCycleData();

	/** An map of Long vendor_id to an aggregation of BudgetCycleData for the vendor non-target campaign data. */
	private Map<Long, BudgetCycleData> nonTargetCampaignVendorBudgetCycleDataMap = new HashMap<Long, BudgetCycleData>();

	/** Constructor. */
	public BudgetCycleProductData(String prodInstId) {
		this.prodInstId = prodInstId;
	}
	
	//
	// Getters/Setters
	//
	
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
	 * @return the startDate
	 */
	public Date getStartDate() {
		return startDate;
	}

	/**
	 * @param startDate the startDate to set
	 */
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	/**
	 * @return the endDate
	 */
	public Date getEndDate() {
		return endDate;
	}

	/**
	 * @param endDate the endDate to set
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	/**
	 * @return the baseBudget
	 */
	public double getBaseBudget() {
		return baseBudget;
	}

	/**
	 * @param baseBudget the baseBudget to set
	 */
	public void setBaseBudget(double baseBudget) {
		this.baseBudget = baseBudget;
	}

	/**
	 * @return the addonBudget
	 */
	public double getAddonBudget() {
		return addonBudget;
	}

	/**
	 * @param addonBudget the addonBudget to set
	 */
	public void setAddonBudget(double addonBudget) {
		this.addonBudget = addonBudget;
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
	 * @return the dailyBudgetRemaining
	 */
	public double getDailyBudgetRemaining() {
		return dailyBudgetRemaining;
	}

	/**
	 * @param dailyBudgetRemaining the dailyBudgetRemaining to set
	 */
	public void setDailyBudgetRemaining(double dailyBudgetRemaining) {
		this.dailyBudgetRemaining = dailyBudgetRemaining;
	}

	/**
	 * @return the monthlyBudgetRemaining
	 */
	public double getMonthlyBudgetRemaining() {
		return monthlyBudgetRemaining;
	}

	/**
	 * @param monthlyBudgetRemaining the monthlyBudgetRemaining to set
	 */
	public void setMonthlyBudgetRemaining(double monthlyBudgetRemaining) {
		this.monthlyBudgetRemaining = monthlyBudgetRemaining;
	}
	
	/**
	 * @return the budgetCycleData
	 */
	public BudgetCycleData getBudgetCycleData() {
		return budgetCycleData;
	}

	/**
	 * @param budgetCycleData the budgetCycleData to set
	 */
	public void setBudgetCycleData(BudgetCycleData budgetCycleData) {
		this.budgetCycleData = budgetCycleData;
	}

	/**
	 * @return the totalTargetBaseMonthlyBudget
	 */
	public double getTotalTargetBaseMonthlyBudget() {
		return totalTargetBaseMonthlyBudget;
	}

	/**
	 * @param totalTargetBaseMonthlyBudget the totalTargetBaseMonthlyBudget to set
	 */
	public void setTotalTargetBaseMonthlyBudget(double totalTargetBaseMonthlyBudget) {
		this.totalTargetBaseMonthlyBudget = totalTargetBaseMonthlyBudget;
	}
	
	/**
	 * @return the totalTargetActualPPCMonthlyBudget
	 */
	public double getTotalTargetActualPPCMonthlyBudget() {
		return totalTargetActualPPCMonthlyBudget;
	}

	/**
	 * @param totalTargetActualPPCMonthlyBudget the totalTargetActualPPCMonthlyBudget to set
	 */
	public void setTotalTargetActualPPCMonthlyBudget(double totalTargetActualPPCMonthlyBudget) {
		this.totalTargetActualPPCMonthlyBudget = totalTargetActualPPCMonthlyBudget;
	}

	/**
	 * @return the totalTargetCurrentCost
	 */
	public double getTotalTargetCurrentCost() {
		return totalTargetCurrentCost;
	}

	/**
	 * @param totalTargetCurrentCost the totalTargetCurrentCost to set
	 */
	public void setTotalTargetCurrentCost(double totalTargetCurrentCost) {
		this.totalTargetCurrentCost = totalTargetCurrentCost;
	}

	/**
	 * @return the totalTargetMarginAmount
	 */
	public double getTotalTargetMarginAmount() {
		return totalTargetMarginAmount;
	}

	/**
	 * @param totalTargetMarginAmount the totalTargetMarginAmount to set
	 */
	public void setTotalTargetMarginAmount(double totalTargetMarginAmount) {
		this.totalTargetMarginAmount = totalTargetMarginAmount;
	}

	/**
	 * @return the totalTargetSuperpagesMonthlyBudget
	 */
	public double getTotalTargetSuperpagesMonthlyBudget() {
		return totalTargetSuperpagesMonthlyBudget;
	}

	/**
	 * @param totalTargetSuperpagesMonthlyBudget the totalTargetSuperpagesMonthlyBudget to set
	 */
	public void setTotalTargetSuperpagesMonthlyBudget(double totalTargetSuperpagesMonthlyBudget) {
		this.totalTargetSuperpagesMonthlyBudget = totalTargetSuperpagesMonthlyBudget;
	}

	/**
	 * @return the totalTargetSEOMonthlyBudget
	 */
	public double getTotalTargetSEOMonthlyBudget() {
		return totalTargetSEOMonthlyBudget;
	}

	/**
	 * @param totalTargetSEOMonthlyBudget the totalTargetSEOMonthlyBudget to set
	 */
	public void setTotalTargetSEOMonthlyBudget(double totalTargetSEOMonthlyBudget) {
		this.totalTargetSEOMonthlyBudget = totalTargetSEOMonthlyBudget;
	}
	
	/**
	 * @return the totalNonTargetMonthlyBudget
	 */
	public double getTotalNonTargetMonthlyBudget() {
		return totalNonTargetMonthlyBudget;
	}

	/**
	 * @param totalNonTargetMonthlyBudget the totalNonTargetMonthlyBudget to set
	 */
	public void setTotalNonTargetMonthlyBudget(double totalNonTargetMonthlyBudget) {
		this.totalNonTargetMonthlyBudget = totalNonTargetMonthlyBudget;
	}
	
	/**
	 * @return the availableNonTargetCampaignMonthlyBudget
	 */
	public double getAvailableNonTargetCampaignMonthlyBudget() {
		return availableNonTargetCampaignMonthlyBudget;
	}

	/**
	 * @param availableNonTargetCampaignMonthlyBudget the availableNonTargetCampaignMonthlyBudget to set
	 */
	public void setAvailableNonTargetCampaignMonthlyBudget(
			double availableNonTargetCampaignMonthlyBudget) {
		this.availableNonTargetCampaignMonthlyBudget = availableNonTargetCampaignMonthlyBudget;
	}
	
	//
	// Derived field getters
	//

	/** Get the total budget for the cycle. */
	public double getTotalBudget() {
		return (budgetCycleData.getTotalCost() + getMonthlyBudgetRemaining());
	}
	
	/** Get the daily adjust amount for today. */
	public double getDailyBudget() {
		return budgetCycleData.getDailyCost() + getDailyBudgetRemaining();
	}

	/** Get the rollover budget amount. */
	public double getRolloverBudget() {
		return getUpdateDate() == null ? 0 : getTotalBudget() - (getBaseBudget() + getAddonBudget() - getTotalTargetMarginAmount());
	}

	//
	// Aggregation structure getters
	//
	
	/**
	 * @return the vendorBudgetCycleDataMap
	 */
	public Map<Long, BudgetCycleData> getVendorBudgetCycleDataMap() {
		return vendorBudgetCycleDataMap;
	}

	/**
	 * @return the campaignBudgetCycleDataMap
	 */
	public Map<Long, BudgetCycleData> getCampaignBudgetCycleDataMap() {
		return campaignBudgetCycleDataMap;
	}

	/**
	 * @return the targetBudgetCycleDataMap
	 */
	public Map<Long, BudgetCycleData> getTargetBudgetCycleDataMap() {
		return targetBudgetCycleDataMap;
	}

	/**
	 * @return the targetVendorBudgetCycleDataMap
	 */
	public Map<Long, Map<Long, BudgetCycleData>> getTargetVendorBudgetCycleDataMap() {
		return targetVendorBudgetCycleDataMap;
	}

	/**
	 * @return the campaignBudgetCycleData
	 */
	public BudgetCycleData getCampaignBudgetCycleData() {
		return campaignBudgetCycleData;
	}

	/**
	 * @return the nonTargetCampaignBudgetCycleData
	 */
	public BudgetCycleData getNonTargetCampaignBudgetCycleData() {
		return nonTargetCampaignBudgetCycleData;
	}
	
	/**
	 * @return the nonTargetVendorCampaignBudgetCycleData
	 */
	public Map<Long, BudgetCycleData> getNonTargetCampaignVendorBudgetCycleDataMap() {
		return nonTargetCampaignVendorBudgetCycleDataMap;
	}
	
	//
	// Factory methods
	//
	
	// Start date is now inclusive, not exclusive.
	private static final String GET_CURRENT_CYCLE_BUDGET_DATA_SQL = 
		"select sum(t.click_count), sum(t.ns_click_cost), sum(t.vendor_click_cost), sum(t.total_lead_count), sum(t.total_lead_cost), "
		+ "t.update_date, t.daily_budget_remaining, t.monthly_budget_remaining, t.daily_cost "
		+ "from ( "
		+ "select prod_inst_id, click_count, ns_click_cost, vendor_click_cost, total_lead_count, total_lead_cost, "
		+ "update_date, daily_budget_remaining, monthly_budget_remaining, ns_click_cost+vendor_click_cost+total_lead_cost daily_cost "
		+ "from product_sum "
		+ "where prod_inst_id=? and update_date between ? and ? "
		+ "order by update_date desc "
		+ ") t "
		+ "group by t.prod_inst_id;";
	
	// We have a separate query for products and campaigns since some data is not tracked at the campaign level. We can't just roll up campaign data to get product data.
	private static final String GET_CURRENT_CYCLE_CAMPAIGN_BUDGET_DATA_SQL = 
		"select t.ns_campaign_id, t.target_id, t.vendor_id, sum(t.click_count), sum(t.ns_click_cost), sum(t.vendor_click_cost), sum(t.total_lead_count), "
		+ "sum(t.total_lead_cost), t.daily_cost "
		+ "from ( "
		+ "select c.prod_inst_id, c.ns_campaign_id, c.target_id, c.vendor_id, cs.click_count, cs.ns_click_cost, cs.vendor_click_cost, cs.total_lead_count, "
		+ "cs.total_lead_cost, cs.ns_click_cost+cs.vendor_click_cost+cs.total_lead_cost daily_cost "
		+ "from ns_campaign c "
		+ "left outer join ns_campaign_sum cs on c.prod_inst_id=cs.prod_inst_id and c.ns_campaign_id=cs.ns_campaign_id and cs.update_date between ? and ? "
		+ "where c.prod_inst_id=? "
		+ "order by update_date desc "
		+ ") t "
		+ "group by t.prod_inst_id, t.ns_campaign_id;";
	
	// Query for addons in the current cycle.
	private static final String GET_CURRENT_CYCLE_ADDON_BUDGET = "select sum(adj_amount) from budget_adj ba "
		+ "inner join product p on ba.prod_inst_id=p.prod_inst_id "
		+ "where ba.prod_inst_id=? and update_date between p.start_date and p.expiration_date and adj_type in ('ADD_ON');";

	
	/** 
	 * Factory method to get current budget cycle product data.
	 * 
	 *  @param pdConn a connection to the PDB database.
	 *  @param prodInstId the product's prod_inst_id.
	 */
	public static BudgetCycleProductData getCurrentBudgetCycleProductData(Connection pdbConn, Product product) throws Exception {
		PreparedStatement pstmt = null;
		
		try {
			String prodInstId = product.getProdInstId();
			BudgetCycleProductData budgetCycleProductData = new BudgetCycleProductData(prodInstId);
			budgetCycleProductData.setStartDate(product.getStartDate());
			budgetCycleProductData.setEndDate(product.getExpirationDate());
			budgetCycleProductData.setBaseBudget(product.getBaseTarget());
			budgetCycleProductData.setBudgetCycleData(new BudgetCycleData()); // Default in case no rows are found.

			pstmt = pdbConn.prepareStatement(GET_CURRENT_CYCLE_BUDGET_DATA_SQL);
			pstmt.setString(1, prodInstId);
			pstmt.setDate(2, product.getStartDate());
			pstmt.setDate(3, product.getExpirationDate());
			log.info(pstmt);
			ResultSet results = pstmt.executeQuery();
			if (results != null && results.next()) {
				// Data structures.
				Map<Long, BudgetCycleData> vendorBudgetCycleDataMap = budgetCycleProductData.getVendorBudgetCycleDataMap();
				Map<Long, BudgetCycleData> campaignBudgetCycleDataMap = budgetCycleProductData.getCampaignBudgetCycleDataMap();
				Map<Long, BudgetCycleData> targetBudgetCycleDataMap = budgetCycleProductData.getTargetBudgetCycleDataMap();
				Map<Long, Map<Long, BudgetCycleData>> targetVendorBudgetCycleDataMap = budgetCycleProductData.getTargetVendorBudgetCycleDataMap();
				BudgetCycleData campaignBudgetCycleData = budgetCycleProductData.getCampaignBudgetCycleData();
				BudgetCycleData nonTargetCampaignBudgetCycleData = budgetCycleProductData.getNonTargetCampaignBudgetCycleData();
				Map<Long, BudgetCycleData> nonTargetCampaignVendorBudgetCycleDataMap = budgetCycleProductData.getNonTargetCampaignVendorBudgetCycleDataMap();
				
				// Get the product data.
				BudgetCycleData productData = new BudgetCycleData();
				int i = 1;
				productData.setClickCount(results.getInt(i++));
				productData.setClickMargin(results.getDouble(i++));
				productData.setVendorClickCost(results.getDouble(i++));
				productData.setLeadCount(results.getInt(i++));
				productData.setLeadCost(results.getDouble(i++));
				budgetCycleProductData.setUpdateDate(results.getDate(i++));
				budgetCycleProductData.setDailyBudgetRemaining(results.getDouble(i++));
				budgetCycleProductData.setMonthlyBudgetRemaining(results.getDouble(i++));
				productData.setDailyCost(results.getDouble(i++));
				budgetCycleProductData.setBudgetCycleData(productData);
				
				// Get the addon budget.
				budgetCycleProductData.setAddonBudget(getCurrentCycleAddonBudget(pdbConn, prodInstId));
				
				// Get the campaign data.
	        	BaseHelper.close(pstmt);
				pstmt = pdbConn.prepareStatement(GET_CURRENT_CYCLE_CAMPAIGN_BUDGET_DATA_SQL);
				pstmt.setDate(1, product.getStartDate());
				pstmt.setDate(2, product.getExpirationDate());
				pstmt.setString(3, prodInstId);
				log.info(pstmt);
				results = pstmt.executeQuery();
				while (results != null && results.next()) {
					i = 1;
					Long nsCampaignId = results.getLong(i++);
					Long targetId = results.getLong(i++);
					targetId = (results.wasNull() ? null : targetId);
					Long vendorId = results.getLong(i++);
					
					// Campaign data.
					BudgetCycleData campaignData = new BudgetCycleData();
					campaignData.setClickCount(results.getInt(i++));
					campaignData.setClickMargin(results.getDouble(i++));
					campaignData.setVendorClickCost(results.getDouble(i++));
					campaignData.setLeadCount(results.getInt(i++));
					campaignData.setLeadCost(results.getDouble(i++));
					campaignData.setDailyCost(results.getDouble(i++));
					campaignBudgetCycleDataMap.put(nsCampaignId, campaignData);

					// Aggregate campaign data.
					// TODO push this block of code to add a data object down into BudgetCycleData
					campaignBudgetCycleData.setClickCount(campaignBudgetCycleData.getClickCount() + campaignData.getClickCount());
					campaignBudgetCycleData.setClickMargin(campaignBudgetCycleData.getClickMargin() + campaignData.getClickMargin());
					campaignBudgetCycleData.setVendorClickCost(campaignBudgetCycleData.getVendorClickCost() + campaignData.getVendorClickCost());
					campaignBudgetCycleData.setLeadCount(campaignBudgetCycleData.getLeadCount() + campaignData.getLeadCount());
					campaignBudgetCycleData.setLeadCost(campaignBudgetCycleData.getLeadCost() + campaignData.getLeadCost());
					campaignBudgetCycleData.setDailyCost(campaignBudgetCycleData.getDailyCost() + campaignData.getDailyCost());
					
					// Aggregate vendor data.
					BudgetCycleData vendorData = vendorBudgetCycleDataMap.get(vendorId);
					if(vendorData == null) {
						vendorData = new BudgetCycleData();
						vendorBudgetCycleDataMap.put(vendorId, vendorData);
					}
					vendorData.setClickCount(vendorData.getClickCount() + campaignData.getClickCount());
					vendorData.setClickMargin(vendorData.getClickMargin() + campaignData.getClickMargin());
					vendorData.setVendorClickCost(vendorData.getVendorClickCost() + campaignData.getVendorClickCost());
					vendorData.setLeadCount(vendorData.getLeadCount() + campaignData.getLeadCount());
					vendorData.setLeadCost(vendorData.getLeadCost() + campaignData.getLeadCost());
					vendorData.setDailyCost(vendorData.getDailyCost() + campaignData.getDailyCost());
					
					if (targetId != null) {
						// Aggregate target data.
						BudgetCycleData targetData = targetBudgetCycleDataMap.get(targetId);
						if (targetData == null) {
							targetData = new BudgetCycleData();
							targetBudgetCycleDataMap.put(targetId, targetData);
						}
						targetData.setClickCount(targetData.getClickCount() + campaignData.getClickCount());
						targetData.setClickMargin(targetData.getClickMargin() + campaignData.getClickMargin());
						targetData.setVendorClickCost(targetData.getVendorClickCost() + campaignData.getVendorClickCost());
						targetData.setLeadCount(targetData.getLeadCount() + campaignData.getLeadCount());
						targetData.setLeadCost(targetData.getLeadCost() + campaignData.getLeadCost());
						targetData.setDailyCost(targetData.getDailyCost() + campaignData.getDailyCost());
						
						// Aggregate target vendor data.
						Map<Long, BudgetCycleData> vendorMap = targetVendorBudgetCycleDataMap.get(targetId);
						if (vendorMap == null) {
							vendorMap = new HashMap<Long, BudgetCycleData>();
							targetVendorBudgetCycleDataMap.put(targetId, vendorMap);
						}
						
						BudgetCycleData targetVendorData = vendorMap.get(vendorId);
						if (targetVendorData == null) {
							targetVendorData = new BudgetCycleData();
							vendorMap.put(vendorId, targetVendorData);
						}
						targetVendorData.setClickCount(targetVendorData.getClickCount() + campaignData.getClickCount());
						targetVendorData.setClickMargin(targetVendorData.getClickMargin() + campaignData.getClickMargin());
						targetVendorData.setVendorClickCost(targetVendorData.getVendorClickCost() + campaignData.getVendorClickCost());
						targetVendorData.setLeadCount(targetVendorData.getLeadCount() + campaignData.getLeadCount());
						targetVendorData.setLeadCost(targetVendorData.getLeadCost() + campaignData.getLeadCost());
						targetVendorData.setDailyCost(targetVendorData.getDailyCost() + campaignData.getDailyCost());
					}
					else {
						// Aggregate non-target campaign data.
						nonTargetCampaignBudgetCycleData.setClickCount(nonTargetCampaignBudgetCycleData.getClickCount() + campaignData.getClickCount());
						nonTargetCampaignBudgetCycleData.setClickMargin(nonTargetCampaignBudgetCycleData.getClickMargin() + campaignData.getClickMargin());
						nonTargetCampaignBudgetCycleData.setVendorClickCost(nonTargetCampaignBudgetCycleData.getVendorClickCost() + campaignData.getVendorClickCost());
						nonTargetCampaignBudgetCycleData.setLeadCount(nonTargetCampaignBudgetCycleData.getLeadCount() + campaignData.getLeadCount());
						nonTargetCampaignBudgetCycleData.setLeadCost(nonTargetCampaignBudgetCycleData.getLeadCost() + campaignData.getLeadCost());
						nonTargetCampaignBudgetCycleData.setDailyCost(nonTargetCampaignBudgetCycleData.getDailyCost() + campaignData.getDailyCost());

						// Aggregate non-target vendor campaign data.
						BudgetCycleData nonTargetCampaignVendorBudgetCycleData = nonTargetCampaignVendorBudgetCycleDataMap.get(vendorId);
						if (nonTargetCampaignVendorBudgetCycleData == null) {
							nonTargetCampaignVendorBudgetCycleData = new BudgetCycleData();
							nonTargetCampaignVendorBudgetCycleDataMap.put(vendorId, nonTargetCampaignVendorBudgetCycleData);
						}
						nonTargetCampaignVendorBudgetCycleData.setClickCount(nonTargetCampaignVendorBudgetCycleData.getClickCount() + campaignData.getClickCount());
						nonTargetCampaignVendorBudgetCycleData.setClickMargin(nonTargetCampaignVendorBudgetCycleData.getClickMargin() + campaignData.getClickMargin());
						nonTargetCampaignVendorBudgetCycleData.setVendorClickCost(nonTargetCampaignVendorBudgetCycleData.getVendorClickCost() + campaignData.getVendorClickCost());
						nonTargetCampaignVendorBudgetCycleData.setLeadCount(nonTargetCampaignVendorBudgetCycleData.getLeadCount() + campaignData.getLeadCount());
						nonTargetCampaignVendorBudgetCycleData.setLeadCost(nonTargetCampaignVendorBudgetCycleData.getLeadCost() + campaignData.getLeadCost());
						nonTargetCampaignVendorBudgetCycleData.setDailyCost(nonTargetCampaignVendorBudgetCycleData.getDailyCost() + campaignData.getDailyCost());
					}
				}
				
				// Get the target totals. This call has to be made after the data structures are built.
				getCurrentCycleTargetTotals(pdbConn, budgetCycleProductData);
				
				
				// Now set the non-target total. This call has to be made after the target totals are calculated.
				double targetAllocation = Math.max(budgetCycleProductData.getTotalTargetActualPPCMonthlyBudget(), budgetCycleProductData.getTotalTargetCurrentCost());
				double totalNonTargetMonthlyBudget = budgetCycleProductData.getTotalBudget() - targetAllocation;
				budgetCycleProductData.setTotalNonTargetMonthlyBudget(totalNonTargetMonthlyBudget);

				// Calculate budget available to non-target campaigns by removing product-only costs (such as some call leads).
				double productOnlyCost = productData.getTotalCost() - campaignBudgetCycleData.getTotalCost();
				double availableNonTargetCampaignMonthlyBudget = totalNonTargetMonthlyBudget - productOnlyCost;
				budgetCycleProductData.setAvailableNonTargetCampaignMonthlyBudget(availableNonTargetCampaignMonthlyBudget);

			}
			return budgetCycleProductData;
		}
        finally {
        	BaseHelper.close(pstmt);
		}
	} 

	//
	// Private methods
	//
	
	/**
	 * Get the current cycle addon budget.
	 */
	private static double getCurrentCycleAddonBudget(Connection pdbConn, String prodInstId) throws Exception {
		PreparedStatement pstmt = null;
		
		try {
			pstmt = pdbConn.prepareStatement(GET_CURRENT_CYCLE_ADDON_BUDGET);
			pstmt.setString(1, prodInstId);
			log.info(pstmt);
			ResultSet results = pstmt.executeQuery();
			double addonBudget = 0;
			if (results != null && results.next()) {
				addonBudget = results.getDouble(1);
			}
			return addonBudget;
		}
        finally {
        	BaseHelper.close(pstmt);
		}
	}
	
	/**
	 * Get current cycle target totals.
	 */
	private static void getCurrentCycleTargetTotals(Connection pdbConn, BudgetCycleProductData budgetCycleProductData) throws Exception {
	    double totalTargetBaseMonthlyBudget = 0;
	    double totalTargetActualPPCMonthlyBudget = 0;
	    double totalTargetCurrentCost = 0;
	    double totalTargetMarginAmount = 0;
	    double totalTargetSuperpagesMonthlyBudget = 0;
	    double totalTargetSEOMonthlyBudget = 0;
	    
		TargetHelper targetHelper = new TargetHelper(log);
        List<Target> targetList = targetHelper.getTargetsValidForCycleDates(logTag, pdbConn, budgetCycleProductData.getProdInstId(), 
        		CalendarUtil.dateToCalendar(budgetCycleProductData.getStartDate()), CalendarUtil.dateToCalendar(budgetCycleProductData.getEndDate()));
        if (targetList != null && !targetList.isEmpty()) {
            // Get target totals for later use.
            for (Target target : targetList) {
            	System.out.println("target=" + target);
                BudgetCycleData targetData = budgetCycleProductData.getTargetBudgetCycleDataMap().get(target.getTargetId());
                totalTargetCurrentCost += targetData == null ? 0 : targetData.getTotalCost();

                totalTargetBaseMonthlyBudget += target.getBudget();
                totalTargetActualPPCMonthlyBudget += target.calculateActualMonthlyPPCBudget();
                totalTargetMarginAmount += target.calculateActualMonthlyMarginAmount();
                totalTargetSEOMonthlyBudget += target.getSeoBudget();

                // Calculate Superpages total monthly budget across targets.
                for (TargetVendor targetVendor : target.getTargetVendors()) {
                	if (targetVendor.getVendorId() == VendorId.SUPERPAGES) {
                		totalTargetSuperpagesMonthlyBudget += targetVendor.getBudget();
                	}
                }
            }
        }
        
        budgetCycleProductData.setTotalTargetBaseMonthlyBudget(totalTargetBaseMonthlyBudget);
        budgetCycleProductData.setTotalTargetActualPPCMonthlyBudget(totalTargetActualPPCMonthlyBudget);
        budgetCycleProductData.setTotalTargetCurrentCost(totalTargetCurrentCost);
        budgetCycleProductData.setTotalTargetMarginAmount(totalTargetMarginAmount);
        budgetCycleProductData.setTotalTargetSuperpagesMonthlyBudget(totalTargetSuperpagesMonthlyBudget);
        budgetCycleProductData.setTotalTargetSEOMonthlyBudget(totalTargetSEOMonthlyBudget);
	}
	
    //
    // Unit test
    //

    /**
     * Simple unit test.
     * @param args
     * @throws Exception
     */
    public static void main(String[] args){
        Connection gdbConn = null;
        Connection pdb1Conn = null;
        Connection pdb2Conn = null;
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            //pdbConn = DriverManager.getConnection("jdbc:mysql://aadb3.prod.netsol.com:4300/adagent?user=adagent&password=adagent");
            //String prodInstId= "WN.PP.267879801";
            gdbConn = DriverManager.getConnection("jdbc:mysql://eng1.dev.netsol.com:4200/adagent?user=adagent&password=adagent");       
            pdb1Conn = DriverManager.getConnection("jdbc:mysql://eng2.dev.netsol.com:4300/adagent?user=adagent&password=adagent");       
            //pdb2Conn = DriverManager.getConnection("jdbc:mysql://eng2.dev.netsol.com:4301/adagent?user=adagent&password=adagent");       
            String prodInstId= "WN.DEV.BING.0002";
            Product product = new Product("Test");
            product.init(gdbConn, pdb1Conn, prodInstId);
            BudgetCycleProductData budgetCycleProductData = getCurrentBudgetCycleProductData(pdb1Conn, product);
            System.out.println(budgetCycleProductData);
        }
        catch(Throwable e) {
            e.printStackTrace(System.out);
        }
        finally {
            BaseHelper.close(gdbConn);
            BaseHelper.close(pdb1Conn);
            BaseHelper.close(pdb2Conn);
        }
    }
}
