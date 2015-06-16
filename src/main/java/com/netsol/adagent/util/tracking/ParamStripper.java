/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.tracking;

public interface ParamStripper {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:34 ParamStripper.java NSI";

    /**
     * Strip parameters from the specified URL and return the new URL.
     * The URL is assumed NOT to be URL encoded.
     */
    public abstract String stripParams(String url);

    /**
     * Strip parameters from the specified URL and return the new URL.
     * The URL is URL encoded.
     */
    public abstract String stripUrlEncodedParams(String url, String characterEncoding);
}
