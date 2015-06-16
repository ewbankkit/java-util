package com.netsol.adagent.util;

import java.math.BigDecimal;
import java.util.Calendar;

import com.netsol.adagent.util.BudgetManager;
import com.netsol.adagent.util.beans.CampaignList;
import com.netsol.adagent.util.beans.CampaignSummaryData;
import com.netsol.adagent.util.beans.PPCAdClick;
import com.netsol.adagent.util.beans.Product;
import com.netsol.adagent.util.budgetadj.BudgetAdjustment;
import com.netsol.adagent.util.codes.CampaignStatus;
import com.netsol.adagent.util.codes.LeadType;
import com.netsol.adagent.util.dbhelpers.BudgetManagerHelper;

/**
 * Unit tests for BudgetManager and BudgetManagerHelper.
 *
 * @author Adam S. Vernon
 */
public abstract class BudgetManagerBaseTest extends BaseTestCase {
    static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:02 BudgetManagerBaseTest.java NSI";

    private static BudgetManager bm = new BudgetManager(new org.apache.commons.logging.impl.SimpleLog(BudgetManager.class.getName()), null, null, null);
    private static BudgetManagerHelper bmh = new BudgetManagerHelper(bm);

    private BudgetAdjustment.System system = BudgetAdjustment.System.MANUAL;

    /**
     * Constructor
     */
    public BudgetManagerBaseTest(String name) {
        super(name);
    }

    //
    // Abstract methods:
    //

    public abstract BudgetManagerTestData getTestData();

    //
    // Test methods:
    //

    /**
     * Test querying budget_factors.
     */
    public void testBudgetFactors() {
        try {
            System.out.println("Running testBudgetFactors");
            BudgetManagerTestData testData = getTestData();
            int channelId = testData.getChannelId();

            // An exception will be thrown if the value can't be found for the channel. BudgetManagerHelper will log the values.
            bmh.getGlobalAverageCPC(gdbConn, channelId);
            bmh.getDefaultConversionRate(gdbConn, channelId);
            bmh.getMaxConversionRate(gdbConn, channelId);
            bmh.getMaxCpcRatio(gdbConn, channelId);
        }
        catch (Throwable e) {
            e.printStackTrace(System.out);
            fail(e.getMessage());
        }
    }

    /**
     * Debit click test method.
     */
    public void testDebitClick() {
        try {
            System.out.println("Running testDebitClick");
            BudgetManagerTestData testData = getTestData();
            resetSumData();

            // Assert that the channel has a cpc_markup_config and that the product has the channel's CPC markup values configured
            // in product_pricing.
            Product product = queryProduct();
            double cpcMarkup = bmh.getCpcMarkup(gdbConn, product.getChannelId(), testData.getProdInstId(), product.isCpcSensitive());
            assertEquals(cpcMarkup, product.getCpcMarkup());

            // Create a click object.
            PPCAdClick click = new PPCAdClick(bm);
            click.setDate(testData.getClickDate());
            click.setNsAdGroupId(testData.getNsAdGroupId());
            click.setNsAdId(testData.getNsAdId());
            click.setNsCampaignId(testData.getNsCampaignId());
            click.setNsKeywordId(testData.getNsKeywordId());
            click.setProdInstId(testData.getProdInstId());
            click.setSystem(system);
            click.setHitId(testData.getHitId());

            // Calculate the click cost.
            click.calculateCosts(pdbConn, product);

            // Assert that the click markup is calculated correctly.
            double clickCost = click.getFullCost();
            assertEquals(click.getBaseCost() + click.getBaseCost() * cpcMarkup, clickCost);
            System.out.println("click baseCost=" + click.getBaseCost() + ", markup=" + click.getMarkup());

            // Perform the debit operation.
            bm.debitClick(gdbConn, pdbConn, testData.getProdInstId(), testData.getHitId(), testData.getClickDate(), testData.getNsCampaignId(),
                    testData.getNsAdGroupId(), testData.getNsKeywordId(), testData.getNsAdId(), system, testData.getVendorId());
            pdbConn.commit();

            // Get the summary data resulting from the debit.
            SummaryData data = TestDbHelper.getSummaryData(pdbConn, testData.getProdInstId(), new java.sql.Date(testData.getClickDate().getTime()),
                    testData.getNsCampaignId());
            System.out.println("product vendor_click_cost=" + data.getProductVendorClickCost() + ", ns_click_cost=" + data.getProductNsClickCost());
            System.out.println("campaign vendor_click_cost=" + data.getCampaignVendorClickCost() + ", ns_click_cost=" + data.getCampaignNsClickCost());

            // Summary data assertions:
            assertEquals(roundTwoDecimals(testData.getDailyBudgetRemaining() - clickCost), data.getDailyBudgetRemaining());
            assertEquals(roundTwoDecimals(testData.getMonthlyBudgetRemaining() - clickCost), data.getMonthlyBudgetRemaining());
            assertEquals(roundTwoDecimals(click.getBaseCost()), data.getProductVendorClickCost());
            assertEquals(roundTwoDecimals(click.getMarkup()), data.getProductNsClickCost());
            assertEquals(1, data.getProductClickCount());
            assertEquals(roundTwoDecimals(click.getBaseCost()), data.getCampaignVendorClickCost());
            assertEquals(roundTwoDecimals(click.getMarkup()), data.getCampaignNsClickCost());
            assertEquals(1, data.getCampaignClickCount());
        }
        catch (Throwable e) {
            e.printStackTrace(System.out);
            fail(e.getMessage());
        }
    }

