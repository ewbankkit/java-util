/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.ccg;

public class CCGFactory {
    public static final String sccsID = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:20 CCGFactory.java NSI";

    public static CCGClient createCCGClient(CCGConnectionCfg conf){
        return new CCGClientImpl(conf);
    }
}
