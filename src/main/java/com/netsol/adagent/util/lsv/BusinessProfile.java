/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.lsv;

import java.util.Date;
import java.util.regex.Pattern;

import com.netsol.adagent.util.beans.BaseData;
import com.netsol.adagent.util.beans.SearchPhrase;

/**
 * Business profile information. 
 */
public class BusinessProfile extends BaseData {
    public final static String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:10 BusinessProfile.java NSI";
    
    private static final Pattern NON_DIGITS_PATTERN = Pattern.compile("\\D");
    private static final String US_COUNTRY_CODE = "US";

    private String additionalInfo;
    private String alternatePhone;
    private String brands;
    private String businessDescription;
    private String businessHeadline;
    private String businessName;
    private int businessProfileId;
    private int categoryId;
    private String city;
    private String countryCode;
    private String discounts;
    private String email;
    private String fax;
    private String firstName;
    private String hoursOfOperation;
    private String keywords;
    private String languagesSpoken;
    private String lastName;
    private Date lastPublishDate;
    private String logoImage;
    private String paymentMethods;
    private String phone;
    private String prodInstId;
    private boolean publish;
    private String serviceArea;
    private String siteUrl;
    private String specialities;
    private String state;
    private String streetAddress1;
    private String streetAddress2;
    private String timeZone;
    private String yearEstablished;
    private String zip; 
    
    /**
     * Constructor.
     */
    public BusinessProfile() {
        super();
        
        return;
    }
    
    public String getAdditionalInfo() {
        return this.additionalInfo;
    }

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    public String getAlternatePhone() {
        return this.alternatePhone;
    }

    public void setAlternatePhone(String alternatePhone) {
        this.alternatePhone = alternatePhone;
    }

    public String getAlternatePhoneRaw() {
        return BusinessProfile.getRawPhoneNumber(this.alternatePhone, this.isUSAddress());
    }

    public String getBrands() {
        return this.brands;
    }

    public void setBrands(String brands) {
        this.brands = brands;
    }

    public String getBusinessDescription() {
        return this.businessDescription;
    }

    public void setBusinessDescription(String businessDescription) {
        this.businessDescription = businessDescription;
    }
    
    public String getBusinessHeadline() {
        return this.businessHeadline;
    }

    public void setBusinessHeadline(String businessHeadline) {
        this.businessHeadline = businessHeadline;
    }
    
    public String getBusinessName() {
        return this.businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }
    
    public int getBusinessProfileId() {
        return this.businessProfileId;
    }

    public void setBusinessProfileId(int businessProfileId) {
        this.businessProfileId = businessProfileId;
    }

