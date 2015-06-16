package com.netsol.adagent.util.recommendations;

import java.io.Serializable;
import java.sql.Connection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Recommendation type - keyword bid adjustment due to ad position being outside the recommended range.
 * 
 * @author Adam S. Vernon
 */
@SuppressWarnings("serial")
public class RecommendationTypeKeywordBidAdjustmentAdPlacement extends RecommendationType implements Serializable  {
	public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:25 RecommendationTypeKeywordBidAdjustmentAdPlacement.java NSI";
	
	/** The miminum recommended position at the vendor. */
	private Integer minPosition;
	/** The maximum recommended position at the vendor. */ 
	private Integer maxPosition;
	/** The analysis range in days for determining the average position. */
	private Integer analysisRangeDays;
	/** The percentage to adjust the bid if not within the recommended position range. */
	private Double adjustmentPercent;
	
	/**
	 * @return the minPosition
	 */
	public Integer getMinPosition() {
		return minPosition;
	}

	/**
	 * @param minPosition the minPosition to set
	 */
	public void setMinPosition(Integer minPosition) {
		this.minPosition = minPosition;
	}

	/**
	 * @return the maxPosition
	 */
	public Integer getMaxPosition() {
		return maxPosition;
	}

	/**
	 * @param maxPosition the maxPosition to set
	 */
	public void setMaxPosition(Integer maxPosition) {
		this.maxPosition = maxPosition;
	}

	/**
	 * @return the analysisRangeDays
	 */
	public Integer getAnalysisRangeDays() {
		return analysisRangeDays;
	}

	/**
	 * @param analysisRangeDays the analysisRangeDays to set
	 */
	public void setAnalysisRangeDays(Integer analysisRangeDays) {
		this.analysisRangeDays = analysisRangeDays;
	}

	/**
	 * @return the adjustmentPercent
	 */
	public Double getAdjustmentPercent() {
		return adjustmentPercent;
	}

	/**
	 * @param adjustmentPercent the adjustmentPercent to set
	 */
	public void setAdjustmentPercent(Double adjustmentPercent) {
		this.adjustmentPercent = adjustmentPercent;
	}
	
	/**
	 * Override Object.toString().
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(super.toString());
		sb.append(", minPosition=" + minPosition + ", ");
		sb.append("maxPosition=" + maxPosition + ", ");
		sb.append("analysisRangeDays=" + analysisRangeDays + ", ");
		sb.append("adjustmentPercent=" + adjustmentPercent);
		return sb.toString();
	}
	
	//
	// RecommendationType abstract interface implementation:
	//
	
	/**
	 * Generate recommendations for the product, if needed.
	 * 
	 * @paran pdbConn
	 * @param prodInstId
	 * @param updatedBy
	 * @return
	 */
	public boolean generateRecommendations(Connection pdbConn, String prodInstId, String updatedBy) throws Exception {
		boolean generated = false;
		// Initialize analysis data for all active keywords outside the configured position range. All keywords returned
		// in the KeywordAnalysisData array need a recommendation.
		RecommendationKeywordAnalysisData[] keywordAnalysisDataArray = RecommendationKeywordAnalysisData.getKeywordImpressionData(pdbConn, prodInstId,
				analysisRangeDays, minPosition, maxPosition);
		// Get active recommendations.
		RecommendationFactory rf = RecommendationFactory.getInstance();
		Map<Object, Recommendation> activeRecommendationMap = rf.getActiveRecommendationsOfType(pdbConn, prodInstId, this);
		Set<Object> nsKeywordIdSet = new HashSet<Object>();
		for (RecommendationKeywordAnalysisData keywordAnalysisData : keywordAnalysisDataArray) {
			generated = true;
			Long nsKeywordId = keywordAnalysisData.getNsKeywordId();
			// Calculate the new bid.
			double currentBid = keywordAnalysisData.getCurrentBid();
			double newBid = currentBid + (currentBid * adjustmentPercent);
			if (keywordAnalysisData.getAveragePosition() < minPosition) {
				// Lower the bid if it is too high.
				newBid = currentBid - (currentBid * adjustmentPercent);
			}
			// Create the recommendation data.
			RecommendationData recommendationData = new RecommendationDataKeywordBidAdjustmentAdPlacement(newBid, keywordAnalysisData.getTotalImpressions(), 
					keywordAnalysisData.getTotalClicks(), keywordAnalysisData.getAveragePosition());
			Recommendation activeRecommendation = activeRecommendationMap.get(nsKeywordId);
			if (activeRecommendation != null) {
				// An active recommendation already exists. We will update the data and recommendation date.
				activeRecommendation.setRecommendationData(recommendationData);
				activeRecommendation.update(pdbConn, updatedBy);
			}
			else {
				// There is not an existing active recommendation. We will create a new one.
				Recommendation recommendation = new Recommendation(prodInstId, this);
				recommendation.setNsCampaignId(keywordAnalysisData.getNsCampaignId());
				recommendation.setNsAdGroupId(keywordAnalysisData.getNsAdGroupId());
				recommendation.setNsKeywordId(nsKeywordId);
				recommendation.setRecommendationData(recommendationData);
				recommendation.insert(pdbConn, updatedBy);
			}
			
			// While we are looping over the keywords, build a set of nsKeywordIds so that the next step will be easier.
			nsKeywordIdSet.add(nsKeywordId);
		}

		// Now remove any active recommendations that are no longer valid.
		Set<Object> activeNsKeywordIdSet = activeRecommendationMap.keySet();
		for (Object nsKeywordId : nsKeywordIdSet) {
			if (activeNsKeywordIdSet.contains(nsKeywordId)) {
				// Update the status to DELETED.
				Recommendation recommendation = activeRecommendationMap.get(nsKeywordId);
				recommendation.setStatus(Recommendation.Status.DELETED.toString());
				recommendation.update(pdbConn, updatedBy);
			}
		}
		return generated;
	}
	
	//
	// Unit test
	//
	
	public static void main(String[] args) {
		// Test that two different Object ref instances of the same Long nsKeywordId value compare correctly across two different collections.
		Set<Object> activeNsKeywordIdSet = new HashSet<Object>();
		activeNsKeywordIdSet.add((Object)new Long(1000L));
		Set<Object> nsKeywordIdSet = new HashSet<Object>();
		nsKeywordIdSet.add((Object)new Long(1000L));
		for (Object nsKeywordId : nsKeywordIdSet) {
			if (activeNsKeywordIdSet.contains(nsKeywordId)) {
				System.out.println("It works ok.");
			}
			else {
				System.out.println("Sorry, you're screwed.");
			}
		}
		
		System.out.println("Does contains work? " + activeNsKeywordIdSet.contains(new Long(1000L)));
		
	}
}
