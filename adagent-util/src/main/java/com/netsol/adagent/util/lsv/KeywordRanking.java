/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.lsv;

import java.util.Date;

import com.netsol.adagent.util.beans.BaseData;

// Keyword ranking.
public class KeywordRanking extends BaseData {
    public final static String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:11 KeywordRanking.java NSI";

    /* package-private */ final static int RANKING_TYPE_DEFAULT = 0;
    /* package-private */ final static int RANKING_TYPE_INITIAL = 1;
    /* package-private */ final static int RANKING_TYPE_LATEST = 2;

    private String keyword;
    private String keywordType;
    private int ranking;
    private Date rankingDate;
    private int rankingType;
    private String vendorName;

    /**
     * Constructor.
     */
    public KeywordRanking() {
        super();
        
        return;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getKeyword() {
        return this.keyword;
    }

    public void setKeywordType(String keywordType) {
        this.keywordType = keywordType;
    }

    public String getKeywordType() {
        return this.keywordType;
    }

    public void setRanking(int ranking) {
        this.ranking = ranking;
    }

    public int getRanking() {
        return this.ranking;
    }

    public void setRankingDate(Date rankingDate) {
        this.rankingDate = rankingDate;
    }

    public Date getRankingDate() {
        return this.rankingDate;
    }

    public void setRankingType(int rankingType) {
        this.rankingType = rankingType;
    }

    public int getRankingType() {
        return this.rankingType;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    public String getVendorName() {
        return this.vendorName;
    }
}
