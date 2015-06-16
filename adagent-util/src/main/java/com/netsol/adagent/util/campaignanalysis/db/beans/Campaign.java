package com.netsol.adagent.util.campaignanalysis.db.beans;

import java.util.List;

import com.netsol.adagent.util.campaignanalysis.type.CampaignType;

public class Campaign {
	public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:16 Campaign.java NSI";
	
	
	
	private Long campaignId;
	private Long campaignGroupId;
	
		
	private String name;
	private CampaignType type;
	
	List<AdGroup> adGroups;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public CampaignType getType() {
		return type;
	}

	public void setType(CampaignType type) {
		this.type = type;
	}

	public List<AdGroup> getAdGroups() {
		return adGroups;
	}

	public void setAdGroups(List<AdGroup> adGroups) {
		this.adGroups = adGroups;
	}

	public Long getCampaignId() {
		return campaignId;
	}

	public void setCampaignId(Long campaignId) {
		this.campaignId = campaignId;
	}



	public Long getCampaignGroupId() {
		return campaignGroupId;
	}

	public void setCampaignGroupId(Long campaignGroupId) {
		this.campaignGroupId = campaignGroupId;
	}
	
}
