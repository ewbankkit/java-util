package com.netsol.adagent.util;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * The test suite can be set up to run individual tests of combinations of tests.
 * 
 * @author Adam S. Vernon
 */
public class BudgetManagerTestSuite {

    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    public static Test suite() {
    	//return getNetSolTestSuite();
    	TestSuite suite = new TestSuite();
        suite.addTest(new BudgetManagerNSTest("testDebitPhoneLead"));
    	suite.addTest(new BudgetManagerNSTest("testDebitUnansweredPhoneLead"));
    	return suite;
    }
    
    private static TestSuite getNetSolTestSuite() {
        TestSuite suite = new TestSuite();
        suite.addTest(new BudgetManagerNSTest("testBudgetFactors"));
        suite.addTest(new BudgetManagerNSTest("testDebitClick"));
        suite.addTest(new BudgetManagerNSTest("testDebitFormLead"));
        suite.addTest(new BudgetManagerNSTest("testDebitPageLead"));
        suite.addTest(new BudgetManagerNSTest("testDebitCartLead"));
        suite.addTest(new BudgetManagerNSTest("testDebitPhoneLead"));
        suite.addTest(new BudgetManagerNSTest("testDebitEmailLead"));
        suite.addTest(new BudgetManagerNSTest("testDebitUnansweredPhoneLead"));
        suite.addTest(new BudgetManagerNSTest("testGetCampaigns"));
        suite.addTest(new BudgetManagerNSTest("testPersistCampaignSummaryData"));
        suite.addTest(new BudgetManagerNSTest("testAddCampaignVendorSynch"));
        suite.addTest(new BudgetManagerNSTest("testAddCampaign"));
        return suite;
    }
}
