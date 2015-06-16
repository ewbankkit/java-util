/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.github.ewbankkit.util.log;

import org.apache.commons.logging.Log;

public class SimpleLoggable extends BaseLoggable {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:08 SimpleLoggable.java NSI";

    /**
     * Constructor.
     */
    public SimpleLoggable(String logComponent) {
        super(logComponent);

        return;
    }

    /**
     * Constructor.
     */
    public SimpleLoggable(Log logger) {
        super(logger);

        return;
    }
}

