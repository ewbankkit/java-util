package com.netsol.adagent.util.campaignanalysis.db.beans;

public class LocationTarget {
	public static final String sccsID = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:17 LocationTarget.java NSI";
	
	private Long analysisId;
	private String city;
	private String state;
	private boolean primaryLocation;
	
	public Long getAnalysisId() {
		return analysisId;
	}
	public void setAnalysisId(Long analysisId) {
		this.analysisId = analysisId;
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
	public boolean isPrimaryLocation() {
		return primaryLocation;
	}
	public void setPrimaryLocation(boolean primaryLocation) {
		this.primaryLocation = primaryLocation;
	}
}
