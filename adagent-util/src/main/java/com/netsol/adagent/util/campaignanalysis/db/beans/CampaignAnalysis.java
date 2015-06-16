package com.netsol.adagent.util.campaignanalysis.db.beans;

import java.util.Date;
import java.util.List;

public class CampaignAnalysis {
	public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:16 CampaignAnalysis.java NSI";
	
	public static enum Status{INITIAL, SUBMITTED, IN_PROGRESS, COMPLETE,ABORTED };
	
	
	private Long analysisId;
	private String prodInstId;
	private Status status;
	private Date completionDate;
	private boolean isLocal;
	private boolean sellsOnline;
	private double avgTicket;
	List<LocationTarget> locationTargets; 
	List<ProximityTarget> proximityTargets;
	List<CampaignGroup> campaignGroups;
	List<SeedKeyword> seedKeywords;
	
	
	public List<SeedKeyword> getSeedKeywords() {
		return seedKeywords;
	}

	public void setSeedKeywords(List<SeedKeyword> seedKeywords) {
		this.seedKeywords = seedKeywords;
	}

	//helper to get Primary Location
	public LocationTarget getPrimaryLocation(){
		LocationTarget primary = null;
		if(locationTargets != null){
			for(LocationTarget t: locationTargets){
				if(t.isPrimaryLocation()){
					primary = t;
					break;
				}
			}
		}
		return primary;
	}
	
	public Long getAnalysisId() {
		return analysisId;
	}
	public void setAnalysisId(Long analysisId) {
		this.analysisId = analysisId;
	}
	public String getProdInstId() {
		return prodInstId;
	}
	public void setProdInstId(String prodInstId) {
		this.prodInstId = prodInstId;
	}
	public Status getStatus() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
	}
	public Date getCompletionDate() {
		return completionDate;
	}
	public void setCompletionDate(Date completionDate) {
		this.completionDate = completionDate;
	}
	public List<CampaignGroup> getCampaignGroups() {
		return campaignGroups;
	}
	public void setCampaignGroups(List<CampaignGroup> campaignGroups) {
		this.campaignGroups = campaignGroups;
	}
	
	@Override
	public String toString(){
		String out = 
			"\nCampaign Analysis:  "+prodInstId +
			"\n  Status: "+status +
			"\n  AnalysisId: "+analysisId +
			"\n  completionDate: "+completionDate +
			"\n  Num groups:" + campaignGroups.size();
		return out;	
		/*for(CampaignGroup group: campaignGroups){
			out += 
				"\n    Group Name: " + group.getName() +
				"\n    campaignGroupId: " + group.getCampaignGroupId() +
				"\n    Campaigns: ";
			for(Campaign camp: group.getCampaigns()){
				out += 
					"\n       "+camp.getCampaignId()+": " + camp.getName() +
					"\n       Type: " + camp.getType() +
					"\n       Location: " + camp.getZip() + ", " +camp.getRadius() +"mi" +
					"\n       AdGroups: ";
				for(AdGroup adgroup: camp.getAdGroups()){
					out += 
						"\n         "+adgroup.getAdGroupId()+": " + adgroup.getName() +
					
						"\n           Ads: "+ adgroup.getAds().size();
					for(Ad ad: adgroup.getAds()){
						out += 
							"\n        Headline: " + ad.getHeadline() +
							//"\n        Desc: " + ad.getDescription() +
							//"\n        Destination: " + ad.getDestinationUrl() +
							"\n        DisplayURL: " + ad.getDisplayUrl();
					}
					out += "\n           Keywords: " + adgroup.getKeywords().size();
					for(Keyword kw: adgroup.getKeywords()){
						out += "\n        "+kw.getText()+
							", cpc est: "+kw.getCpcEstimate()+
							//", dailyClicksEstimate: "+ kw.getDailyClicksEstimate() +
							//", searchVolumeEstimate: "+ kw.getSearchVolumeEstimate() + 
							//", competitionScale: "+ kw.getCompetitionScale() + 
							//", searchPositionEstimate: "+ kw.getSearchPositionEstimate() + 
							", bid: "+ kw.getBid();
					}
						
				}
					
			}
			
		}*/
		
	}
	public List<LocationTarget> getLocationTargets() {
		return locationTargets;
	}
	public void setLocationTargets(List<LocationTarget> locationTargets) {
		this.locationTargets = locationTargets;
	}
	public List<ProximityTarget> getProximityTargets() {
		return proximityTargets;
	}
	public void setProximityTargets(List<ProximityTarget> proximityTargets) {
		this.proximityTargets = proximityTargets;
	}

	public boolean isLocal() {
		return isLocal;
	}

	public void setLocal(boolean isLocal) {
		this.isLocal = isLocal;
	}

	public boolean isSellsOnline() {
		return sellsOnline;
	}

	public void setSellsOnline(boolean sellsOnline) {
		this.sellsOnline = sellsOnline;
	}

	public double getAvgTicket() {
		return avgTicket;
	}

	public void setAvgTicket(double avgTicket) {
		this.avgTicket = avgTicket;
	}
	
	
}
