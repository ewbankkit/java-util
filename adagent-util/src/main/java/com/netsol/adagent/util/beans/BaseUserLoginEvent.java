/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.beans;

/**
 * Abstract base class for user login events.
 */
public abstract class BaseUserLoginEvent extends BaseData {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:53:50 BaseUserLoginEvent.java NSI";

    public static final String LOGIN_FAILURE_EVENT_TYPE = "LOGIN_FAILURE";
    public static final String LOGIN_SUCCESS_EVENT_TYPE = "LOGIN_SUCCESS";
    public static final String LOGOUT_EVENT_TYPE        = "LOGOUT";

    private String createdBySystem;
    private String details;
    private final String eventType;
    private String ipAddress;
    private String userLoginDomain;
    private String userName;

    protected BaseUserLoginEvent(String eventType) {
        this.eventType = eventType;
    }

    public void setCreatedBySystem(String createdBySystem) {
        this.createdBySystem = createdBySystem;
    }

    public String getCreatedBySystem() {
        return createdBySystem;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getDetails() {
        return details;
    }

    public String getEventType() {
        return eventType;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setUserLoginDomain(String userLoginDomain) {
        this.userLoginDomain = userLoginDomain;
    }

    public String getUserLoginDomain() {
        return userLoginDomain;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }
}
