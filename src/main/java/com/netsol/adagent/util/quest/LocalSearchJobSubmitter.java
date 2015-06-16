/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.quest;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Formatter;

import com.netsol.adagent.util.log.BaseLoggable;

/**
 * Quest local search job submitter.
 */
/* package-private */ class LocalSearchJobSubmitter extends JobSubmitter<LocalSearchPhrase> {
    public final static String sccsID = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:20 LocalSearchJobSubmitter.java NSI";

    public static final int REQUEST_TYPE = 9;
    public static final int WORKFLOW_ID = 18;

    /**
     * Constructor.
     */
    public LocalSearchJobSubmitter(BaseLoggable baseLoggable) {
        super(baseLoggable);

        return;
    }

    /**
     * Submit a job.
     */
    public long  submitJob(String logTag, Connection connection, Collection<LocalSearchPhrase> phrases, String jobName, Long startTime) throws SQLException {
        return this.submitJob(logTag, connection, phrases, jobName, LocalSearchJobSubmitter.WORKFLOW_ID, LocalSearchJobSubmitter.REQUEST_TYPE, startTime);
    }

    /**
     * Add a request to a job.
     */
    @Override
    protected final void addRequest(String logTag, Connection connection, long jobId, LocalSearchPhrase phrase) throws SQLException {
        // Request format is:
        //  <URL>|<Search phrase>|<Search phrase source>|<Location>
        StringBuilder sb = new StringBuilder();
        Formatter formatter = new Formatter(sb);
        formatter.format(
                "%1$s|%2$s|%3$s|%4$s",
                phrase.getUrl(),
                phrase.getSearchPhrase(),
                phrase.getSearchPhraseSource(),
                phrase.getLocation());
        this.questQueueHelper.addRequest(logTag, connection, jobId, sb.toString());

        return;
    }
}
