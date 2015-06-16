package com.netsol.adagent.util.siteAnalysis.beans;

public class KeywordPpcData {
    private long keywordPpcDataId;
    private long keywordId;
    private double bid;
    private double cpc;
    private double targetPosition;
    private double actualPosition;
    
    public long getKeywordPpcDataId() {
        return keywordPpcDataId;
    }
    public void setKeywordPpcDataId(long keywordPpcDataId) {
        this.keywordPpcDataId = keywordPpcDataId;
    }
    public long getKeywordId() {
        return keywordId;
    }
    public void setKeywordId(long keywordId) {
        this.keywordId = keywordId;
    }
    public double getBid() {
        return bid;
    }
    public void setBid(double bid) {
        this.bid = bid;
    }
    public double getCpc() {
        return cpc;
    }
    public void setCpc(double cpc) {
        this.cpc = cpc;
    }
    public double getTargetPosition() {
        return targetPosition;
    }
    public void setTargetPosition(double targetPosition) {
        this.targetPosition = targetPosition;
    }
    public double getActualPosition() {
        return actualPosition;
    }
    public void setActualPosition(double actualPosition) {
        this.actualPosition = actualPosition;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(actualPosition);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(bid);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(cpc);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + (int) (keywordId ^ (keywordId >>> 32));
        result = prime * result + (int) (keywordPpcDataId ^ (keywordPpcDataId >>> 32));
        temp = Double.doubleToLongBits(targetPosition);
        result = prime * result + (int) (temp ^ (temp >>> 32));
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
        KeywordPpcData other = (KeywordPpcData) obj;
        if (Double.doubleToLongBits(actualPosition) != Double.doubleToLongBits(other.actualPosition))
            return false;
        if (Double.doubleToLongBits(bid) != Double.doubleToLongBits(other.bid))
            return false;
        if (Double.doubleToLongBits(cpc) != Double.doubleToLongBits(other.cpc))
            return false;
        if (keywordId != other.keywordId)
            return false;
        if (keywordPpcDataId != other.keywordPpcDataId)
            return false;
        if (Double.doubleToLongBits(targetPosition) != Double.doubleToLongBits(other.targetPosition))
            return false;
        return true;
    }
}
