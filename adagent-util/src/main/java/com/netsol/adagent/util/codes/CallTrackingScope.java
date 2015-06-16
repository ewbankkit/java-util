package com.netsol.adagent.util.codes;

/**
 * Codes class defining the values for a targetNumbers tracking scope.
 * @author pmitchel
 */
public class CallTrackingScope {
	public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:24 CallTrackingScope.java NSI";
	
	/**
	 * Tracking numbers are managed by the system. One tracking number will be automatically provisioned for each campaign.
	 * One additional tracking number will be created at the product level, as a catch-all. 
	 * Tracking numbers will be deactivated and provisioned throughout the product's lifecycle each time a campaign is added or removed.
	 */
	public static final String EACH_CAMPAIGN = "EACH_CAMPAIGN";  
	

	/**
	 * Tracking numbers are managed by the system. One tracking number will be automatically provisioned for each ad group.
	 * One additional tracking number will be created at the product level, as a catch-all. 
	 * Tracking numbers will be deactivated and provisioned throughout the product's lifecycle each time an adgroup is added or removed.
	 */
	public static final String EACH_ADGROUP = "EACH_ADGROUP";  
	

	/**
	 * One Single tracking number will be created for the entire product.
	 */		
	public static final String PRODUCT = "PRODUCT";
	

	/**
	 * One tracking number will be created and associated to a single campaign.
	 */
	public static final String ONE_CAMPAIGN = "ONE_CAMPAIGN";
	

	/**
	 * One tracking number will be created for a single ad group.
	 */
	public static final String ONE_ADGROUP = "ONE_ADGROUP";


}
