package com.netsol.adagent.util.campaignanalysis.db.beans;

public class SeedKeyword {
	public static final String sccsID = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:18 SeedKeyword.java NSI";
	
	private Long analysisId;
	private String seedKeyword;
	
	public Long getAnalysisId() {
		return analysisId;
	}
	public void setAnalysisId(Long analysisId) {
		this.analysisId = analysisId;
	}
	public String getSeedKeyword() {
		return seedKeyword;
	}
	public void setSeedKeyword(String seedKeyword) {
		this.seedKeyword = seedKeyword;
	}
	
}
