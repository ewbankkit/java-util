package com.netsol.adagent.util.beans;

public class SeoKeyword{
    public static final String sccsID = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:07 SeoKeyword.java NSI";
    
    public static enum KeywordStatus{ ACTIVE, DELETED } 
    
    private String prodInstId;
    private long seoKeywordGroupId;
    private long seoKeywordId;
    private String keyword;
    private KeywordStatus status;
    private String updatedBySystem;
    private String updatedByUser;
    
    public String getProdInstId() {
        return prodInstId;
    }
    public void setProdInstId(String prodInstId) {
        this.prodInstId = prodInstId;
    }
    public long getSeoKeywordGroupId() {
        return seoKeywordGroupId;
    }
    public void setSeoKeywordGroupId(long seoKeywordGroupId) {
        this.seoKeywordGroupId = seoKeywordGroupId;
    }
    public long getSeoKeywordId() {
        return seoKeywordId;
    }
    public void setSeoKeywordId(long seoKeywordId) {
        this.seoKeywordId = seoKeywordId;
    }
    public String getKeyword() {
        return keyword;
    }
    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
    public KeywordStatus getStatus()
    {
        return (this.status);
    }
    public void setStatus(KeywordStatus status)
    {
        this.status = status;
    }
    public String getUpdatedBySystem() {
        return updatedBySystem;
    }
    public void setUpdatedBySystem(String updatedBySystem) {
        this.updatedBySystem = updatedBySystem;
    }
    public String getUpdatedByUser() {
        return updatedByUser;
    }
    public void setUpdatedByUser(String updatedByUser) {
        this.updatedByUser = updatedByUser;
    }
    
}
