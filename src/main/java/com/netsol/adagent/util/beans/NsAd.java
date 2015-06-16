/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.beans;

/**
 * Represents an NS ad.
 */
public class NsAd extends NsEntity {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:53:59 NsAd.java NSI";

    private String adType = "";
    private String descriptionLine1 = "";
    private String descriptionLine2 = "";
    private String destinationUrl = "";
    private String disapprovedReason;
    private String displayUrl = "";
    private String editorialStatus;
    private long nsAdGroupId;

    public void setAdType(String adType) {
        this.adType = adType;
    }

    public String getAdType() {
        return adType;
    }

    public void setDescriptionLine1(String descriptionLine1) {
        this.descriptionLine1 = descriptionLine1;
    }

    public String getDescriptionLine1() {
        return descriptionLine1;
    }

    public void setDescriptionLine2(String descriptionLine2) {
        this.descriptionLine2 = descriptionLine2;
    }

    public String getDescriptionLine2() {
        return descriptionLine2;
    }

    public void setDestinationUrl(String destinationUrl) {
        this.destinationUrl = destinationUrl;
    }

    public String getDestinationUrl() {
        return destinationUrl;
    }

    public void setDisapprovedReason(String disapprovedReason) {
        this.disapprovedReason = disapprovedReason;
    }

    public String getDisapprovedReason() {
        return disapprovedReason;
    }

    public void setDisplayUrl(String displayUrl) {
        this.displayUrl = displayUrl;
    }

    public String getDisplayUrl() {
        return displayUrl;
    }

    public void setEditorialStatus(String editorialStatus) {
        this.editorialStatus = editorialStatus;
    }

    public String getEditorialStatus() {
        return editorialStatus;
    }

    public void setHeadline(String headline) {
        setName(headline);
    }

    public String getHeadline() {
        return getName();
    }

    public void setNsAdGroupId(long nsAdGroupId) {
        this.nsAdGroupId = nsAdGroupId;
    }

    public long getNsAdGroupId() {
        return nsAdGroupId;
    }

    public void setNsAdId(long nsAdId) {
        setNsEntityId(nsAdId);
    }

    public long getNsAdId() {
        return getNsEntityId();
    }

    public void setVendorAdId(Long vendorAdId) {
        setVendorEntityId(vendorAdId);
    }

    public Long getVendorAdId() {
        return getVendorEntityId();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof NsAd)) {
            return false;
        }
        return equals(this, (NsAd)o);
    }
}
