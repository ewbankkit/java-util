package com.netsol.adagent.util.campaignanalysis.db.beans;

public class ProximityTarget {
	public static final String sccsID = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:18 ProximityTarget.java NSI";
	
	private Long analysisId;
	private String zipCode;
	private long radius;
	
	public Long getAnalysisId() {
		return analysisId;
	}
	public void setAnalysisId(Long analysisId) {
		this.analysisId = analysisId;
	}
	public String getZipCode() {
		return zipCode;
	}
	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}
	public long getRadius() {
		return radius;
	}
	public void setRadius(long radius) {
		this.radius = radius;
	}
	
}
