/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.dbhelpers;

import static com.netsol.adagent.util.beans.BaseBudgetManagerData.getLogTag;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;

import org.apache.commons.logging.Log;

import com.netsol.adagent.util.CalendarUtil;
import com.netsol.adagent.util.beans.Product;
import com.netsol.adagent.util.log.BaseLoggable;

/**
 * This helper class contains methods that BM uses to get data related to ad scheduling.
 *
 * @author Adam S. Vernon
 */
public class AdScheduleHelper extends BaseHelper {
    public static final String sccsId = "@(#) adagent-util_d16.18.0.4 09/21/12 13:51:38 AdScheduleHelper.java NSI";

    private static HashMap<String, Integer> dayMap = new HashMap<String, Integer>();
    private static HashSet<Integer> defaultDays = new HashSet<Integer>();
    static {
        dayMap.put("Sunday", Calendar.SUNDAY);
        dayMap.put("Monday", Calendar.MONDAY);
        dayMap.put("Tuesday", Calendar.TUESDAY);
        dayMap.put("Wednesday", Calendar.WEDNESDAY);
        dayMap.put("Thursday", Calendar.THURSDAY);
        dayMap.put("Friday", Calendar.FRIDAY);
        dayMap.put("Saturday", Calendar.SATURDAY);

        defaultDays.add(Calendar.SUNDAY);
        defaultDays.add(Calendar.MONDAY);
        defaultDays.add(Calendar.TUESDAY);
        defaultDays.add(Calendar.WEDNESDAY);
        defaultDays.add(Calendar.THURSDAY);
        defaultDays.add(Calendar.FRIDAY);
        defaultDays.add(Calendar.SATURDAY);
    }

    /**
     * Constructor.
     */
    public AdScheduleHelper(String logComponent) {
        super(logComponent);
    }

    /**
     * Constructor.
     */
    public AdScheduleHelper(Log logger) {
        super(logger);
    }

    /**
     * Constructor.
     */
    public AdScheduleHelper(BaseLoggable baseLoggable) {
        super(baseLoggable);
    }

    /**
     * Count the number of days remaining in the current cycle on which this product has scheduled campaigns. This method never returns
     * less than one; the standard use case for this value is to divide by it.
     *
     * @param gdbConn
     * @param pdbConn
     * @param prodInstId
     * @return the number of days remaining in the current cycle on which this product has scheduled campaigns.
     */
    public int getNumberOfDaysRemainingInCycleWithCampaignsScheduled(Connection gdbConn, Connection pdbConn, String prodInstId) {
        HashSet<Integer> days = getDaysOfWeekWithScheduledCampaigns(getLogTag(prodInstId), pdbConn, prodInstId);
        return countDaysOfWeekRemainingInCycle(gdbConn, pdbConn, prodInstId, days);
    }

    /**
     * Count the number of days remaining in the current cycle on which this product has scheduled campaigns. This method never returns
     * less than one; the standard use case for this value is to divide by it.
     *
     * @param gdbConn
     * @param pdbConn
     * @param prodInstId
     * @return the number of days remaining in the current cycle on which this product has scheduled campaigns.
     */
    public int getNumberOfDaysRemainingInCycleWhenCampaignIsScheduled(Connection gdbConn, Connection pdbConn, String prodInstId, Long nsCampaignId) {
        HashSet<Integer> days = getDaysOfWeekWhenCampaignIsScheduled(getLogTag(prodInstId), pdbConn, prodInstId, nsCampaignId);
        return countDaysOfWeekRemainingInCycle(gdbConn, pdbConn, prodInstId, days);
    }

    //
    // Private methods
    //

    /**
     * Get the days of the week on which this product has active campaigns.
     * @param pdbConn
     * @param prodInstId
     * @return a Set of the Calendar int constants for the days of the week
     */
    private HashSet<Integer> getDaysOfWeekWithScheduledCampaigns(String logTag, Connection pdbConn, String prodInstId) {
        PreparedStatement statement = null;
        HashSet<Integer> days = null;
        try {
            String sql = "select distinct day_of_week from ns_campaign_ad_schedule cas, ns_campaign c where cas.prod_inst_id=?"
            + "and cas.ns_campaign_id= c.ns_campaign_id and c.status in ('ACTIVE', 'SYSTEM_PAUSE')";
            statement = pdbConn.prepareStatement(sql);
            statement.setString(1, prodInstId); // indexed
            logSqlStatement(logTag, statement);
            ResultSet results = statement.executeQuery();
            days = new HashSet<Integer>();
            while (results != null && results.next()) {
                String day = results.getString(1);
                if (dayMap.containsKey(day)) {
                    days.add(dayMap.get(day));
                }
                else {
                    // Bad day_of_week data.
                    logWarning(logTag, "bad data for day_of_week: " + day);
                }
            }

            // If there is no ad scheduling, return all the days.
            if (days.size() == 0) {
                days = defaultDays;
            }

        }
        catch (Exception e) {
            logError(logTag, e);
        }
        finally {
            close(statement);
        }
        return days;
    }

