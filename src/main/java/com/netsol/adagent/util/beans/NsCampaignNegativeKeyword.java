/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.beans;

/**
 * Represents an NS campaign negative keyword.
 */
public class NsCampaignNegativeKeyword extends NsCampaignCriterion {
    private String keyword;
    private String keywordType;
    private long nsNegativeId;
    private String updatedBySystem;
    private String updatedByUser;

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeywordType(String keywordType) {
        this.keywordType = keywordType;
    }

    public String getKeywordType() {
        return keywordType;
    }

    public void setNsNegativeId(long nsNegativeId) {
        this.nsNegativeId = nsNegativeId;
    }

    public long getNsNegativeId() {
        return this.nsNegativeId;
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
}
