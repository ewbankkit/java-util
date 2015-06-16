/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.lsv;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

import org.apache.commons.logging.Log;

import com.netsol.adagent.util.DbAccess;
import com.netsol.adagent.util.TrackingUtil;
import com.netsol.adagent.util.beans.Pair;
import com.netsol.adagent.util.codes.DBAlias;
import com.netsol.adagent.util.dbhelpers.BaseHelper;
import com.netsol.adagent.util.log.BaseLoggable;

/**
 * Keyword ranking processor.
 */
public final class KeywordRankingProcessor extends BaseLoggable {
    public final static String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:11 KeywordRankingProcessor.java NSI";

    private DbAccess questDbAccess;
    private final KeywordRankingHelper keywordRankingHelper;

    /**
     * Constructor.
     */
    public KeywordRankingProcessor(String logComponent) {
        super(logComponent);
        keywordRankingHelper = new KeywordRankingHelper(logComponent);
    }

    /**
     * Constructor.
     */
    public KeywordRankingProcessor(Log logger) {
        super(logger);
        keywordRankingHelper = new KeywordRankingHelper(logger);
    }

    public void setQuestDbAccess(DbAccess questDbAccess) {
        this.questDbAccess = questDbAccess;
    }

    /**
     * Return the latest profile URL for the specified site URL and search engine name.
     */
    public Pair<String, Date> getLatestProfileUrl(String logTag, String siteUrl, String searchEngineName) throws SQLException {
        String hostName = TrackingUtil.extractHostName(siteUrl);
        if (hostName == null) {
            return null;
        }

        Connection questDbConnection = null;
        try {
            questDbConnection = questDbAccess.getConnection(logTag, DBAlias.QUEST, hostName);
            return keywordRankingHelper.getLatestProfileUrl(logTag, questDbConnection, hostName, siteUrl, searchEngineName);
        }
        finally {
            BaseHelper.close(questDbConnection);
        }
    }
}
