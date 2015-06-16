package com.netsol.adagent.util.campaignanalysis.db.beans;


public class CustomerProductInfo {
	
	private String prodInstId;
	private String seedKeywords;
	private String location;
	private double avgTicket;	
	private String city;
	private String state;
	private int zipCode;
	private int radius;
	private double monthlyBudget;
	private double convRate;
	private String url;
	
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getProdInstId() {
		return prodInstId;
	}
	public void setProdInstId(String prodInstId) {
		this.prodInstId = prodInstId;
	}
	public String getSeedKeywords() {
		return seedKeywords;
	}
	public void setSeedKeywords(String seedKeywords) {
		this.seedKeywords = seedKeywords;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public double getAvgTicket() {
		return avgTicket;
	}
	public void setAvgTicket(double avgTicket) {
		this.avgTicket = avgTicket;
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
	public int getZipCode() {
		return zipCode;
	}
	public void setZipCode(int zipCode) {
		this.zipCode = zipCode;
	}
	public int getRadius() {
		return radius;
	}
	public void setRadius(int radius) {
		this.radius = radius;
	}
	public double getMonthlyBudget() {
		return monthlyBudget;
	}
	public void setMonthlyBudget(double monthlyBudget) {
		this.monthlyBudget = monthlyBudget;
	}
	public double getConvRate() {
		return convRate;
	}
	public void setConvRate(double convRate) {
		this.convRate = convRate;
	}
	
	
}
