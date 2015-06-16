/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.ccg;

public class CCGClientException extends Exception {
    public static final String sccsID = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:19 CCGClientException.java NSI";

    private static final long serialVersionUID = 1L;

    public CCGClientException() {
        super();
    }

    public CCGClientException(Throwable e) {
        super(e);
    }

    public CCGClientException(String msg) {
        super(msg);
    }

    public CCGClientException(String msg, Throwable e) {
        super(msg, e);
    }
}
