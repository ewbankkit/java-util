package com.netsol.adagent.util.beans;

/**
 * Target vendor bean.
 * 
 * @author Adam S. Vernon
 */
public class TargetVendor extends BaseDataWithUpdateTracking {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:08 TargetVendor.java NSI";

    private String prodInstId;
    private long targetId;
    private long vendorId;
    @ColumnName("budget")
    private double budget;
    @ColumnName("spend_aggressiveness")
    private Float spendAggressiveness;
    @ColumnName("external_id")
    private String externalId;
    @ColumnName("updated_by_system")
    private String updatedBySystem;
    @ColumnName("updated_by_user")
    private String updatedByUser;
    
	/**
	 * @return the prodInstId
	 */
	public String getProdInstId() {
		return prodInstId;
	}
	
	/**
	 * @param prodInstId the prodInstId to set
	 */
	public void setProdInstId(String prodInstId) {
		this.prodInstId = prodInstId;
	}
	
	/**
	 * @return the targetId
	 */
	public long getTargetId() {
		return targetId;
	}
	
	/**
	 * @param targetId the targetId to set
	 */
	public void setTargetId(long targetId) {
		this.targetId = targetId;
	}
	
	/**
	 * @return the vendorId
	 */
	public long getVendorId() {
		return vendorId;
	}
	
	/**
	 * @param vendorId the vendorId to set
	 */
	public void setVendorId(long vendorId) {
		this.vendorId = vendorId;
	}
	
	/**
	 * @return the budget
	 */
	public double getBudget() {
		return budget;
	}
	
	/**
	 * @param budget the budget to set
	 */
	public void setBudget(double budget) {
		setTrackedField("budget", budget);
	}
	
	/**
	 * @return the spendAggressiveness
	 */
	public Float getSpendAggressiveness() {
		return spendAggressiveness;
	}
	
	/**
	 * @param spendAggressiveness the spendAggressiveness to set
	 */
	public void setSpendAggressiveness(Float spendAggressiveness) {
		setTrackedField("spendAggressiveness", spendAggressiveness);
	}
	
	/**
	 * @return the externalId
	 */
	public String getExternalId() {
		return externalId;
	}
	
	/**
	 * @param externalId the externalId to set
	 */
	public void setExternalId(String externalId) {
		setTrackedField("externalId", externalId);
	}
	
	/**
	 * @return the updatedBySystem
	 */
	public String getUpdatedBySystem() {
		return updatedBySystem;
	}
	
	/**
	 * @param updatedBySystem the updatedBySystem to set
	 */
	public void setUpdatedBySystem(String updatedBySystem) {
		setTrackedField("updatedBySystem", updatedBySystem);
	}
	
	/**
	 * @return the updatedByUser
	 */
	public String getUpdatedByUser() {
		return updatedByUser;
	}
	
	/**
	 * @param updatedByUser the updatedByUser to set
	 */
	public void setUpdatedByUser(String updatedByUser) {
		setTrackedField("updatedByUser", updatedByUser);
	}   
}
