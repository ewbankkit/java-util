/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.quest;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import com.netsol.adagent.util.TrackingUtil;
import com.netsol.adagent.util.beans.BaseData;
import com.netsol.adagent.util.dbhelpers.BaseHelper;
import com.netsol.adagent.util.dbhelpers.SeoKeywordsHelper;
import com.netsol.adagent.util.log.BaseLoggable;
import com.netsol.adagent.util.log.SimpleLoggable;

/**
 * SEO search phrase generator.
 */
/* package-private */ class SeoSearchPhraseGenerator extends BaseLoggable {
    public final static String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:22 SeoSearchPhraseGenerator.java NSI";

    private final SeoKeywordsHelper seoKeywordsHelper;
    private final TrackingUtil trackingUtil;

    /**
     * Constructor.
     */
    public SeoSearchPhraseGenerator(BaseLoggable baseLoggable) {
        super(baseLoggable);

        this.seoKeywordsHelper = new SeoKeywordsHelper(baseLoggable);
        this.trackingUtil = new TrackingUtil(baseLoggable);

        return;
    }

    /**
     * Generate search engine phrases.
     */
    public Collection<SearchEnginePhrase> generateSearchEnginePhrases(String logTag, Connection connection, String prodInstId) throws SQLException {
        String productUrl = this.trackingUtil.getProductUrl(logTag, connection, prodInstId);
        if (BaseData.stringIsEmpty(productUrl)) {
            return Collections.emptyList();
        }

        Collection<SearchEnginePhrase> phrases = new ArrayList<SearchEnginePhrase>();
        Collection<String> keywords = this.seoKeywordsHelper.getSeoKeywords(logTag, connection, prodInstId);
        for (String keyword: keywords) {
            SearchEnginePhrase phrase = new SearchEnginePhrase();
            phrase.setSearchPhrase(keyword);
            phrase.setSearchPhraseSource(SearchEnginePhrase.DESCRIPTIVE_KEYWORD_SOURCE);
            phrase.setUrl(productUrl);
            phrases.add(phrase);
        }

        return phrases;
    }

    // Test harness.
    public static void main(String[] args) {
        try {
            Connection connection = null;

            try {
                String logTag = null;
                String prodInstId = "WN.DEV.BING.0001";

                Class.forName("com.mysql.jdbc.Driver").newInstance();
                connection = DriverManager.getConnection("jdbc:mysql://eng2.dev.netsol.com:4300/adagent?user=adagent&password=adagent");
                SeoSearchPhraseGenerator seoSearchPhraseGenerator = new SeoSearchPhraseGenerator(new SimpleLoggable(""));
                System.out.println(BaseData.toString(seoSearchPhraseGenerator.generateSearchEnginePhrases(logTag, connection, prodInstId)));
            }
            finally {
                BaseHelper.close(connection);
            }
        }
        catch (Throwable t) {
            t.printStackTrace();
        }

        return;
    }
}
