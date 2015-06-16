/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.beans;

public class UserLoginSuccessEvent extends BaseUserLoginEvent {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:09 UserLoginSuccessEvent.java NSI";

    public UserLoginSuccessEvent() {
        super(LOGIN_SUCCESS_EVENT_TYPE);
    }
}
