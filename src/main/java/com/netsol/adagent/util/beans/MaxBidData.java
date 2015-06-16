package com.netsol.adagent.util.beans;

public class MaxBidData {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:53:59 MaxBidData.java NSI";
    
    /** The ns_campaign_id. */
    private Long nsCampaignId;
    /** The max bid for the given campaign. */
    private Double maxBidForCampaign;
    /** The max bid for a new Google campaign. */
    private Double maxBidNewGoogleCampaign;
    /** The max bid for a new Bing campaign. */
    private Double maxBidNewBingCampaign;
    
	/**
	 * @return the nsCampaignId
	 */
	public Long getNsCampaignId() {
		return nsCampaignId;
	}
	
	/**
	 * @param nsCampaignId the nsCampaignId to set
	 */
	public void setNsCampaignId(Long nsCampaignId) {
		this.nsCampaignId = nsCampaignId;
	}
	
	/**
	 * @return the maxBidForCampaign
	 */
	public Double getMaxBidForCampaign() {
		return maxBidForCampaign;
	}
	
	/**
	 * @param maxBidForCampaign the maxBidForCampaign to set
	 */
	public void setMaxBidForCampaign(Double maxBidForCampaign) {
		this.maxBidForCampaign = maxBidForCampaign;
	}
	
	/**
	 * @return the maxBidNewGoogleCampaign
	 */
	public Double getMaxBidNewGoogleCampaign() {
		return maxBidNewGoogleCampaign;
	}
	
	/**
	 * @param maxBidNewGoogleCampaign the maxBidNewGoogleCampaign to set
	 */
	public void setMaxBidNewGoogleCampaign(Double maxBidNewGoogleCampaign) {
		this.maxBidNewGoogleCampaign = maxBidNewGoogleCampaign;
	}
	
	/**
	 * @return the maxBidNewBingCampaign
	 */
	public Double getMaxBidNewBingCampaign() {
		return maxBidNewBingCampaign;
	}
	
	/**
	 * @param maxBidNewBingCampaign the maxBidNewBingCampaign to set
	 */
	public void setMaxBidNewBingCampaign(Double maxBidNewBingCampaign) {
		this.maxBidNewBingCampaign = maxBidNewBingCampaign;
	}
    
	/**
	 * Override Object.toString().
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("nsCampaignId=" + nsCampaignId + ", ");
		sb.append("maxBidForCampaign=" + maxBidForCampaign + ", ");
		sb.append("maxBidNewGoogleCampaign=" + maxBidNewGoogleCampaign + ", ");
		sb.append("maxBidNewBingCampaign=" + maxBidNewBingCampaign);		
		return sb.toString();
	}
}
