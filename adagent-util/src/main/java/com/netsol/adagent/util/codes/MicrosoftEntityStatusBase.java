/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.codes;

// Microsoft entity statuses.
public abstract class MicrosoftEntityStatusBase {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:30 MicrosoftEntityStatusBase.java NSI";
    
    public final static String ACTIVE = "Active";
    public final static String DELETED = "Deleted";
    public final static String PAUSED = "Paused";
    
    protected MicrosoftEntityStatusBase() {
        return;
    }
}
