/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.dbhelpers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.logging.Log;

import com.netsol.adagent.util.LocalHost;
import com.netsol.adagent.util.beans.Pair;
import com.netsol.adagent.util.beans.SeoKeyword;
import com.netsol.adagent.util.beans.SeoKeywordGroup;
import com.netsol.adagent.util.beans.SeoKeyword.KeywordStatus;
import com.netsol.adagent.util.beans.SeoKeywordGroup.Status;
import com.netsol.adagent.util.codes.ProductStatus;
import com.netsol.adagent.util.codes.SeoKeywordGroupStatus;
import com.netsol.adagent.util.codes.SeoKeywordStatus;
import com.netsol.adagent.util.log.BaseLoggable;

public class SeoKeywordsHelper extends BaseHelper {
    public static final String sccsID = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:55 SeoKeywordsHelper.java NSI";

    /**
     * Constructor.
     */
    public SeoKeywordsHelper(String logComponent) {
        super(logComponent);
    }

    /**
     * Constructor.
     */
    public SeoKeywordsHelper(Log logger) {
        super(logger);
    }

    /**
     * Constructor.
     */
    public SeoKeywordsHelper(BaseLoggable baseLoggable) {
        super(baseLoggable);
    }

    /**
     * Constructor.
     */
    public SeoKeywordsHelper(String logComponent, boolean logSqlStatements) {
        super(logComponent, logSqlStatements);
    }

    /**
     * Constructor.
     */
    public SeoKeywordsHelper(Log logger, boolean logSqlStatements) {
        super(logger, logSqlStatements);
    }

