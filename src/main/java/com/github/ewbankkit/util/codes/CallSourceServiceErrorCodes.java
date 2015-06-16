/**
 * Kit's Java Utils.
 */

package com.github.ewbankkit.util.codes;

// CallSource service error codes.
// Values are returned via IntHubException.
public final class CallSourceServiceErrorCodes {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:24 CallSourceServiceErrorCodes.java NSI";

    public static final int VALIDATION_ERROR = 8000;
    public static final int CALLSOURCE_ERROR = 8001;
    public static final int DB_ERROR = 8002;
    public static final int TELMETRICS_ERROR = 8003;

    private CallSourceServiceErrorCodes() {
        return;
    }
}
