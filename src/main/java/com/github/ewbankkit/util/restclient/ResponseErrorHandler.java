/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.github.ewbankkit.util.restclient;

/**
 * Response error handler.
 */
public interface ResponseErrorHandler {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:28 ResponseErrorHandler.java NSI";

    public abstract void handleAnyError(int httpStatus, String response) throws Exception;
}
