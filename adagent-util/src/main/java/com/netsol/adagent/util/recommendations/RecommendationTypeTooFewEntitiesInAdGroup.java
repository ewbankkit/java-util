package com.netsol.adagent.util.recommendations;

import java.io.Serializable;
import java.sql.Connection;
import java.util.Map;

/**
 * Recommendation type - too few entities in ad group.
 * 
 * @author Adam S. Vernon
 */
@SuppressWarnings("serial")
public abstract class RecommendationTypeTooFewEntitiesInAdGroup extends RecommendationTypeTooFewEntities implements Serializable {
	public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:27 RecommendationTypeTooFewEntitiesInAdGroup.java NSI";
	
	//
	// RecommendationType abstract interface implementation
	//

	/**
	 * Generate recommendations for the product, if needed. Override the implementation in RecommendationTypeTooFewEntities.
	 * 
	 * @paran pdbConn
	 * @param prodInstId
	 * @param updatedBy
	 * @return
	 */
	protected boolean generateRecommendations(Connection pdbConn, String prodInstId, String updatedBy) throws Exception {
		boolean generated = false;
		// Initialize active recommendations for this type.
		RecommendationFactory rf = RecommendationFactory.getInstance();
		Map<Object, Recommendation> activeRecommendationMap = rf.getActiveRecommendationsOfType(pdbConn, prodInstId, this);
		// Get the IDs for active ad group in active campaigns.
		RecommendationEntityTypeData[] entityTypeDataArray = RecommendationEntityTypeData.getDataForActiveAdGroupsInActiveCampaigns(pdbConn, prodInstId);
		for (RecommendationEntityTypeData entityTypeData : entityTypeDataArray) {
			Long nsAdGroupId = entityTypeData.getNsAdGroupId();
			// Get active recommendations.
			Recommendation activeRecommendation = activeRecommendationMap.get(nsAdGroupId);
			// Check to see if the recommendation is needed.
			if (isRecommendationNeeded(pdbConn, prodInstId, nsAdGroupId)) {
				generated = true;
				if (activeRecommendation != null) {
					// Update the existing active recommendation. We are only updating the recommendation_date for this type so there is nothing to set.
					activeRecommendation.update(pdbConn, updatedBy);
				}
				else {
					// Create the recommendation.
					Recommendation recommendation = new Recommendation(prodInstId, this);
					recommendation.setNsAdGroupId(nsAdGroupId);
					recommendation.setNsCampaignId(entityTypeData.getNsCampaignId());
					recommendation.insert(pdbConn, updatedBy);
				}
			}
			else {
				// Remove the active recommendation if it exists because it is no longer relevant.
				if (activeRecommendation != null) {
					activeRecommendation.setStatus(Recommendation.Status.DELETED.toString());
					activeRecommendation.update(pdbConn, updatedBy);
				}
			}			
		}
		return generated;
	}
}
