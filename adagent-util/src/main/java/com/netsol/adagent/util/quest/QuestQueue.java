/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.quest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;

import com.netsol.adagent.util.beans.SearchPhrase;
import com.netsol.adagent.util.dbhelpers.BaseHelper;
import com.netsol.adagent.util.dbhelpers.DataSourceFactory;
import com.netsol.adagent.util.dbhelpers.SearchPhraseHelper;
import com.netsol.adagent.util.dbhelpers.SimpleDataSourceFactory;
import com.netsol.adagent.util.log.BaseLoggable;
import com.netsol.adagent.util.lsv.BusinessProfile;

/**
 * QUEST queue.
 */
public final class QuestQueue extends BaseLoggable {
    public final static String sccsId = "@(#) adagent-util_d16.20.0.3 10/19/12 09:23:13 QuestQueue.java NSI";

    public static final int SEO_JOB_WORKFLOW_ID = SearchEngineJobSubmitter.WORKFLOW_ID;

    private final LocalSearchJobSubmitter localSearchJobSubmitter;
    private final LsvSearchPhraseGenerator lsvSearchPhraseGenerator;
    private final DataSource questQueueDataSource;
    private final DbHelper dbHelper;
    private final SearchEngineJobSubmitter searchEngineJobSubmitter;
    private final SearchPhraseHelper searchPhraseHelper;
    private final SeoSearchPhraseGenerator seoSearchPhraseGenerator;

    /**
     * Constructor.
     */
    public QuestQueue(String logComponent, String questQueueUrl) {
        this(logComponent, null, questQueueUrl, SimpleDataSourceFactory.INSTANCE);
    }

    /**
     * Constructor.
     */
    public QuestQueue(Log logger, String questQueueUrl) {
        this(null, logger, questQueueUrl, SimpleDataSourceFactory.INSTANCE);
    }

    /**
     * Constructor.
     */
    public QuestQueue(String logComponent, String questQueueUrl, DataSourceFactory questQueueDataSourceFactory) {
        this(logComponent, null, questQueueUrl, questQueueDataSourceFactory);
    }

    /**
     * Constructor.
     */
    public QuestQueue(Log logger, String questQueueUrl, DataSourceFactory questQueueDataSourceFactory) {
        this(null, logger, questQueueUrl, questQueueDataSourceFactory);
    }

    /**
     * Constructor.
     */
    private QuestQueue(String logComponent, Log logger, String questQueueUrl, DataSourceFactory questQueueDataSourceFactory) {
        super(logComponent, logger);
        dbHelper = new DbHelper(this);
        localSearchJobSubmitter = new LocalSearchJobSubmitter(this);
        lsvSearchPhraseGenerator = new LsvSearchPhraseGenerator(this);
        questQueueDataSource = questQueueDataSourceFactory.newDataSource(questQueueUrl);
        searchEngineJobSubmitter = new SearchEngineJobSubmitter(this);
        searchPhraseHelper = new SearchPhraseHelper(this);
        seoSearchPhraseGenerator = new SeoSearchPhraseGenerator(this);
    }

    /**
     * Submit LSV jobs for the specified business profile.
     */
    public void submitLsvJobs(String logTag, Connection gdbConnection, Connection pdbConnection, BusinessProfile businessProfile, Long startTime, String updatedBy) throws SQLException {
        String prodInstId = businessProfile.getProdInstId();
        Collection<LocalSearchPhrase> localSearchPhrases = lsvSearchPhraseGenerator.generateLocalSearchPhrases(logTag, gdbConnection, businessProfile);
        Collection<SearchEnginePhrase> searchEnginePhrases = lsvSearchPhraseGenerator.generateSearchEnginePhrases(logTag, gdbConnection, businessProfile);

        Connection questQueueConnection = null;
        try {
            questQueueConnection = questQueueDataSource.getConnection();
            if (localSearchPhrases.isEmpty()) {
                logInfo(logTag, "No local search phrases");
            }
            else {
                long jobId =
                    localSearchJobSubmitter.submitJob(
                        logTag,
                        questQueueConnection,
                        localSearchPhrases,
                        "Quest LSV local search job for " + prodInstId,
                        startTime);
                dbHelper.insertQuestJob(logTag, pdbConnection, prodInstId, LocalSearchJobSubmitter.WORKFLOW_ID, jobId, startTime, updatedBy);

            }
            if (searchEnginePhrases.isEmpty()) {
                logInfo(logTag, "No search engine phrases");
            }
            else {
                long jobId =
                    searchEngineJobSubmitter.submitJob(
                        logTag,
                        questQueueConnection,
                        searchEnginePhrases,
                        "Quest LSV search engine job for " + prodInstId,
                        startTime);
                dbHelper.insertQuestJob(logTag, pdbConnection, prodInstId, SearchEngineJobSubmitter.WORKFLOW_ID, jobId, startTime, updatedBy);
            }
        }
        finally {
            BaseHelper.close(questQueueConnection);
        }

        // Record the submitted keywords.
        Collection<String> keywords = new ArrayList<String>();
        for (LocalSearchPhrase localSearchPhrase : localSearchPhrases) {
            // Don't record searches by phone number, street address or ZIP code.
            String searchPhraseSource = localSearchPhrase.getSearchPhraseSource();
            if (SearchEnginePhrase.BUSINESS_PHONE_NUMBER_SOURCE.equals(searchPhraseSource) ||
                SearchEnginePhrase.BUSINESS_STREET_ADDRESS_SOURCE.equals(searchPhraseSource) ||
                SearchEnginePhrase.BUSINESS_ZIP_CODE_SOURCE.equals(searchPhraseSource)) {
                continue;
            }
            keywords.add(localSearchPhrase.getSearchPhrase());
        }
        for (SearchEnginePhrase searchEnginePhrase : searchEnginePhrases) {
            keywords.add(searchEnginePhrase.getSearchPhrase());
        }
        if (!keywords.isEmpty()) {
            SearchPhrase searchPhrase = new SearchPhrase();
            searchPhrase.setKeywords(SearchPhrase.getSearchKeywords(keywords, SearchEnginePhrase.SEARCH_PHRASE_DELIMITER));
            searchPhrase.setProdInstId(prodInstId);
            searchPhraseHelper.replaceSearchPhrase(logTag, gdbConnection, searchPhrase);
        }
    }

