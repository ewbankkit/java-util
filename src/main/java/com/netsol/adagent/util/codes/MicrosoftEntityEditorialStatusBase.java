/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.codes;

// Microsoft entity editorial statuses.
public abstract class MicrosoftEntityEditorialStatusBase {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:29 MicrosoftEntityEditorialStatusBase.java NSI";
    
    public final static String ACTIVE = "Active";
    public final static String DISAPPROVED = "Disapproved";
    public final static String INACTIVE = "Inactive";
    
    protected MicrosoftEntityEditorialStatusBase() {
        return;
    }
}
