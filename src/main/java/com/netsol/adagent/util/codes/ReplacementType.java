/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.codes;

// Interceptor replacement types.
public final class ReplacementType {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:33 ReplacementType.java NSI";

    public static final String CUSTOM = "custom";
    public static final String HTTP_REQUEST_HEADER = "http_request_header";
    public static final String HTTP_RESPONSE_HEADER = "http_response_header";
    public static final String IMAGE = "image";
    public static final String NONE = "";
    public static final String PHONE = "phone";
    public static final String URL = "url";

    private ReplacementType() {
        return;
    }
}
