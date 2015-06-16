/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.tracking;

public interface ParamAdder {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:34 ParamAdder.java NSI";

    /**
     * Add parameters to the specified URL and return the new URL.
     * The URL is assumed NOT to be URL encoded.
     */
    public abstract String addParams(String url);

    /**
     * Add parameters to the specified URL and return the new URL.
     * The URL is URL encoded.
     */
    public abstract String addUrlEncodedParams(String url, String characterEncoding);
}
