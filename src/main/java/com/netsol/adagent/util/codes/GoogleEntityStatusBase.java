/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.codes;

// Google entity statuses.
public abstract class GoogleEntityStatusBase {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:26 GoogleEntityStatusBase.java NSI";
    
    public final static String ACTIVE = "Enabled";
    public final static String DELETED = "Deleted";
    public final static String DISAPPROVED = "Disapproved";
    public final static String PAUSED = "Paused";
    
    protected GoogleEntityStatusBase() {
        return;
    }
}