    /**
     * Return all active product instance IDs and SEO keywords.
     */
    public List<Pair<String, String>> getAllProductsAndSeoKeywords(String logTag, Connection connection) throws SQLException {
        final String SQL =
            "SELECT DISTINCT" +
            "  sk.prod_inst_id AS prod_inst_id," +
            "  sk.keyword AS keyword " +
            "FROM" +
            "  seo_keyword AS sk " +
            "INNER JOIN" +
            "  product AS p " +
            "ON" +
            "  (p.prod_inst_id = sk.prod_inst_id) " +
            "INNER JOIN" +
            "  seo_keyword_group AS skg " +
            "ON" +
            "  (skg.prod_inst_id = sk.prod_inst_id AND" +
            "   skg.seo_keyword_group_id = sk.seo_keyword_group_id) " +
            "WHERE" +
            "  p.status = ? AND" +
            "  skg.status = ? AND" +
            "  sk.status = ?;";

        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(SQL);
            statement.setString(1, ProductStatus.ACTIVE);
            statement.setString(2, SeoKeywordGroupStatus.ACTIVE);
            statement.setString(3, SeoKeywordStatus.ACTIVE);
            logSqlStatement(logTag, statement);
            resultSet = statement.executeQuery();
            return newList(resultSet, ProductAndKeywordHelper.INSTANCE);
        }
        finally {
            close(statement, resultSet);
        }
    }

    /**
     * Return all active SEO keywords for the specified product instance ID.
     */
    public List<String> getSeoKeywords(String logTag, Connection connection, String prodInstId) throws SQLException {
        final String SQL =
            "SELECT DISTINCT" +
            "  sk.keyword AS keyword " +
            "FROM" +
            "  seo_keyword AS sk " +
            "INNER JOIN" +
            "  seo_keyword_group AS skg " +
            "ON" +
            "  (skg.prod_inst_id = sk.prod_inst_id AND" +
            "   skg.seo_keyword_group_id = sk.seo_keyword_group_id) " +
            "WHERE" +
            "  skg.status = ? AND" +
            "  sk.status = ? AND" +
            "  sk.prod_inst_id = ?;";

        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(SQL);
            statement.setString(1, SeoKeywordGroupStatus.ACTIVE);
            statement.setString(2, SeoKeywordStatus.ACTIVE);
            statement.setString(3, prodInstId);
            logSqlStatement(logTag, statement);
            resultSet = statement.executeQuery();
            return newList(resultSet, new StringFactory("keyword") {});
        }
        finally {
            close(statement, resultSet);
        }
    }

    /**
     * Return the SEO keyword group ID corresponding to the specified keyword.
     */
    public Long getSeoKeywordGroupId(String logTag, Connection connection, String prodInstId, String keyword) throws SQLException {
        final String SQL =
            "SELECT" +
            "  sk.seo_keyword_group_id AS seo_keyword_group_id " +
            "FROM" +
            "  seo_keyword AS sk " +
            "INNER JOIN" +
            "  seo_keyword_group AS skg " +
            "ON" +
            "  (skg.prod_inst_id = sk.prod_inst_id AND" +
            "   skg.seo_keyword_group_id = sk.seo_keyword_group_id) " +
            "WHERE" +
            "  sk.prod_inst_id = ? AND" +
            "  sk.keyword = ? " +
            "ORDER BY" +
            "  FIELD(skg.status, ?, ?, ?) DESC," +
            "  FIELD(sk.status, ?, ?) DESC;";

        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(SQL);
            statement.setString(1, prodInstId);
            statement.setString(2, keyword);
            statement.setString(3, SeoKeywordGroupStatus.DELETED);
            statement.setString(4, SeoKeywordGroupStatus.PAUSED);
            statement.setString(5, SeoKeywordGroupStatus.ACTIVE);
            statement.setString(6, SeoKeywordStatus.DELETED);
            statement.setString(7, SeoKeywordStatus.ACTIVE);
            logSqlStatement(logTag, statement);
            resultSet = statement.executeQuery();
            return firstValue(resultSet, "seo_keyword_group_id", Long.class);
        }
        finally {
            close(statement, resultSet);
        }
    }

    public List<SeoKeywordGroup> getSEOKeywordGroups(Connection conn, String prodInstId, Collection<Status> statuses) throws SQLException {
        List<SeoKeywordGroup> allGroups = new ArrayList<SeoKeywordGroup>();
        if (statuses == null) {
            statuses = Arrays.asList(new Status[]{null});
        }

        for (Status status : statuses) {
            List<SeoKeywordGroup> groups = querySeoKeywordGroups(conn, prodInstId, status);
            if (groups != null) {
                allGroups.addAll(groups);
                for (SeoKeywordGroup group : groups) {
                    List<SeoKeyword> keywords = querySeoKeywords(conn, prodInstId, group.getSeoKeywordGroupId());
                    group.setKeywords(keywords);
                }
            }
        }

        return allGroups;
    }

    public void insertKeywordGroups(Connection pdb, List<SeoKeywordGroup> kwGroups, String prodInstId, String updatedBy) throws SQLException {
        final String insertKeywordGroups =
            "insert into seo_keyword_group( " +
            "  prod_inst_id, " +
            "  name, " +
            "  status, " +
            "  updated_by_user, " +
            "  updated_by_system, " +
            "  created_date " +
            ") values( ?, ?, ?, ?, ?, now() ) ";

        PreparedStatement stat = null;
        try {
            stat = pdb.prepareStatement(insertKeywordGroups, Statement.RETURN_GENERATED_KEYS);
            for (SeoKeywordGroup group : kwGroups) {
                int arg = 1;
                stat.clearParameters();
                stat.setString(arg++, prodInstId);
                stat.setString(arg++, group.getName());
                stat.setString(arg++, (group.getStatus() != null ? group.getStatus().toString() : null));
                stat.setString(arg++, updatedBy);
                stat.setString(arg++, LocalHost.NAME);
                logSqlStatement(prodInstId, stat);
                stat.execute();
                Long id = getAutoIncrementId(stat);
                if (id != null) {
                    group.setSeoKeywordGroupId(id);
                }
            }
        }
        finally {
            close(stat);
        }
    }

    public void insertKeywords(Connection pdb, List<SeoKeyword> keywords, String prodInstId, String updatedBy) throws SQLException {
        final String insertKeyword =
            "insert into seo_keyword( " +
            "  prod_inst_id, " +
            "  seo_keyword_group_id, " +
            "  keyword, " +
            "  status, " +
            "  updated_by_user, " +
            "  updated_by_system, " +
            "  created_date " +
            ") values( ?, ?, ?, ?, ?, ?, now() ) ";

        PreparedStatement stat = null;
        try {
            stat = pdb.prepareStatement(insertKeyword);
            for (SeoKeyword keyword : keywords) {
                int arg = 1;
                stat.clearParameters();
                stat.setString(arg++, prodInstId);
                stat.setLong(arg++, keyword.getSeoKeywordGroupId());
                stat.setString(arg++, keyword.getKeyword());
                stat.setString(arg++, (keyword.getStatus() != null ? keyword.getStatus().toString() : null));
                stat.setString(arg++, updatedBy);
                stat.setString(arg++, LocalHost.NAME);
                logSqlStatement(prodInstId, stat);
                stat.execute();
            }
        }
        finally {
            close(stat);
        }
    }

    public void updateNonDeletedKeywordGroups(Connection pdb, Collection<SeoKeywordGroup> kwGroups, String prodInstId, String updatedBy) throws SQLException {
        final String updateKeywordGroup =
            "update seo_keyword_group " +
            "  set name = ?, " +
            "  status = ?, " +
            "  updated_by_user = ?, " +
            "  updated_by_system = ? " +
            "  where status <> ? and " +
            "  seo_keyword_group_id = ? ";

        PreparedStatement stat = null;
        try {
            stat = pdb.prepareStatement(updateKeywordGroup);
            for (SeoKeywordGroup group : kwGroups) {
                int arg = 1;
                stat.clearParameters();
                stat.setString(arg++, group.getName());
                stat.setString(arg++, (group.getStatus() != null) ? group.getStatus().toString() : null);
                stat.setString(arg++, updatedBy);
                stat.setString(arg++, LocalHost.NAME);
                stat.setString(arg++, Status.DELETED.toString());
                stat.setLong(arg++, group.getSeoKeywordGroupId());
                logSqlStatement(prodInstId, stat);
                stat.execute();
            }
        }
        finally {
            close(stat);
        }
    }

    public void updateNonDeletedKeywords(Connection pdb, Collection<SeoKeyword> keywords, String prodInstId, String updatedBy) throws SQLException {
        final String updateKeyword =
            "update seo_keyword " +
            "  set status = ?, " +
            "  keyword = ?, " +
            "  updated_by_user = ?, " +
            "  updated_by_system = ? " +
            "  where status <> ? and " +
            "  seo_keyword_id = ? ";

        PreparedStatement stat = null;
        try {
            stat = pdb.prepareStatement(updateKeyword);
            for (SeoKeyword keyword : keywords) {
                int arg = 1;
                stat.clearParameters();
                stat.setString(arg++, (keyword.getStatus() != null ? keyword.getStatus().toString() : null));
                stat.setString(arg++, keyword.getKeyword());
                stat.setString(arg++, updatedBy);
                stat.setString(arg++, LocalHost.NAME);
                stat.setString(arg++, KeywordStatus.DELETED.toString());
                stat.setLong(arg++, keyword.getSeoKeywordId());
                logSqlStatement(prodInstId, stat);
                stat.execute();
            }
        }
        finally {
            close(stat);
        }
    }

    private List<SeoKeyword> querySeoKeywords(Connection conn, String prodInstId, long seoKeywordGroupId) throws SQLException {
        String querySeoKws =
            "select seo_keyword_group_id, " +
            " prod_inst_id, " +
            " seo_keyword_id, " +
            " keyword, " +
            " status, " +
            " updated_by_user, " +
            " updated_by_system " +
            "from seo_keyword " +
            " where prod_inst_id = ? " +
            " and seo_keyword_group_id = ? " +
            " and status <> ?";

        PreparedStatement stat = null;
        ResultSet rs = null;
        try {
            stat = conn.prepareStatement(querySeoKws);
            stat.setString(1, prodInstId);
            stat.setLong(2, seoKeywordGroupId);
            stat.setString(3, Status.DELETED.name());
            logSqlStatement(prodInstId, stat);
            rs = stat.executeQuery();
            List<SeoKeyword> keywords = new ArrayList<SeoKeyword>();
            while (rs.next()) {
                SeoKeyword keyword = new SeoKeyword();
                keyword.setSeoKeywordGroupId(rs.getLong("seo_keyword_group_id"));
                keyword.setProdInstId(rs.getString("prod_inst_id"));
                keyword.setSeoKeywordId(rs.getLong("seo_keyword_id"));
                keyword.setKeyword(rs.getString("keyword"));
                keyword.setStatus(KeywordStatus.valueOf(rs.getString("status")));
                keyword.setUpdatedByUser(rs.getString("updated_by_user"));
                keyword.setUpdatedBySystem(rs.getString("updated_by_system"));
                keywords.add(keyword);
            }
            return keywords;
        }
        finally {
            close(rs);
            close(stat);
        }
    }

    private List<SeoKeywordGroup> querySeoKeywordGroups(Connection conn, String prodInstId, Status status) throws SQLException {
        String querySeoKwGroups =
            "select seo_keyword_group_id, " +
            " prod_inst_id, " +
            " name, " +
            " status, " +
            " updated_by_user, " +
            " updated_by_system " +
            " from seo_keyword_group " +
            " where prod_inst_id = ? " +
            (status != null ? " and status = ?" : "");

        PreparedStatement stat = null;
        ResultSet rs = null;
        try {
            stat = conn.prepareStatement(querySeoKwGroups);
            stat.setString(1, prodInstId);
            if (status != null) {
                stat.setString(2, status.toString());
            }
            logSqlStatement(prodInstId, stat);
            rs = stat.executeQuery();
            List<SeoKeywordGroup> groups = new ArrayList<SeoKeywordGroup>();
            while (rs.next()) {
                SeoKeywordGroup group = new SeoKeywordGroup();
                group.setSeoKeywordGroupId(rs.getLong("seo_keyword_group_id"));
                group.setProdInstId(rs.getString("prod_inst_id"));
                group.setName(rs.getString("name"));
                String groupStatus = rs.getString("status");
                try {
                    group.setStatus(Status.valueOf(groupStatus));
                }
                catch (Exception ex) {}
                group.setUpdatedByUser(rs.getString("updated_by_user"));
                group.setUpdatedBySystem(rs.getString("updated_by_system"));
                groups.add(group);
            }
            return groups;
        }
        finally {
            close(rs);
            close(stat);
        }
    }

    private static final class ProductAndKeywordHelper implements Factory<Pair<String, String>> {
        public static final ProductAndKeywordHelper INSTANCE = new ProductAndKeywordHelper();

        /**
         * Constructor.
         */
        private ProductAndKeywordHelper() {
            return;
        }

        /**
         * Return a new instance with values from the result set.
         */
        public Pair<String, String> newInstance(ResultSet resultSet) throws SQLException {
            return Pair.from(resultSet.getString("prod_inst_id"), resultSet.getString("keyword"));
        }
    }

    public static void main(String[] args) {
        SeoKeywordsHelper seoKeywordsHelper = new SeoKeywordsHelper("");
//        List<SeoKeywordGroup> seoKeywordGroups = seoKeywordsHelper.getSEOKeywordGroups(
//                BaseHelper.createDevPdbConnection(), "WN.PP.33344444", Arrays.asList(Status.ACTIVE));
        SeoKeywordGroup kwg = new SeoKeywordGroup();
        kwg.setKeywords(null);
        kwg.setName("name");
        kwg.setProdInstId("WN.PP.33344444");
        kwg.setStatus(Status.ACTIVE);
        System.out.println(Arrays.asList(kwg).size());
        Connection connection = null;
        String logTag = null;
        try {
            connection = BaseHelper.createDevPdb1Connection();
            System.out.println(seoKeywordsHelper.getAllProductsAndSeoKeywords(logTag, connection));
            System.out.println(seoKeywordsHelper.getSeoKeywordGroupId(logTag, connection, "WN.DEV.BING.0001", "fdgfdg"));
            // seoKeywordsHelper.insertKeywordGroups(connection, Arrays.asList(kwg), "WN.PP.33344444", "jberruet");
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
        finally {
            BaseHelper.close(connection);
        }

        return;
    }
}
