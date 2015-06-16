package com.netsol.adagent.util.recommendations;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Types;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.netsol.adagent.util.dbhelpers.BaseHelper;

/**
 * 
 * @author Adam S. Vernon
 */
@SuppressWarnings("serial")
public class Recommendation implements Serializable {
	public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:23 Recommendation.java NSI";
	private static final Log log = LogFactory.getLog(Recommendation.class);
	
	/** Status enumeration. */
	public enum Status { ACTIVE, IGNORED, COMPLETED, DELETED };
	
	/** The primary key of the recommendation table. */
	private Long recommendationId;
	/** The product instance ID. */
	private String prodInstId;
	/** The recommendation type object. */
	private RecommendationType recommendationType;
	/** The recommendation status. */
	private String status;
	/** The campaign ID, if applicable. */
	private Long nsCampaignId;
	/** The ad group ID, if applicable. */
	private Long nsAdGroupId;
	/** The ad ID, if applicable. */
	private Long nsAdId;
	/** The keyword ID, if applicable. */
	private Long nsKeywordId;
	/** The date of the recommendation in string format YYYY-MM-DD. */
	private String recommendationDateString;
	/** The date the recommendation was completedin string format YYYY-MM-DD. */
	private String completedDateString;
	/** The date the recommendation was ignoredin string format YYYY-MM-DD. */
	private String ignoredDateString;
	/** The recommendation data object. */
	private RecommendationData recommendationData;
	
	/**
	 * Constructor for use when querying recommendations from the database. 
	 * 
	 * @param recommendationId
	 * @param prodInstId
	 * @param recommendationType
	 * @param status
	 * @param nsCampaignId
	 * @param nsAdGroupId
	 * @param nsAdId
	 * @param nsKeywordId
	 * @param recommendationDate
	 * @param completedDate
	 * @param ignoredDate
	 */
	public Recommendation(Long recommendationId, String prodInstId, RecommendationType recommendationType, Status status, 
			Long nsCampaignId, Long nsAdGroupId, Long nsAdId, Long nsKeywordId, String recommendationDateString, String completedDateString,
			String ignoredDateString) {
		this.recommendationId = recommendationId;
		this.prodInstId = prodInstId;
		this.recommendationType = recommendationType;
		this.status = status.toString();
		this.nsCampaignId = nsCampaignId;
		this.nsAdGroupId = nsAdGroupId;
		this.nsAdId = nsAdId;
		this.nsKeywordId = nsKeywordId;
		this.recommendationDateString = recommendationDateString;
		this.completedDateString = completedDateString;
		this.ignoredDateString = ignoredDateString;
	}
	
	/**
	 * Constructor for creating a new recommendation for insertion. Status is set to ACTIVE.
	 * 
	 * @param prodInstId
	 * @param recommendationType
	 */	
	public Recommendation(String prodInstId, RecommendationType recommendationType) {
		this.prodInstId = prodInstId;
		this.recommendationType = recommendationType;
		this.status = Status.ACTIVE.toString();
	}

	/**
	 * @return the recommendationId
	 */
	public Long getRecommendationId() {
		return recommendationId;
	}

	/**
	 * @param recommendationId the recommendationId to set
	 */
	public void setRecommendationId(Long recommendationId) {
		this.recommendationId = recommendationId;
	}

	/**
	 * @return the prodInstId
	 */
	public String getProdInstId() {
		return prodInstId;
	}

	/**
	 * @param prodInstId the prodInstId to set
	 */
	public void setProdInstId(String prodInstId) {
		this.prodInstId = prodInstId;
	}

	/**
	 * @return the recommendationType
	 */
	public RecommendationType getRecommendationType() {
		return recommendationType;
	}

