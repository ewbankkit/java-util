package com.netsol.adagent.util.beans;

import java.util.Collection;


public class SeoKeywordGroup{
    public static final String sccsID = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:07 SeoKeywordGroup.java NSI";
    
    public static enum Status{ ACTIVE, PAUSED, DELETED} 
    
    private String prodInstId;
    private long seoKeywordGroupId;
    private String name;
    private Status status;
    private String updatedBySystem;
    private String updatedByUser;
    private Collection<SeoKeyword> keywords;
    
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
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Status getStatus() {
        return status;
    }
    public void setStatus(Status status) {
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
    public void setKeywords(Collection<SeoKeyword> keywords) {
        this.keywords = keywords;
    }
    public Collection<SeoKeyword> getKeywords() {
        return keywords;
    }
   
    
}
