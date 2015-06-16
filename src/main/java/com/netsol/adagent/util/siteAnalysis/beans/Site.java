package com.netsol.adagent.util.siteAnalysis.beans;

import java.util.List;

public class Site {
    long siteId;
    String url;
    String businessName;
    String city;
    String state;    
    List<Competitor> competitors;
    List<ContentSection> contentSections;
    List<Keyword> keywords;
    
    /**
     *  Build longtailed version of each keyword by appending the business city and state. Only append if the keyword does not already contain city/state
     */
    public void fixupKeywordLongtails(){
       if(keywords != null){
           Keyword.fixupKeywordLongtails(keywords, city, state);
        }
    }
    
    
    public long getSiteId() {
        return siteId;
    }
    public void setSiteId(long siteId) {
        this.siteId = siteId;
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public String getBusinessName() {
        return businessName;
    }
    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }
    public String getCity() {
        return city;
    }
    public void setCity(String city) {
        this.city = city;
    }
    public String getState() {
        return state;
    }
    public void setState(String state) {
        this.state = state;
    }
    public List<Competitor> getCompetitors() {
        return competitors;
    }
    public void setCompetitors(List<Competitor> competitors) {
        this.competitors = competitors;
    }
    public List<ContentSection> getContentSections() {
        return contentSections;
    }
    public void setContentSections(List<ContentSection> contentSections) {
        this.contentSections = contentSections;
    }
    public List<Keyword> getKeywords() {
        return keywords;
    }
    public void setKeywords(List<Keyword> keywords) {
        this.keywords = keywords;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((businessName == null) ? 0 : businessName.hashCode());
        result = prime * result + ((city == null) ? 0 : city.hashCode());
        result = prime * result + ((competitors == null) ? 0 : competitors.hashCode());
        result = prime * result + ((contentSections == null) ? 0 : contentSections.hashCode());
        result = prime * result + ((keywords == null) ? 0 : keywords.hashCode());
        result = prime * result + (int) (siteId ^ (siteId >>> 32));
        result = prime * result + ((state == null) ? 0 : state.hashCode());
        result = prime * result + ((url == null) ? 0 : url.hashCode());
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
        Site other = (Site) obj;
        if (businessName == null) {
            if (other.businessName != null)
                return false;
        } else if (!businessName.equals(other.businessName))
            return false;
        if (city == null) {
            if (other.city != null)
                return false;
        } else if (!city.equals(other.city))
            return false;
        if (competitors == null) {
            if (other.competitors != null)
                return false;
        } else if (!competitors.equals(other.competitors))
            return false;
        if (contentSections == null) {
            if (other.contentSections != null)
                return false;
        } else if (!contentSections.equals(other.contentSections))
            return false;
        if (keywords == null) {
            if (other.keywords != null)
                return false;
        } else if (!keywords.equals(other.keywords))
            return false;
        if (siteId != other.siteId)
            return false;
        if (state == null) {
            if (other.state != null)
                return false;
        } else if (!state.equals(other.state))
            return false;
        if (url == null) {
            if (other.url != null)
                return false;
        } else if (!url.equals(other.url))
            return false;
        return true;
    }

    
    
    
}
