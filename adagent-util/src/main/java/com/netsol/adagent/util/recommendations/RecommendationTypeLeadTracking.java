package com.netsol.adagent.util.recommendations;

import java.io.Serializable;
import java.sql.Connection;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.netsol.adagent.util.beans.InterceptorFeatures;
import com.netsol.adagent.util.dbhelpers.InterceptorFeaturesHelper;

/**
 * Recommendation type - lead tracking.
 * 
 * @author Adam S. Vernon
 */
@SuppressWarnings("serial")
public class RecommendationTypeLeadTracking extends RecommendationType implements Serializable {
	public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:26 RecommendationTypeLeadTracking.java NSI";
	private static final Log log = LogFactory.getLog(RecommendationTypeLeadTracking.class);
	
	//
	// Abstract RecommendationType interface implementation
	//
	
	/**
	 * Generate a lead tracking recommendation, if needed.
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
		Recommendation activeRecommendation = activeRecommendationMap.get(prodInstId);
		if (isRecommendationNeeded(pdbConn, prodInstId)) {
			generated = true;
			if (activeRecommendation != null) {
				// Update the existing active recommendation. We are only updating the recommendation_date for this type so there is nothing to set.
				activeRecommendation.update(pdbConn, updatedBy);
			}
			else {
				// Create the recommendation.
				Recommendation recommendation = new Recommendation(prodInstId, this);
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
		return generated;
	}
	
	//
	// Private helper methods
	//
	
	/**
	 * Determine if this recommendation is needed.
	 * 
	 * @paran pdbConn
	 * @param prodInstId
	 */
	private boolean isRecommendationNeeded(Connection pdbConn, String prodInstId) throws Exception {
		// Load the interceptor features.
		InterceptorFeaturesHelper ifh = new InterceptorFeaturesHelper(log);
		InterceptorFeatures features = ifh.getFeatures("RecommendationTypeLeadTracking", pdbConn, prodInstId);
		return (!features.isTrackEmail() && !features.isTrackForm() && !features.isTrackHighValuePage() && !features.isTrackShoppingCart());
	}
}
