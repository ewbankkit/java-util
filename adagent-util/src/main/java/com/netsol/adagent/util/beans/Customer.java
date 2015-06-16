/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.beans;;

public class Customer extends BaseData {
    public final static String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:53:53 Customer.java NSI";

    private Long accountId;
    private String businessName;
    private String city;
    private String contactEmail;
    private String contactName;
    private String contactPhone;
    private String countryCode;
    private String crmId;
    private long customerId;
    private Long fulfillmentId;
    private Long personOrgId;
    private String state;
    private String streetAddress1;
    private String streetAddress2;
    private String updatedBySystem;
    private String updatedByUser;
    private String zip;

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCity() {
        return city;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCrmId(String crmId) {
        this.crmId = crmId;
    }

    public String getCrmId() {
        return crmId;
    }

    public void setCustomerId(long customerId) {
        this.customerId = customerId;
    }

    public long getCustomerId() {
        return customerId;
    }

    public void setFulfillmentId(Long fulfillmentId) {
        this.fulfillmentId = fulfillmentId;
    }

    public Long getFulfillmentId() {
        return fulfillmentId;
    }

    public void setPersonOrgId(Long personOrgId) {
        this.personOrgId = personOrgId;
    }

    public Long getPersonOrgId() {
        return personOrgId;
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
}
