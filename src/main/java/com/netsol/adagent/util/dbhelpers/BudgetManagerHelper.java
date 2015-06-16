/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.dbhelpers;

import static com.netsol.adagent.util.beans.BaseBudgetManagerData.getLogTag;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.apache.commons.logging.Log;

import com.netsol.adagent.util.BudgetManagerException;
import com.netsol.adagent.util.DateUtil;
import com.netsol.adagent.util.beans.Campaign;
import com.netsol.adagent.util.beans.CampaignList;
import com.netsol.adagent.util.beans.DebitableItem;
import com.netsol.adagent.util.beans.NsCampaign;
import com.netsol.adagent.util.beans.PPCAdClick;
import com.netsol.adagent.util.beans.PPCDebitableItem;
import com.netsol.adagent.util.beans.PPCLead;
import com.netsol.adagent.util.beans.Product;
import com.netsol.adagent.util.beans.SuperpagesClick;
import com.netsol.adagent.util.beans.VendorTimeZone;
import com.netsol.adagent.util.codes.AdGroupStatus;
import com.netsol.adagent.util.codes.CampaignStatus;
import com.netsol.adagent.util.codes.KeywordStatus;
import com.netsol.adagent.util.codes.LeadType;
import com.netsol.adagent.util.log.BaseLoggable;

public class BudgetManagerHelper extends BaseHelper {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/14/12 13:45:30 BudgetManagerHelper.java NSI";

    private static final String GET_BUDGET_FACTOR = "SELECT value FROM budget_factors WHERE channel_id = ? AND factor = ?";
    private static final String KEYWORD_CPC = "SELECT cost_per_click FROM vendor_keyword WHERE prod_inst_id = ? AND ns_keyword_id = ? AND cost_per_click != 0 ORDER BY update_date DESC limit 1";
    private static final String KEYWORD_BID = "SELECT ns_keyword.bid, ns_ad_group.max_cpc FROM ns_keyword INNER JOIN ns_ad_group ON ns_keyword.prod_inst_id=ns_ad_group.prod_inst_id and ns_keyword.ns_ad_group_id=ns_ad_group.ns_ad_group_id WHERE ns_keyword.prod_inst_id = ? AND ns_keyword_id = ?";
    private static final String AVERAGE_CPC = "SELECT AVG(cost_per_click) FROM vendor_keyword WHERE prod_inst_id = ? AND update_date BETWEEN ? AND ? AND cost_per_click != 0";
    private static final String DEBIT_CLICK_FROM_AD_GROUP = "INSERT INTO ns_ad_group_sum (prod_inst_id, ns_ad_group_id, update_date, ns_click_cost, vendor_click_cost, click_count) VALUES (?, ?, ?, ?, ?, 1) ON DUPLICATE KEY UPDATE ns_click_cost = ns_click_cost + VALUES(ns_click_cost), vendor_click_cost = vendor_click_cost + VALUES(vendor_click_cost), click_count = click_count + VALUES(click_count)";
    private static final String DEBIT_CLICK_FROM_KEYWORD = "INSERT INTO ns_keyword_sum (prod_inst_id, ns_keyword_id, update_date, ns_click_cost, vendor_click_cost, click_count) VALUES (?, ?, ?, ?, ?, 1) ON DUPLICATE KEY UPDATE ns_click_cost = ns_click_cost + VALUES(ns_click_cost), vendor_click_cost = vendor_click_cost + VALUES(vendor_click_cost), click_count = click_count + VALUES(click_count)";
    private static final String DEBIT_CLICK_FROM_AD = "INSERT INTO ns_ad_sum (prod_inst_id, ns_ad_id, update_date, ns_click_cost, vendor_click_cost, click_count) VALUES (?, ?, ?, ?, ?, 1) ON DUPLICATE KEY UPDATE ns_click_cost = ns_click_cost + VALUES(ns_click_cost), vendor_click_cost = vendor_click_cost + VALUES(vendor_click_cost), click_count = click_count + VALUES(click_count)";
    private static final String DEBIT_CLICK_FROM_CAMPAIGN = "INSERT INTO ns_campaign_sum (prod_inst_id, ns_campaign_id, update_date, ns_click_cost, vendor_click_cost, click_count, percent_of_budget) VALUES (?, ?, ?, ?, ?, 1, ?) ON DUPLICATE KEY UPDATE ns_click_cost = ns_click_cost + VALUES(ns_click_cost), vendor_click_cost = vendor_click_cost + VALUES(vendor_click_cost), click_count = click_count + VALUES(click_count)";
    private static final String DEBIT_CLICK_FROM_PRODUCT = "UPDATE product_sum SET daily_budget_remaining = daily_budget_remaining - ?, monthly_budget_remaining = monthly_budget_remaining - ?, vendor_click_cost = vendor_click_cost + ?, ns_click_cost = ns_click_cost + ?, click_count = click_count + 1 WHERE prod_inst_id = ? AND update_date = ?";
    private static final String DEBIT_LEAD_FROM_AD_GROUP = "INSERT INTO ns_ad_group_sum (prod_inst_id, ns_ad_group_id, update_date, total_lead_cost, total_lead_value, total_lead_count) VALUES (?, ?, ?, ?, ?, 1) ON DUPLICATE KEY UPDATE total_lead_cost = total_lead_cost + VALUES(total_lead_cost), total_lead_value = total_lead_value + VALUES(total_lead_value), total_lead_count = total_lead_count + VALUES(total_lead_count)";
    private static final String DEBIT_LEAD_FROM_KEYWORD = "INSERT INTO ns_keyword_sum (prod_inst_id, ns_keyword_id, update_date, total_lead_cost, total_lead_value, total_lead_count) VALUES (?, ?, ?, ?, ?, 1) ON DUPLICATE KEY UPDATE total_lead_cost = total_lead_cost + VALUES(total_lead_cost), total_lead_value = total_lead_value + VALUES(total_lead_value), total_lead_count = total_lead_count + VALUES(total_lead_count)";
    private static final String DEBIT_LEAD_FROM_AD = "INSERT INTO ns_ad_sum (prod_inst_id, ns_ad_id, update_date, total_lead_cost, total_lead_value, total_lead_count) VALUES (?, ?, ?, ?, ?, 1) ON DUPLICATE KEY UPDATE total_lead_cost = total_lead_cost + VALUES(total_lead_cost), total_lead_value = total_lead_value + VALUES(total_lead_value), total_lead_count = total_lead_count + VALUES(total_lead_count)";
    private static final String GET_BUDGET_REMAINING = "SELECT monthly_budget_remaining, daily_budget_remaining FROM product_sum WHERE prod_inst_id = ? AND update_date BETWEEN ? AND ? ORDER BY update_date DESC";
    private static final String GET_CAMPAIGN_BUDGET_REMAINING = "SELECT c.daily_budget - s.vendor_click_cost - s.ns_click_cost - s.total_lead_cost AS campaign_budget_remaining, c.daily_budget FROM ns_campaign c, ns_campaign_sum s WHERE s.prod_inst_id = ? AND s.ns_campaign_id = ? AND s.update_date = ? AND s.prod_inst_id = c.prod_inst_id AND s.ns_campaign_id = c.ns_campaign_id";
    private static final String GET_CALL_LEAD_DUPLE = "SELECT l.lead_id FROM call_leads_v l, (SELECT prod_inst_id, lead_id, tracking_number, call_src_number, lead_date FROM call_leads_v WHERE prod_inst_id = ? AND lead_id = ?) n WHERE l.prod_inst_id = n.prod_inst_id AND l.lead_id != n.lead_id AND l.tracking_number = n.tracking_number AND l.call_src_number = n.call_src_number AND l.lead_date BETWEEN ADDDATE(n.lead_date, INTERVAL -1 DAY) AND n.lead_date";
    private static final String GET_EMAIL_LEAD_DUPLE = "SELECT l.lead_id FROM email_leads_v l, (SELECT prod_inst_id, lead_id, lead_date, visit_id, sender FROM email_leads_v WHERE prod_inst_id = ? AND lead_id = ?) n WHERE l.prod_inst_id = n.prod_inst_id AND l.lead_id != n.lead_id AND l.visit_id = n.visit_id AND l.sender = n.sender AND l.lead_date BETWEEN ADDDATE(n.lead_date, INTERVAL -30 MINUTE) AND n.lead_date";
    private static final String GET_FORM_LEAD_DUPLE = "SELECT l.lead_id FROM form_leads_v l, (SELECT prod_inst_id, lead_id, lead_date, visit_id, ip FROM form_leads_v WHERE prod_inst_id = ? AND lead_id = ?) n WHERE l.prod_inst_id = n.prod_inst_id AND l.lead_id != n.lead_id AND l.visit_id = n.visit_id AND l.ip = n.ip AND l.lead_date BETWEEN ADDDATE(n.lead_date, INTERVAL -30 MINUTE) AND n.lead_date";
    private static final String GET_PAGE_LOAD_LEAD_DUPLE = "SELECT l.lead_id FROM page_load_leads_v l, (SELECT prod_inst_id, lead_id, lead_date, visit_id, page_name FROM page_load_leads_v WHERE prod_inst_id = ? AND lead_id = ?) n WHERE l.prod_inst_id = n.prod_inst_id AND l.lead_id != n.lead_id AND l.visit_id = n.visit_id AND l.page_name = n.page_name AND l.lead_date BETWEEN ADDDATE(n.lead_date, INTERVAL -30 MINUTE) AND n.lead_date";
    private static final String GET_PHONE_CALL_DURATION = "SELECT call_duration FROM call_leads_v WHERE prod_inst_id = ? AND lead_id = ?";
    private static final String GET_CURRENT_CONVERSION_RATE = "SELECT AVG(total_lead_count/click_count) AS conv_rate FROM product_sum WHERE prod_inst_id = ? AND update_date BETWEEN ? AND ?";
    private static final String GET_TIER_VALUES = "SELECT tier, multiplier FROM tier_config WHERE channel_id = ? and max_value >= ? ORDER BY tier ASC LIMIT 1;";
    private static final String GET_CPC_MARKUP = "SELECT cpc.default_markup, cpc.cpc_sensitive_markup FROM cpc_markup_config cpc, product p WHERE cpc.channel_id = ? AND cpc.max_value >= p.base_target AND p.prod_inst_id = ? ORDER BY max_value ASC LIMIT 1";
    private static final String GET_CAMPAIGN_DAILY_BUDGETS_TOTAL = "SELECT CAST(IFNULL(SUM(c.daily_budget), p.current_target/30.0) AS DECIMAL(10,2)) daily_budget FROM product p, ns_campaign c WHERE p.prod_inst_id = c.prod_inst_id AND p.prod_inst_id = ? AND c.status IN (?, ?)";
    private static final String GET_CAMPAIGN_BUDGET_BY_AD_GROUP = "SELECT c.daily_budget FROM ns_campaign c, ns_ad_group ag WHERE ag.prod_inst_id = ? AND ag.ns_ad_group_id = ? AND ag.prod_inst_id = c.prod_inst_id AND ag.ns_campaign_id = c.ns_campaign_id";
    private static final String GET_ACTIVE_AD_GROUP_COUNT = "SELECT count(*) ad_group_count FROM ns_ad_group WHERE prod_inst_id = ? AND status IN (?, ?)";
    private static final String GET_ACTIVE_KEYWORD_COUNT = "SELECT count(*) keyword_count FROM ns_keyword WHERE prod_inst_id = ? AND ns_ad_group_id = ? AND status = ?";
    private static final String GET_MAX_LEAD_COST = "SELECT pr.multiplier * MAX(bt.price) max_lead_cost FROM product_pricing pr, base_tier bt WHERE pr.prod_inst_id = ?";
    private static final String GET_AD_GROUP_TARGET_CONVERSION_RATE = "SELECT target_conv_rate, max_cpc, max_content_cpc, max_cpm FROM ns_ad_group WHERE prod_inst_id = ? AND ns_ad_group_id = ?";
    private static final String UPDATE_BIDCAP_FOR_AD_GROUP = "UPDATE ns_keyword SET bid_cap = ?, updated_date = NOW(), updated_by_user = 'Budget Manager', updated_by_system = 'BM.calculateBidCap' WHERE prod_inst_id = ? AND ns_ad_group_id = ? AND status = ?";
    private static final String GET_VENDOR_SPEND_AGGRESSIVENESS_FOR_PRODUCT = "SELECT spend_aggressiveness FROM ns_campaign WHERE prod_inst_id = ? AND vendor_id = ? LIMIT  1;";

