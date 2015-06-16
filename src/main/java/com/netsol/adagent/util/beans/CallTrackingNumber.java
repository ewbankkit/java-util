package com.netsol.adagent.util.beans;

/**
 * Bean for a single call tracking number. 
 * @see CallTargetNumber
 */
public class CallTrackingNumber {
	static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:53:51 CallTrackingNumber.java NSI";
	
	private String trackingNumber;
	private Long nsAdGroupId;
	private Long nsCampaignId;
	private Integer vendorId;
	private Long targetNumberId;
	
	
	@Override public String toString(){
		return trackingNumber+", "+vendorId+", "+nsCampaignId +", "+nsAdGroupId;
	}
	
	public Long getNsAdGroupId() {
		return nsAdGroupId;
	}
	public void setNsAdGroupId(Long nsAdGroupId) {
		this.nsAdGroupId = nsAdGroupId;
	}
	public Long getNsCampaignId() {
		return nsCampaignId;
	}
	public void setNsCampaignId(Long nsCampaignId) {
		this.nsCampaignId = nsCampaignId;
	}
	public Integer getVendorId() {
		return vendorId;
	}
	public void setVendorId(Integer vendorId) {
		this.vendorId = vendorId;
	}
	public String getTrackingNumber() {
		return trackingNumber;
	}
	public void setTrackingNumber(String trackingNumber) {
		this.trackingNumber = trackingNumber;
	}

	public void setTargetNumberId(Long targetNumberId) {
		this.targetNumberId = targetNumberId;
	}

	public Long getTargetNumberId() {
		return targetNumberId;
	}
	
}
