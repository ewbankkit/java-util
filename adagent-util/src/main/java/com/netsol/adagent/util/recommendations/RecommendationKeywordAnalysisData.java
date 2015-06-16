package com.netsol.adagent.util.recommendations;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.netsol.adagent.util.CalendarUtil;
import com.netsol.adagent.util.dbhelpers.BaseHelper;

/**
 * Keyword analysis data for bid adjustment recommendations based on ad placement.
 * 
 * @author Adam S. Vernon
 */
final class RecommendationKeywordAnalysisData {
	public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:25 RecommendationKeywordAnalysisData.java NSI";
	private static final Log log = LogFactory.getLog(RecommendationKeywordAnalysisData.class);
	
	/** The ns_keyword_id. */
	private Long nsKeywordId;
	/** The current bid. */
	private Double currentBid;
	/** Total impressions over the date range. */
	private Long totalImpressions;
	/** Total clicks over the date range. */
	private Long totalClicks;
	/** Average position over the date range. */
	private Double averagePosition;
	/** The ns_ad_group_id. */
	private Long nsAdGroupId;
	/** The ns_campaign_id. */
	private Long nsCampaignId;
	
	/**
	 * Constructor.
	 * 
	 * @param nsKeywordId
	 * @paran currentBid
	 * @param totalImpressions
	 * @param totalClicks
	 * @param averagePosition
	 * @param nsAdGroupId
	 * @param nsCampaignId
	 */
	private RecommendationKeywordAnalysisData(Long nsKeywordId, Double currentBid, Long totalImpressions, Long totalClicks, 
			Double averagePosition, Long nsAdGroupId, Long nsCampaignId) {
		this.nsKeywordId = nsKeywordId;
		this.currentBid = currentBid;
		this.totalImpressions = totalImpressions;
		this.totalClicks = totalClicks;
		this.averagePosition = averagePosition;
		this.nsAdGroupId = nsAdGroupId;
		this.nsCampaignId = nsCampaignId;
	}
	
	/**
	 * @return the nsKeywordId
	 */
	Long getNsKeywordId() {
		return nsKeywordId;
	}
	
	/**
	 * @param nsKeywordId the nsKeywordId to set
	 */
	void setNsKeywordId(Long nsKeywordId) {
		this.nsKeywordId = nsKeywordId;
	}
	
	/**
	 * @return the currentBid
	 */
	Double getCurrentBid() {
		return currentBid;
	}

	/**
	 * @param currentBid the currentBid to set
	 */
	void setCurrentBid(Double currentBid) {
		this.currentBid = currentBid;
	}
	
	/**
	 * @return the totalImpressions
	 */
	Long getTotalImpressions() {
		return totalImpressions;
	}
	
	/**
	 * @param totalImpressions the totalImpressions to set
	 */
	void setTotalImpressions(Long totalImpressions) {
		this.totalImpressions = totalImpressions;
	}

	/**
	 * @return the totalClicks
	 */
	Long getTotalClicks() {
		return totalClicks;
	}

	/**
	 * @param totalClicks the totalClicks to set
	 */
	void setTotalClicks(Long totalClicks) {
		this.totalClicks = totalClicks;
	}
	
	/**
	 * @return the averagePosition
	 */
	Double getAveragePosition() {
		return averagePosition;
	}
	
	/**
	 * @param averagePosition the averagePosition to set
	 */
	void setAveragePosition(Double averagePosition) {
		this.averagePosition = averagePosition;
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
	 * Override Object.toString().
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("nsKeywordId=" + nsKeywordId + ", ");
		sb.append("currentBid=" + currentBid + ", ");
		sb.append("totalImpressions=" + totalImpressions + ", ");
		sb.append("totalClicks=" + totalClicks + ", ");
		sb.append("averagePosition=" + averagePosition + ", ");
		sb.append("nsAdGroupId=" + nsAdGroupId + ", ");
		sb.append("nsCampaignId=" + nsCampaignId);
		return sb.toString();
	}
	
	//
	// Database
	//
	
