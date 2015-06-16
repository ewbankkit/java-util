/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.beans;

/**
 * Represents a vendor account.
 */
public class VendorAccount extends BaseData {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:10 VendorAccount.java NSI";

    private int channelId;
    private boolean masterAccount;
    private String status;
    private int vendorAccountId;
    private int vendorId;

    public void setChannelId(int channelId) {
        this.channelId = channelId;
    }

    public int getChannelId() {
        return channelId;
    }

    public void setMasterAccount(boolean masterAccount) {
        this.masterAccount = masterAccount;
    }

    public boolean isMasterAccount() {
        return masterAccount;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setVendorAccountId(int vendorAccountId) {
        this.vendorAccountId = vendorAccountId;
    }

    public int getVendorAccountId() {
        return vendorAccountId;
    }

    public void setVendorId(int vendorId) {
        this.vendorId = vendorId;
    }

    public int getVendorId() {
        return vendorId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof VendorAccount)) {
            return false;
        }
        VendorAccount rhs = (VendorAccount)o;
        return vendorAccountId == rhs.vendorAccountId;
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + this.vendorAccountId;
        return result;
    }
}
