package com.netsol.adagent.util.recommendations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.netsol.adagent.util.dbhelpers.BaseHelper;

/**
 * Recommendation data for adding new keywords.
 * 
 * @author Adam S. Vernon
 */
public class RecommendationDataAddNewKeywords extends RecommendationData {
	public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:24 RecommendationDataAddNewKeywords.java NSI";
	private static final Log log = LogFactory.getLog(RecommendationDataAddNewKeywords.class);

	/** The campaign name. */
	private String campaignName;
	/** The vendor ID. */
	private Long vendorId;
	/** The ad group name. */
	private String adGroupName;
	/** Existing ads. */
	private String[] existingKeywords;

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
	 * @return the existingKeywords
	 */
	public String[] getExistingKeywords() {
		return existingKeywords;
	}

	/**
	 * @param existingKeywords the existingKeywords to set
	 */
	public void setExistingKeywords(String[] existingKeywords) {
		this.existingKeywords = existingKeywords;
	}
	
	/**
	 * Override Object.toString().
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("campaignName=" + campaignName + ", ");
		sb.append("vendorId=" + vendorId + ", ");
		sb.append("adGroupName=" + adGroupName + ", ");
		sb.append("existingKeywords=\n");
		if (existingKeywords != null) {
			for (int i = 0; i < existingKeywords.length; i++) {
				sb.append("\t" + existingKeywords[i] + "\n");
			}
		}
		return sb.toString();
	}
	
	//
	// RecommendationData interface implementation
	//
	
	private static String GET_DATA_SQL = 
		"select c.name, c.vendor_id, ag.name, k.base_keyword "
		+ "from ns_campaign c "
		+ "left join ns_ad_group ag on c.ns_campaign_id=ag.ns_campaign_id "
		+ "left join ns_keyword k on ag.ns_ad_group_id=k.ns_ad_group_id "
		+ "where c.ns_campaign_id=? and ag.ns_ad_group_id=? and k.status='ACTIVE'";
	
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
			ArrayList<String> list = new ArrayList<String>();
			if (results != null && results.next()) {
				campaignName = results.getString(i++);
				vendorId = results.getLong(i++);
				adGroupName = results.getString(i++);
				list.add(results.getString(i++));
			}
			while (results != null && results.next()) {
				i = 4;
				list.add(results.getString(i++));
			}
			existingKeywords = (String[])list.toArray(new String[list.size()]);
		}
		finally {
			BaseHelper.close(pstmt);
		}
	}
}
