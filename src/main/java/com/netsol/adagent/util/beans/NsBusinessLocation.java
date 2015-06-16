/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.beans;

/**
 * Represents an NS business location.
 */
@ColumnNameOverride({"vendorEntityId/vendor_business_location_id"})
public class NsBusinessLocation extends NsEntity {
    @ColumnName("city")
    private String city;
    @ColumnName("country_code")
    private String countryCode;
    @ColumnName("description")
    private String description;
    @ColumnName("geo_code_status")
    private String geoCodeStatus;
    @ColumnName("state")
    private String state;
    @ColumnName("street_addr_1")
    private String streetAddress1;
    @ColumnName("street_addr_2")
    private String streetAddress2;
    @ColumnName("zip")
    private String zip;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        setTrackedField("city", city);
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        setTrackedField("countryCode", countryCode);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        setTrackedField("description", description);
    }

    public String getGeoCodeStatus() {
        return geoCodeStatus;
    }

    public void setGeoCodeStatus(String geoCodeStatus) {
        setTrackedField("geoCodeStatus", geoCodeStatus);
    }

    public long getNsBusinessLocationId() {
        return getNsEntityId();
    }

    public void setNsBusinessLocationId(long nsBusinessLocationId) {
        setNsEntityId(nsBusinessLocationId);
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        setTrackedField("state", state);
    }

    public String getStreetAddress1() {
        return streetAddress1;
    }

    public void setStreetAddress1(String streetAddress1) {
        setTrackedField("streetAddress1", streetAddress1);
    }

    public String getStreetAddress2() {
        return streetAddress2;
    }

    public void setStreetAddress2(String streetAddress2) {
        setTrackedField("streetAddress2", streetAddress2);
    }

    public Long getVendorBusinessLocationId() {
        return getVendorEntityId();
    }

    public void setVendorBusinessLocationId(Long vendorBusinessLocationId) {
        setVendorEntityId(vendorBusinessLocationId);
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        setTrackedField("zip", zip);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof NsBusinessLocation)) {
            return false;
        }
        return equals(this, (NsBusinessLocation)o);
    }

    public static void copy(NsBusinessLocation src, NsBusinessLocation dest) {
        if ((src != null) && (dest != null)) {
            copy((NsEntity)src, (NsEntity)dest);
            dest.setCity(src.getCity());
            dest.setCountryCode(src.getCountryCode());
            dest.setDescription(src.getDescription());
            dest.setGeoCodeStatus(src.getGeoCodeStatus());
            dest.setState(src.getState());
            dest.setStreetAddress1(src.getStreetAddress1());
            dest.setStreetAddress2(src.getStreetAddress2());
            dest.setZip(src.getZip());
        }
    }
}
