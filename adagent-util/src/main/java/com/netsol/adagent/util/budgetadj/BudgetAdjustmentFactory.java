package com.netsol.adagent.util.budgetadj;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

/**
 * BudgetAdjustment factory singleton for creating new BudgetAdjustment instances for insertion.
 * 
 * @author Adam S. Vernon
 */
public class BudgetAdjustmentFactory {
    static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:13 BudgetAdjustmentFactory.java NSI";

	/**
	 * The singleton instance.
	 */
	private static final BudgetAdjustmentFactory instance = new BudgetAdjustmentFactory();
	
	/**
	 * The hostname where this code is running.
	 */
	private static  String hostname;
	
	/*
	 * Initialize the hostname.
	 */
	static {
        try {
            BudgetAdjustmentFactory.hostname = InetAddress.getLocalHost().getHostName();
        }
        catch (UnknownHostException uhex) {
            BudgetAdjustmentFactory.hostname = "localhost";
        }
	};
	
	/**
	 * Private constructor enforces the singleton pattern.
	 */
	private BudgetAdjustmentFactory() {}
	
	/**
	 * Get the singleton instance.
	 */
	public static BudgetAdjustmentFactory getInstance() {
		return instance;
	}
	
	/**
	 * Get a BudgetAdjustment instance for a debit click adjustment.
	 */
	public BudgetAdjustment getDebitClickBudgetAdjustment(String prodInstId, Date updateDate, BudgetAdjustment.System system,
			Long hitId, Date hitDate, double vendorClickAmount, double nsClickAmount, long nsCampaignId, long nsAdGroupId, 
			long nsAdId, long nsKeywordId, double monthlyBudgetRemaining, double dailyBudgetRemaining, double campaignDailyBudget,
			int vendorId) {
		BudgetAdjustment budgetAdjustment = createBaseBudgetAdjustment(prodInstId, updateDate, BudgetAdjustment.AdjustmentType.DEBIT_CLICK,
				system, monthlyBudgetRemaining, dailyBudgetRemaining);
		budgetAdjustment.setHitId(hitId);
		budgetAdjustment.setHitDate(hitDate);
		budgetAdjustment.setVendorClickAmount(vendorClickAmount);
		budgetAdjustment.setNsClickAmount(nsClickAmount);
		budgetAdjustment.setVendorId(vendorId);
		setNsIds(budgetAdjustment, nsCampaignId, nsAdGroupId, nsAdId, nsKeywordId);
		budgetAdjustment.setCampaignDailyBudget(campaignDailyBudget);
		return budgetAdjustment;
	}

	/**
	 * Get a BudgetAdjustment instance for a debit click adjustment.
	 */
	public BudgetAdjustment getDebitGenericClickBudgetAdjustment(String prodInstId, Date updateDate, BudgetAdjustment.System system,
			Long hitId, Date hitDate, double vendorClickAmount, double nsClickAmount, long nsCampaignId, long nsAdGroupId, 
			double monthlyBudgetRemaining, double dailyBudgetRemaining, double campaignDailyBudget, int vendorId) {
		BudgetAdjustment budgetAdjustment = createBaseBudgetAdjustment(prodInstId, updateDate, 
				BudgetAdjustment.AdjustmentType.DEBIT_CLICK, system, monthlyBudgetRemaining, dailyBudgetRemaining);
		budgetAdjustment.setHitId(hitId);
		budgetAdjustment.setHitDate(hitDate);
		budgetAdjustment.setVendorClickAmount(vendorClickAmount);
		budgetAdjustment.setNsClickAmount(nsClickAmount);
		budgetAdjustment.setVendorId(vendorId);
		setNsIds(budgetAdjustment, nsCampaignId, nsAdGroupId, null, null);
		budgetAdjustment.setCampaignDailyBudget(campaignDailyBudget);
		return budgetAdjustment;
	}

	/**
	 * Get a BudgetAdjustment instance for a debit click adjustment.
	 */
	public BudgetAdjustment getDebitSuperpagesClickBudgetAdjustment(String prodInstId, Date updateDate, BudgetAdjustment.System system,
			Long hitId, Date hitDate, double vendorClickAmount, double nsClickAmount, long nsCampaignId, double monthlyBudgetRemaining, double dailyBudgetRemaining, 
			double campaignDailyBudget,	int vendorId) {
		BudgetAdjustment budgetAdjustment = createBaseBudgetAdjustment(prodInstId, updateDate, BudgetAdjustment.AdjustmentType.DEBIT_CLICK,
				system, monthlyBudgetRemaining, dailyBudgetRemaining);
		budgetAdjustment.setHitId(hitId);
		budgetAdjustment.setHitDate(hitDate);
		budgetAdjustment.setVendorClickAmount(vendorClickAmount);
		budgetAdjustment.setNsClickAmount(nsClickAmount);
		budgetAdjustment.setVendorId(vendorId);
		budgetAdjustment.setNsCampaignId(nsCampaignId);
		budgetAdjustment.setCampaignDailyBudget(campaignDailyBudget);
		return budgetAdjustment;
	}
	
