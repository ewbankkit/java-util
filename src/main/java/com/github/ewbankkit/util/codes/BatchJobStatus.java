/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.github.ewbankkit.util.codes;

public final class BatchJobStatus {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:23 BatchJobStatus.java NSI";

    public static final String FAILED = "FAILED";
    public static final String FINISHED_FAILED = "FINISHED, FAILED";
    public static final String FINISHED_SUCCESSFUL = "FINISHED, SUCCESSFUL";
    public static final String STARTED = "STARTED";
    public static final String SUCCESSFUL = "SUCCESSFUL";

    private BatchJobStatus() {
        return;
    }
}
