package com.netsol.adagent.util.siteAnalysis.beans;

public class Competitor implements Comparable<Competitor> {
    private long competitorId;
    private long siteId;
    private String url;
    private long score;
    
    public Competitor() {}
    
    public Competitor(long siteId, String url, long score) {
    	this.siteId = siteId;
    	this.url = url;
    	this.score = score;
    }
    
    public long getCompetitorId() {
        return competitorId;
    }
    public void setCompetitorId(long competitorId) {
        this.competitorId = competitorId;
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
    public long getScore() {
        return score;
    }
    public void setScore(long score) {
        this.score = score;
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (competitorId ^ (competitorId >>> 32));
        result = prime * result + (int) (score ^ (score >>> 32));
        result = prime * result + (int) (siteId ^ (siteId >>> 32));
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
        Competitor other = (Competitor) obj;
        if (competitorId != other.competitorId)
            return false;
        if (score != other.score)
            return false;
        if (siteId != other.siteId)
            return false;
        if (url == null) {
            if (other.url != null)
                return false;
        } else if (!url.equals(other.url))
            return false;
        return true;
    }
    
    public int compareTo(Competitor c) {
    	return Long.valueOf(c.getScore()).compareTo(Long.valueOf(score));
    }
    
    @Override
    public String toString() {
    	return "competitorId= " + competitorId + ", siteId=" + siteId + ", url=" + url + ", score=" + score;
    }
}
