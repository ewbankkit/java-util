package com.netsol.adagent.util;

import java.sql.SQLException;
import java.util.Date;

import com.netsol.adagent.util.budgetadj.BudgetAdjustment;
import com.netsol.adagent.util.budgetadj.BudgetAdjustmentFactory;

/**
 * Unit tests for budget adjustments.
 * 
 * @author Adam S. Vernon
 */
public class BudgetAdjustmentTest extends BaseTestCase {
    static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:01 BudgetAdjustmentTest.java NSI";
    
    private static final String prodInstId = "WN.PP.33344444";
    private static final Date updateDate = new Date();
    private static final String user = "avernon";
    
    private static final long hitId = 1l;
    private static final Date hitDate = new Date();
    private static final double vendorClickAmount = 2.00d;
    private static final double nsClickAmount = 1.00d;
    private static final int clickCountDiff = 1;
    
    private static final long leadId = 2l;
    private static final int leadTypeId = 1;
    private static final double leadAmount = 10d;
    private static final double leadTollAmount = 2d;

    private static final long nsCampaignId = 69l;
    private static final long nsAdGroupId = 4551l;
    private static final long nsAdId = 10445l;
    private static final long nsKeywordId = 62563l;
    
    private static final double adjustmentAmount = 50.00d;
    
    private static final double monthlyBudgetRemaining = 500.00d;
    private static final double dailyBudgetRemaining = 100.00d;
    private static final double campaignDailyBudget = 15.00d;
    
    private static final Date startDate = new Date();
    private static final Date endDate = new Date();
    
    private static final int vendorId = 1;

	/**
	 * Constructor
	 */
	public BudgetAdjustmentTest(String name) {
		super(name);
	}

	public void testGetAll() throws SQLException {
		BudgetAdjustment[] budgetAdjArray = BudgetAdjustment.getBudgetAdjustments(pdbConn, prodInstId);
		for (int i = 0; i < budgetAdjArray.length; i++) {
			System.out.println(budgetAdjArray[i].toString());
		}
	}
	
	public void testGetByDate() throws SQLException {
		BudgetAdjustment[] budgetAdjArray = BudgetAdjustment.getBudgetAdjustmentsByDate(pdbConn, prodInstId, startDate, endDate);
		for (int i = 0; i < budgetAdjArray.length; i++) {
			System.out.println(budgetAdjArray[i].toString());
		}
	}
	
	public void testInsertDebitClick() throws SQLException {
		BudgetAdjustmentFactory factory = BudgetAdjustmentFactory.getInstance();
		BudgetAdjustment budgetAdjustment = factory.getDebitClickBudgetAdjustment(prodInstId, updateDate, BudgetAdjustment.System.AGGREGATOR,
				hitId, hitDate, vendorClickAmount, nsClickAmount, nsCampaignId, nsAdGroupId, nsAdId, nsKeywordId, monthlyBudgetRemaining, 
				dailyBudgetRemaining, campaignDailyBudget, vendorId);
		budgetAdjustment.insert(pdbConn);
	}
	
	public void testInsertDebitLead() throws SQLException {
		BudgetAdjustmentFactory factory = BudgetAdjustmentFactory.getInstance();
		BudgetAdjustment budgetAdjustment = factory.getDebitLeadBudgetAdjustment(prodInstId, updateDate, BudgetAdjustment.System.AGGREGATOR,
				leadId, leadTypeId,	leadAmount, leadTollAmount, nsCampaignId, nsAdGroupId, nsAdId, nsKeywordId, monthlyBudgetRemaining, 
				dailyBudgetRemaining, campaignDailyBudget);
		budgetAdjustment.insert(pdbConn);
	}
	
	public void testInsertReconcileClicks() throws SQLException {
		BudgetAdjustmentFactory factory = BudgetAdjustmentFactory.getInstance();
		BudgetAdjustment budgetAdjustment = factory.getReconcileClicksBudgetAdjustment(prodInstId, updateDate, vendorClickAmount, 
				nsClickAmount, clickCountDiff, nsCampaignId, nsAdGroupId, nsKeywordId,	monthlyBudgetRemaining, dailyBudgetRemaining,
				campaignDailyBudget);
		budgetAdjustment.insert(pdbConn);
	}
	
	public void testInsertRenewBudget() throws SQLException {
		BudgetAdjustmentFactory factory = BudgetAdjustmentFactory.getInstance();
		BudgetAdjustment budgetAdjustment = factory.getRenewBudgetAdjustment(prodInstId, updateDate, BudgetAdjustment.System.WS_ADAGENT, 
				adjustmentAmount, monthlyBudgetRemaining, dailyBudgetRemaining);
		budgetAdjustment.insert(pdbConn);
	}
	
	public void testInsertAddOn() throws SQLException {
		BudgetAdjustmentFactory factory = BudgetAdjustmentFactory.getInstance();
		BudgetAdjustment budgetAdjustment = factory.getAddonBudgetAdjustment(prodInstId, updateDate, BudgetAdjustment.System.WS_ADAGENT, 
				user, adjustmentAmount, monthlyBudgetRemaining, dailyBudgetRemaining);
		budgetAdjustment.insert(pdbConn);
	}
	
	public void testInsertUpgradeDowngrade() throws SQLException {
		BudgetAdjustmentFactory factory = BudgetAdjustmentFactory.getInstance();
		BudgetAdjustment budgetAdjustment = factory.getUpgradeDowngradeBudgetAdjustment(prodInstId, updateDate, BudgetAdjustment.System.WS_ADAGENT,  
				user, adjustmentAmount, monthlyBudgetRemaining, dailyBudgetRemaining);
		budgetAdjustment.insert(pdbConn);
	}
	
	public void testInsertLeadRefund() throws SQLException {
		BudgetAdjustmentFactory factory = BudgetAdjustmentFactory.getInstance();
		BudgetAdjustment budgetAdjustment = factory.getLeadRefundBudgetAdjustment(prodInstId, updateDate, BudgetAdjustment.System.DATA_SERVICES, 
				user, leadId, leadTypeId,	leadAmount, nsCampaignId, nsAdGroupId, nsAdId, nsKeywordId, monthlyBudgetRemaining, 
				dailyBudgetRemaining, campaignDailyBudget);
		budgetAdjustment.insert(pdbConn);
	}
	
	public void testInsertManualAdjustment() throws SQLException {
		BudgetAdjustmentFactory factory = BudgetAdjustmentFactory.getInstance();
		BudgetAdjustment budgetAdjustment = factory.getManualBudgetAdjustment(prodInstId, updateDate, BudgetAdjustment.System.MANUAL, 
			adjustmentAmount, monthlyBudgetRemaining, dailyBudgetRemaining);
		budgetAdjustment.insert(pdbConn);
	}
}
