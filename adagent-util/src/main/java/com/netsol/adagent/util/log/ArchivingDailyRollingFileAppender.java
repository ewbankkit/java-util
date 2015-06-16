/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.log;

import java.io.IOException;

import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.Layout;

/**
 * log4j appender that rolls files over daily and archives old files.
 */
public  class ArchivingDailyRollingFileAppender extends DailyRollingFileAppender {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:08 ArchivingDailyRollingFileAppender.java NSI";

    /**
     * Constructor.
     */
    public ArchivingDailyRollingFileAppender() {
        super();

        return;
    }

    /**
     * Constructor.
     */
    public ArchivingDailyRollingFileAppender(Layout layout, String filename, String datePattern) throws IOException {
        super(layout, filename, datePattern);

        return;
    }
}