    private static final String TMP_DEBIT_COUNT = " + 1, ";
    private static final String TMP_DEBIT_COST = " + ?, ";
    private static final String TMP_DEBIT_VALUE = " + ? WHERE prod_inst_id = ? AND update_date = ?";

    /**
     * Constructor.
     */
    public BudgetManagerHelper(String logComponent) {
        super(logComponent);
    }

    /**
     * Constructor.
     */
    public BudgetManagerHelper(Log logger) {
        super(logger);
    }

    /**
     * Constructor.
     */
    public BudgetManagerHelper(BaseLoggable baseLoggable) {
        super(baseLoggable);
    }

    public double getGlobalAverageCPC(Connection conn, Integer channelId) throws BudgetManagerException {
        return getBudgetFactor(conn, channelId, "global_avg_cpc");
    }

    public double getDefaultConversionRate(Connection conn, Integer channelId) throws BudgetManagerException {
        return getBudgetFactor(conn, channelId, "default_conversion_rate");
    }

    public double getMaxConversionRate(Connection conn, Integer channelId) throws BudgetManagerException {
        return getBudgetFactor(conn, channelId, "max_conv_rate");
    }

    public double getMaxCpcRatio(Connection conn, Integer channelId) throws BudgetManagerException {
        return getBudgetFactor(conn, channelId, "max_cpc_ratio");
    }

    public double getCostForClick(Connection conn, PPCAdClick click) throws BudgetManagerException {
        String logTag = getLogTag(click.getProdInstId());
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        Calendar oneMonthAgo = Calendar.getInstance();
        oneMonthAgo.setTimeInMillis(click.getDate().getTime());
        oneMonthAgo.add(Calendar.MONTH, -1);

        try {
            pstmt = conn.prepareStatement(KEYWORD_CPC);
            pstmt.setString(1, click.getProdInstId());
            pstmt.setLong(2, click.getNsKeywordId());
            logInfo(logTag, "getCostForClick -> About to execute KEYWORD_CPC:");
            logSqlStatement(logTag, pstmt);
            rs = pstmt.executeQuery();

            if (rs != null) {
                if (rs.next()) {
                    // return most recent CPC
                    logInfo(logTag, "getCostForClick -> Found CPC: " + rs.getDouble(1));
                    return rs.getDouble(1);
                } else {
                    // no results from first query, default to secondary method (get bid)
                    logInfo(logTag, "getCostForClick -> No CPC found for " + click.getProdInstId() + ">Keyword" + click.getNsKeywordId() + ".");
                    pstmt = conn.prepareStatement(KEYWORD_BID);
                    pstmt.setString(1, click.getProdInstId());
                    pstmt.setLong(2, click.getNsKeywordId());
                    logInfo(logTag, "getCostForClick -> About to execute KEYWORD_BID:");
                    logSqlStatement(logTag, pstmt);
                    rs = pstmt.executeQuery();

                    if (rs != null) {
                        if (rs.next()) {
                            // return bid
                            logInfo(logTag, "getCostForClick -> Found bid: " + rs.getDouble(1));
                            double bid = rs.getDouble(1);
                            if (bid == 0d) {
                            	bid = rs.getDouble(2); // If the keyword bid is 0, get the ad group max CPC.
                            }
                            return bid;
                        } else {
                            // this shouldn't happen, but if it does, BudgetManager will use default CPC.
                            logWarning(logTag, "getCostForClick -> How did you get here?");
                            return 0.0;
                        }
                    } else {
                        // DB failure, but reconciliation will fix this. Returning 0.0 will cause BudgetManager to use default CPC.
                        logWarning(logTag, "getCostForClick -> DB error, returning zero value...");
                        return 0.0;
                    }
                }
            } else {
                // DB failure, but reconciliation will fix this. Returning 0.0 will cause BudgetManager to use default CPC.
                logWarning(logTag, "getCostForClick -> DB error, returning zero value (outside)...");
                return 0.0;
            }
        } catch (SQLException sqle) {
            logSqlException(logTag, sqle);
            throw new BudgetManagerException(BudgetManagerException.DATABASE_ERROR, "Error occurred accessing DB during getCost for click!", ""+sqle, sqle);
        } catch (Exception e) {
            logError(logTag, e);
            throw new BudgetManagerException(BudgetManagerException.UNKNOWN_ERROR, "Unexpected Error occurred during getCost for click!", ""+e, e);
        } finally {
            close(pstmt, rs);
        }
    }

    // TODO no longer being used
    public double getRecentAverageCpcForProduct(Connection conn, PPCDebitableItem item) throws BudgetManagerException {
        String logTag = getLogTag(item.getProdInstId());
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        Calendar oneWeekAgo = Calendar.getInstance();
        oneWeekAgo.setTimeInMillis(item.getDate().getTime());
        oneWeekAgo.add(Calendar.WEEK_OF_YEAR, -1);

        try {
            pstmt = conn.prepareStatement(AVERAGE_CPC);
            pstmt.setString(1, item.getProdInstId());
            pstmt.setDate(2, new Date(oneWeekAgo.getTimeInMillis()));
            pstmt.setDate(3, new Date(item.getDate().getTime()));
            logInfo(logTag, "getRecentAverageCpcForProduct -> About to execute AVERAGE_CPC:");
            logSqlStatement(logTag, pstmt);
            rs = pstmt.executeQuery();

            if (rs != null) {
                if (rs.next()) {
                    // found it!
                    logInfo(logTag, "getRecentAverageCpcForProduct -> AVERAGE_CPC found for " + item.getProdInstId() + ": " + rs.getDouble(1));
                    return rs.getDouble(1);
                } else {
                    // no row present, send back 1.0 so it does not affect anything
                    logWarning(logTag, "getRecentAverageCpcForProduct -> NO AVERAGE_CPC found for " + item.getProdInstId());
                    return 1.0;
                }
            } else {
                logError(logTag, "getRecentAverageCpcForProduct -> Null result set returned during needAdjustForDay!");
                throw new BudgetManagerException(BudgetManagerException.DATABASE_ERROR, "Null result set returned during needAdjustForDay!");
            }
        } catch (BudgetManagerException bme) {
            throw bme;
        } catch (SQLException sqle) {
            logSqlException(logTag, sqle);
            throw new BudgetManagerException(BudgetManagerException.DATABASE_ERROR, "Error occurred accessing DB during getRecentAverageCpcForProduct!", ""+sqle, sqle);
        } catch (Exception e) {
            logError(logTag, e);
            throw new BudgetManagerException(BudgetManagerException.UNKNOWN_ERROR, "Unexpected Error occurred during getRecentAverageCpcForProduct!", ""+e, e);
        } finally {
            close(pstmt, rs);
        }
    }

    // Get the average cost per click over the last 30 days that the campaign had clicks.
    private static final String GET_SUPERPAGES_CAMPAIGN_CPC =
    	"select sum(clicks * cost_per_click)/sum(clicks)  from " +
    	"(select vccg.clicks, vccg.cost_per_click from vendor_campaign_category_geography vccg " +
    	"inner join ns_campaign_category ncc on ncc.ns_campaign_category_id = vccg.ns_campaign_category_id " +
    	"where ncc.prod_inst_id=? and ncc.ns_campaign_id=? order by update_date desc limit 30) as t;";