	/** 
	 * Query product for all active keywords with impressions > 0 over the last N days with average position within the given range, 
	 * including average position. 
	 */ 
	private static final String GET_KEYWORD_IMPRESSION_DATA = 
		"select tmp.*, k.bid, k.ns_ad_group_id, ag.ns_campaign_id from ( "
			+ "select ns_keyword_id, sum(impressions) total_impressions, sum(clicks) total_clicks, sum(impressions*position)/sum(impressions) average_position "
			+ "from vendor_keyword "
			+ "where "
			+ "prod_inst_id=? and update_date >= ? and impressions > 0 and "
			+ "ns_keyword_id in "
			+ "(select ns_keyword_id from ns_keyword "
			+ "inner join ns_ad_group on ns_keyword.ns_ad_group_id=ns_ad_group.ns_ad_group_id "
			+ "inner join ns_campaign on ns_ad_group.ns_campaign_id=ns_campaign.ns_campaign_id "
			+ "where ns_keyword.prod_inst_id=? and ns_keyword.status='ACTIVE' and ns_ad_group.status='ACTIVE' " 
			+ "and ns_campaign.status in ('ACTIVE','SYSTEM_PAUSE')) "
			+ "group by ns_keyword_id) tmp "
		+ "inner join ns_keyword k on tmp.ns_keyword_id=k.ns_keyword_id "
		+ "inner join ns_ad_group ag on k.ns_ad_group_id=ag.ns_ad_group_id "
		+ "where average_position < ? or average_position > ?";

	/**
	 * Get analysis data for all active keywords with average position outside the given min/max range over the 
	 * given day range. Not all active keywords will return data.
	 * 
	 * @param pdbConn
	 * @param prodInstId
	 * @param analysisRangeDays
	 * @param minPosition
	 * @param maxPosition
	 * @return an array of KeywordImpressionData objects
	 * @throws Exception
	 */
	static RecommendationKeywordAnalysisData[] getKeywordImpressionData(Connection pdbConn, String prodInstId,
			Integer analysisRangeDays, Integer minPosition, Integer maxPosition) throws Exception {
		
		RecommendationKeywordAnalysisData[] returnValue = {};
		PreparedStatement pstmt = null;
		try {			
			Calendar analysisStartDate = Calendar.getInstance();
			analysisStartDate.add(Calendar.DAY_OF_YEAR, - (analysisRangeDays));
			
			pstmt = pdbConn.prepareStatement(GET_KEYWORD_IMPRESSION_DATA);
			pstmt.setString(1, prodInstId);
			pstmt.setDate(2, CalendarUtil.calendarToSqlDate(analysisStartDate));
			pstmt.setString(3, prodInstId);
			pstmt.setInt(4, minPosition);
			pstmt.setInt(5, maxPosition);
			log.info(pstmt);
			ResultSet results = pstmt.executeQuery();
			ArrayList<RecommendationKeywordAnalysisData> list = new ArrayList<RecommendationKeywordAnalysisData>();
			while (results != null && results.next()) {
				int i = 1;
				Long nsKeywordId = results.getLong(i++);
				Long totalImpressions = results.getLong(i++);
				Long totalClicks = results.getLong(i++);
				Double averagePosition = results.getDouble(i++);
				Double bid = results.getDouble(i++);
				Long nsAdGroupId = results.getLong(i++);
				Long nsCampaignId = results.getLong(i++);
				list.add(new RecommendationKeywordAnalysisData(nsKeywordId, bid, totalImpressions, totalClicks, averagePosition, nsAdGroupId, nsCampaignId));
			}	
			returnValue = list.toArray(new RecommendationKeywordAnalysisData[list.size()]);
		}
		finally {
			BaseHelper.close(pstmt);
		}
		return returnValue;
	}
	
	//
	// Unit test
	//
	
	/**
	 * Unit test.
	 */
	public static void main(String[] args) {
		Connection pdbConn = null;
		try {
			// Get dev pdb2 connection.
			Class.forName("com.mysql.jdbc.Driver").newInstance();
		    pdbConn = DriverManager.getConnection("jdbc:mysql://eng2.dev.netsol.com:4301/adagent?user=adagent&password=adagent");

		    RecommendationKeywordAnalysisData[] dataArray =  RecommendationKeywordAnalysisData.getKeywordImpressionData(pdbConn, 
		    		"WN-DIY-TEST9", 7, 2, 8);
		    for (RecommendationKeywordAnalysisData data : dataArray) {
		    	System.out.println(data);
		    }
		}
		catch(Throwable e) {
			e.printStackTrace(System.out);
		}
		finally {
	    	BaseHelper.close(pdbConn);
		}
	}
}
