package com.netsol.adagent.util.recommendations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.netsol.adagent.util.dbhelpers.BaseHelper;

/**
 * Recommendation data for adding a new ad.
 * 
 * @author Adam S. Vernon
 */
public class RecommendationDataAddNewAd extends RecommendationData {
	public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:23 RecommendationDataAddNewAd.java NSI";
	private static final Log log = LogFactory.getLog(RecommendationDataAddNewAd.class);

	/** The campaign name. */
	private String campaignName;
	/** The vendor ID. */
	private Long vendorId;
	/** The ad group name. */
	private String adGroupName;
	/** Existing ads. */
	private RecommendationAdData[] existingAds;

	/**
	 * @return the campaignName
	 */
	public String getCampaignName() {
		return campaignName;
	}

	/**
	 * @param campaignName the campaignName to set
	 */
	public void setCampaignName(String campaignName) {
		this.campaignName = campaignName;
	}

	/**
	 * @return the vendorId
	 */
	public Long getVendorId() {
		return vendorId;
	}

	/**
	 * @param vendorId the vendorId to set
	 */
	public void setVendorId(Long vendorId) {
		this.vendorId = vendorId;
	}

	/**
	 * @return the adGroupName
	 */
	public String getAdGroupName() {
		return adGroupName;
	}

	/**
	 * @param adGroupName the adGroupName to set
	 */
	public void setAdGroupName(String adGroupName) {
		this.adGroupName = adGroupName;
	}

	/**
	 * @return the existingAds
	 */
	public RecommendationAdData[] getExistingAds() {
		return existingAds;
	}

	/**
	 * @param existingAds the existingAds to set
	 */
	public void setExistingAds(RecommendationAdData[] existingAds) {
		this.existingAds = existingAds;
	}
	
	/**
	 * Override Object.toString().
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("campaignName=" + campaignName + ", ");
		sb.append("vendorId=" + vendorId + ", ");
		sb.append("adGroupName=" + adGroupName + ", ");
		sb.append("existingAds=\n");
		if (existingAds != null) {
			for (int i = 0; i < existingAds.length; i++) {
				sb.append("\t" + existingAds[i] + "\n");
			}
		}
		return sb.toString();
	}
	
	//
	// RecommendationData interface implementation
	//
	
	private static String GET_DATA_SQL = 
		"select c.name, c.vendor_id, ag.name, a.headline, a.desc_1, a.desc_2, a.display_url, a.destination_url "
		+ "from ns_campaign c "
		+ "left join ns_ad_group ag on c.ns_campaign_id=ag.ns_campaign_id "
		+ "left join ns_ad a on ag.ns_ad_group_id=a.ns_ad_group_id "
		+ "where c.ns_campaign_id=? and ag.ns_ad_group_id=?";
	
	/**
	 * Initialize the recommendation data. 
	 */
	public void init(Connection pdbConn, Recommendation recommendation) throws Exception {
		PreparedStatement pstmt = null;
		try {
			pstmt = pdbConn.prepareStatement(GET_DATA_SQL);
			pstmt.setLong(1, recommendation.getNsCampaignId());
			pstmt.setLong(2, recommendation.getNsAdGroupId());
			log.info(pstmt);
			ResultSet results = pstmt.executeQuery();
			int i = 1;
			ArrayList<RecommendationAdData> list = new ArrayList<RecommendationAdData>();
			if (results != null && results.next()) {
				campaignName = results.getString(i++);
				vendorId = results.getLong(i++);
				adGroupName = results.getString(i++);
				list.add(getRecommendationAdData(results, i));
			}
			while (results != null && results.next()) {
				i = 4;
				list.add(getRecommendationAdData(results, i));
			}
			existingAds = (RecommendationAdData[])list.toArray(new RecommendationAdData[list.size()]);
		}
		finally {
			BaseHelper.close(pstmt);
		}
	}
	
	//
	// Private helper methods
	//
	
	private RecommendationAdData getRecommendationAdData(ResultSet results, int i) throws Exception {
		String headline = results.getString(i++);
		String desc1 = results.getString(i++);
		String desc2 = results.getString(i++);
		String displayUrl = results.getString(i++);
		String destinationUrl = results.getString(i++);
		return new RecommendationAdData(headline, desc1, desc2, displayUrl, destinationUrl);
	}
}