    // Get the average cost per click over the last 30 days that the product had clicks.
    private static final String GET_SUPERPAGES_PRODUCT_CPC =
    	"select sum(clicks * cost_per_click)/sum(clicks)  from " +
    	"(select vccg.clicks, vccg.cost_per_click from vendor_campaign_category_geography vccg " +
    	"inner join ns_campaign_category ncc on ncc.ns_campaign_category_id = vccg.ns_campaign_category_id " +
    	"where ncc.prod_inst_id=? order by update_date desc limit 30) as t;";

    // Get the average bid amount for a campaign's active categories.
    private static final String GET_SUPERPAGES_ACTIVE_CAMPAIGN_BID =
    	"select avg(bid) from ns_campaign_category where prod_inst_id=? and ns_campaign_id=? and status = 'ACTIVE'";

    // Get the average bid amount for a campaign including deleted categories.
    private static final String GET_SUPERPAGES_NON_ACTIVE_CAMPAIGN_BID =
    	"select avg(bid) from ns_campaign_category where prod_inst_id=? and ns_campaign_id=?;";

    /**
     * Get the cost for a Superpages click.
     *
     * @param pdbConn
     * @param click
     * @return
     * @throws BudgetManagerException
     */
    public double getSuperpagesCostForClick(Connection pdbConn, SuperpagesClick click) throws BudgetManagerException {
        String logTag = getLogTag(click.getProdInstId());
        PreparedStatement pstmt = null;
        try {
        	// There are four layers of Superpages click cost estimation. If one layer returns no data, we proceed to the next layer. The last layer should always return data.
        	// 1. Get the average cost per click over the last 30 days that the campaign had clicks.
        	// 2. Get the average cost per click over the last 30 days that the product had clicks.
        	// 3. Get the average bid amount for a campaign's active categories.
        	// 4. Get the average bid amount for a campaign including deleted categories.

        	// 1. Look for campaign click data.
        	double cost = 0;
        	pstmt = pdbConn.prepareStatement(GET_SUPERPAGES_CAMPAIGN_CPC);
            pstmt.setString(1, click.getProdInstId());
            pstmt.setLong(2, click.getNsCampaignId());
            logInfo(logTag, "getSuperpagesCostForClick: About to execute GET_SUPERPAGES_CAMPAIGN_CPC:");
            logSqlStatement(logTag, pstmt);
            ResultSet results = pstmt.executeQuery();
            if (results != null && results.next() && results.getDouble(1) != 0d) {
            	cost = results.getDouble(1);
            }
            else {
            	// 2. Look for product click data.
            	close(pstmt, results);
            	pstmt = pdbConn.prepareStatement(GET_SUPERPAGES_PRODUCT_CPC);
                pstmt.setString(1, click.getProdInstId());
                logInfo(logTag, "getSuperpagesCostForClick: About to execute GET_SUPERPAGES_PRODUCT_CPC:");
                logSqlStatement(logTag, pstmt);
                results = pstmt.executeQuery();
                if (results != null && results.next() && results.getDouble(1) != 0d) {
                	cost = results.getDouble(1);
                }
                else {
                	// 3. Look for active category bid data.
                	close(pstmt, results);
                	pstmt = pdbConn.prepareStatement(GET_SUPERPAGES_ACTIVE_CAMPAIGN_BID);
                    pstmt.setString(1, click.getProdInstId());
                    pstmt.setLong(2, click.getNsCampaignId());
                    logInfo(logTag, "getSuperpagesCostForClick: About to execute GET_SUPERPAGES_ACTIVE_CAMPAIGN_BID:");
                    logSqlStatement(logTag, pstmt);
                    results = pstmt.executeQuery();
                    if (results != null && results.next() && results.getDouble(1) != 0d) {
                    	cost = results.getDouble(1);
                    }
                    else {
                    	// 4. Look for non-active category bid data.
                    	close(pstmt, results);
                    	pstmt = pdbConn.prepareStatement(GET_SUPERPAGES_NON_ACTIVE_CAMPAIGN_BID);
                        pstmt.setString(1, click.getProdInstId());
                        pstmt.setLong(2, click.getNsCampaignId());
                        logInfo(logTag, "getSuperpagesCostForClick: About to execute GET_SUPERPAGES_NON_ACTIVE_CAMPAIGN_BID:");
                        logSqlStatement(logTag, pstmt);
                        results = pstmt.executeQuery();
                        if (results != null && results.next() && results.getDouble(1) != 0d) {
                        	cost = results.getDouble(1);
                        }
                        else {
                        	// Something went wrong... somehow a Superpages campaign became active without having any categories, which should not be possible.
                        	// In this case, just let the cost be zero and log a warning since it should never happen.
                        	logWarning(logTag, "getSuperpagesCostForClick: Unable to estimate click cost for click:" + click);
                        }
                    }
                }
            }
        	return cost;
        } catch (Exception e) {
            logError(logTag, e);
            throw new BudgetManagerException(BudgetManagerException.UNKNOWN_ERROR, "Unexpected Error occurred during getSuperpagesCostForClick.", e.getMessage(), e);
        } finally {
            close(pstmt);
        }
    }

