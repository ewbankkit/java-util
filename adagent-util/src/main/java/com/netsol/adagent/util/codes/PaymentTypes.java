package com.netsol.adagent.util.codes;

import java.util.HashMap;

public class PaymentTypes {
	
	private String sccsID = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:31 PaymentTypes.java NSI";

	public static final String NONE_NAME = null;
	public static final String ALL_NAME = "ALL";
	public static final String AMEX_NAME = "AMEX";
	public static final String DINERS_NAME = "DINERS";
	public static final String DISCOVER_NAME = "DISCOVER";
	public static final String JCB_NAME = "JCB";
	public static final String MASTERCARD_NAME = "MASTERCARD";
	public static final String VISA_NAME = "VISA";
	public static final String CHECK_NAME = "CHECK";
	public static final String CASH_NAME = "CASH";
	public static final String DEBIT_NAME = "DEBIT";
	public static final String GOOGLECHECKOUT_NAME = "GOOGLE_CHECK_OUT";
	public static final String PAYPAL_NAME = "PAYPAL";

	public static HashMap<Integer,String> idMap = new HashMap<Integer,String>();

	static {
		idMap.put(0,NONE_NAME);
		idMap.put(1,ALL_NAME);
		idMap.put(2,VISA_NAME);
		idMap.put(4,MASTERCARD_NAME);
		idMap.put(8,AMEX_NAME);
		idMap.put(16,DISCOVER_NAME);
		idMap.put(32,DINERS_NAME);
		idMap.put(64,CASH_NAME);
		idMap.put(128,CHECK_NAME);
		idMap.put(256,DEBIT_NAME);
		idMap.put(512,GOOGLECHECKOUT_NAME);
		idMap.put(1024,PAYPAL_NAME);
	}

}
