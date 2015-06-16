/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.beans;

public class UserLoginFailureEvent extends BaseUserLoginEvent {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:09 UserLoginFailureEvent.java NSI";

    public UserLoginFailureEvent() {
        super(LOGIN_FAILURE_EVENT_TYPE);
    }
}
