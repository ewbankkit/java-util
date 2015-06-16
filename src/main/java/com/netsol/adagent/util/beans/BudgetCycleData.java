package com.netsol.adagent.util.beans;

/**
 * Budget cycle data for the current month.
*
 * @author Adam S. Vernon
 */
public class BudgetCycleData extends BaseData {
	public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:53:50 BudgetCycleData.java NSI";

	/** The click count. */
	private int clickCount;
	
	/** Vendor click cost in the cycle. */
	private double vendorClickCost;
	
	/** Click markup in the cycle. */
	private double clickMargin;

	/** The lead count. */
	private int leadCount;
	
	/** Lead cost in the cycle. */
	private double leadCost;

	/** The daily cost for today. */
	private double dailyCost;

	/** Constructor. */
	public BudgetCycleData() {}

	//
	// Getters/Setters
	//

	/**
	 * @return the clickCount
	 */
	public int getClickCount() {
		return clickCount;
	}

	/**
	 * @param clickCount the clickCount to set
	 */
	public void setClickCount(int clickCount) {
		this.clickCount = clickCount;
	}
	
	/**
	 * @return the vendorClickCost
	 */
	public double getVendorClickCost() {
		return vendorClickCost;
	}

	/**
	 * @param vendorClickCost the vendorClickCost to set
	 */
	public void setVendorClickCost(double vendorClickCost) {
		this.vendorClickCost = vendorClickCost;
	}

	/**
	 * @return the clickMargin
	 */
	public double getClickMargin() {
		return clickMargin;
	}

	/**
	 * @param clickMargin the clickMargin to set
	 */
	public void setClickMargin(double clickMargin) {
		this.clickMargin = clickMargin;
	}

	/**
	 * @return the leadCount
	 */
	public int getLeadCount() {
		return leadCount;
	}

	/**
	 * @param leadCount the leadCount to set
	 */
	public void setLeadCount(int leadCount) {
		this.leadCount = leadCount;
	}
	
	/**
	 * @return the leadCost
	 */
	public double getLeadCost() {
		return leadCost;
	}

	/**
	 * @param leadCost the leadCost to set
	 */
	public void setLeadCost(double leadCost) {
		this.leadCost = leadCost;
	}
	
	/**
	 * @return the dailyCost
	 */
	public double getDailyCost() {
		return dailyCost;
	}

	/**
	 * @param dailyCost the dailyCost to set
	 */
	public void setDailyCost(double dailyCost) {
		this.dailyCost = dailyCost;
	}
	
	//
	// Derived field getters
	//
	
	/** Get the total click cost for the cycle. */
	public double getTotalClickCost() {
		return getVendorClickCost() + getClickMargin();
	}
	
	/** Get the total cost for the cycle. */
	public double getTotalCost() {
		return  getTotalClickCost() + getLeadCost();
	}
}
