package com.netsol.adagent.util.siteAnalysis.beans;

public class ContentSection {
    //Content Section Types
    public static enum Type{
        BODY_CONTENT, HEADER, ANCHOR, DESCRIPTION, TITLE;
    }
    
    long contentSectionId;
    long siteId;
    String type;    
    String content;
    
    public long getContentSectionId() {
        return contentSectionId;
    }
    public void setContentSectionId(long contentSectionId) {
        this.contentSectionId = contentSectionId;
    }
  
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    
    public long getSiteId() {
        return siteId;
    }
    public void setSiteId(long siteId) {
        this.siteId = siteId;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((content == null) ? 0 : content.hashCode());
        result = prime * result + (int) (contentSectionId ^ (contentSectionId >>> 32));
        result = prime * result + (int) (siteId ^ (siteId >>> 32));
        result = prime * result + ((type == null) ? 0 : type.hashCode());
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
        ContentSection other = (ContentSection) obj;
        if (content == null) {
            if (other.content != null)
                return false;
        } else if (!content.equals(other.content))
            return false;
        if (contentSectionId != other.contentSectionId)
            return false;
        if (siteId != other.siteId)
            return false;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        return true;
    }
}
