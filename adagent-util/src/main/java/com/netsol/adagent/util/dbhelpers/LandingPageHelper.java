/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.dbhelpers;

import static com.netsol.adagent.util.beans.BaseData.stringIsBlank;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.logging.Log;

import com.netsol.adagent.util.TrackingUtil;
import com.netsol.adagent.util.codes.AdGroupStatus;
import com.netsol.adagent.util.codes.AdStatus;
import com.netsol.adagent.util.codes.CampaignAdExtensionStatus;
import com.netsol.adagent.util.codes.CampaignStatus;
import com.netsol.adagent.util.codes.KeywordStatus;
import com.netsol.adagent.util.tracking.ParamStripper;
import com.netsol.adagent.util.tracking.ParamTools;

public class LandingPageHelper extends BaseHelper {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:51 LandingPageHelper.java NSI";

    /**
     * Constructor.
     */
    public LandingPageHelper(String logComponent) {
        super(logComponent);
    }

    /**
     * Constructor.
     */
    public LandingPageHelper(Log logger) {
        super(logger);
    }

    /**
     * Return all active landing pages.
     */
    public Collection<String> getLandingPages(String logTag, Connection connection, String prodInstId) throws SQLException {
        Collection<String> landingPages = new HashSet<String>(); // Eliminate duplicates.
        for (String landingPage : getUniqueLandingPages(logTag, connection, prodInstId)) {
            if (stringIsBlank(landingPage)) {
                continue;
            }
            // Strip all ad parameters.
            for (ParamStripper paramStripper : ParamTools.getParamStrippers()) {
                landingPage = paramStripper.stripParams(landingPage);
            }
            if (stringIsBlank(landingPage)) {
                continue;
            }
            landingPages.add(TrackingUtil.lowercaseHostAndProtocol(landingPage));
        }
        return landingPages;
    }

    private List<String> getUniqueLandingPages(String logTag, Connection connection, String prodInstId) throws SQLException {
        final String SQL =
                "SELECT DISTINCT nsa.destination_url                           " +
                "FROM   ns_ad AS nsa                                           " +
                "       INNER JOIN ns_ad_group AS nsag                         " +
                "         ON nsag.prod_inst_id = nsa.prod_inst_id              " +
                "            AND nsag.ns_ad_group_id = nsa.ns_ad_group_id      " +
                "       INNER JOIN ns_campaign AS nsc                          " +
                "         ON nsc.prod_inst_id = nsag.prod_inst_id              " +
                "            AND nsc.ns_campaign_id = nsag.ns_campaign_id      " +
                "WHERE  nsa.`status` <> ?                                      " +
                "       AND nsag.`status` <> ?                                 " +
                "       AND nsc.`status` <> ?                                  " +
                "       AND nsa.prod_inst_id = ?                               " +
                "UNION DISTINCT                                                " +
                "SELECT DISTINCT nsk.dest_url                                  " +
                "FROM   ns_keyword AS nsk                                      " +
                "       INNER JOIN ns_ad_group AS nsag                         " +
                "         ON nsag.prod_inst_id = nsk.prod_inst_id              " +
                "            AND nsag.ns_ad_group_id = nsk.ns_ad_group_id      " +
                "       INNER JOIN ns_campaign AS nsc                          " +
                "         ON nsc.prod_inst_id = nsag.prod_inst_id              " +
                "            AND nsc.ns_campaign_id = nsag.ns_campaign_id      " +
                "WHERE  nsk.`status` <> ?                                      " +
                "       AND nsag.`status` <> ?                                 " +
                "       AND nsc.`status` <> ?                                  " +
                "       AND nsk.prod_inst_id = ?                               " +
                "UNION DISTINCT                                                " +
                "SELECT DISTINCT nss.destination_url                           " +
                "FROM   ns_sitelink AS nss                                     " +
                "       INNER JOIN ns_campaign_ad_sitelinks_extension AS nscase" +
                "         ON nscase.prod_inst_id = nss.prod_inst_id            " +
                "            AND nscase.ns_campaign_ad_sitelinks_extension_id =" +
                "                nss.ns_campaign_ad_sitelinks_extension_id     " +
                "       INNER JOIN ns_campaign AS nsc                          " +
                "         ON nsc.prod_inst_id = nscase.prod_inst_id            " +
                "            AND nsc.ns_campaign_id = nscase.ns_campaign_id    " +
                "WHERE  nscase.`status` <> ?                                   " +
                "       AND nsc.`status` <> ?                                  " +
                "       AND nss.prod_inst_id = ?                               " +
                "UNION DISTINCT                                                " +
                "SELECT DISTINCT nssc.destination_url                          " +
                "FROM   ns_superpages_campaign AS nssc                         " +
                "       INNER JOIN ns_campaign AS nsc                          " +
                "         ON nsc.prod_inst_id = nssc.prod_inst_id              " +
                "            AND nsc.ns_campaign_id = nssc.ns_campaign_id      " +
                "WHERE  nsc.`status` <> ?                                      " +
                "       AND nssc.prod_inst_id = ?;                             ";

        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(SQL);
            int parameterIndex = 1;
            statement.setString(parameterIndex++, AdStatus.DELETED);
            statement.setString(parameterIndex++, AdGroupStatus.DELETED);
            statement.setString(parameterIndex++, CampaignStatus.DELETED);
            statement.setString(parameterIndex++, prodInstId);
            statement.setString(parameterIndex++, KeywordStatus.DELETED);
            statement.setString(parameterIndex++, AdGroupStatus.DELETED);
            statement.setString(parameterIndex++, CampaignStatus.DELETED);
            statement.setString(parameterIndex++, prodInstId);
            statement.setString(parameterIndex++, CampaignAdExtensionStatus.DELETED);
            statement.setString(parameterIndex++, CampaignStatus.DELETED);
            statement.setString(parameterIndex++, prodInstId);
            statement.setString(parameterIndex++, CampaignStatus.DELETED);
            statement.setString(parameterIndex++, prodInstId);
            logSqlStatement(logTag, statement);
            resultSet = statement.executeQuery();
            return newList(resultSet, new StringFactory(1) {});
        }
        finally {
            close(statement, resultSet);
        }
    }
}
