/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.beans;

public class UserLogoutEvent extends BaseUserLoginEvent {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:10 UserLogoutEvent.java NSI";

    public UserLogoutEvent() {
        super(LOGOUT_EVENT_TYPE);
    }
}
