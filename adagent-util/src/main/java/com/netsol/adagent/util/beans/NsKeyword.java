/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.beans;

import java.math.BigDecimal;

import com.netsol.adagent.util.codes.KeywordMatchType;
import com.netsol.adagent.util.dbhelpers.BaseHelper;

/**
 * Represents an NS keyword.
 */
public class NsKeyword extends NsEntity {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:02 NsKeyword.java NSI";

    private static final int DEFAULT_TARGET_RANGE_END = 7;
    private static final int DEFAULT_TARGET_RANGE_START = 1;

    private static final int BID_CAP_SCALE = TWO_DECIMAL_PLACES;
    private static final int BID_SCALE = TWO_DECIMAL_PLACES;
    private static final int MAX_CPC_SCALE = TWO_DECIMAL_PLACES;
    private static final int MIN_CPC_SCALE = TWO_DECIMAL_PLACES;

    private BigDecimal bid = BigDecimal.ZERO;
    private BigDecimal bidCap;
    private String destinationUrl;
    private String editorialStatus;
    private String keywordType = KeywordMatchType.BROAD;
    private String location;
    private Long marketGeographyKeywordId;
    private Long marketSubCategoryGroupId;
    private BigDecimal maxCpc;
    private BigDecimal minCpc;
    private long nsAdGroupId;
    private Integer qualityScore;
    private int targetRangeEnd = DEFAULT_TARGET_RANGE_END;
    private int targetRangeStart = DEFAULT_TARGET_RANGE_START;
    private long vendorQualityScore;

    public void setBaseKeyword(String baseKeyword) {
        setName(baseKeyword);
    }

    public String getBaseKeyword() {
        return getName();
    }

    public void setBid(double bid) {
        this.bid = BaseHelper.toBigDecimal(bid, BID_SCALE);
    }

    public BigDecimal getBid() {
        return bid;
    }

    public void setBidCap(Double bidCap) {
        this.bidCap = BaseHelper.toBigDecimal(bidCap, BID_CAP_SCALE);
    }

    public BigDecimal getBidCap() {
        return bidCap;
    }

    public void setDestinationUrl(String destinationUrl) {
        this.destinationUrl = destinationUrl;
    }

    public String getDestinationUrl() {
        return destinationUrl;
    }

    public void setEditorialStatus(String editorialStatus) {
        this.editorialStatus = editorialStatus;
    }

    public String getEditorialStatus() {
        return editorialStatus;
    }

    public void setKeywordType(String keywordType) {
        this.keywordType = keywordType;
    }

    public String getKeywordType() {
        return keywordType;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLocation() {
        return location;
    }

    public void setMarketGeographyKeywordId(Long marketGeographyKeywordId) {
        this.marketGeographyKeywordId = marketGeographyKeywordId;
    }

    public Long getMarketGeographyKeywordId() {
        return marketGeographyKeywordId;
    }

    public void setMarketSubCategoryGroupId(Long marketSubCategoryGroupId) {
        this.marketSubCategoryGroupId = marketSubCategoryGroupId;
    }

    public Long getMarketSubCategoryGroupId() {
        return marketSubCategoryGroupId;
    }

    public void setMaxCpc(Double maxCpc) {
        this.maxCpc = BaseHelper.toBigDecimal(maxCpc, MAX_CPC_SCALE);
    }

    public BigDecimal getMaxCpc() {
        return maxCpc;
    }

    public void setMinCpc(Double minCpc) {
        this.minCpc = BaseHelper.toBigDecimal(minCpc, MIN_CPC_SCALE);
    }

    public BigDecimal getMinCpc() {
        return minCpc;
    }

    public void setNsAdGroupId(long nsAdGroupId) {
        this.nsAdGroupId = nsAdGroupId;
    }

    public long getNsAdGroupId() {
        return nsAdGroupId;
    }

    public void setNsKeywordId(long nsKeywordId) {
        setNsEntityId(nsKeywordId);
    }

    public long getNsKeywordId() {
        return getNsEntityId();
    }

    public void setQualityScore(Integer qualityScore) {
        this.qualityScore = qualityScore;
    }

    public Integer getQualityScore() {
        return qualityScore;
    }

    public void setTargetRangeEnd(int targetRangeEnd) {
        this.targetRangeEnd = targetRangeEnd;
    }

    public int getTargetRangeEnd() {
        return targetRangeEnd;
    }

    public void setTargetRangeStart(int targetRangeStart) {
        this.targetRangeStart = targetRangeStart;
    }

    public int getTargetRangeStart() {
        return targetRangeStart;
    }

    public void setVendorKeywordId(Long vendorKeywordId) {
        setVendorEntityId(vendorKeywordId);
    }

    public Long getVendorKeywordId() {
        return getVendorEntityId();
    }

    public void setVendorQualityScore(long vendorQualityScore) {
        this.vendorQualityScore = vendorQualityScore;
    }

    public long getVendorQualityScore() {
        return vendorQualityScore;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof NsKeyword)) {
            return false;
        }
        return equals(this, (NsKeyword)o);
    }
}
