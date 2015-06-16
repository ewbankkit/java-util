package com.netsol.adagent.util.beans;

import java.util.List;

import org.apache.commons.lang.StringUtils;

/**
 * Bean containing details of the endpoint phone number, or "target number".
 * Each target number may be associated to one or more tracking numbers, depending on its scope.
 * @author pmitchel
 *
 */
public class CallTargetNumber {
	
	private String prodInstId;
	
	private String targetNumber;
	private String vanityNumber;
	private boolean isLocal;
	private String trackingScope;
	private Long targetNumberId;
	
	CallTrackingNumber[] trackingNumbers;	
	
	@Override
	public boolean equals(Object obj){
		if(this == obj) {
			return true;
		}
		if((obj == null) || (obj.getClass() != this.getClass())) {
			return false;
		}
		CallTargetNumber number = (CallTargetNumber)obj;
		
		
		return StringUtils.equals(this.targetNumber, number.targetNumber) &&
			StringUtils.equals(this.prodInstId, number.prodInstId) &&
			StringUtils.equals(this.trackingScope, number.trackingScope) &&
			this.isLocal == number.isLocal;			
	}
	
	@Override
	public int hashCode(){
		int hash = 7;		
		hash = hash * 31 + (prodInstId==null ? 0 : prodInstId.hashCode());
		hash = hash * 31 + (targetNumber == null ? 0 : targetNumber.hashCode());
		hash = hash * 31 + (trackingScope == null ? 0 : trackingScope.hashCode());
		hash = hash * 31 + (Boolean.valueOf(isLocal).hashCode());
		return hash;
	}
	
	public String getProdInstId() {
		return prodInstId;
	}

	public void setProdInstId(String prodInstId) {
		this.prodInstId = prodInstId;
	}

	public String getTargetNumber() {
		return targetNumber;
	}

	public void setTargetNumber(String targetNumber) {
		this.targetNumber = targetNumber;
	}

	public String getVanityNumber() {
		return vanityNumber;
	}

	public void setVanityNumber(String vanityNumber) {
		this.vanityNumber = vanityNumber;
	}

	public String getTrackingScope() {
		return trackingScope;
	}

	public void setTrackingScope(String trackingScope) {
		this.trackingScope = trackingScope;
	}

	public CallTrackingNumber[] getTrackingNumbers() {
		return trackingNumbers;
	}

	public void setTrackingNumbers(CallTrackingNumber[] trackingNumbers) {
		this.trackingNumbers = trackingNumbers;
	}

	public boolean isLocal() {
		return isLocal;
	}

	public void setLocal(boolean isLocal) {
		this.isLocal = isLocal;
	}

	public void setTargetNumberId(Long targetNumberId) {
		this.targetNumberId = targetNumberId;
	}

	public Long getTargetNumberId() {
		return targetNumberId;
	}

}
