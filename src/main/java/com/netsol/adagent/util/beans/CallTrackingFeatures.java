/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.beans;

/**
 * Represents call tracking features.
 */
public class CallTrackingFeatures extends BaseData {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:53:51 CallTrackingFeatures.java NSI";

    private int callLeadMinDuration;
    private boolean callTrackingEnabled;
    private boolean leadRecordingEnabled;
    private String prodInstId;
    private boolean trackUnansweredCalls;
    private String updatedBySystem;
    private String updatedByUser;

    public void setCallLeadMinDuration(int callLeadMinDuration) {
        this.callLeadMinDuration = callLeadMinDuration;
    }

    public int getCallLeadMinDuration() {
        return callLeadMinDuration;
    }

    public void setCallTrackingEnabled(boolean callTrackingEnabled) {
        this.callTrackingEnabled = callTrackingEnabled;
    }

    public boolean isCallTrackingEnabled() {
        return callTrackingEnabled;
    }

    public void setLeadRecordingEnabled(boolean leadRecordingEnabled) {
        this.leadRecordingEnabled = leadRecordingEnabled;
    }

    public boolean isLeadRecordingEnabled() {
        return leadRecordingEnabled;
    }

    public String getProdInstId() {
        return prodInstId;
    }

    public void setProdInstId(String prodInstId) {
        this.prodInstId = prodInstId;
    }

    public void setTrackUnansweredCalls(boolean trackUnansweredCalls) {
        this.trackUnansweredCalls = trackUnansweredCalls;
    }

    public boolean isTrackUnansweredCalls() {
        return trackUnansweredCalls;
    }

    public void setUpdatedBySystem(String updatedBySystem) {
        this.updatedBySystem = updatedBySystem;
    }

    public String getUpdatedBySystem() {
        return updatedBySystem;
    }

    public String getUpdatedByUser() {
        return updatedByUser;
    }

    public void setUpdatedByUser(String updatedByUser) {
        this.updatedByUser = updatedByUser;
    }
}
