/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.recommendations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.netsol.adagent.util.dbhelpers.BaseHelper;
import com.netsol.adagent.util.json.JsonUtil;

/**
 * Recommendation data for keyword bid adjustment due to ad placement.
 *
 * @author Adam S. Vernon
 */
public class RecommendationDataKeywordBidAdjustmentAdPlacement extends RecommendationData {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:24 RecommendationDataKeywordBidAdjustmentAdPlacement.java NSI";
    private static final Log log = LogFactory.getLog(RecommendationDataKeywordBidAdjustmentAdPlacement.class);

    /** The campaign name. */
    private String campaignName;
    /** The vendor ID. */
    private Long vendorId;
    /** The ad group name. */
    private String adGroupName;
    /** Old keyword bid amount. */
    private Double oldBid;
    /** The actual keyword. */
    private String keyword;
    /** New keyword bid amount. */
    private Double newBid;
    /** Total impressions over the date range. */
    private Long totalImpressions;
    /** Total clicks over the date range. */
    private Long totalClicks;
    /** Average position over the date range. */
    private Double averagePosition;

    /** Default constructor- used for JSON unmarshaling. */
    public RecommendationDataKeywordBidAdjustmentAdPlacement() {}

    /**
     * Construct a new data object for insertion into the database.
     *
     * @param newBid
     */
    public RecommendationDataKeywordBidAdjustmentAdPlacement(Double newBid, Long totalImpressions, Long totalClicks, Double averagePosition) {
        this.newBid = newBid;
        this.totalImpressions = totalImpressions;
        this.totalClicks = totalClicks;
        this.averagePosition = averagePosition;
    }

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
     * @return the oldBid
     */
    public Double getOldBid() {
        return oldBid;
    }

    /**
     * @param oldBid the oldBid to set
     */
    public void setOldBid(Double oldBid) {
        this.oldBid = oldBid;
    }

    /**
     * @return the keyword
     */
    public String getKeyword() {
        return keyword;
    }

    /**
     * @param keyword the keyword to set
     */
    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    /**
     * @return the newBid
     */
    public Double getNewBid() {
        return newBid;
    }

    /**
     * @param newBid the newBid to set
     */
    public void setNewBid(Double newBid) {
        this.newBid = newBid;
    }

    /**
     * @return the totalImpressions
     */
    public Long getTotalImpressions() {
        return totalImpressions;
    }

    /**
     * @param totalImpressions the totalImpressions to set
     */
    public void setTotalImpressions(Long totalImpressions) {
        this.totalImpressions = totalImpressions;
    }

    /**
     * @return the totalClicks
     */
    public Long getTotalClicks() {
        return totalClicks;
    }

    /**
     * @param totalClicks the totalClicks to set
     */
    public void setTotalClicks(Long totalClicks) {
        this.totalClicks = totalClicks;
    }

    /**
     * @return the averagePosition
     */
    public Double getAveragePosition() {
        return averagePosition;
    }

    /**
     * @param averagePosition the averagePosition to set
     */
    public void setAveragePosition(Double averagePosition) {
        this.averagePosition = averagePosition;
    }

    /**
     * Override Object.toString().
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("campaignName=" + campaignName + ", ");
        sb.append("vendorId=" + vendorId + ", ");
        sb.append("adGroupName=" + adGroupName + ", ");
        sb.append("oldBid=" + oldBid + ", ");
        sb.append("newBid=" + newBid + ", ");
        sb.append("keyword=" + keyword + ", ");
        sb.append("totalImpressions=" + totalImpressions + ", ");
        sb.append("totalClicks=" + totalClicks + ", ");
        sb.append("averagePosition=" + averagePosition);
        return sb.toString();
    }

    //
    // RecommendationData interface implementation
    //

    private static String GET_DATA_SQL =
        "select c.name, c.vendor_id, ag.name, k.base_keyword, k.bid "
        + "from ns_campaign c "
        + "left join ns_ad_group ag on c.ns_campaign_id=ag.ns_campaign_id "
        + "left join ns_keyword k on ag.ns_ad_group_id=k.ns_ad_group_id "
        + "where c.ns_campaign_id=? and ag.ns_ad_group_id=? and k.ns_keyword_id=?";

    /**
     * Initialize the recommendation data.
     */
    public void init(Connection pdbConn, Recommendation recommendation) throws Exception {
        PreparedStatement pstmt = null;
        try {
            pstmt = pdbConn.prepareStatement(GET_DATA_SQL);
            pstmt.setLong(1, recommendation.getNsCampaignId());
            pstmt.setLong(2, recommendation.getNsAdGroupId());
            pstmt.setLong(3, recommendation.getNsKeywordId());
            log.info(pstmt);
            ResultSet results = pstmt.executeQuery();
            while (results != null && results.next()) {
                int i = 1;
                campaignName = results.getString(i++);
                vendorId = results.getLong(i++);
                adGroupName = results.getString(i++);
                keyword = results.getString(i++);
                oldBid = results.getDouble(i++);
            }
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
    public static void main(String[] args) {

        String json = "{ \"newBid\" : 1.0 }";
        RecommendationDataKeywordBidAdjustmentAdPlacement data = JsonUtil.fromJson(json, RecommendationDataKeywordBidAdjustmentAdPlacement.class);
        System.out.println("From string: json=\n" + data.toJson());

        json = "{}";
        data = JsonUtil.fromJson(json, RecommendationDataKeywordBidAdjustmentAdPlacement.class);
        System.out.println("From string, no data: json=\n" + data.toJson());

        data = new RecommendationDataKeywordBidAdjustmentAdPlacement();
        data.setNewBid(2.0);
        System.out.println("From object: json=\n" + data.toJson());

        data = new RecommendationDataKeywordBidAdjustmentAdPlacement();
        System.out.println("From object, no data: json=\n" + data.toJson());
    }
}
