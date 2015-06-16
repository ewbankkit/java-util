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
 * Quest search engine job submitter.
 */
/* package-private */ class SearchEngineJobSubmitter extends JobSubmitter<SearchEnginePhrase> {
    public final static String sccsID = "@(#) adagent-util_d16.20.0.3 10/19/12 09:23:13 SearchEngineJobSubmitter.java NSI";

    public static final int REQUEST_TYPE = 6;
    public static final int WORKFLOW_ID = 17;
    public static final int LQT_WORKFLOW_ID = 26;

    /**
     * Constructor.
     */
    public SearchEngineJobSubmitter(BaseLoggable baseLoggable) {
        super(baseLoggable);

        return;
    }

    /**
     * Submit a job.
     * Return the job ID.
     */
    public long submitJob(String logTag, Connection connection, Collection<SearchEnginePhrase> phrases, String jobName, Long startTime) throws SQLException {
        return this.submitJob(logTag, connection, phrases, jobName, SearchEngineJobSubmitter.WORKFLOW_ID, SearchEngineJobSubmitter.REQUEST_TYPE, startTime);
    }

    /**
     * Add a request to a job.
     */
    @Override
    protected final void addRequest(String logTag, Connection connection, long jobId, SearchEnginePhrase phrase) throws SQLException {
        // Request format is:
        //  <URL>|<Search phrase>|<Search phrase source>
        StringBuilder sb = new StringBuilder();
        Formatter formatter = new Formatter(sb);
        formatter.format(
                "%1$s|%2$s|%3$s",
                phrase.getUrl(),
                phrase.getSearchPhrase(),
                phrase.getSearchPhraseSource());
        this.questQueueHelper.addRequest(logTag, connection, jobId, sb.toString());

        return;
    }
}
