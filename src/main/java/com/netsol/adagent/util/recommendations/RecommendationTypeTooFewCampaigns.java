package com.netsol.adagent.util.recommendations;

import java.io.Serializable;

/**
 * Recommendation type - too few active campaigns.
 * 
 * @author Adam S. Vernon
 */
@SuppressWarnings("serial")
public class RecommendationTypeTooFewCampaigns extends RecommendationTypeTooFewEntities implements Serializable {
	public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:26 RecommendationTypeTooFewCampaigns.java NSI";
		
	//
	// Abstract RecommendationTypeTooFewEntities interface implementation
	//
	
	/** SQL to count active campaigns. */
	private static final String ACTIVE_CAMPAIGN_COUNT_SQL = 
		"select count(*) from ns_campaign where prod_inst_id=? and status in ('ACTIVE', 'SYSTEM_PAUSE')";

	/**
	 * Get SQL to query the entity count.
	 */
	protected String getEntityCountSql() {
		return ACTIVE_CAMPAIGN_COUNT_SQL;
	}
}
