package com.netsol.adagent.util.recommendations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.netsol.adagent.util.dbhelpers.BaseHelper;

/**
 * Recommendation entity type data reusable helper class. The entity types are campaign, ad group, ad, and keyword.
 * 
 * For now, it's only being used for getting IDs of active ad groups in active campaigns. 
 * 
 * @author Adam S. Vernon
 */
final class RecommendationEntityTypeData {
	public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:24 RecommendationEntityTypeData.java NSI";
	private static final Log log = LogFactory.getLog(RecommendationEntityTypeData.class);
	
	/** The campaign ID. */
	private Long nsCampaignId;
	/** The ad group ID. */
	private Long nsAdGroupId;

	/**
	 * @return the nsCampaignId
	 */
	Long getNsCampaignId() {
		return nsCampaignId;
	}
	
	/**
	 * @param nsCampaignId the nsCampaignId to set
	 */
	void setNsCampaignId(Long nsCampaignId) {
		this.nsCampaignId = nsCampaignId;
	}
	
	/**
	 * @return the nsAdGroupId
	 */
	Long getNsAdGroupId() {
		return nsAdGroupId;
	}
	
	/**
	 * @param nsAdGroupId the nsAdGroupId to set
	 */
	void setNsAdGroupId(Long nsAdGroupId) {
		this.nsAdGroupId = nsAdGroupId;
	}
	
	/** SQL to get data for all active ad groups that are in active campaigns. */
	private static final String GET_DATA_ACTIVE_AD_GROUPS_IN_ACTIVE_CAMPAIGNS =
		"select c.ns_campaign_id,ag.ns_ad_group_id "
		+ "from ns_campaign c inner join ns_ad_group ag on c.ns_campaign_id=ag.ns_campaign_id "
		+ "where c.prod_inst_id=? and c.status in ('ACTIVE', 'SYSTEM_PAUSE') and ag.status='ACTIVE'";
	
	/**
	 * 
	 * @param pdbConn
	 * @param prodInstId
	 * @return
	 * @throws Exception
	 */
	static RecommendationEntityTypeData[] getDataForActiveAdGroupsInActiveCampaigns(Connection pdbConn, String prodInstId) throws Exception {
		RecommendationEntityTypeData[] returnValue = {};
		PreparedStatement pstmt = null;
		try {			
			pstmt = pdbConn.prepareStatement(GET_DATA_ACTIVE_AD_GROUPS_IN_ACTIVE_CAMPAIGNS);
			pstmt.setString(1, prodInstId);
			log.info(pstmt);
			ResultSet results = pstmt.executeQuery();
			ArrayList<RecommendationEntityTypeData> list = new ArrayList<RecommendationEntityTypeData>();
			while (results != null && results.next()) {
				RecommendationEntityTypeData data = new RecommendationEntityTypeData();
				data.setNsCampaignId(results.getLong(1));
				data.setNsAdGroupId(results.getLong(2));
				list.add(data);
			}	
			returnValue = (RecommendationEntityTypeData[])list.toArray(new RecommendationEntityTypeData[list.size()]);
		}
		finally {
			BaseHelper.close(pstmt);
		}
		return returnValue;		
	}
}