    /**
     * Test debiting a form lead.
     */
    public void testDebitFormLead() {
        try {
            System.out.println("Running testDebitFormLead");
            BudgetManagerTestData testData = getTestData();

            // Debit the lead.
            int leadTypeId = LeadType.FORM_LEAD;
            debitLeadTest(leadTypeId);

            // Get the summary data resulting from the debit.
            SummaryData data = TestDbHelper.getSummaryData(pdbConn, testData.getProdInstId(), new java.sql.Date(System.currentTimeMillis()),
                    testData.getNsCampaignId());
            assertEquals(1, data.getProductFormLeadCount());
            assertEquals(1, data.getCampaignFormLeadCount());
        }
        catch (Throwable e) {
            e.printStackTrace(System.out);
            fail(e.getMessage());
        }
    }

    /**
     * Test debiting a page load lead.
     */
    public void testDebitPageLead() {
        try {
            System.out.println("Running testDebitPageLead");
            BudgetManagerTestData testData = getTestData();

            // Debit the lead.
            int leadTypeId = LeadType.HIGH_VALUE_PAGE_LEAD;
            debitLeadTest(leadTypeId);

            // Get the summary data resulting from the debit.
            SummaryData data = TestDbHelper.getSummaryData(pdbConn, testData.getProdInstId(), new java.sql.Date(System.currentTimeMillis()),
                    testData.getNsCampaignId());
            assertEquals(1, data.getProductPageLeadCount());
            assertEquals(1, data.getCampaignPageLeadCount());
        }
        catch (Throwable e) {
            e.printStackTrace(System.out);
            fail(e.getMessage());
        }
    }

    /**
     * Test debiting a shopping cart lead.
     */
    public void testDebitCartLead() {
        try {
            System.out.println("Running testDebitCartLead");
            BudgetManagerTestData testData = getTestData();

            // Debit the lead.
            int leadTypeId = LeadType.SHOPPING_CART_LEAD;
            debitLeadTest(leadTypeId);

            // Get the summary data resulting from the debit.
            SummaryData data = TestDbHelper.getSummaryData(pdbConn, testData.getProdInstId(), new java.sql.Date(System.currentTimeMillis()),
                    testData.getNsCampaignId());
            assertEquals(1, data.getProductCartLeadCount());
            assertEquals(1, data.getCampaignCartLeadCount());
        }
        catch (Throwable e) {
            e.printStackTrace(System.out);
            fail(e.getMessage());
        }
    }

