/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.ccg;

/**
 * Config Object to hold CCG Connection parameters
 * @author pmitchel
 */
public class CCGConnectionCfg {
    public static final String sccsID = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:20 CCGConnectionCfg.java NSI";

    private String emailGenUrl;
    private String customEmailGenUrl;
    private String uploadServiceUrl;
    private String userName;
    private String password;
    private int timeoutMillis;
    private boolean stubMode = false;

    public String getEmailGenUrl() {
        return emailGenUrl;
    }
    public void setEmailGenUrl(String emailGenUrl) {
        this.emailGenUrl = emailGenUrl;
    }
    public String getCustomEmailGenUrl() {
        return customEmailGenUrl;
    }
    public void setCustomEmailGenUrl(String customEmailGenUrl) {
        this.customEmailGenUrl = customEmailGenUrl;
    }
    public String getUploadServiceUrl() {
        return uploadServiceUrl;
    }
    public void setUploadServiceUrl(String uploadServiceUrl) {
        this.uploadServiceUrl = uploadServiceUrl;
    }
    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public int getTimeoutMillis() {
        return timeoutMillis;
    }
    public void setTimeoutMillis(int timeoutMillis) {
        this.timeoutMillis = timeoutMillis;
    }
    public boolean isStubMode() {
        return stubMode;
    }
    public void setStubMode(boolean stubMode) {
        this.stubMode = stubMode;
    }
}
