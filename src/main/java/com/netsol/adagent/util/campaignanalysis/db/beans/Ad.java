package com.netsol.adagent.util.campaignanalysis.db.beans;

public class Ad {
	public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:16 Ad.java NSI";
	
	Long adId;

	Long adGroupId;	

	String headline;
	String description;
	String destinationUrl;
	String displayUrl;
	
	public String getHeadline() {
		return headline;
	}
	public void setHeadline(String headline) {
		this.headline = headline;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getDestinationUrl() {
		return destinationUrl;
	}
	public void setDestinationUrl(String destinationUrl) {
		this.destinationUrl = destinationUrl;
	}
	public String getDisplayUrl() {
		return displayUrl;
	}
	public void setDisplayUrl(String displayUrl) {
		this.displayUrl = displayUrl;
	}
	public Long getAdId() {
		return adId;
	}
	public void setAdId(Long adId) {
		this.adId = adId;
	}
	public Long getAdGroupId() {
		return adGroupId;
	}
	public void setAdGroupId(Long adGroupId) {
		this.adGroupId = adGroupId;
	}
	
	
	
	
}