    public boolean debitClickFromAdGroup(Connection conn, PPCAdClick click) throws BudgetManagerException {
        String logTag = getLogTag(click.getProdInstId());
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            pstmt = conn.prepareStatement(DEBIT_CLICK_FROM_AD_GROUP);
            pstmt.setString(1, click.getProdInstId());
            pstmt.setLong(2, click.getNsAdGroupId());
            pstmt.setDate(3, new Date(click.getDate().getTime()));
            pstmt.setDouble(4, click.getMarkup());
            pstmt.setDouble(5, click.getBaseCost());
            logInfo(logTag, "debitClickFromAdGroup -> About to execute DEBIT_CLICK_FROM_AD_GROUP:");
            logSqlStatement(logTag, pstmt);

            if (pstmt.executeUpdate() > 0) {
                logInfo(logTag, "debitClickFromAdGroup -> Debit successful!");
                return true;
            } else {
                logInfo(logTag, "debitClickFromAdGroup -> Debit failed!");
                return false;
            }
        } catch (SQLException sqle) {
            logSqlException(logTag, sqle);
            throw new BudgetManagerException(BudgetManagerException.DATABASE_ERROR, "Error occurred accessing DB during debitClickFromAdGroup!", ""+sqle, sqle);
        } catch (Exception e) {
            logError(logTag, e);
            throw new BudgetManagerException(BudgetManagerException.UNKNOWN_ERROR, "Unexpected Error occurred during debitClickFromAdGroup!", ""+e, e);
        } finally {
            close(pstmt, rs);
        }
    }

    public boolean debitClickFromKeyword(Connection conn, PPCAdClick click) throws BudgetManagerException {
        String logTag = getLogTag(click.getProdInstId());
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            pstmt = conn.prepareStatement(DEBIT_CLICK_FROM_KEYWORD);
            pstmt.setString(1, click.getProdInstId());
            pstmt.setLong(2, click.getNsKeywordId());
            pstmt.setDate(3, new Date(click.getDate().getTime()));
            pstmt.setDouble(4, click.getMarkup());
            pstmt.setDouble(5, click.getBaseCost());
            logInfo(logTag, "debitClickFromKeyword -> About to execute DEBIT_CLICK_FROM_KEYWORD:");
            logSqlStatement(logTag, pstmt);

            if (pstmt.executeUpdate() > 0) {
                logInfo(logTag, "debitClickFromKeyword -> Debit successful!");
                return true;
            } else {
                logInfo(logTag, "debitClickFromKeyword -> Debit failed!");
                return false;
            }
        } catch (SQLException sqle) {
            logSqlException(logTag, sqle);
            throw new BudgetManagerException(BudgetManagerException.DATABASE_ERROR, "Error occurred accessing DB during debitClickFromKeyword!", ""+sqle, sqle);
        } catch (Exception e) {
            logError(logTag, e);
            throw new BudgetManagerException(BudgetManagerException.UNKNOWN_ERROR, "Unexpected Error occurred during debitClickFromKeyword!", ""+e, e);
        } finally {
            close(pstmt, rs);
        }
    }

    public boolean debitClickFromAd(Connection conn, PPCAdClick click) throws BudgetManagerException {
        String logTag = getLogTag(click.getProdInstId());
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            pstmt = conn.prepareStatement(DEBIT_CLICK_FROM_AD);
            pstmt.setString(1, click.getProdInstId());
            pstmt.setLong(2, click.getNsAdId());
            pstmt.setDate(3, new Date(click.getDate().getTime()));
            pstmt.setDouble(4, click.getMarkup());
            pstmt.setDouble(5, click.getBaseCost());
            logInfo(logTag, "debitClickFromAd -> About to execute DEBIT_CLICK_FROM_AD:");
            logSqlStatement(logTag, pstmt);

            if (pstmt.executeUpdate() > 0) {
                logInfo(logTag, "debitClickFromAd -> Debit successful!");
                return true;
            } else {
                logInfo(logTag, "debitClickFromAd -> Debit failed!");
                return false;
            }
        } catch (SQLException sqle) {
            logSqlException(logTag, sqle);
            throw new BudgetManagerException(BudgetManagerException.DATABASE_ERROR, "Error occurred accessing DB during debitClickFromAd!", ""+sqle, sqle);
        } catch (Exception e) {
            logError(logTag, e);
            throw new BudgetManagerException(BudgetManagerException.UNKNOWN_ERROR, "Unexpected Error occurred during debitClickFromAd!", ""+e, e);
        } finally {
            close(pstmt, rs);
        }
    }

    public boolean debitClickFromCampaign(Connection conn, PPCAdClick click) throws BudgetManagerException {
        String logTag = getLogTag(click.getProdInstId());
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            String prodInstId = click.getProdInstId();
            long nsCampaignId = click.getNsCampaignId();
            if (nsCampaignId > 0) {
                // Query the campaign to get the percent_of_budget in case this is an insert.
                CampaignList campaignList = getSingleCampaignInfo(conn, prodInstId, nsCampaignId);
                if (campaignList.getCampaigns().size() <= 0) {
                    logError(logTag, "Could not find campaign " + nsCampaignId + " for " + prodInstId);
                    return true;
                }
                Campaign campaign = campaignList.getCampaigns().get(0);

                pstmt = conn.prepareStatement(DEBIT_CLICK_FROM_CAMPAIGN);
                pstmt.setString(1, prodInstId);
                pstmt.setLong(2, nsCampaignId);
                pstmt.setDate(3, new Date(click.getDate().getTime()));
                pstmt.setDouble(4, click.getMarkup());
                pstmt.setDouble(5, click.getBaseCost());
                pstmt.setDouble(6, campaign.getPercentOfBudget());
                logInfo(logTag, "debitClickFromCampaign -> About to execute DEBIT_CLICK_FROM_CAMPAIGN:");
                logSqlStatement(logTag, pstmt);

                if (pstmt.executeUpdate() > 0) {
                    logInfo(logTag, "debitClickFromCampaign -> Debit successful!");
                    return true;
                } else {
                    logInfo(logTag, "debitClickFromCampaign -> Debit failed!");
                    return false;
                }
            }
            else {
                // No campaign.
                logWarning(logTag, "debitClickFromCampaign called with no campaign: " + nsCampaignId + " for " + prodInstId);
                return true;
            }
        } catch (SQLException sqle) {
            logSqlException(logTag, sqle);
            throw new BudgetManagerException(BudgetManagerException.DATABASE_ERROR, "Error occurred accessing DB during debitClickFromCampaign!", ""+sqle, sqle);
        } catch (Exception e) {
            logError(logTag, e);
            throw new BudgetManagerException(BudgetManagerException.UNKNOWN_ERROR, "Unexpected Error occurred during debitClickFromCampaign!", ""+e, e);
        } finally {
            close(pstmt, rs);
        }
    }

    public boolean debitClickFromProduct(Connection conn, PPCAdClick click) throws BudgetManagerException {
        String logTag = getLogTag(click.getProdInstId());
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            pstmt = conn.prepareStatement(DEBIT_CLICK_FROM_PRODUCT);
            pstmt.setDouble(1, click.getFullCost());
            pstmt.setDouble(2, click.getFullCost());
            pstmt.setDouble(3, click.getBaseCost());
            // TR70052 - Markup is the dollar amount.
            pstmt.setDouble(4, click.getMarkup());
            pstmt.setString(5, click.getProdInstId());
            pstmt.setDate(6, new Date(click.getDate().getTime()));
            logInfo(logTag, "debitClickFromProduct -> About to execute DEBIT_CLICK_FROM_PRODUCT:");
            logSqlStatement(logTag, pstmt);

            if (pstmt.executeUpdate() > 0) {
                logInfo(logTag, "debitClickFromProduct -> Debit successful!");
                return true;
            } else {
                logInfo(logTag, "debitClickFromProduct -> Debit failed!");
                return false;
            }
        } catch (SQLException sqle) {
            logSqlException(logTag, sqle);
            throw new BudgetManagerException(BudgetManagerException.DATABASE_ERROR, "Error occurred accessing DB during debitClickFromProduct!", ""+sqle, sqle);
        } catch (Exception e) {
            logError(logTag, e);
            throw new BudgetManagerException(BudgetManagerException.UNKNOWN_ERROR, "Unexpected Error occurred during debitClickFromProduct!", ""+e, e);
        } finally {
            close(pstmt, rs);
        }
    }

    public boolean debitLeadFromAdGroup(Connection conn, PPCLead lead) throws BudgetManagerException {
        String logTag = getLogTag(lead.getProdInstId());
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            pstmt = conn.prepareStatement(DEBIT_LEAD_FROM_AD_GROUP);
            pstmt.setString(1, lead.getProdInstId());
            pstmt.setLong(2, lead.getNsAdGroupId());
            pstmt.setDate(3, new Date(lead.getDate().getTime()));
            pstmt.setDouble(4, lead.getFullCost());
            pstmt.setDouble(5, lead.getRating());
            logInfo(logTag, "debitLeadFromAdGroup -> About to execute DEBIT_LEAD_FROM_AD_GROUP:");
            logSqlStatement(logTag, pstmt);

            if (pstmt.executeUpdate() > 0) {
                logInfo(logTag, "debitLeadFromAdGroup -> Debit successful!");
                return true;
            } else {
                logInfo(logTag, "debitLeadFromAdGroup -> Debit failed!");
                return false;
            }
        } catch (SQLException sqle) {
            logSqlException(logTag, sqle);
            throw new BudgetManagerException(BudgetManagerException.DATABASE_ERROR, "Error occurred accessing DB during debitLeadFromAdGroup!", ""+sqle, sqle);
        } catch (Exception e) {
            logError(logTag, e);
            throw new BudgetManagerException(BudgetManagerException.UNKNOWN_ERROR, "Unexpected Error occurred during debitLeadFromAdGroup!", ""+e, e);
        } finally {
            close(pstmt, rs);
        }
    }

    public boolean debitLeadFromKeyword(Connection conn, PPCLead lead) throws BudgetManagerException {
        String logTag = getLogTag(lead.getProdInstId());
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            pstmt = conn.prepareStatement(DEBIT_LEAD_FROM_KEYWORD);
            pstmt.setString(1, lead.getProdInstId());
            pstmt.setLong(2, lead.getNsKeywordId());
            pstmt.setDate(3, new Date(lead.getDate().getTime()));
            pstmt.setDouble(4, lead.getFullCost());
            pstmt.setDouble(5, lead.getRating());
            logInfo(logTag, "debitLeadFromKeyword -> About to execute DEBIT_LEAD_FROM_KEYWORD:");
            logSqlStatement(logTag, pstmt);

            if (pstmt.executeUpdate() > 0) {
                logInfo(logTag, "debitLeadFromKeyword -> Debit successful!");
                return true;
            } else {
                logInfo(logTag, "debitLeadFromKeyword -> Debit failed!");
                return false;
            }
        } catch (SQLException sqle) {
            logSqlException(logTag, sqle);
            throw new BudgetManagerException(BudgetManagerException.DATABASE_ERROR, "Error occurred accessing DB during debitLeadFromKeyword!", ""+sqle, sqle);
        } catch (Exception e) {
            logError(logTag, e);
            throw new BudgetManagerException(BudgetManagerException.UNKNOWN_ERROR, "Unexpected Error occurred during debitLeadFromKeyword!", ""+e, e);
        } finally {
            close(pstmt, rs);
        }
    }

    public boolean debitLeadFromAd(Connection conn, PPCLead lead) throws BudgetManagerException {
        String logTag = getLogTag(lead.getProdInstId());
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            // INSERT INTO ns_ad_sum (prod_inst_id, ns_ad_id, update_date, total_lead_cost, total_lead_value, total_lead_count) VALUES (?, ?, ?, ?, ?, 1)
            pstmt = conn.prepareStatement(DEBIT_LEAD_FROM_AD);
            pstmt.setString(1, lead.getProdInstId());
            pstmt.setLong(2, lead.getNsAdId());
            pstmt.setDate(3, new Date(lead.getDate().getTime()));
            pstmt.setDouble(4, lead.getFullCost());
            pstmt.setDouble(5, lead.getRating());
            logInfo(logTag, "debitLeadFromAd -> About to execute DEBIT_LEAD_FROM_AD:");
            logSqlStatement(logTag, pstmt);

            if (pstmt.executeUpdate() > 0) {
                logInfo(logTag, "debitLeadFromAd -> Debit successful!");
                return true;
            } else {
                logInfo(logTag, "debitLeadFromAd -> Debit failed!");
                return false;
            }
        } catch (SQLException sqle) {
            logSqlException(logTag, sqle);
            throw new BudgetManagerException(BudgetManagerException.DATABASE_ERROR, "Error occurred accessing DB during debitLeadFromAd!", ""+sqle, sqle);
        } catch (Exception e) {
            logError(logTag, e);
            throw new BudgetManagerException(BudgetManagerException.UNKNOWN_ERROR, "Unexpected Error occurred during debitLeadFromAd!", ""+e, e);
        } finally {
            close(pstmt, rs);
        }
    }

    public boolean debitLeadFromCampaign(Connection conn, PPCLead lead) throws BudgetManagerException {
        String logTag = getLogTag(lead.getProdInstId());

        String prodInstId = lead.getProdInstId();
        long nsCampaignId = lead.getNsCampaignId();
        if (nsCampaignId > 0) {
            // Query the campaign to get the percent_of_budget in case this is an insert.
            CampaignList campaignList = getSingleCampaignInfo(conn, prodInstId, nsCampaignId);
            if (campaignList.getCampaigns().size() <= 0) {
                logError(logTag, "Could not find campaign " + nsCampaignId + " for " + prodInstId);
                return true;
            }
            Campaign campaign = campaignList.getCampaigns().get(0);

            String countField = lead.getPrefix().getPrefix() + "count";
            String costField = lead.getPrefix().getPrefix() + "cost";
            String valueField = lead.getPrefix().getPrefix() + "value";

            String sql = "INSERT INTO ns_campaign_sum (prod_inst_id, ns_campaign_id, update_date, percent_of_budget, "
                + "total_lead_count, total_lead_cost, total_lead_value, "
                + countField + ", " + costField + ", " + valueField + ") "
                + "VALUES (?, ?, ?, ?, 1, ?, ?, 1, ?, ?) "
                + "ON DUPLICATE KEY UPDATE total_lead_count = total_lead_count + 1, "
                + "total_lead_cost = total_lead_cost + VALUES(" + costField + "), "
                + "total_lead_value = total_lead_value + VALUES(" + valueField + "), "
                + countField + " = " + countField + " + 1, "
                + costField + " = " + costField + " + VALUES(" + costField + "), "
                + valueField + " = " + valueField + " + VALUES(" + valueField + ")";

            PreparedStatement pstmt = null;
            ResultSet rs = null;

            try {
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, lead.getProdInstId());
                pstmt.setLong(2, lead.getNsCampaignId());
                pstmt.setDate(3, new java.sql.Date(lead.getDate().getTime()));
                pstmt.setDouble(4, campaign.getPercentOfBudget());
                pstmt.setDouble(5, lead.getFullCost());
                pstmt.setInt(6, lead.getRating());
                pstmt.setDouble(7, lead.getFullCost());
                pstmt.setInt(8, lead.getRating());

                logInfo(logTag, "debitLeadFromCampaign -> About to execute DEBIT_LEAD_FROM_CAMPAIGN:");
                logSqlStatement(logTag, pstmt);

                if (pstmt.executeUpdate() > 0) {
                    logInfo(logTag, "debitLeadFromCampaign -> Debit successful!");
                    return true;
                } else {
                    logInfo(logTag, "debitLeadFromCampaign -> Debit failed!");
                    return false;
                }
            } catch (SQLException sqle) {
                logSqlException(logTag, sqle);
                throw new BudgetManagerException(BudgetManagerException.DATABASE_ERROR, "Error occurred accessing DB during debitLeadFromCampaign!", ""+sqle, sqle);
            } catch (Exception e) {
                logError(logTag, e);
                throw new BudgetManagerException(BudgetManagerException.UNKNOWN_ERROR, "Unexpected Error occurred during debitLeadFromCampaign!", ""+e, e);
            } finally {
                close(pstmt, rs);
            }
        }
        else {
            // No campaign.
            logWarning(logTag, "debitLeadFromCampaign called with no campaign: " + nsCampaignId + " for " + prodInstId);
            return true;
        }
    }

    public boolean debitLeadFromProduct(Connection conn, PPCLead lead) throws BudgetManagerException {
        String logTag = getLogTag(lead.getProdInstId());
        StringBuilder debitLeadFromProductSql =
            new StringBuilder("UPDATE product_sum SET daily_budget_remaining = daily_budget_remaining - ?, monthly_budget_remaining = monthly_budget_remaining - ?, "
                + "total_lead_count = total_lead_count + 1, total_lead_cost = total_lead_cost + ?, total_lead_value = total_lead_value + ?, ");
        debitLeadFromProductSql.append(lead.getPrefix().getPrefix()).append("count = ").append(lead.getPrefix().getPrefix()).append("count").append(TMP_DEBIT_COUNT);
        debitLeadFromProductSql.append(lead.getPrefix().getPrefix()).append("cost = ").append(lead.getPrefix().getPrefix()).append("cost").append(TMP_DEBIT_COST);
        debitLeadFromProductSql.append(lead.getPrefix().getPrefix()).append("value = ").append(lead.getPrefix().getPrefix()).append("value").append(TMP_DEBIT_VALUE);

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            pstmt = conn.prepareStatement(debitLeadFromProductSql.toString());
            pstmt.setDouble(1, lead.getFullCost());
            pstmt.setDouble(2, lead.getFullCost());
            pstmt.setDouble(3, lead.getFullCost());
            pstmt.setDouble(4, lead.getRating());
            pstmt.setDouble(5, lead.getFullCost());
            pstmt.setDouble(6, lead.getRating());
            pstmt.setString(7, lead.getProdInstId());
            pstmt.setDate(8, new java.sql.Date(lead.getDate().getTime()));
            logInfo(logTag, "debitLeadFromAccount -> About to execute DEBIT_LEAD_FROM_PRODUCT:");
            logSqlStatement(logTag, pstmt);

            if (pstmt.executeUpdate() > 0) {
                logInfo(logTag, "debitLeadFromAccount -> Debit successful!");
                return true;
            } else {
                logInfo(logTag, "debitLeadFromAccount -> Debit failed!");
                return false;
            }
        } catch (SQLException sqle) {
            logSqlException(logTag, sqle);
            throw new BudgetManagerException(BudgetManagerException.DATABASE_ERROR, "Error occurred accessing DB during debitLeadFromAccount!", ""+sqle, sqle);
        } catch (Exception e) {
            logError(logTag, e);
            throw new BudgetManagerException(BudgetManagerException.UNKNOWN_ERROR, "Unexpected Error occurred during debitLeadFromAccount!", ""+e, e);
        } finally {
            close(pstmt, rs);
        }
    }

    public Map<String, Double> getBudgetsRemaining(Connection conn, DebitableItem item) throws BudgetManagerException {
        String logTag = getLogTag(item.getProdInstId());
        Map<String, Double> budgets = new HashMap<String, Double>();
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        Calendar today = Calendar.getInstance();
        Calendar yesterday = Calendar.getInstance();
        yesterday.add(Calendar.DATE, -1);

        try {
            // SELECT monthly_budget_remaining, daily_budget_remaining FROM product_sum WHERE prod_inst_id = ? AND update_date BETWEEN ? AND ? ORDER BY update_date DESC
            pstmt = conn.prepareStatement(GET_BUDGET_REMAINING);
            pstmt.setString(1, item.getProdInstId());
            pstmt.setDate(2, new Date(yesterday.getTimeInMillis()));
            pstmt.setDate(3, new Date(today.getTimeInMillis()));
            logInfo(logTag, "getBudgetsRemaining -> About to execute GET_BUDGET_REMAINING:");
            logSqlStatement(logTag, pstmt);
            rs = pstmt.executeQuery();

            if (rs != null) {
                if (rs.next()) {
                    logInfo(logTag, "getBudgetsRemaining ->  monthly_budget_remaining: " + rs.getDouble(1));
                    logInfo(logTag, "getBudgetsRemaining ->    daily_budget_remaining: " + rs.getDouble(2));
                    budgets.put("monthly_budget_remaining", rs.getDouble(1));
                    budgets.put("daily_budget_remaining", rs.getDouble(2));
                } else {
                    logError(logTag, "getBudgetsRemaining -> Error accessing DB during getBudgetsRemaining: No rows returned!");
                    throw new BudgetManagerException(BudgetManagerException.DATABASE_ERROR, "Error accessing DB during getBudgetsRemaining: No rows returned!");
                }
            } else {
                logError(logTag, "getBudgetsRemaining -> Error accessing DB during getBudgetsRemaining: NULL Result set!");
                throw new BudgetManagerException(BudgetManagerException.DATABASE_ERROR, "Error accessing DB during getBudgetsRemaining: NULL Result set!");
            }

            // SELECT c.daily_budget - s.vendor_click_cost - s.ns_click_cost - s.total_lead_cost AS campaign_budget_remaining, c.daily_budget FROM ns_campaign c, ns_campaign_sum s
            // WHERE s.prod_inst_id = ? AND s.ns_campaign_id = ? AND s.update_date = ? AND s.prod_inst_id = c.prod_inst_id AND s.ns_campaign_id = c.ns_campaign_id
            pstmt = conn.prepareStatement(GET_CAMPAIGN_BUDGET_REMAINING);
            pstmt.setString(1, item.getProdInstId());
            pstmt.setLong(2, item.getNsCampaignId());
            pstmt.setDate(3, new Date(today.getTimeInMillis()));
            logInfo(logTag, "getBudgetsRemaining -> About to execute GET_CAMPAIGN_BUDGET_REMAINING:");
            logSqlStatement(logTag, pstmt);
            rs = pstmt.executeQuery();

            if (rs != null) {
                if (rs.next()) {
                    logInfo(logTag, "getBudgetsRemaining -> campaign_budget_remaining: " + rs.getDouble(1));
                    logInfo(logTag, "getBudgetsRemaining ->     campaign_daily_budget: " + rs.getDouble(2));
                    budgets.put("campaign_budget_remaining", rs.getDouble(1));
                    budgets.put("campaign_daily_budget", rs.getDouble(2));
                }
            } else {
                logError(logTag, "getBudgetsRemaining -> Error accessing DB during getBudgetsRemaining: NULL Result set!");
                throw new BudgetManagerException(BudgetManagerException.DATABASE_ERROR, "Error accessing DB during getBudgetsRemaining: NULL Result set!");
            }

            return budgets;
        } catch (BudgetManagerException bme) {
            throw bme;
        } catch (SQLException sqle) {
            logSqlException(logTag, sqle);
            throw new BudgetManagerException(BudgetManagerException.DATABASE_ERROR, "Error occurred accessing DB during getBudgetsRemaining!", ""+sqle, sqle);
        } catch (Exception e) {
            logError(logTag, e);
            throw new BudgetManagerException(BudgetManagerException.UNKNOWN_ERROR, "Unexpected Error occurred during getBudgetsRemaining!", ""+e, e);
        } finally {
            close(pstmt, rs);
        }
    }

    public boolean isDuplicateLead(Connection conn, PPCLead lead) throws BudgetManagerException {
        String logTag = getLogTag(lead.getProdInstId());
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            switch (lead.getPrefix().getId()) {
            case LeadType.PHONE_LEAD: pstmt = conn.prepareStatement(GET_CALL_LEAD_DUPLE); break; // phone lead
            case LeadType.FORM_LEAD: pstmt = conn.prepareStatement(GET_FORM_LEAD_DUPLE); break; // form lead
            case LeadType.EMAIL_LEAD: pstmt = conn.prepareStatement(GET_EMAIL_LEAD_DUPLE); break; // email lead
            case LeadType.HIGH_VALUE_PAGE_LEAD: pstmt = conn.prepareStatement(GET_PAGE_LOAD_LEAD_DUPLE); break; // page load lead
            // shop cart leads will never be counted as duples
            case LeadType.SHOPPING_CART_LEAD: return false;
            case LeadType.UNANSWERED_PHONE_LEAD: return false;
            default: logInfo(logTag, "isDuplicateLead -> detected invalid lead type!"); return false;
            }

            pstmt.setString(1, lead.getProdInstId());
            pstmt.setLong(2, lead.getLeadId());
            logInfo(logTag, "isDuplicateLead -> getting duples:");
            logSqlStatement(logTag, pstmt);
            rs = pstmt.executeQuery();

            if (rs != null) {
                if (rs.next()) {
                    logInfo(logTag, "isDuplicateLead -> duplicates found for product "+lead.getProdInstId()+" lead "+lead.getLeadId());
                    return true;
                } else {
                    logInfo(logTag, "isDuplicateLead -> no duplicates found for product "+lead.getProdInstId()+" lead "+lead.getLeadId());
                    return false;
                }
            } else {
                logError(logTag, "Error retrieving duplicate lead from DB for lead "+lead.getLeadId());
                throw new BudgetManagerException(BudgetManagerException.DATABASE_ERROR, "Error retrieving duplicate lead from DB for lead "+lead.getLeadId());
            }
        } catch (BudgetManagerException bme) {
            throw bme;
        } catch (SQLException sqle) {
            logSqlException(logTag, sqle);
            throw new BudgetManagerException(BudgetManagerException.DATABASE_ERROR, "Error occurred accessing DB during isDuplicateLead!", ""+sqle, sqle);
        } catch (Exception e) {
            logError(logTag, e);
            throw new BudgetManagerException(BudgetManagerException.UNKNOWN_ERROR, "Unexpected Error occurred during isDuplicateLead!", ""+e, e);
        } finally {
            close(pstmt, rs);
        }
    }

    public int getCallDuration(Connection conn, PPCLead lead) throws BudgetManagerException {
        String logTag = getLogTag(lead.getProdInstId());
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            pstmt = conn.prepareStatement(GET_PHONE_CALL_DURATION);
            pstmt.setString(1, lead.getProdInstId());
            pstmt.setLong(2, lead.getLeadId());
            logInfo(logTag, "getCallInfo -> About to execute GET_CALL_INFO:");
            logSqlStatement(logTag, pstmt);
            rs = pstmt.executeQuery();
            Integer duration = singleValue(rs, 1, Integer.class);
            if (duration == null) {
                logError(logTag, "getCallDuration -> Error accessing DB during getCallDuration: No rows returned!");
                throw new BudgetManagerException(BudgetManagerException.DATABASE_ERROR, "Error accessing DB during getCallDuration: No rows returned!");
            }
            return duration.intValue();
        }
        catch (SQLException ex) {
            logSqlException(logTag, ex);
            throw new BudgetManagerException(BudgetManagerException.DATABASE_ERROR, "Error occurred accessing DB during getCallDuration!", ""+ex, ex);
        }
        finally {
            close(pstmt, rs);
        }
    }

    /**
     * Get product campaigns by status, or get all campaigns if statuses is null or empty.
     *
     * @param conn
     * @param prodInstId
     * @param statuses
     * @return the CampaignList
     * @throws BudgetManagerException
     */
    public CampaignList getCampaigns(Connection conn, String prodInstId, String[] statuses) throws BudgetManagerException {
        String logTag = getLogTag(prodInstId);
        List<NsCampaign> nsCampaigns = null;
        try {
            nsCampaigns = new NsEntityHelper(this).getNsAdCampaignsIncludingStatuses(logTag, conn, prodInstId, statuses);
        }
        catch (SQLException ex) {
            logError(logTag, ex);
            throw new BudgetManagerException(BudgetManagerException.DATABASE_ERROR, "Generic DB Error", ex.getMessage(), ex);
        }

        CampaignList campaigns = toCampaignList(prodInstId, nsCampaigns);
        campaigns.setProdInstId(prodInstId);
        for (Campaign campaign : campaigns.getCampaigns()) {
            logInfo(logTag, "getActiveCampaigns -> got campaign " + campaign.getNsCampaignId());
        }

        return campaigns;
    }

    public void setCurrentConversionRate(Connection conn, Product product) throws BudgetManagerException {
        String logTag = getLogTag(product.getProdInstId());
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        Calendar today = Calendar.getInstance();

        try {
            pstmt = conn.prepareStatement(GET_CURRENT_CONVERSION_RATE);
            pstmt.setString(1, product.getProdInstId());
            pstmt.setDate(3, new java.sql.Date(today.getTimeInMillis()));
            if (product.getConvRateWeeks() == 0) {
                // if conversion rate weeks is equal to 0, then do the last month
                Calendar lastMonth = Calendar.getInstance();
                lastMonth.add(java.util.Calendar.MONTH, -1);
                // set the week counter to 4 since we are doing a month's performance
                product.setConvRateWeeks(4);
                // set start date of analysis to one month ago
                pstmt.setDate(2, new java.sql.Date(lastMonth.getTimeInMillis()));
            } else {
                // else, do the last week only
                java.util.Calendar lastWeek = java.util.Calendar.getInstance();
                lastWeek.add(java.util.Calendar.WEEK_OF_MONTH, -1);
                // since there was a previous value, multiply out the conversion rate by weeks
                product.setConvRate(product.getConvRate() * product.getConvRateWeeks());
                // now add a week to the counter
                product.setConvRateWeeks(product.getConvRateWeeks() + 1);
                // set start date of analysis to one week ago.
                pstmt.setDate(2, new java.sql.Date(lastWeek.getTimeInMillis()));
            }
            logInfo(logTag, "calculateTier -> About to execute GET_CURRENT_CONVERSION_RATE:");
            logSqlStatement(logTag, pstmt);
            rs = pstmt.executeQuery();

            if (rs != null) {
                if (rs.next()) {
                    product.setConvRate(product.getConvRate() + rs.getDouble(1));
                    // if we are doing a weekly update, now we must divide by the week counter to get the new avg conversion rate (lifetime)
                    if (product.getConvRateWeeks() > 4) {
                        product.setConvRate(product.getConvRate() / product.getConvRateWeeks());
                    }
                    logInfo(logTag, "calculateTier -> new convRate: " + product.getConvRate());
                }
            } else {
                logError(logTag, "Error retrieving data from the DB! Null result set");
                throw new BudgetManagerException(BudgetManagerException.DATABASE_ERROR,"Error retrieving data from DB! Null result set");
            }
        } catch (BudgetManagerException bme) {
            throw bme;
        } catch (SQLException sqle) {
            logSqlException(logTag, sqle);
            throw new BudgetManagerException(BudgetManagerException.DATABASE_ERROR, "Error occurred accessing DB during setCurrentConversionRate!", ""+sqle, sqle);
        } catch (Exception e) {
            logError(logTag, e);
            throw new BudgetManagerException(BudgetManagerException.UNKNOWN_ERROR, "Unexpected Error occurred during setCurrentConversionRate!", ""+e, e);
        } finally {
            close(pstmt, rs);
        }
    }

    public void setTierValues(Connection pdbConn, Product product) throws BudgetManagerException {
        String logTag = getLogTag(product.getProdInstId());
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            pstmt = pdbConn.prepareStatement(GET_TIER_VALUES);
            pstmt.setInt(1, product.getChannelId());
            pstmt.setDouble(2, product.getValuePerClick(pdbConn));
            logInfo(logTag, "calculateTier -> About to execute GET_TIER_VALUES:");
            logSqlStatement(logTag, pstmt);
            rs = pstmt.executeQuery();

            if (rs != null) {
                if (rs.next()) {
                    product.setTier(rs.getInt(1));
                    product.setMultiplier(rs.getDouble(2));
                }
            } else {
                logError(logTag, "Error retrieving tier data from the DB! Null result set");
                throw new BudgetManagerException(BudgetManagerException.DATABASE_ERROR,"Error retrieving tier data from DB! Null result set");
            }
        } catch (BudgetManagerException bme) {
            throw bme;
        } catch (SQLException sqle) {
            logSqlException(logTag, sqle);
            throw new BudgetManagerException(BudgetManagerException.DATABASE_ERROR, "Error occurred accessing DB during setTierValues!", ""+sqle, sqle);
        } catch (Exception e) {
            logError(logTag, e);
            throw new BudgetManagerException(BudgetManagerException.UNKNOWN_ERROR, "Unexpected Error occurred during setTierValues!", ""+e, e);
        } finally {
            close(pstmt, rs);
        }
    }

    public double getCpcMarkup(Connection conn, Integer channelId, String prodInstId, boolean isCpcSensitive) throws BudgetManagerException {
        String logTag = getLogTag(prodInstId);
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            pstmt = conn.prepareStatement(GET_CPC_MARKUP);
            pstmt.setInt(1, channelId);
            pstmt.setString(2, prodInstId);
            logInfo(logTag, "getCpcMarkup -> About to execute GET_CPC_MARKUP:");
            logSqlStatement(logTag, pstmt);
            rs = pstmt.executeQuery();
            if (rs != null && rs.next()) {
                return (isCpcSensitive ? rs.getDouble(2) : rs.getDouble(1));
            } else {
                logError(logTag, "getCpcMarkup -> Error retrieving CPC markup from the DB! Null result set");
                throw new BudgetManagerException(BudgetManagerException.DATABASE_ERROR,"Error retrieving CPC markup from DB! Null result set");
            }
        } catch (BudgetManagerException bme) {
            throw bme;
        } catch (SQLException sqle) {
            logSqlException(logTag, sqle);
            throw new BudgetManagerException(BudgetManagerException.DATABASE_ERROR, "Error occurred accessing DB during getCpcMarkup!", ""+sqle, sqle);
        } catch (Exception e) {
            logError(logTag, e);
            throw new BudgetManagerException(BudgetManagerException.UNKNOWN_ERROR, "Unexpected Error occurred during getCpcMarkup!", ""+e, e);
        } finally {
            close(pstmt, rs);
        }
    }

    public double getCampaignDailyBudgetsTotal(Connection conn, String prodInstId) throws BudgetManagerException {
        String logTag = getLogTag(prodInstId);
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            pstmt = conn.prepareStatement(GET_CAMPAIGN_DAILY_BUDGETS_TOTAL);
            pstmt.setString(1, prodInstId);
            pstmt.setString(2, CampaignStatus.ACTIVE);
            pstmt.setString(3, CampaignStatus.SYSTEM_PAUSE);
            logInfo(logTag, "getCampaignDailyBudgetsTotal -> About to execute GET_CAMPAIGN_DAILY_BUDGETS_TOTAL:");
            logSqlStatement(logTag, pstmt);
            rs = pstmt.executeQuery();
            if (rs != null && rs.next()) {
                return rs.getDouble(1);
            } else {
                logError(logTag, "getCampaignDailyBudgetsTotal -> Error retrieving CampaignDailyBudgetsTotal from the DB! Null result set");
                throw new BudgetManagerException(BudgetManagerException.DATABASE_ERROR,"Error retrieving CampaignDailyBudgetsTotal from DB! Null result set");
            }
        } catch (BudgetManagerException bme) {
            throw bme;
        } catch (SQLException sqle) {
            logSqlException(logTag, sqle);
            throw new BudgetManagerException(BudgetManagerException.DATABASE_ERROR, "Error occurred accessing DB during getCampaignDailyBudgetsTotal!", ""+sqle, sqle);
        } catch (Exception e) {
            logError(logTag, e);
            throw new BudgetManagerException(BudgetManagerException.UNKNOWN_ERROR, "Unexpected Error occurred during getCampaignDailyBudgetsTotal!", ""+e, e);
        } finally {
            close(pstmt, rs);
        }
    }

    public CampaignList getSingleCampaignInfo(Connection conn, String prodInstId, long nsCampaignId) throws BudgetManagerException {
        String logTag = getLogTag(prodInstId);

        NsCampaign nsCampaign = null;
        try {
            nsCampaign = new NsEntityHelper(this).getNsCampaign(logTag, conn, prodInstId, nsCampaignId);
        }
        catch (SQLException ex) {
            logError(logTag, ex);
            throw new BudgetManagerException(BudgetManagerException.DATABASE_ERROR, "Generic DB Error", ex.getMessage(), ex);
        }
        if (nsCampaign == null) {
            logError(logTag, "getSingleCampaignInfo -> Error retrieving SingleCampaignInfo from the DB! Null result set");
            throw new BudgetManagerException(BudgetManagerException.DATABASE_ERROR,"Error retrieving SingleCampaignInfo from DB! Null result set");
        }

        return toCampaignList(prodInstId, Collections.singleton(nsCampaign));
    }

    public double getCampaignBudgetByAdGroup(Connection conn, String prodInstId, long nsAdGroupId) throws BudgetManagerException {
        String logTag = getLogTag(prodInstId);
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            pstmt = conn.prepareStatement(GET_CAMPAIGN_BUDGET_BY_AD_GROUP);
            pstmt.setString(1, prodInstId);
            pstmt.setLong(2, nsAdGroupId);
            logInfo(logTag, "getCampaignBudgetByAdGroup -> About to execute GET_CAMPAIGN_BUDGET_BY_AD_GROUP:");
            logSqlStatement(logTag, pstmt);
            rs = pstmt.executeQuery();
            if (rs != null && rs.next()) {
                return rs.getDouble(1);
            } else {
                logError(logTag, "getCampaignBudgetByAdGroup -> Error retrieving CampaignBudgetByAdGroup from the DB! Null result set");
                throw new BudgetManagerException(BudgetManagerException.DATABASE_ERROR,"Error retrieving CampaignBudgetByAdGroup from DB! Null result set");
            }
        } catch (BudgetManagerException bme) {
            throw bme;
        } catch (SQLException sqle) {
            logSqlException(logTag, sqle);
            throw new BudgetManagerException(BudgetManagerException.DATABASE_ERROR, "Error occurred accessing DB during getCampaignBudgetByAdGroup!", ""+sqle, sqle);
        } catch (Exception e) {
            logError(logTag, e);
            throw new BudgetManagerException(BudgetManagerException.UNKNOWN_ERROR, "Unexpected Error occurred during getCampaignBudgetByAdGroup!", ""+e, e);
        } finally {
            close(pstmt, rs);
        }
    }

    public int getCountOfActiveAdGroups(Connection conn, String prodInstId) throws BudgetManagerException {
        String logTag = getLogTag(prodInstId);
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            pstmt = conn.prepareStatement(GET_ACTIVE_AD_GROUP_COUNT);
            pstmt.setString(1, prodInstId);
            pstmt.setString(2, AdGroupStatus.ACTIVE);
            pstmt.setString(3, AdGroupStatus.MANUAL_PAUSE);
            logInfo(logTag, "getCountOfActiveAdGroups -> About to execute GET_ACTIVE_AD_GROUP_COUNT:");
            logSqlStatement(logTag, pstmt);
            rs = pstmt.executeQuery();
            if (rs != null && rs.next()) {
                return rs.getInt(1);
            } else {
                logError(logTag, "getCountOfActiveAdGroups -> Error retrieving CountOfActiveAdGroups from the DB! Null result set");
                throw new BudgetManagerException(BudgetManagerException.DATABASE_ERROR,"Error retrieving CountOfActiveAdGroups from DB! Null result set");
            }
        } catch (BudgetManagerException bme) {
            throw bme;
        } catch (SQLException sqle) {
            logSqlException(logTag, sqle);
            throw new BudgetManagerException(BudgetManagerException.DATABASE_ERROR, "Error occurred accessing DB during getCountOfActiveAdGroups!", ""+sqle, sqle);
        } catch (Exception e) {
            logError(logTag, e);
            throw new BudgetManagerException(BudgetManagerException.UNKNOWN_ERROR, "Unexpected Error occurred during getCountOfActiveAdGroups!", ""+e, e);
        } finally {
            close(pstmt, rs);
        }
    }

    public int getCountOfActiveKeywords(Connection conn, String prodInstId, long nsAdGroupId) throws BudgetManagerException {
        String logTag = getLogTag(prodInstId);
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            pstmt = conn.prepareStatement(GET_ACTIVE_KEYWORD_COUNT);
            pstmt.setString(1, prodInstId);
            pstmt.setLong(2, nsAdGroupId);
            pstmt.setString(3, KeywordStatus.ACTIVE);
            logInfo(logTag, "getCountOfActiveKeywords -> About to execute GET_ACTIVE_KEYWORD_COUNT:");
            logSqlStatement(logTag, pstmt);
            rs = pstmt.executeQuery();
            if (rs != null && rs.next()) {
                return rs.getInt(1);
            } else {
                logError(logTag, "getCountOfActiveKeywords -> Error retrieving CountOfActiveKeywords from the DB! Null result set");
                throw new BudgetManagerException(BudgetManagerException.DATABASE_ERROR,"Error retrieving CountOfActiveKeywords from DB! Null result set");
            }
        } catch (BudgetManagerException bme) {
            throw bme;
        } catch (SQLException sqle) {
            logSqlException(logTag, sqle);
            throw new BudgetManagerException(BudgetManagerException.DATABASE_ERROR, "Error occurred accessing DB during getCountOfActiveKeywords!", ""+sqle, sqle);
        } catch (Exception e) {
            logError(logTag, e);
            throw new BudgetManagerException(BudgetManagerException.UNKNOWN_ERROR, "Unexpected Error occurred during getCountOfActiveKeywords!", ""+e, e);
        } finally {
            close(pstmt, rs);
        }
    }

    public double getMaxLeadCost(Connection conn, String prodInstId) throws BudgetManagerException {
        String logTag = getLogTag(prodInstId);
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            pstmt = conn.prepareStatement(GET_MAX_LEAD_COST);
            pstmt.setString(1, prodInstId);
            logInfo(logTag, "getMaxLeadCost -> About to execute GET_MAX_LEAD_COST:");
            logSqlStatement(logTag, pstmt);
            rs = pstmt.executeQuery();
            if (rs != null && rs.next()) {
                return rs.getDouble(1);
            } else {
                logError(logTag, "getMaxLeadCost -> Error retrieving MaxLeadCost from the DB! Null result set");
                throw new BudgetManagerException(BudgetManagerException.DATABASE_ERROR,"Error retrieving MaxLeadCost from DB! Null result set");
            }
        } catch (BudgetManagerException bme) {
            throw bme;
        } catch (SQLException sqle) {
            logSqlException(logTag, sqle);
            throw new BudgetManagerException(BudgetManagerException.DATABASE_ERROR, "Error occurred accessing DB during getMaxLeadCost!", ""+sqle, sqle);
        } catch (Exception e) {
            logError(logTag, e);
            throw new BudgetManagerException(BudgetManagerException.UNKNOWN_ERROR, "Unexpected Error occurred during getMaxLeadCost!", ""+e, e);
        } finally {
            close(pstmt, rs);
        }
    }

    public Map<String, Double> getAdGroupTargetConversionRate(Connection conn, String prodInstId, long nsAdGroupId) throws BudgetManagerException {
        String logTag = getLogTag(prodInstId);
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            pstmt = conn.prepareStatement(GET_AD_GROUP_TARGET_CONVERSION_RATE);
            pstmt.setString(1, prodInstId);
            pstmt.setLong(2, nsAdGroupId);
            logInfo(logTag, "getAdGroupTargetConversionRate -> About to execute GET_AD_GROUP_TARGET_CONVERSION_RATE:");
            logSqlStatement(logTag, pstmt);
            rs = pstmt.executeQuery();
            if (rs != null && rs.next()) {
                Map<String, Double> values = new HashMap<String, Double>();
                values.put("target_conversion_rate", rs.getDouble(1));
                if (rs.getDouble(2) > 0.0) {
                    values.put("ad_group_cpc", rs.getDouble(2));
                } else if (rs.getDouble(3) > 0.0) {
                    values.put("ad_group_cpc", rs.getDouble(3));
                } else if (rs.getDouble(4) > 0.0) {
                    values.put("ad_group_cpc", rs.getDouble(4));
                } else {
                    values.put("ad_group_cpc", -1.0);
                }
                return values;
            } else {
                logError(logTag, "getAdGroupTargetConversionRate -> Error retrieving AdGroupTargetConversionRate from the DB! Null result set");
                throw new BudgetManagerException(BudgetManagerException.DATABASE_ERROR,"Error retrieving AdGroupTargetConversionRate from DB! Null result set");
            }
        } catch (BudgetManagerException bme) {
            throw bme;
        } catch (SQLException sqle) {
            logSqlException(logTag, sqle);
            throw new BudgetManagerException(BudgetManagerException.DATABASE_ERROR, "Error occurred accessing DB during getAdGroupTargetConversionRate!", ""+sqle, sqle);
        } catch (Exception e) {
            logError(logTag, e);
            throw new BudgetManagerException(BudgetManagerException.UNKNOWN_ERROR, "Unexpected Error occurred during getAdGroupTargetConversionRate!", ""+e, e);
        } finally {
            close(pstmt, rs);
        }
    }

    public void updateBidcapForAdGroup(Connection conn, String prodInstId, long nsAdGroupId, double bidcap) throws BudgetManagerException {
        String logTag = getLogTag(prodInstId);
        PreparedStatement pstmt = null;

        try {
            pstmt = conn.prepareStatement(UPDATE_BIDCAP_FOR_AD_GROUP);
            pstmt.setDouble(1, bidcap);
            pstmt.setString(2, prodInstId);
            pstmt.setLong(3, nsAdGroupId);
            pstmt.setString(4, KeywordStatus.ACTIVE);
            logInfo(logTag, "updateBidcapForAdGroup -> About to execute UPDATE_BIDCAP_FOR_AD_GROUP:");
            logSqlStatement(logTag, pstmt);
            pstmt.executeUpdate();
        } catch (SQLException sqle) {
            logSqlException(logTag, sqle);
            throw new BudgetManagerException(BudgetManagerException.DATABASE_ERROR, "Error occurred accessing DB during updateBidcapForAdGroup!", ""+sqle, sqle);
        } catch (Exception e) {
            logError(logTag, e);
            throw new BudgetManagerException(BudgetManagerException.UNKNOWN_ERROR, "Unexpected Error occurred during updateBidcapForAdGroup!", ""+e, e);
        } finally {
            close(pstmt);
        }
    }

    /**
     * Get the vendor spend aggressiveness for the product. It is currently stored in the ns_campaign table.
     *
     * In the near future, change this functionality to store it in a product-level table, perhaps vendor_budget_allocation.
     *
     * @param pdbConn
     * @param prodInstId
     * @param vendorId
     * @return
     * @throws Exception
     */
    public float getVendorSpendAggressivenessForProduct(Connection pdbConn, String prodInstId, long vendorId) throws BudgetManagerException {
        String logTag = getLogTag(prodInstId);
        PreparedStatement pstmt = null;

        try {
            pstmt = pdbConn.prepareStatement(GET_VENDOR_SPEND_AGGRESSIVENESS_FOR_PRODUCT);
            pstmt.setString(1, prodInstId);
            pstmt.setLong(2, vendorId);
            logInfo(logTag, "getVendorSpendAggressivenessForProduct: About to execute GET_VENDOR_SPEND_AGGRESSIVENESS_FOR_PRODUCT:");
            logSqlStatement(logTag, pstmt);
            ResultSet results = pstmt.executeQuery();
            float vendorSpendAggressiveness = 1;
            if (results != null && results.next()) {
                vendorSpendAggressiveness = results.getFloat(1);
            }
            return vendorSpendAggressiveness;
        } catch (Exception e) {
            logError(logTag, e);
            throw new BudgetManagerException(BudgetManagerException.UNKNOWN_ERROR, "Unexpected Error occurred during getVendorSpendAggressivenessForProduct. ", ""+e, e);
        }
        finally {
            close(pstmt);
        }
    }

    /**
     * Shift a click or lead's date based on the time zone configured for the specified vendor.
     */
    public java.util.Date shiftClickOrLeadDate(Connection conn, String prodInstId, int vendorId, java.util.Date clickOrLeadDate) throws BudgetManagerException {
        String logTag = getLogTag(prodInstId);
        VendorTimeZone vendorTimeZone = null;
        try {
            vendorTimeZone =
                new VendorTimeZoneHelper(getCurrentLogComponent()).getVendorTimeZoneByVendorIdAndProdInstId(logTag, conn, vendorId, prodInstId);
        }
        catch (SQLException ex) {
            logError(logTag, ex);
            throw new BudgetManagerException(BudgetManagerException.DATABASE_ERROR, "Generic DB Error", ex.getMessage(), ex);
        }
        String javaName = null;
        if ((vendorTimeZone == null) || ((javaName = vendorTimeZone.getJavaName()) == null)) {
            return clickOrLeadDate;
        }
        return DateUtil.shiftFromTimeZone(clickOrLeadDate, TimeZone.getTimeZone(javaName));
    }

    /**
     * Update the roadmap cycle budget for the current cycle and all future cycles that exist.
     *
     * @param prodInstId
     * @param budget
     */
    public void updateRoadmapCycleBudget(Connection pdbConn, String prodInstId, double budget) throws BudgetManagerException {
    	String sql = "update roadmap_cycle set budget=? where prod_inst_id=? and end_date > date(now());";
    	String logTag = getLogTag(prodInstId);
        PreparedStatement pstmt = null;
        try {
            pstmt = pdbConn.prepareStatement(sql);
            pstmt.setDouble(1, budget);
            pstmt.setString(2, prodInstId);
            logSqlStatement(logTag, pstmt);
            pstmt.executeUpdate();
        }
        catch (Exception e) {
            logError(logTag, e);
            throw new BudgetManagerException(BudgetManagerException.DATABASE_ERROR, "Generic DB Error", e.getMessage(), e);
        }
        finally {
            close(pstmt);
        }
    }

    //
    // Private methods
    //

    private double getBudgetFactor(Connection conn, Integer channelId, String factor) throws BudgetManagerException {
        String logTag = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            pstmt = conn.prepareStatement(GET_BUDGET_FACTOR);
            pstmt.setInt(1, channelId);
            pstmt.setString(2, factor);
            logInfo(logTag, "getBudgetFactor -> About to execute GET_BUDGET_FACTOR:");
            logSqlStatement(logTag, pstmt);
            rs = pstmt.executeQuery();

            if (rs != null) {
                if (rs.next()) {
                    logInfo(logTag, "getBudgetFactor -> " + factor + ": " + rs.getDouble(1));
                    return rs.getDouble(1);
                } else {
                    logError(logTag, "getBudgetFactor -> Error occurred getting budget factor: No rows returned!");
                    throw new BudgetManagerException(BudgetManagerException.DATABASE_ERROR, "Error occurred getting budget factor: No rows returned!");
                }
            } else {
                logError(logTag, "getBudgetFactor -> Error occurred getting budget factor: Null Result Set!");
                throw new BudgetManagerException(BudgetManagerException.DATABASE_ERROR, "Error occurred getting budget factor: Null Result Set!");
            }
        } catch (BudgetManagerException bme) {
            throw bme;
        } catch (SQLException sqle) {
            logSqlException(logTag, sqle);
            throw new BudgetManagerException(BudgetManagerException.DATABASE_ERROR, "Error occurred accessing DB during getDefaultConversionRate!", ""+sqle, sqle);
        } catch (Exception e) {
            logError(logTag, e);
            throw new BudgetManagerException(BudgetManagerException.UNKNOWN_ERROR, "Unexpected Error occurred during getDefaultConversionRate!", ""+e, e);
        } finally {
            close(pstmt, rs);
        }
    }

    private CampaignList toCampaignList(String prodInstId, Collection<NsCampaign> nsCampaigns) {
        CampaignList campaigns = new CampaignList(this);
        campaigns.setProdInstId(prodInstId);
        for (NsCampaign nsCampaign : nsCampaigns) {
            Campaign campaign = new Campaign();
            campaign.setProdInstId(prodInstId);
            campaign.setDailyBudget(nsCampaign.getDailyBudget().doubleValue());
            campaign.setNsCampaignId(nsCampaign.getNsCampaignId());
            campaign.setPercentOfBudget(nsCampaign.getPercentOfBudget());
            campaign.setSpendAggressiveness(nsCampaign.getSpendAggressiveness());
            campaign.setVendorId(nsCampaign.getVendorId());
            campaign.setStatus(nsCampaign.getNsStatus());
            campaign.setTargetId(nsCampaign.getTargetId());
            campaign.setMonthlyBudget(BaseHelper.toDouble(nsCampaign.getMonthlyBudget()));

            campaigns.getCampaigns().add(campaign);
        }
        return campaigns;
    }

    //
    // Unit test
    //

    /**
     * Simple unit test.
     */
    public static void main(String[] args) {
        Connection pdbConn = null;
        try {
            pdbConn = BaseHelper.createDevPdb1Connection();
            BudgetManagerHelper bmh = new BudgetManagerHelper(new org.apache.commons.logging.impl.SimpleLog("BM"));
            System.out.println(bmh.getVendorSpendAggressivenessForProduct(pdbConn, "WN.DEV.BING.0002", 1));
            System.out.println(bmh.getVendorSpendAggressivenessForProduct(pdbConn, "WN.DEV.BING.0002", 3));
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        finally {
            BaseHelper.close(pdbConn);
        }
    }
}