	/**
	 * Get a BudgetAdjustment instance for a debit lead adjustment.
	 */
	public BudgetAdjustment getDebitLeadBudgetAdjustment(String prodInstId, Date updateDate, BudgetAdjustment.System system, 
			long leadId, int leadTypeId, double leadAmount, Double leadTollAmount, Long nsCampaignId, Long nsAdGroupId, Long nsAdId, 
			Long nsKeywordId, double monthlyBudgetRemaining, double dailyBudgetRemaining, Double campaignDailyBudget) {
		BudgetAdjustment.AdjustmentType type = BudgetAdjustment.AdjustmentType.DEBIT_LEAD;
		if (system.equals(BudgetAdjustment.System.RECONCILER_BATCH)) {
			type = BudgetAdjustment.AdjustmentType.RECONCILE_LEAD;
		}
		BudgetAdjustment budgetAdjustment = createBaseBudgetAdjustment(prodInstId, updateDate, type,
				system, monthlyBudgetRemaining, dailyBudgetRemaining);
		setCommonLeadFields(budgetAdjustment, leadId, leadTypeId, leadAmount, campaignDailyBudget);
		budgetAdjustment.setLeadTollAmount(leadTollAmount);
		setNsIds(budgetAdjustment, nsCampaignId, nsAdGroupId, nsAdId, nsKeywordId);
		return budgetAdjustment;
	}

	/**
	 * Get a BudgetAdjustment instance for a reconcile clicks adjustment. Clicks are reconciled in aggregate for a 
	 * given keyword (ns_keyword_id) in a given day (update_date). Reconciled clicks cannot be tied back to a particular ad (ns_ad_id). 
	 */
	public BudgetAdjustment getReconcileClicksBudgetAdjustment(String prodInstId, Date updateDate, double vendorClickAmount, 
			double nsClickAmount, int clickCountDiff, long nsCampaignId, long nsAdGroupId, long nsKeywordId, 
			double monthlyBudgetRemaining, double dailyBudgetRemaining, double campaignDailyBudget) {
		BudgetAdjustment budgetAdjustment = createBaseBudgetAdjustment(prodInstId, updateDate, BudgetAdjustment.AdjustmentType.RECONCILE_CLICKS,
				BudgetAdjustment.System.RECONCILER_BATCH, monthlyBudgetRemaining, dailyBudgetRemaining);
		budgetAdjustment.setVendorClickAmount(vendorClickAmount);
		budgetAdjustment.setNsClickAmount(nsClickAmount);
		budgetAdjustment.setClickCountDiff(clickCountDiff);
		setNsIds(budgetAdjustment, nsCampaignId, nsAdGroupId, null, nsKeywordId);
		budgetAdjustment.setCampaignDailyBudget(campaignDailyBudget);
		return budgetAdjustment;
	}
	
	/**
	 * Get a BudgetAdjustment instance for a renew adjustment.
	 */
	public BudgetAdjustment getRenewBudgetAdjustment(String prodInstId, Date updateDate, BudgetAdjustment.System system, 
			double adjustmentAmount, double monthlyBudgetRemaining, double dailyBudgetRemaining) {
		BudgetAdjustment budgetAdjustment = createBaseBudgetAdjustment(prodInstId, updateDate, BudgetAdjustment.AdjustmentType.RENEW,
				system, monthlyBudgetRemaining, dailyBudgetRemaining);
		budgetAdjustment.setAdjustmentAmount(adjustmentAmount);
		return budgetAdjustment;
	}
	
	/**
	 * Get a BudgetAdjustment instance for a addon or remove addon (one-time) adjustment.
	 */
	public BudgetAdjustment getAddonBudgetAdjustment(String prodInstId, Date updateDate, BudgetAdjustment.System system, String user,
			double adjustmentAmount, double monthlyBudgetRemaining, double dailyBudgetRemaining) {
		BudgetAdjustment budgetAdjustment = createBaseBudgetAdjustment(prodInstId, updateDate, BudgetAdjustment.AdjustmentType.ADD_ON,
				system, monthlyBudgetRemaining, dailyBudgetRemaining);
		budgetAdjustment.setAdjustmentAmount(adjustmentAmount);
		budgetAdjustment.setUser(user);
		return budgetAdjustment;
	}