	/**
	 * @param recommendationType the recommendationType to set
	 */
	public void setRecommendationType(RecommendationType recommendationType) {
		this.recommendationType = recommendationType;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

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
	 * @return the nsAdGroupId
	 */
	public Long getNsAdGroupId() {
		return nsAdGroupId;
	}

	/**
	 * @param nsAdGroupId the nsAdGroupId to set
	 */
	public void setNsAdGroupId(Long nsAdGroupId) {
		this.nsAdGroupId = nsAdGroupId;
	}

	/**
	 * @return the nsAdId
	 */
	public Long getNsAdId() {
		return nsAdId;
	}

	/**
	 * @param nsAdId the nsAdId to set
	 */
	public void setNsAdId(Long nsAdId) {
		this.nsAdId = nsAdId;
	}

	/**
	 * @return the nsKeywordId
	 */
	public Long getNsKeywordId() {
		return nsKeywordId;
	}

	/**
	 * @param nsKeywordId the nsKeywordId to set
	 */
	public void setNsKeywordId(Long nsKeywordId) {
		this.nsKeywordId = nsKeywordId;
	}

	/**
	 * @return the recommendationDateString
	 */
	public String getRecommendationDateString() {
		return recommendationDateString;
	}

	/**
	 * @param recommendationDateString the recommendationDateString to set
	 */
	public void setRecommendationDateString(String recommendationDateString) {
		this.recommendationDateString = recommendationDateString;
	}

	/**
	 * @return the completedDateString
	 */
	public String getCompletedDateString() {
		return completedDateString;
	}

	/**
	 * @param completedDateString the completedDateString to set
	 */
	public void setCompletedDateString(String completedDateString) {
		this.completedDateString = completedDateString;
	}

	/**
	 * @return the ignoredDateString
	 */
	public String getIgnoredDateString() {
		return ignoredDateString;
	}

	/**
	 * @param ignoredDate the ignoredDate to set
	 */
	public void setIgnoredDateString(String ignoredDateString) {
		this.ignoredDateString = ignoredDateString;
	}

	/**
	 * @return the recommendationData
	 */
	public RecommendationData getRecommendationData() {
		return recommendationData;
	}

	/**
	 * @param recommendationData the recommendationData to set
	 */
	public void setRecommendationData(RecommendationData recommendationData) {
		this.recommendationData = recommendationData;
	}
	
	/**
	 * Get the entity ID for this recommendation.
	 * @return
	 */
	public Object getEntityId() {
		Object entityId = null;
		switch(RecommendationType.EntityType.valueOf(recommendationType.getEntityType())) {
			case PRODUCT:
				entityId = prodInstId;
				break;
			case CAMPAIGN:
				entityId = nsCampaignId;
				break;
			case AD_GROUP:
				entityId = nsAdGroupId;
				break;
			case AD:
				entityId = nsAdId;
				break;
			case KEYWORD:
				entityId = nsKeywordId;
				break;
		}
		return entityId;
	}
	
	/**
	 * Override Object.toString().
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("recommendationId=" + recommendationId + ", ");
		sb.append("prodInstId=" + prodInstId + ", ");
		sb.append("recommendationType=" + recommendationType + ", ");
		sb.append("status=" + status + ", ");
		sb.append("nsCampaignId=" + nsCampaignId + ", ");
		sb.append("nsAdGroupId=" + nsAdGroupId + ", ");
		sb.append("nsAdId=" + nsAdId + ", ");
		sb.append("nsKeywordId=" + nsKeywordId + ", ");
		sb.append("recommendationDateString=" + recommendationDateString + ", ");
		sb.append("completedDateString=" + completedDateString + ", ");
		sb.append("ignoredDateString=" + ignoredDateString + ", ");
		sb.append("recommendationData=" + recommendationData);		
		return sb.toString();
	}
	
	//
	// Database
	//

	/** SQL to update the status to IGNORED. The ignored_date is also updated. */
	private static final String UPDATE_STATUS_IGNORED_SQL = 
		"update recommendation set status='IGNORED', ignored_date=now(), updated_by_user=? where recommendation_id=?";
	/** SQL to update the status to COMPLETED. The completed_date is also updated. */
	private static final String UPDATE_STATUS_COMPLETED_SQL = 
		"update recommendation set status='COMPLETED', completed_date=now(), updated_by_user=? where recommendation_id=?";
	/** SQL to update the status. */
	private static final String UPDATE_STATUS_OTHER_SQL = 
		"update recommendation set status=?, updated_by_user=? where recommendation_id=?";
	
	/**
	 * Update the recommendation status.
	 * 
	 * @param pdbConn
	 * @param recommendationId
	 * @param status
	 * @throws Exception
	 */
	public static void updateStatus(Connection pdbConn, Long recommendationId, String status, String updatedByUser) throws Exception {
		PreparedStatement pstmt = null;
		try {
			Status s = Status.valueOf(status);
			if (Status.IGNORED.equals(s)) {
				pstmt = pdbConn.prepareStatement(UPDATE_STATUS_IGNORED_SQL);
				pstmt.setString(1, updatedByUser);
				pstmt.setLong(2, recommendationId);	
			}
			else if (Status.COMPLETED.equals(s)) {
				pstmt = pdbConn.prepareStatement(UPDATE_STATUS_COMPLETED_SQL);
				pstmt.setString(1, updatedByUser);
				pstmt.setLong(2, recommendationId);
			}
			else {
				pstmt = pdbConn.prepareStatement(UPDATE_STATUS_OTHER_SQL);
				pstmt.setString(1, status);
				pstmt.setString(2, updatedByUser);
				pstmt.setLong(3, recommendationId);
			}
			log.info(pstmt);
			pstmt.executeUpdate();
		}
		finally {
			BaseHelper.close(pstmt);
		}
	}	

	/** SQL to update the recommendation date. */
	private static final String UPDATE_RECOMMENDATION_SQL = 
		"update recommendation set recommendation_date=now(), status=?, recommendation_data=?, updated_by_user=? where recommendation_id=?";
	
	/**
	 * Update the status, recommendation data, and recommendation date.
	 * 
	 * @param pdbConn
	 * @param updatedByUser
	 * @throws Exception
	 */
	public void update(Connection pdbConn, String updatedByUser) throws Exception {
		PreparedStatement pstmt = null;
		try {
			pstmt = pdbConn.prepareStatement(UPDATE_RECOMMENDATION_SQL);
			pstmt.setString(1, status.toString());
			if (recommendationData != null) {
				pstmt.setString(2, recommendationData.toJson());
			}
			else {
				pstmt.setNull(2, Types.VARCHAR);
			}
			pstmt.setString(3, updatedByUser);
			pstmt.setLong(4, recommendationId);	
			log.info(pstmt);
			pstmt.executeUpdate();
		}
		finally {
			BaseHelper.close(pstmt);
		}
	}	
	
	private static String INSERT_SQL = "insert into recommendation (prod_inst_id, recommendation_type_id,status,ns_campaign_id,ns_ad_group_id,"
			+ "ns_ad_id,ns_keyword_id,recommendation_date,recommendation_data,created_date,updated_by_user) "
			+ "values (?,?,?,?,?,?,?,now(),?,now(),?)";
	
	public void insert(Connection pdbConn, String updatedBy) throws Exception {
		PreparedStatement pstmt = null;
		try {
			pstmt = pdbConn.prepareStatement(INSERT_SQL);
			pstmt.setString(1, prodInstId);
			pstmt.setLong(2, recommendationType.getRecommendationTypeId());
			pstmt.setString(3, status.toString());
			pstmt.setObject(4, nsCampaignId);
			pstmt.setObject(5, nsAdGroupId);
			pstmt.setObject(6, nsAdId);
			pstmt.setObject(7, nsKeywordId);
			if (recommendationData != null) {
				pstmt.setString(8, recommendationData.toJson());
			}
			else {
				pstmt.setNull(8, Types.VARCHAR);
			}
			pstmt.setString(9, updatedBy);
			log.info(pstmt);
			pstmt.executeUpdate();
		}
		finally {
			BaseHelper.close(pstmt);
		}
	}

	//
	// Unit test
	//
	
	/**
	 * Simple unit test.
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args){
		Connection pdbConn = null;
		try {
			// Get dev pdb2 connection.
			Class.forName("com.mysql.jdbc.Driver").newInstance();
		    pdbConn = DriverManager.getConnection("jdbc:mysql://eng2.dev.netsol.com:4301/adagent?user=adagent&password=adagent");

		    // updateStatus
		    Recommendation.updateStatus(pdbConn, 31l, "IGNORED", "avernon");
		    Recommendation.updateStatus(pdbConn, 30l, "COMPLETED", "avernon");
		    Recommendation.updateStatus(pdbConn, 29l, "DELETED", "avernon");

		    System.out.println("Recommendations:");
		    RecommendationFactory rf = RecommendationFactory.getInstance();
		    Recommendation[] recommendations = rf.getRecommendations(pdbConn, "WN-DIY-TEST4");
		    for (Recommendation r : recommendations) {
		    	System.out.println(r);
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