    /**
     * Get the days of the week on which this campaign is scheduled.
     * @param pdbConn
     * @param prodInstId
     * @return a Set of the Calendar int constants for the days of the week
     */
    private HashSet<Integer> getDaysOfWeekWhenCampaignIsScheduled(String logTag, Connection pdbConn, String prodInstId, Long nsCampaignId) {
        PreparedStatement statement = null;
        HashSet<Integer> days = null;
        try {
            String sql = "select distinct day_of_week from ns_campaign_ad_schedule cas "
                + "inner join ns_campaign c on cas.prod_inst_id=c.prod_inst_id and cas.ns_campaign_id=c.ns_campaign_id "
                + "where cas.prod_inst_id=? and cas.ns_campaign_id=? and c.status in ('ACTIVE', 'SYSTEM_PAUSE')";
            statement = pdbConn.prepareStatement(sql);
            statement.setString(1, prodInstId); // indexed
            statement.setLong(2, nsCampaignId); // indexed
            logSqlStatement(logTag, statement);
            ResultSet results = statement.executeQuery();
            days = new HashSet<Integer>();
            while (results != null && results.next()) {
                String day = results.getString(1);
                if (dayMap.containsKey(day)) {
                    days.add(dayMap.get(day));
                }
                else {
                    // Bad day_of_week data.
                    logWarning(logTag, "bad data for day_of_week: " + day);
                }
            }

            // If there is no ad scheduling, return all the days.
            if (days.size() == 0) {
                days = defaultDays;
            }

        }
        catch (Exception e) {
            logError(logTag, e);
            // Don't just fail... use every day.
            days = defaultDays;
        }
        finally {
            close(statement);
        }
        return days;
    }

    /**
     * Count the number of days remaining in the current cycle on which this product has scheduled campaigns. This method never returns
     * less than one; the standard use case for this value is to divide by it.
     *
     * @param gdbConn
     * @param pdbConn
     * @param prodInstId
     * @return the number of days remaining in the current cycle on which this product has scheduled campaigns.
     */
    private int countDaysOfWeekRemainingInCycle(Connection gdbConn, Connection pdbConn, String prodInstId, HashSet<Integer> days) {
        String logTag = getLogTag(prodInstId);
        int dayCount = 0;
        try {
            Product product = new Product(this);
            product.init(gdbConn, pdbConn, prodInstId);
            Calendar start = Calendar.getInstance();
            Calendar end = CalendarUtil.dateToCalendar(product.getExpirationDate());
            Calendar[] calArray = CalendarUtil.getCalendarsBetweenTwoDays(start, end);
            for (Calendar cal : calArray) {
                if (days.contains(cal.get(Calendar.DAY_OF_WEEK))) {
                    dayCount++;
                }
            }
        }
        catch (Exception e) {
            logError(logTag, e);
        }
        return Math.max(dayCount, 1); // always return at least 1
    }

    //
    // Unit test
    //


    /**
     * Simple unit test method.
     */
    public static void main(String[] args) {
        String prodInstId = "WN.DEV.ADAM.001"; // WN.PP.224046192, WN.DEV.ADAM.001, WN.PP.DEV1111
        Connection gdbConn = null;
        Connection pdbConn = null;
        try {
            gdbConn = BaseHelper.createDevGdbConnection();
            pdbConn = BaseHelper.createDevPdb1Connection();
            int dayCount = new AdScheduleHelper("").getNumberOfDaysRemainingInCycleWithCampaignsScheduled(gdbConn, pdbConn, prodInstId);
            System.out.println("dayCount=" + dayCount);
        }
        catch (Exception ex) {
            ex.printStackTrace(System.out);
        }
        finally {
            BaseHelper.close(gdbConn, pdbConn);
        }
    }
}