    public int getCategoryId() {
        return this.categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getCity() {
        return this.city;
    }

    public void setCity(String city) {
        this.city = city;
    }
    
    public String getCountryCode() {
        return this.countryCode;
    }

    public void setCountryCode(String countryCode) {
        // Default country code is US.
        this.countryCode =
            BaseData.stringIsBlank(countryCode) ? BusinessProfile.US_COUNTRY_CODE : countryCode;
    }
    
    public String getDiscounts() {
        return this.discounts;
    }

    public void setDiscounts(String discounts) {
        this.discounts = discounts;
    }
    
    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getFax() {
        return this.fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getFaxRaw() {
        return BusinessProfile.getRawPhoneNumber(this.fax, this.isUSAddress());
    }

    public String getFirstName() {
        return this.firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getHoursOfOperation() {
        return this.hoursOfOperation;
    }

    public void setHoursOfOperation(String hoursOfOperation) {
        this.hoursOfOperation = hoursOfOperation;
    }

    public String getKeywords() {
        return this.keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getKeywordsReformatted() {
        StringBuilder sb = new StringBuilder();
        for (String keyword : SearchPhrase.getSearchKeywords(this.keywords)) {
            sb.append(keyword).append(',');
        }
        int length = sb.length();
        if (length > 0) {
            // Remove trailing comma.
            sb.setLength(length - 1);
        }
        return sb.toString();
    }
    
    public String getLanguagesSpoken() {
        return this.languagesSpoken;
    }

    public void setLanguagesSpoken(String languagesSpoken) {
        this.languagesSpoken = languagesSpoken;
    }
    
    public String getLastName() {
        return this.lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public Date getLastPublishDate() {
        return this.lastPublishDate;
    }

    public void setLastPublishDate(Date lastPublishDate) {
        this.lastPublishDate = lastPublishDate;
    }
    
    public String getLogoImage() {
        return this.logoImage;
    }

    public void setLogoImage(String logoImage) {
        this.logoImage = logoImage;
    }
    
    public String getPaymentMethods() {
        return this.paymentMethods;
    }

    public void setPaymentMethods(String paymentMethods) {
        this.paymentMethods = paymentMethods;
    }

    public String getPhone() {
        return this.phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhoneRaw() {
        return BusinessProfile.getRawPhoneNumber(this.phone, this.isUSAddress());
    }
    
    public String getProdInstId() {
        return this.prodInstId;
    }

    public void setProdInstId(String prodInstId) {
        this.prodInstId = prodInstId;
    }
    
    public boolean isPublish() {
        return this.publish;
    }

    public void setPublish(boolean publish) {
        this.publish = publish;
    }

    public String getServiceArea() {
        return this.serviceArea;
    }

    public void setServiceArea(String serviceArea) {
        this.serviceArea = serviceArea;
    }

    public String getSiteUrl() {
        return this.siteUrl;
    }

    public void setSiteUrl(String siteUrl) {
        this.siteUrl = siteUrl;
    }
    
    public String getSpecialities() {
        return this.specialities;
    }

    public void setSpecialities(String specialities) {
        this.specialities = specialities;
    }

    public String getState() {
        return this.state;
    }

    public void setState(String state) {
        this.state = state;
    }
    
    public String getStreetAddress1() {
        return this.streetAddress1;
    }

    public void setStreetAddress1(String streetAddress1) {
        this.streetAddress1 = streetAddress1;
    }

    public String getStreetAddress2() {
        return this.streetAddress2;
    }

    public void setStreetAddress2(String streetAddress2) {
        this.streetAddress2 = streetAddress2;
    }
    
    public String getTimeZone() {
        return this.timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }
    
    public boolean isUSAddress() {
        return BaseData.stringsEqualIgnoreCase(BusinessProfile.US_COUNTRY_CODE, this.countryCode);
    }
    
    public String getYearEstablished() {
        return this.yearEstablished;
    }

    public void setYearEstablished(String yearEstablished) {
        this.yearEstablished = yearEstablished;
    }

    public String getZip() {
        return this.zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getZip4Raw() {
        if (BaseData.stringIsNotBlank(this.zip)) {
            String rawZip = BusinessProfile.removeNonDigitCharacters(this.zip);
            if (rawZip.length() >= 9) {
                return rawZip.substring(5, 9);
            }
            return null;
        }
        return this.zip;
    }
    
    public String getZip5Raw() {
        if (BaseData.stringIsNotBlank(this.zip)) {
            String rawZip = BusinessProfile.removeNonDigitCharacters(this.zip);
            if (rawZip.length() > 5) {
                return rawZip.substring(0, 5);
            }
            return rawZip;
        }
        return this.zip;
    }
    
    /**
     * Return a 'raw' phone number. 
     */
    private static String getRawPhoneNumber(String s, boolean isUSAddress) {
        if (BaseData.stringIsNotBlank(s)) {
            s = BusinessProfile.removeNonDigitCharacters(s);
            if (isUSAddress && BaseData.stringIsNotBlank(s) && (s.charAt(0) == '1')) {
                return s.substring(1);
            }
        }
        
        return s;
    }

    private static String removeNonDigitCharacters(String s) {
        return BusinessProfile.NON_DIGITS_PATTERN.matcher(s).replaceAll("");
    }
}
