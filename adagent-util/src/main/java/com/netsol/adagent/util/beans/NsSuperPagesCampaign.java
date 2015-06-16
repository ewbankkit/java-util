/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.beans;

/**
 * Represents an NS SuperPages campaign.
 */
public class NsSuperPagesCampaign extends BaseData {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:02 NsSuperPagesCampaign.java NSI";

    private String city;
    private String description;
    private String destinationUrl;
    private Short displayAddressOptions;
    private boolean displayEmailAddress;
    private boolean displayMap;
    private boolean displayPhoneNumber;
    private String displayUrl;
    private String emailAddress;
    private boolean localCampaign;
    private long nsCampaignId;
    private long nsSuperPagesCampaignId;
    private String phoneNumber;
    private String prodInstId;
    private String state;
    private String streetAddress1;
    private String streetAddress2;
    private String title;
    private String updatedBySystem;
    private String updatedByUser;
    private String zip;

    public void setCity(String city) {
        this.city = city;
    }

    public String getCity() {
        return city;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDestinationUrl(String destinationUrl) {
        this.destinationUrl = destinationUrl;
    }

    public String getDestinationUrl() {
        return destinationUrl;
    }

    public void setDisplayAddressOptions(Short displayAddressOptions) {
        this.displayAddressOptions = displayAddressOptions;
    }

    public Short getDisplayAddressOptions() {
        return displayAddressOptions;
    }

    public void setDisplayEmailAddress(boolean displayEmailAddress) {
        this.displayEmailAddress = displayEmailAddress;
    }

    public boolean isDisplayEmailAddress() {
        return displayEmailAddress;
    }

    public void setDisplayMap(boolean displayMap) {
        this.displayMap = displayMap;
    }

    public boolean isDisplayMap() {
        return displayMap;
    }

    public void setDisplayPhoneNumber(boolean displayPhoneNumber) {
        this.displayPhoneNumber = displayPhoneNumber;
    }

    public boolean isDisplayPhoneNumber() {
        return displayPhoneNumber;
    }

    public void setDisplayUrl(String displayUrl) {
        this.displayUrl = displayUrl;
    }

    public String getDisplayUrl() {
        return displayUrl;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setLocalCampaign(boolean localCampaign) {
        this.localCampaign = localCampaign;
    }

    public boolean isLocalCampaign() {
        return localCampaign;
    }

    public void setNsCampaignId(long nsCampaignId) {
        this.nsCampaignId = nsCampaignId;
    }

    public long getNsCampaignId() {
        return nsCampaignId;
    }

    public void setNsSuperPagesCampaignId(long nsSuperPagesCampaignId) {
        this.nsSuperPagesCampaignId = nsSuperPagesCampaignId;
    }

    public long getNsSuperPagesCampaignId() {
        return nsSuperPagesCampaignId;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setProdInstId(String prodInstId) {
        this.prodInstId = prodInstId;
    }

    public String getProdInstId() {
        return prodInstId;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getState() {
        return state;
    }

    public void setStreetAddress1(String streetAddress1) {
        this.streetAddress1 = streetAddress1;
    }

    public String getStreetAddress1() {
        return streetAddress1;
    }

    public void setStreetAddress2(String streetAddress2) {
        this.streetAddress2 = streetAddress2;
    }

    public String getStreetAddress2() {
        return streetAddress2;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setUpdatedBySystem(String updatedBySystem) {
        this.updatedBySystem = updatedBySystem;
    }

    public String getUpdatedBySystem() {
        return updatedBySystem;
    }

    public void setUpdatedByUser(String updatedByUser) {
        this.updatedByUser = updatedByUser;
    }

    public String getUpdatedByUser() {
        return updatedByUser;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getZip() {
        return zip;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof NsSuperPagesCampaign)) {
            return false;
        }
        NsSuperPagesCampaign that = (NsSuperPagesCampaign)o;
        return stringsEqual(prodInstId, that.prodInstId) && (nsSuperPagesCampaignId == that.nsSuperPagesCampaignId);
    }
}
