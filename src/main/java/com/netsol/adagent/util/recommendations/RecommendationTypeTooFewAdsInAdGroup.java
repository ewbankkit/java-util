package com.netsol.adagent.util.recommendations;

import java.io.Serializable;

/**
 * Recommendation type - too few ads in ad group.
 * 
 * @author Adam S. Vernon
 */
@SuppressWarnings("serial")
public class RecommendationTypeTooFewAdsInAdGroup extends RecommendationTypeTooFewEntitiesInAdGroup implements Serializable {
	public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:26 RecommendationTypeTooFewAdsInAdGroup.java NSI";

	// 
	// RecommendationTypeTooFewEntitiesInAdGroup abstract implemention
	//

	/** SQL to count active campaigns. */
	private static final String COUNT_ACTIVE_ADS_IN_AD_GROUP_SQL = 
		"select count(*) from ns_ad where prod_inst_id=? and ns_ad_group_id=? and status in ('ACTIVE')";

	/**
	 * Get SQL to query the entity count.
	 */
	protected String getEntityCountSql() {
		return COUNT_ACTIVE_ADS_IN_AD_GROUP_SQL;
	}
}
