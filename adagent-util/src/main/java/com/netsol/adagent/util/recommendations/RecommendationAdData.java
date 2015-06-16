package com.netsol.adagent.util.recommendations;

import java.io.Serializable;

/**
 * Add data for recommendations.
 * 
 * @author Adam S. Vernon
 */
@SuppressWarnings("serial")
public class RecommendationAdData implements Serializable {
	public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:23 RecommendationAdData.java NSI";
	
	/** The ad headline. */
	private String headline;
	/** The ad description line 1. */
	private String desc1;
	/** The ad description line 2. */
	private String desc2;
	/** The ad display URL. */
	private String displayUrl;
	/** The ad destination URL. */
	private String destinationUrl;
	
	/** Default constructor for GSON. */
	public RecommendationAdData() {}
	
	/**
	 * Constructor.
	 * 
	 * @param headline
	 * @param desc1
	 * @param desc2
	 * @param displayUrl
	 * @param destinationUrl
	 */
	public RecommendationAdData(String headline, String desc1, String desc2, String displayUrl, String destinationUrl) {
		this.headline = headline;
		this.desc1 = desc1;
		this.desc2 = desc2;
		this.displayUrl = displayUrl;
		this.destinationUrl = destinationUrl;
	}
	
	/**
	 * @return the headline
	 */
	public String getHeadline() {
		return headline;
	}
	
	/**
	 * @param headline the headline to set
	 */
	public void setHeadline(String headline) {
		this.headline = headline;
	}
	
	/**
	 * @return the desc1
	 */
	public String getDesc1() {
		return desc1;
	}
	
	/**
	 * @param desc1 the desc1 to set
	 */
	public void setDesc1(String desc1) {
		this.desc1 = desc1;
	}
	
	/**
	 * @return the desc2
	 */
	public String getDesc2() {
		return desc2;
	}
	
	/**
	 * @param desc2 the desc2 to set
	 */
	public void setDesc2(String desc2) {
		this.desc2 = desc2;
	}
	
	/**
	 * @return the displayUrl
	 */
	public String getDisplayUrl() {
		return displayUrl;
	}
	
	/**
	 * @param displayUrl the displayUrl to set
	 */
	public void setDisplayUrl(String displayUrl) {
		this.displayUrl = displayUrl;
	}
	
	/**
	 * @return the destinationUrl
	 */
	public String getDestinationUrl() {
		return destinationUrl;
	}
	
	/**
	 * @param destinationUrl the destinationUrl to set
	 */
	public void setDestinationUrl(String destinationUrl) {
		this.destinationUrl = destinationUrl;
	}
	
	/**
	 * Override Object.toString().
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("headline=" + headline + ", ");
		sb.append("desc1=" + desc1 + ", ");
		sb.append("desc2=" + desc2 + ", ");
		sb.append("displayUrl=" + displayUrl + ", ");
		sb.append("destinationUrl=" + destinationUrl);		
		return sb.toString();
	}
}
