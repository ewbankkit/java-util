package com.netsol.adagent.util.campaignanalysis.db.beans;

public class Keyword {

	public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:17 Keyword.java NSI";
	
	Long keywordId;
	Long adGroupId;	

	String text;
	Double cpcEstimate;
	Double dailyClicksEstimate;
	Double searchVolumeEstimate;
	Long competitionScale;
	Double searchPositionEstimate;
	Double bid;
	public Long getKeywordId() {
		return keywordId;
	}
	public void setKeywordId(Long keywordId) {
		this.keywordId = keywordId;
	}
	
	public Long getAdGroupId() {
		return adGroupId;
	}
	public void setAdGroupId(Long adGroupId) {
		this.adGroupId = adGroupId;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public Double getCpcEstimate() {
		return cpcEstimate;
	}
	public void setCpcEstimate(Double cpcEstimate) {
		this.cpcEstimate = cpcEstimate;
	}
	public Double getDailyClicksEstimate() {
		return dailyClicksEstimate;
	}
	public void setDailyClicksEstimate(Double dailyClicksEstimate) {
		this.dailyClicksEstimate = dailyClicksEstimate;
	}
	public Double getSearchVolumeEstimate() {
		return searchVolumeEstimate;
	}
	public void setSearchVolumeEstimate(Double searchVolumeEstimate) {
		this.searchVolumeEstimate = searchVolumeEstimate;
	}
	public Long getCompetitionScale() {
		return competitionScale;
	}
	public void setCompetitionScale(Long competitionScale) {
		this.competitionScale = competitionScale;
	}
	public Double getSearchPositionEstimate() {
		return searchPositionEstimate;
	}
	public void setSearchPositionEstimate(Double searchPositionEstimate) {
		this.searchPositionEstimate = searchPositionEstimate;
	}
	public Double getBid() {
		return bid;
	}
	public void setBid(Double bid) {
		this.bid = bid;
	}
	
	
	
}
