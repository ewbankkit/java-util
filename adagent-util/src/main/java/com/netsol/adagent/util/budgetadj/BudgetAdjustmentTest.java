package com.netsol.adagent.util.budgetadj;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Date;

import com.netsol.adagent.util.beans.PPCLead;

/**
 * Simple unit test class for budget adjustments.
 * 
 * @author Adam S. Vernon
 */
public class BudgetAdjustmentTest {
    static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:13 BudgetAdjustmentTest.java NSI";
    
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

    private static final long nsCampaignId = 4350l;
    private static final long nsAdGroupId = 7490l;
    private static final long nsAdId = 18018l;
    private static final long nsKeywordId = 73489l;
    
    private static final double adjustmentAmount = 50.00d;
    
    private static final double monthlyBudgetRemaining = 500.00d;
    private static final double dailyBudgetRemaining = 100.00d;
    private static final double campaignDailyBudget = 15.00d;
    
    private static final Date startDate = new Date();
    private static final Date endDate = new Date();
    
    private static final int vendorId = 1;
    
	/**
	 * Unit test for budget adjustments.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		Connection pdbConnection = null;
		
		try {
		    Class.forName("com.mysql.jdbc.Driver").newInstance();
			pdbConnection = DriverManager.getConnection("jdbc:mysql://eng2.dev.netsol.com:4300/adagent?user=adagent&password=adagent"); // pdb1
			
			
			testInsertAddOn(pdbConnection);
			testInsertDebitClick(pdbConnection);
			testInsertDebitLead(pdbConnection);
			testInsertDebitLeadNullCampaign(pdbConnection);
			testInsertReconcileClicks(pdbConnection);
			testInsertRenewBudget(pdbConnection);
			testInsertUpgradeDowngrade(pdbConnection);
			testInsertLeadRefund(pdbConnection);
			testInsertManualAdjustment(pdbConnection);
					
			testGetByDate(pdbConnection);
			testGetAll(pdbConnection);
			
			testPPCLeadInsertBudgetAdjusment(pdbConnection);
			
		}
		catch (Throwable e) {
			System.out.println("error getting data");
			e.printStackTrace(System.out);
		}
		finally {
			if (pdbConnection != null) {
				try {
					pdbConnection.close();
				}
				catch (SQLException e) {
					System.out.println("could not close a non-null connection");
					e.printStackTrace(System.out);
				}
			}
		}
	}

	private static void testGetAll(Connection connection) throws SQLException {
		BudgetAdjustment[] budgetAdjArray = BudgetAdjustment.getBudgetAdjustments(connection, prodInstId);
		for (int i = 0; i < budgetAdjArray.length; i++) {
			System.out.println(budgetAdjArray[i].toString());
		}
	}
	
	private static void testGetByDate(Connection connection) throws SQLException {
		BudgetAdjustment[] budgetAdjArray = BudgetAdjustment.getBudgetAdjustmentsByDate(connection, prodInstId, startDate, endDate);
		for (int i = 0; i < budgetAdjArray.length; i++) {
			System.out.println(budgetAdjArray[i].toString());
		}
	}
	
	private static void testInsertDebitClick(Connection connection) throws SQLException {
		BudgetAdjustmentFactory factory = BudgetAdjustmentFactory.getInstance();
		BudgetAdjustment budgetAdjustment = factory.getDebitClickBudgetAdjustment(prodInstId, updateDate, BudgetAdjustment.System.AGGREGATOR,
				hitId, hitDate, vendorClickAmount, nsClickAmount, nsCampaignId, nsAdGroupId, nsAdId, nsKeywordId, monthlyBudgetRemaining, 
				dailyBudgetRemaining, campaignDailyBudget, vendorId);
		budgetAdjustment.insert(connection);
	}
	
	private static void testInsertDebitLead(Connection connection) throws SQLException {
		BudgetAdjustmentFactory factory = BudgetAdjustmentFactory.getInstance();
		BudgetAdjustment budgetAdjustment = factory.getDebitLeadBudgetAdjustment(prodInstId, updateDate, BudgetAdjustment.System.AGGREGATOR,
				leadId, leadTypeId,	leadAmount, leadTollAmount, nsCampaignId, nsAdGroupId, nsAdId, nsKeywordId, monthlyBudgetRemaining, 
				dailyBudgetRemaining, campaignDailyBudget);
		budgetAdjustment.insert(connection);
	}
	
	private static void testInsertDebitLeadNullCampaign(Connection connection) throws SQLException {
		BudgetAdjustmentFactory factory = BudgetAdjustmentFactory.getInstance();
		BudgetAdjustment budgetAdjustment = factory.getDebitLeadBudgetAdjustment(prodInstId, updateDate, BudgetAdjustment.System.AGGREGATOR,
				leadId, leadTypeId,	leadAmount, leadTollAmount, null, null, null, null, monthlyBudgetRemaining, 
				dailyBudgetRemaining, null);
		budgetAdjustment.insert(connection);
	}

	
	private static void testInsertReconcileClicks(Connection connection) throws SQLException {
		BudgetAdjustmentFactory factory = BudgetAdjustmentFactory.getInstance();
		BudgetAdjustment budgetAdjustment = factory.getReconcileClicksBudgetAdjustment(prodInstId, updateDate, vendorClickAmount, 
				nsClickAmount, clickCountDiff, nsCampaignId, nsAdGroupId, nsKeywordId,	monthlyBudgetRemaining, dailyBudgetRemaining,
				campaignDailyBudget);
		budgetAdjustment.insert(connection);
	}
	
	private static void testInsertRenewBudget(Connection connection) throws SQLException {
		BudgetAdjustmentFactory factory = BudgetAdjustmentFactory.getInstance();
		BudgetAdjustment budgetAdjustment = factory.getRenewBudgetAdjustment(prodInstId, updateDate, BudgetAdjustment.System.WS_ADAGENT, 
				adjustmentAmount, monthlyBudgetRemaining, dailyBudgetRemaining);
		budgetAdjustment.insert(connection);
	}
	
	private static void testInsertAddOn(Connection connection) throws SQLException {
		BudgetAdjustmentFactory factory = BudgetAdjustmentFactory.getInstance();
		BudgetAdjustment budgetAdjustment = factory.getAddonBudgetAdjustment(prodInstId, updateDate, BudgetAdjustment.System.WS_ADAGENT, 
				user, adjustmentAmount, monthlyBudgetRemaining, dailyBudgetRemaining);
		budgetAdjustment.insert(connection);
	}
	
	private static void testInsertUpgradeDowngrade(Connection connection) throws SQLException {
		BudgetAdjustmentFactory factory = BudgetAdjustmentFactory.getInstance();
		BudgetAdjustment budgetAdjustment = factory.getUpgradeDowngradeBudgetAdjustment(prodInstId, updateDate, BudgetAdjustment.System.WS_ADAGENT,  
				user, adjustmentAmount, monthlyBudgetRemaining, dailyBudgetRemaining);
		budgetAdjustment.insert(connection);
	}
	
	private static void testInsertLeadRefund(Connection connection) throws SQLException {
		BudgetAdjustmentFactory factory = BudgetAdjustmentFactory.getInstance();
		BudgetAdjustment budgetAdjustment = factory.getLeadRefundBudgetAdjustment(prodInstId, updateDate, BudgetAdjustment.System.DATA_SERVICES, 
				user, leadId, leadTypeId,	leadAmount, nsCampaignId, nsAdGroupId, nsAdId, nsKeywordId, monthlyBudgetRemaining, 
				dailyBudgetRemaining, campaignDailyBudget);
		budgetAdjustment.insert(connection);
	}
	
	private static void testInsertManualAdjustment(Connection connection) throws SQLException {
		BudgetAdjustmentFactory factory = BudgetAdjustmentFactory.getInstance();
		BudgetAdjustment budgetAdjustment = factory.getManualBudgetAdjustment(prodInstId, updateDate, BudgetAdjustment.System.MANUAL, 
			adjustmentAmount, monthlyBudgetRemaining, dailyBudgetRemaining);
		budgetAdjustment.insert(connection);
	}
	
	private static void testPPCLeadInsertBudgetAdjusment(Connection connection) throws Exception {
		PPCLead ppcLead = new PPCLead("test");
		ppcLead.init(connection, 1, prodInstId, 121056, 0); // type 5
		ppcLead.setSystem(BudgetAdjustment.System.AGGREGATOR);
		System.out.println(ppcLead.toString());
		ppcLead.insertBudgetAdjustment(connection);
		
		PPCLead ppcLead2 = new PPCLead("test");
		ppcLead2.init(connection, 1, prodInstId, 121057, 0); // type 1 
		ppcLead2.setSystem(BudgetAdjustment.System.AGGREGATOR);
		System.out.println(ppcLead2.toString());
		ppcLead2.insertBudgetAdjustment(connection);

		
		PPCLead ppcLead3 = new PPCLead("test");
		ppcLead3.init(connection, 1, prodInstId, 121058, 0); // type 3
		ppcLead3.setSystem(BudgetAdjustment.System.AGGREGATOR);
		System.out.println(ppcLead3.toString());
		ppcLead3.insertBudgetAdjustment(connection);

	}
}