    /**
     * Submit an SEO job for the specified product instance ID.
     */
    public void submitSeoJob(String logTag, Connection pdbConnection, String prodInstId, Long startTime, String updatedBy) throws SQLException {
        Collection<SearchEnginePhrase> searchEnginePhrases = seoSearchPhraseGenerator.generateSearchEnginePhrases(logTag, pdbConnection, prodInstId);

        Connection questQueueConnection = null;
        try {
            questQueueConnection = questQueueDataSource.getConnection();
            if (searchEnginePhrases.isEmpty()) {
                logInfo(logTag, "No search engine phrases");
            }
            else {
                long jobId = searchEngineJobSubmitter.submitJob(
                        logTag,
                        questQueueConnection,
                        searchEnginePhrases,
                        "Quest SEO search engine job for " + prodInstId,
                        startTime);
                dbHelper.insertQuestJob(logTag, pdbConnection, prodInstId, SearchEngineJobSubmitter.WORKFLOW_ID, jobId, startTime, updatedBy);
            }
        }
        finally {
            BaseHelper.close(questQueueConnection);
        }
    }
    
    /**
     * Submit a search engine phrase job.
     */
    public long submitSearchEnginePhraseJob(String logTag, Collection<SearchEnginePhrase> searchEnginePhrases, String updatedBy) throws Exception {
    	return submitSearchEnginePhraseJob(logTag, searchEnginePhrases, updatedBy, SearchEngineJobSubmitter.WORKFLOW_ID);
    }
    
    /**
     * Submit a LQT search engine phrase job.
     */
    public long submitLQTSearchEnginePhraseJob(String logTag, Collection<SearchEnginePhrase> searchEnginePhrases, String updatedBy) throws Exception {
    	return submitSearchEnginePhraseJob(logTag, searchEnginePhrases, updatedBy, SearchEngineJobSubmitter.LQT_WORKFLOW_ID);
    }
    /**
     * Submit a search engine phrase job.
     */
    private long submitSearchEnginePhraseJob(String logTag, Collection<SearchEnginePhrase> searchEnginePhrases, String updatedBy, int workflowId) throws Exception {
        Connection questQueueConnection = null;
        try {
            questQueueConnection = questQueueDataSource.getConnection();
            if (searchEnginePhrases.isEmpty()) {
                logInfo(logTag, "No search engine phrases submitted");
                throw new RuntimeException("No search engine phrases submitted");
            }
            else {
                long jobId = searchEngineJobSubmitter.submitJob(
                        logTag,
                        questQueueConnection,
                        searchEnginePhrases,
                        "Search engine phrase job from " + updatedBy,
                        workflowId,
                        SearchEngineJobSubmitter.REQUEST_TYPE,
                        System.currentTimeMillis());
                return jobId;
            }
        }
        finally {
            BaseHelper.close(questQueueConnection);
        }
    }

    private static class DbHelper extends BaseHelper {
        /**
         * Constructor.
         */
        public DbHelper(BaseLoggable baseLoggable) {
            super(baseLoggable);
        }

        /**
         * Insert the specified SEO product details.
         */
        public void insertQuestJob(String logTag, Connection connection, String prodInstId, int workflowId, long jobId, Long jobStartTime, String updatedBy) throws SQLException {
            final String SQL =
                "INSERT INTO quest_job " +
                "(prod_inst_id, workflow_id, job_id, job_start_date," +
                " created_date, updated_by, updated_date) " +
                "VALUES" +
                "(?, ?, ?, COALESCE(?, NOW())," +
                " NOW(), ?, NOW()) " +
                "ON DUPLICATE KEY UPDATE" +
                "  job_id = VALUES(job_id)," +
                "  job_start_date = VALUES(job_start_date)," +
                "  updated_by = VALUES(updated_by)," +
                "  updated_date = NOW();";

            PreparedStatement statement = null;
            try {
                statement = connection.prepareStatement(SQL);
                statement.setString(1, prodInstId);
                statement.setInt(2, workflowId);
                statement.setLong(3, jobId);
                statement.setTimestamp(4, toSqlTimestamp(jobStartTime));
                statement.setString(5, updatedBy);
                logSqlStatement(logTag, statement);
                statement.executeUpdate();
            }
            finally {
                close(statement);
            }
        }
    }
}
