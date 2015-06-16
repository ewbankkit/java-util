package com.netsol.adagent.util.siteAnalysis.beans;

import java.util.List;
import java.util.regex.Pattern;

public class Keyword {
    public enum Type{
        META, BUSINESS_NAME, COMPETITOR, SUGGESTION
    }
    
    private long keywordId; 
    private long siteId;
    private Long competitorId;
    private String keyword;
    private String baseKeyword;
    private Long searchVolume;
    private String type;
    private Long wafJobId;
    private Integer rank;
    
    private List<KeywordPpcData> keywordPpcData;
 
    
    /**
     *  Build longtailed version of each keyword by appending the business city and state. Only append if the keyword does not already contain city/state
     */
    public static void fixupKeywordLongtails(List<Keyword> keywords, String city, String state){        
        Pattern cityPattern = Pattern.compile("\\b"+city+"\\b", Pattern.CASE_INSENSITIVE);
        Pattern statePattern = Pattern.compile("\\b"+state+"\\b", Pattern.CASE_INSENSITIVE);
        for (Keyword kw : keywords) {                
            String kwText = kw.getBaseKeyword();
            if(!cityPattern.matcher(kwText).find()){
                kwText += " "+ city;
            }
            if(!statePattern.matcher(kwText).find()){
                kwText += " "+ state;
            }
            kw.setKeyword(kwText.toLowerCase());                    
        }
    }


    public long getKeywordId() {
        return keywordId;
    }


    public void setKeywordId(long keywordId) {
        this.keywordId = keywordId;
    }


    public String getKeyword() {
        return keyword;
    }


    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }


    public String getBaseKeyword() {
        return baseKeyword;
    }


    public void setBaseKeyword(String baseKeyword) {
        this.baseKeyword = baseKeyword;
    }


    public Long getSearchVolume() {
        return searchVolume;
    }


    public void setSearchVolume(Long searchVolume) {
        this.searchVolume = searchVolume;
    }


    public List<KeywordPpcData> getKeywordPpcData() {
        return keywordPpcData;
    }


    public void setKeywordPpcData(List<KeywordPpcData> keywordPpcData) {
        this.keywordPpcData = keywordPpcData;
    }


    public long getSiteId() {
        return siteId;
    }


    public void setSiteId(long siteId) {
        this.siteId = siteId;
    }


    public Long getCompetitorId() {
        return competitorId;
    }


    public void setCompetitorId(Long competitorId) {
        this.competitorId = competitorId;
    }


    public String getType() {
        return type;
    }


    public void setType(String type) {
        this.type = type;
    }


    public Long getWafJobId() {
        return wafJobId;
    }


    public void setWafJobId(Long wafJobId) {
        this.wafJobId = wafJobId;
    }


    public Integer getRank() {
        return rank;
    }


    public void setRank(Integer rank) {
        this.rank = rank;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((baseKeyword == null) ? 0 : baseKeyword.hashCode());
        result = prime * result + ((competitorId == null) ? 0 : competitorId.hashCode());
        result = prime * result + ((keyword == null) ? 0 : keyword.hashCode());
        result = prime * result + (int) (keywordId ^ (keywordId >>> 32));
        result = prime * result + ((keywordPpcData == null) ? 0 : keywordPpcData.hashCode());
        result = prime * result + ((rank == null) ? 0 : rank.hashCode());
        result = prime * result + ((searchVolume == null) ? 0 : searchVolume.hashCode());
        result = prime * result + (int) (siteId ^ (siteId >>> 32));
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        result = prime * result + ((wafJobId == null) ? 0 : wafJobId.hashCode());
        return result;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Keyword other = (Keyword) obj;
        if (baseKeyword == null) {
            if (other.baseKeyword != null)
                return false;
        } else if (!baseKeyword.equals(other.baseKeyword))
            return false;
        if (competitorId == null) {
            if (other.competitorId != null)
                return false;
        } else if (!competitorId.equals(other.competitorId))
            return false;
        if (keyword == null) {
            if (other.keyword != null)
                return false;
        } else if (!keyword.equals(other.keyword))
            return false;
        if (keywordId != other.keywordId)
            return false;
        if (keywordPpcData == null) {
            if (other.keywordPpcData != null)
                return false;
        } else if (!keywordPpcData.equals(other.keywordPpcData))
            return false;
        if (rank == null) {
            if (other.rank != null)
                return false;
        } else if (!rank.equals(other.rank))
            return false;
        if (searchVolume == null) {
            if (other.searchVolume != null)
                return false;
        } else if (!searchVolume.equals(other.searchVolume))
            return false;
        if (siteId != other.siteId)
            return false;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        if (wafJobId == null) {
            if (other.wafJobId != null)
                return false;
        } else if (!wafJobId.equals(other.wafJobId))
            return false;
        return true;
    }
}
