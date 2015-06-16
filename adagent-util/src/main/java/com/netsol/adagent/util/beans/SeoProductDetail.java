/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.beans;

import java.util.Date;

/**
 * Represents SEO product details.
 */
public class SeoProductDetail extends BaseData {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:07 SeoProductDetail.java NSI";

    private String accountManagerUserName;
    private String copywriterUserName;
    private Date jsCheckDate;
    private Boolean jsCheckResult;
    private String prodInstId;
    private String qaCopywriterUserName;
    private String updatedBySystem;
    private String updatedByUser;

    public void setAccountManagerUserName(String accountManagerUserName) {
        this.accountManagerUserName = accountManagerUserName;
    }

    public String getAccountManagerUserName() {
        return accountManagerUserName;
    }

    public void setCopywriterUserName(String copywriterUserName) {
        this.copywriterUserName = copywriterUserName;
    }

    public String getCopywriterUserName() {
        return copywriterUserName;
    }

    public void setJsCheckDate(Date jsCheckDate) {
        this.jsCheckDate = jsCheckDate;
    }

    public Date getJsCheckDate() {
        return jsCheckDate;
    }

    public void setJsCheckResult(Boolean jsCheckResult) {
        this.jsCheckResult = jsCheckResult;
    }

    public Boolean getJsCheckResult() {
        return jsCheckResult;
    }

    public void setProdInstId(String prodInstId) {
        this.prodInstId = prodInstId;
    }

    public String getProdInstId() {
        return prodInstId;
    }

    public void setQaCopywriterUserName(String qaCopywriterUserName) {
        this.qaCopywriterUserName = qaCopywriterUserName;
    }

    public String getQaCopywriterUserName() {
        return qaCopywriterUserName;
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