	/**
	 * Get a BudgetAdjustment instance for an updgrade or downgrade adjustment.
	 */
	public BudgetAdjustment getUpgradeDowngradeBudgetAdjustment(String prodInstId, Date updateDate, BudgetAdjustment.System system, String user,
			double adjustmentAmount, double monthlyBudgetRemaining, double dailyBudgetRemaining) {
		BudgetAdjustment budgetAdjustment = createBaseBudgetAdjustment(prodInstId, updateDate, BudgetAdjustment.AdjustmentType.UPDGRADE_DOWNGRADE,
				system, monthlyBudgetRemaining, dailyBudgetRemaining);
		budgetAdjustment.setAdjustmentAmount(adjustmentAmount);
		budgetAdjustment.setUser(user);
		return budgetAdjustment;
	}
	
	/**
	 * Get a BudgetAdjustment instance for a lead refund adjustment.
	 */
	public BudgetAdjustment getLeadRefundBudgetAdjustment(String prodInstId, Date updateDate, BudgetAdjustment.System system, String user,
			long leadId, int leadTypeId, double leadAmount, long nsCampaignId, long nsAdGroupId, long nsAdId, long nsKeywordId,
			double monthlyBudgetRemaining, double dailyBudgetRemaining, double campaignDailyBudget) {
		BudgetAdjustment budgetAdjustment = createBaseBudgetAdjustment(prodInstId, updateDate, BudgetAdjustment.AdjustmentType.LEAD_REFUND,
				system, monthlyBudgetRemaining, dailyBudgetRemaining);
		setCommonLeadFields(budgetAdjustment, leadId, leadTypeId, leadAmount, campaignDailyBudget);
		setNsIds(budgetAdjustment, nsCampaignId, nsAdGroupId, nsAdId, nsKeywordId);
		budgetAdjustment.setUser(user);
		return budgetAdjustment;
	}
	
	/**
	 * Get a BudgetAdjustment instance for a manual adjustment.
	 */
	public BudgetAdjustment getManualBudgetAdjustment(String prodInstId, Date updateDate, BudgetAdjustment.System system, 
			double adjustmentAmount, double monthlyBudgetRemaining, double dailyBudgetRemaining) {
		BudgetAdjustment budgetAdjustment = createBaseBudgetAdjustment(prodInstId, updateDate, BudgetAdjustment.AdjustmentType.MANUAL_ADJ,
				system, monthlyBudgetRemaining, dailyBudgetRemaining);
		budgetAdjustment.setAdjustmentAmount(adjustmentAmount);
		return budgetAdjustment;
	}
	
	//
	// Private methods:
	//
	
	/**
	 * Create a base BudgetAdjustment instance with all the required fields populated. This method reduces code duplication.
	 */
	private BudgetAdjustment createBaseBudgetAdjustment(String prodInstId, Date updateDate, BudgetAdjustment.AdjustmentType adjustmentType, 
			BudgetAdjustment.System system,	double monthlyBudgetRemaining, double dailyBudgetRemaining) {
		BudgetAdjustment budgetAdjustment = new BudgetAdjustment(prodInstId);
		budgetAdjustment.setAdjustmentType(adjustmentType);
		budgetAdjustment.setUpdateDate(updateDate);
		budgetAdjustment.setSystem(system);
		budgetAdjustment.setMonthlyBudgetRemaining(monthlyBudgetRemaining);
		budgetAdjustment.setDailyBudgetRemaining(dailyBudgetRemaining);
		budgetAdjustment.setHostname(hostname);
		return budgetAdjustment;
	}
	
	/**
	 * Set fields common to all lead adjustments in a BudgetAdjustment instance. This method reduces code duplication.
	 */
	private void setCommonLeadFields(BudgetAdjustment budgetAdjustment, long leadId, int leadTypeId, double leadAmount, 
			Double campaignDailyBudget) {
		budgetAdjustment.setLeadId(leadId);
		budgetAdjustment.setLeadTypeId(leadTypeId);
		budgetAdjustment.setLeadAmount(leadAmount);
		budgetAdjustment.setCampaignDailyBudget(campaignDailyBudget);
	}
	
	/**
	 * Set ns_*_id fields in a BudgetAdjustment instance. This method reduces code duplication.
	 */
	private void setNsIds(BudgetAdjustment budgetAdjustment, Long nsCampaignId,	Long nsAdGroupId, Long nsAdId, Long nsKeywordId) {
		budgetAdjustment.setNsCampaignId(nsCampaignId);
		budgetAdjustment.setNsAdGroupId(nsAdGroupId);
		budgetAdjustment.setNsAdId(nsAdId);
		budgetAdjustment.setNsKeywordId(nsKeywordId);
	}
}
