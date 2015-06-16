/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.beans;

/**
 * Represents an NS campaign ad location extension.
 */
public class NsCampaignAdLocationExtension extends NsCampaignAdExtension {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:00 NsCampaignAdLocationExtension.java NSI";

    @ColumnName("business_name")
    private String businessName;
    @ColumnName("city")
    private String city;
    @ColumnName("country_code")
    private String countryCode;
    @ColumnName("encoded_location")
    private String encodedLocation;
    @ColumnName("phone_number")
    private String phoneNumber;
    @ColumnName("state")
    private String state;
    @ColumnName("street_addr_1")
    private String streetAddress1;
    @ColumnName("street_addr_2")
    private String streetAddress2;
    @ColumnName("zip")
    private String zip;

    public void setBusinessName(String businessName) {
        setTrackedField("businessName", businessName);
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setCity(String city) {
        setTrackedField("city", city);
    }

    public String getCity() {
        return city;
    }

    public void setCountryCode(String countryCode) {
        setTrackedField("countryCode", countryCode);
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setEncodedLocation(String encodedLocation) {
        setTrackedField("encodedLocation", encodedLocation);
    }

    public String getEncodedLocation() {
        return encodedLocation;
    }

    public void setNsCampaignAdLocationExtensionId(long nsCampaignAdLocationExtensionId) {
        setNsEntityId(nsCampaignAdLocationExtensionId);
    }

    public long getNsCampaignAdLocationExtensionId() {
        return getNsEntityId();
    }

    public void setPhoneNumber(String phoneNumber) {
        setTrackedField("phoneNumber", phoneNumber);
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setState(String state) {
        setTrackedField("state", state);
    }

    public String getState() {
        return state;
    }

    public void setStreetAddress1(String streetAddress1) {
        setTrackedField("streetAddress1", streetAddress1);
    }

    public String getStreetAddress1() {
        return streetAddress1;
    }

    public void setStreetAddress2(String streetAddress2) {
        setTrackedField("streetAddress2", streetAddress2);
    }

    public String getStreetAddress2() {
        return streetAddress2;
    }

    public void setZip(String zip) {
        setTrackedField("zip", zip);
    }

    public String getZip() {
        return zip;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof NsCampaignAdLocationExtension)) {
            return false;
        }
        return equals(this, (NsCampaignAdLocationExtension)o);
    }

    public static void copy(NsCampaignAdLocationExtension src, NsCampaignAdLocationExtension dest) {
        if ((src != null) && (dest != null)) {
            copy((NsCampaignAdExtension)src, (NsCampaignAdExtension)dest);
            dest.setBusinessName(src.getBusinessName());
            dest.setCity(src.getCity());
            dest.setCountryCode(src.getCountryCode());
            dest.setEncodedLocation(src.getEncodedLocation());
            dest.setPhoneNumber(src.getPhoneNumber());
            // Don't overwrite with a null state.
            if (src.getState() != null) {
                dest.setState(src.getState());
            }
            dest.setStreetAddress1(src.getStreetAddress1());
            dest.setStreetAddress2(src.getStreetAddress2());
            dest.setZip(src.getZip());
        }
    }
}
