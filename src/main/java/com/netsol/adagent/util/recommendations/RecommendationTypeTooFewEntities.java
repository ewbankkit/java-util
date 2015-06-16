package com.netsol.adagent.util.recommendations;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.netsol.adagent.util.dbhelpers.BaseHelper;

/**
 * A recommendation type - too few entities.
 * 
 * @author Adam S. Vernon
 */
@SuppressWarnings("serial")
public abstract class RecommendationTypeTooFewEntities extends RecommendationType implements Serializable {
	public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:26 RecommendationTypeTooFewEntities.java NSI";
	private static final Log log = LogFactory.getLog(RecommendationTypeTooFewEntities.class);

	/** The minimum number of entities. */
	private Integer minCount;
	
	/**
	 * @return the minCount
	 */
	public Integer getMinCount() {
		return minCount;
	}

	/**
	 * @param minCount the minCount to set
	 */
	public void setMinCount(Integer minCount) {
		this.minCount = minCount;
	}
	
	/**
	 * Override Object.toString().
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(super.toString());
		sb.append(", minCount=" + minCount);
		return sb.toString();
	}
	
	//
	// RecommendationType abstract interface implementation
	//

	/**
	 * Generate recommendations for the product, if needed.
	 * 
	 * @paran pdbConn
	 * @param prodInstId
	 * @param updatedBy
	 * @return
	 */
	protected boolean generateRecommendations(Connection pdbConn, String prodInstId, String updatedBy) throws Exception {
		boolean generated = false;
		// Get active recommendations.
		RecommendationFactory rf = RecommendationFactory.getInstance();
		Map<Object, Recommendation> activeRecommendationMap = rf.getActiveRecommendationsOfType(pdbConn, prodInstId, this);
		Recommendation activeRecommendation = activeRecommendationMap.get(prodInstId);
		// Check to see if the recommendation is needed.
		if (isRecommendationNeeded(pdbConn, prodInstId, null)) {
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
	// Abstract interface
	//
	
	/**
	 * Get SQL to query the entity count.
	 */
	protected abstract String getEntityCountSql();
	
	// 
	// Protected helper methods
	//

	/**
	 * Determine if there are too few entities.
	 * 
	 * @param conn
	 * @param prodInstId
	 * @param nsAdGroupId - optional
	 * @return true if a new recommendation is needed
	 */
	protected boolean isRecommendationNeeded(Connection pdbConn, String prodInstId, Long nsAdGroupId) throws Exception {
		PreparedStatement pstmt = null;
		boolean returnValue = false;
		try {
			pstmt = pdbConn.prepareStatement(getEntityCountSql());
			pstmt.setString(1, prodInstId);
			if (nsAdGroupId != null) {
				pstmt.setLong(2, nsAdGroupId);
			}
			log.info(pstmt);
			ResultSet results = pstmt.executeQuery();
			if (results != null && results.next()) {
				int count = results.getInt(1);
				returnValue = count < getMinCount();
			}
		}
		finally {
			BaseHelper.close(pstmt);
		}		
		return returnValue;
	}
}