    /**
     * Test debiting a phone lead.
     */
    public void testDebitPhoneLead() {
        try {
            System.out.println("Running testDebitPhoneLead");
            BudgetManagerTestData testData = getTestData();

            // Debit the lead.
            int leadTypeId = LeadType.PHONE_LEAD;
            debitLeadTest(leadTypeId);

            // Get the summary data resulting from the debit.
            SummaryData data = TestDbHelper.getSummaryData(pdbConn, testData.getProdInstId(), new java.sql.Date(System.currentTimeMillis()),
                    testData.getNsCampaignId());
            assertEquals(1,data.getProductPhoneLeadCount());
            assertEquals(1, data.getCampaignPhoneLeadCount());
        }
        catch (Throwable e) {
            e.printStackTrace(System.out);
            fail(e.getMessage());
        }
    }

    /**
     * Test debiting an unanswered phone lead.
     */
    public void testDebitUnansweredPhoneLead() {
        try {
            System.out.println("Running testDebitUnansweredPhoneLead");
            BudgetManagerTestData testData = getTestData();

            // Debit the lead.
            int leadTypeId = LeadType.UNANSWERED_PHONE_LEAD;
            debitLeadTest(leadTypeId);

            // Get the summary data resulting from the debit.
            SummaryData data = TestDbHelper.getSummaryData(pdbConn, testData.getProdInstId(), new java.sql.Date(System.currentTimeMillis()),
                    testData.getNsCampaignId());
            assertEquals(1,data.getCampaignUnansweredPhoneLeadCount());
            assertEquals(1, data.getProductUnansweredPhoneLeadCount());
        }
        catch (Throwable e) {
            e.printStackTrace(System.out);
            fail(e.getMessage());
        }
    }

    /**
     * Test debiting an email lead.
     */
    public void testDebitEmailLead() {
        try {
            System.out.println("Running testDebitEmailLead");
            BudgetManagerTestData testData = getTestData();

            // Debit the lead.
            int leadTypeId = LeadType.EMAIL_LEAD;
            debitLeadTest(leadTypeId);

            // Get the summary data resulting from the debit.
            SummaryData data = TestDbHelper.getSummaryData(pdbConn, testData.getProdInstId(), new java.sql.Date(System.currentTimeMillis()),
                    testData.getNsCampaignId());
            assertEquals(1, data.getProductEmailLeadCount());
            assertEquals(1, data.getCampaignEmailLeadCount());
        }
        catch (Throwable e) {
            e.printStackTrace(System.out);
            fail(e.getMessage());
        }
    }

    /**
     * Test querying campaigns.
     */
    public void testGetCampaigns() {
        try {
            System.out.println("Running testGetCampaigns");
            BudgetManagerTestData testData = getTestData();
            String prodInstId = testData.getProdInstId();

            System.out.println("Get campaigns by status:");
            String[] statuses = { CampaignStatus.ACTIVE, CampaignStatus.SYSTEM_PAUSE, CampaignStatus.MANUAL_PAUSE};
            CampaignList campaigns = bmh.getCampaigns(pdbConn, prodInstId, statuses);
            System.out.println(campaigns);

            System.out.println("Get all campaigns (null statuses):");
            campaigns = bmh.getCampaigns(pdbConn, prodInstId, null);
            System.out.println(campaigns);

            System.out.println("Get all campaigns (empty statuses):");
            campaigns = bmh.getCampaigns(pdbConn, prodInstId, new String[0]);
            System.out.println(campaigns);
        }
        catch (Throwable e) {
            e.printStackTrace(System.out);
            fail(e.getMessage());
        }
    }

    /**
     * Test persisting campaign summary data.
     */
    public void testPersistCampaignSummaryData() {
        try {
            System.out.println("Running testPersistCampaignSummaryData");
            BudgetManagerTestData testData = getTestData();
            String prodInstId = testData.getProdInstId();

            resetSumData();

            String[] statuses = { CampaignStatus.ACTIVE, CampaignStatus.SYSTEM_PAUSE, CampaignStatus.MANUAL_PAUSE};
            CampaignList campaigns = bmh.getCampaigns(pdbConn, prodInstId, statuses);

            CampaignSummaryData campaignSummary = new CampaignSummaryData(bm);
            campaignSummary.init(pdbConn, prodInstId);
            campaignSummary.setCampaigns(campaigns);
            campaignSummary.setUpdateDate(Calendar.getInstance());

            System.out.println("CampaignSummaryData:" + campaignSummary);

            campaignSummary.persist();

            // Persist again to make sure the ON DUPLICATE KEY UPDATE clause is tested.
            campaignSummary.persist();

            pdbConn.commit();
        }
        catch (Throwable e) {
            e.printStackTrace(System.out);
            fail(e.getMessage());
        }
    }

