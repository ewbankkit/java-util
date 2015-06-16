/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.beans;

/**
 * Represents an NS campaign ad call extension.
 */
public class NsCampaignAdCallExtension extends NsCampaignAdExtension {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:00 NsCampaignAdCallExtension.java NSI";

    @ColumnName("call_only")
    private boolean callOnly;
    @ColumnName("country_code")
    private String countryCode;
    @ColumnName("phone_number")
    private String phoneNumber;

    public void setCallOnly(boolean callOnly) {
        setTrackedField("callOnly", callOnly);
    }

    public boolean isCallOnly() {
        return callOnly;
    }

    public void setCountryCode(String countryCode) {
        setTrackedField("countryCode", countryCode);
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setNsCampaignAdCallExtensionId(long nsCampaignAdCallExtensionId) {
        setNsEntityId(nsCampaignAdCallExtensionId);
    }

    public long getNsCampaignAdCallExtensionId() {
        return getNsEntityId();
    }

    public void setPhoneNumber(String phoneNumber) {
        setTrackedField("phoneNumber", phoneNumber);
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof NsCampaignAdCallExtension)) {
            return false;
        }
        return equals(this, (NsCampaignAdCallExtension)o);
    }
    
    public static void copy(NsCampaignAdCallExtension src, NsCampaignAdCallExtension dest) {
        if ((src != null) && (dest != null)) {
            copy((NsCampaignAdExtension)src, (NsCampaignAdExtension)dest);
            dest.setCallOnly(src.isCallOnly());
            dest.setCountryCode(src.getCountryCode());
            dest.setPhoneNumber(src.getPhoneNumber());
        }
    }
}
