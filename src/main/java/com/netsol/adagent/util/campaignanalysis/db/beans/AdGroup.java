package com.netsol.adagent.util.campaignanalysis.db.beans;

import java.util.List;

public class AdGroup {

	public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:16 AdGroup.java NSI";
	
	Long adGroupId;
	Long campaignId;
	
	String name;
	
	List<Ad> ads;
	List<Keyword> keywords;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<Ad> getAds() {
		return ads;
	}
	public void setAds(List<Ad> ads) {
		this.ads = ads;
	}
	public List<Keyword> getKeywords() {
		return keywords;
	}
	public void setKeywords(List<Keyword> keywords) {
		this.keywords = keywords;
	}
	public Long getAdGroupId() {
		return adGroupId;
	}
	public void setAdGroupId(Long adGroupId) {
		this.adGroupId = adGroupId;
	}
	
	public Long getCampaignId() {
		return campaignId;
	}
	public void setCampaignId(Long campaignId) {
		this.campaignId = campaignId;
	}

	
}