    /**
     * Test vendor synch adding campaigns.
     */
    public void testAddCampaignVendorSynch() {
        try {
            System.out.println("Running testAddCampaignVendorSynch");
            BudgetManagerTestData testData = getTestData();

            //bm.addCampaignVendorSynch(pdbConn, testData.getProdInstId(), testData.getNsCampaignId());
            pdbConn.commit();

            // It's hard to do any verification with this test product because the campaign composition is changing all the time.
            // I'm just manually verifying in the database for now...
        }
        catch (Throwable e) {
            e.printStackTrace(System.out);
            fail(e.getMessage());
        }
    }

    /**
     * Test add campaign.
     */
    public void testAddCampaign() {
        try {
            System.out.println("Running testAddCampaign");
            BudgetManagerTestData testData = getTestData();

            //bm.addCampaign(gdbConn, pdbConn, testData.getProdInstId(), testData.getNsCampaignId());
            pdbConn.commit();

            // It's hard to do any verification with this test product because the campaign composition is changing all the time.
            // I'm just manually verifying in the database for now...
        }
        catch (Throwable e) {
            e.printStackTrace(System.out);
            fail(e.getMessage());
        }
    }

    //
    // Protected utility methods:
    //

    /**
     * Query the product bean.
     */
    protected Product queryProduct() throws Exception {
        Product product = new Product(bm);
        product.init(gdbConn, pdbConn, getTestData().getProdInstId());
        return product;
    }

    /**
     * Reset the sum table data.
     */
    protected void resetSumData() throws Exception {
        BudgetManagerTestData testData = getTestData();
        TestDbHelper.setUpProductSum(pdbConn, testData.getProdInstId(), testData.getDailyBudgetRemaining(), testData.getMonthlyBudgetRemaining());
        TestDbHelper.setUpCampaignSum(pdbConn, testData.getProdInstId(), testData.getNsCampaignId());
        pdbConn.commit();
    }

    /**
     * Insert a lead, debit it, and return the leadId.
     */
    protected int debitLeadTest(int leadTypeId) throws Exception {
        BudgetManagerTestData testData = getTestData();

        // Reset the sum table data.
        resetSumData();

        // Insert a test lead.
        int leadId = TestDbHelper.insertLead(pdbConn, testData.getProdInstId(), leadTypeId, testData.getNsKeywordId(), testData.getNsAdId(),
                testData.getNsAdGroupId(), testData.getNsCampaignId());
        if (leadTypeId == LeadType.PHONE_LEAD) {
            TestDbHelper.updatePhoneLeadData(pdbConn, "Answered", 30, leadId);
        }

        pdbConn.commit();
        System.out.println("leadTypeId = " + leadTypeId + ", leadId = " + leadId);

        // Debit the lead.
        bm.debitLead(gdbConn, pdbConn, testData.getProdInstId(), leadId, 0.0d, system);
        pdbConn.commit();

        // Get the summary data resulting from the debit.
        SummaryData data = TestDbHelper.getSummaryData(pdbConn, testData.getProdInstId(), new java.sql.Date(System.currentTimeMillis()),
                testData.getNsCampaignId());

        // Check the total lead counts.
        assertEquals(1, data.getProductTotalLeadCount());
        assertEquals(1, data.getCampaignTotalLeadCount());

        return leadId;
    }

    //
    // Private methods:
    //

    private double roundTwoDecimals(double d) {
        BigDecimal bigD = new BigDecimal(d);
        return bigD.setScale(3, BigDecimal.ROUND_HALF_UP).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }
}
