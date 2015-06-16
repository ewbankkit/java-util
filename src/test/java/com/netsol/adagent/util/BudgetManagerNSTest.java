package com.netsol.adagent.util;

/** 
 * BudgetManager test for NetSol products.
 * 
 * @author Adam S. Vernon
 */
public class BudgetManagerNSTest extends BudgetManagerBaseTest {
    static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:02 BudgetManagerNSTest.java NSI";
    
	private BudgetManagerTestData testData = null;
    
	/**
	 * Constructor
	 */
	public BudgetManagerNSTest(String name) {
		super(name);
	}

	//
	// BudgetManagerBaseTest implementation:
	//
	
	public BudgetManagerTestData getTestData() {
		if (testData == null) {
			testData =  BudgetManagerTestData.getNSTestData();
		}
		return testData;
	}
}
