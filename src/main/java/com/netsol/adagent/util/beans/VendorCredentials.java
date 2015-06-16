/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.beans;

import java.util.Date;

/**
 * Represents vendor credentials.
 */
public class VendorCredentials extends BaseData {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:11 VendorCredentials.java NSI";

    private Date endDate;
    private String prodInstId;
    private String vendorAccountId1;
    private String vendorAccountId2;
    private String vendorAccountName;
    private String vendorAccountPassword;
    private String vendorAccountUserName;
    private int vendorId;
    private Long vendorTimeZoneId;

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setProdInstId(String prodInstId) {
        this.prodInstId = prodInstId;
    }

    public String getProdInstId() {
        return prodInstId;
    }

    public void setVendorAccountId1(String vendorAccountId1) {
        this.vendorAccountId1 = vendorAccountId1;
    }

    public String getVendorAccountId1() {
        return vendorAccountId1;
    }

    public void setVendorAccountId2(String vendorAccountId2) {
        this.vendorAccountId2 = vendorAccountId2;
    }

    public String getVendorAccountId2() {
        return vendorAccountId2;
    }

    public void setVendorAccountName(String vendorAccountName) {
        this.vendorAccountName = vendorAccountName;
    }

    public String getVendorAccountName() {
        return vendorAccountName;
    }

    public void setVendorAccountPassword(String vendorAccountPassword) {
        this.vendorAccountPassword = vendorAccountPassword;
    }

    public String getVendorAccountPassword() {
        return vendorAccountPassword;
    }

    public void setVendorAccountUserName(String vendorAccountUserName) {
        this.vendorAccountUserName = vendorAccountUserName;
    }

    public String getVendorAccountUserName() {
        return vendorAccountUserName;
    }

    public void setVendorId(int vendorId) {
        this.vendorId = vendorId;
    }

    public int getVendorId() {
        return vendorId;
    }

    public void setVendorTimeZoneId(Long vendorTimeZoneId) {
        this.vendorTimeZoneId = vendorTimeZoneId;
    }

    public Long getVendorTimeZoneId() {
        return vendorTimeZoneId;
    }
}
