package com.netsol.adagent.util;


public class BudgetManagerException extends Exception {	
	static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:14 BudgetManagerException.java NSI";
	public static final int DAILY_BUDGET_CONSUMED = 1001;
	public static final int MONTHLY_BUDGET_CONSUMED = 1002;
	public static final int CAMPAIGN_BUDGET_CONSUMED = 1003;
	
	public static final int MONTHLY_BUDGET_CONSUMED_EARLY = 2001;
	public static final int DAILY_BUDGET_CONSUMED_EARLY = 2002;
	public static final int MONTHLY_BUDGET_NOT_CONSUMED = 2003;
	public static final int BUDGET_OVER_CONSUMPTION = 2004;
	
	public static final int UNKNOWN_ERROR = -1;
	public static final int DEBIT_ERROR = 1;
	public static final int DATABASE_ERROR = 3;
	public static final int INVALID_SRC_DATA = 4;
	public static final int ACTIVATION_ERROR = 5;
	public static final int NON_RENEWED_BUDGET_ERROR = 6;
	public static final int PRODUCT_CONFIGURATION_ERROR = 7;
	public static final int NO_KEYWORDS_TO_RECONCILE = 8;
	public static final int DEBIT_INACTIVE_PRODUCT_ERROR = 9;
	public static final int BUDGET_ADJ_ERROR = 10;
	
	public BudgetManagerException(int errorCode, String budgetManagerMessage) {
		super(budgetManagerMessage);
		this.errorCode = errorCode;
		this.budgetErrorMessage = budgetManagerMessage;
	}
	
	public BudgetManagerException(int errorCode, String budgetManagerMessage, String message, Exception e) {
		super(budgetManagerMessage+"\n"+message, e);
		this.errorCode = errorCode;
		this.budgetErrorMessage = budgetManagerMessage;
	}
	
	public int getErrorCode() {
		return this.errorCode;
	}
	
	public String getBudgetErrorMessage() {
		return this.budgetErrorMessage;
	}
	
	private int errorCode = -1;
	private String budgetErrorMessage = null;
}
