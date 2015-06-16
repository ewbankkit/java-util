/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.github.ewbankkit.util.codes;

// Yahoo! entity statuses.
public abstract class YahooEntityStatusBase {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:38 YahooEntityStatusBase.java NSI";

    public final static String ACTIVE = "On";
    public final static String DELETED = "Deleted";
    public final static String PAUSED = "Off";

    protected YahooEntityStatusBase() {
        return;
    }
}
