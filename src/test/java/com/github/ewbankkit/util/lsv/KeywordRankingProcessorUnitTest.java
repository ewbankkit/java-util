/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.github.ewbankkit.util.lsv;

import static org.junit.Assert.assertNull;

import java.sql.SQLException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.netsol.adagent.util.DbAccess;
import com.netsol.adagent.util.lsv.KeywordRankingProcessor;

public class KeywordRankingProcessorUnitTest {
    public final static String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:11 KeywordRankingProcessorUnitTest.java NSI";

    private static DbAccess questDbAccess;
    private static final String logTag = null;

    @BeforeClass
    public static void setup() throws SQLException {
        questDbAccess = new DbAccess("", "jdbc:mysql://eng2.dev.netsol.com:3320/quest?user=waf&password=waf");
    }

    @AfterClass
    public static void teardown() throws SQLException {
        if (questDbAccess != null) {
            questDbAccess.close();
        }
    }

    @Test
    public void getLatestProfileUrlTest1() throws SQLException {
        KeywordRankingProcessor keywordRankingProcessor = new KeywordRankingProcessor("");
        keywordRankingProcessor.setQuestDbAccess(questDbAccess);
        assertNull(keywordRankingProcessor.getLatestProfileUrl(logTag, null, null));
    }

    @Test
    public void getLatestProfileUrlTest2() throws SQLException {
        KeywordRankingProcessor keywordRankingProcessor = new KeywordRankingProcessor("");
        keywordRankingProcessor.setQuestDbAccess(questDbAccess);
        assertNull(keywordRankingProcessor.getLatestProfileUrl(logTag, "http://www.bookkeepingexpress.com/az/phoenix.aspx", "SUPERPAGES"));
    }
}
