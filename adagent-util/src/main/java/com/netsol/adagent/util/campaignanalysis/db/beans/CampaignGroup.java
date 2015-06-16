package com.netsol.adagent.util.campaignanalysis.db.beans;

import java.util.List;

public class CampaignGroup {
	public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:17 CampaignGroup.java NSI";
	
	private Long campaignGroupId;
	private Long analysisId;
	private String name;
	
	private List<Campaign> campaigns;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<Campaign> getCampaigns() {
		return campaigns;
	}
	public void setCampaigns(List<Campaign> campaigns) {
		this.campaigns = campaigns;
	}
	public Long getCampaignGroupId() {
		return campaignGroupId;
	}
	public void setCampaignGroupId(Long campaignGroupId) {
		this.campaignGroupId = campaignGroupId;
	}
	
	public void setAnalysisId(Long analysisId) {
		this.analysisId = analysisId;
	}
	public Long getAnalysisId() {
		return analysisId;
	}
	
	
}
