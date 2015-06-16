/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.beans;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;

import org.apache.commons.logging.Log;

import com.netsol.adagent.util.BudgetManagerException;
import com.netsol.adagent.util.CalendarUtil;
import com.netsol.adagent.util.dbhelpers.BaseHelper;
import com.netsol.adagent.util.log.BaseLoggable;

/**
 * Data bean for product_sum.
 *
 * Note: DO NOT directly update daily_budget_remaining and monthly_budget_remaining. They must be incremented/decremented in the database to assure atomicity across operations
 * such as debits and budget upgrades/downgrades.
 *
 * @author Adam S. Vernon (mostly rewritten, not sure of original author)
 */
public class ProductSummaryData extends PPCSummaryData {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:05 ProductSummaryData.java NSI";

    private static final String GET_PRODUCT_SUMMARY_DATA =
        "SELECT monthly_budget_remaining, daily_budget_remaining, update_date, " +
        "ns_click_cost+vendor_click_cost+total_lead_cost daily_total " +
        "FROM product_sum WHERE prod_inst_id = ? AND update_date <= ? " +
        "ORDER BY update_date DESC LIMIT 1";

    // This use to have an on duplicate key and updated monthly_budget_remaining and daily_budget_remaining. I removed that part.
    private static final String INSERT_PRODUCT_SUMMARY_DATA = "INSERT IGNORE INTO product_sum (prod_inst_id, update_date, monthly_budget_remaining, daily_budget_remaining) "
        + "VALUES (?, ?, ?, ?)";
    // I replaced the on duplicate key update code with these specialized updates for MBR and DBR.
    private static final String UPDATE_PRODUCT_MBR_ADD_BUDGET = "UPDATE product_sum SET monthly_budget_remaining = monthly_budget_remaining + ? WHERE prod_inst_id = ? AND update_date = ?";
    private static final String UPDATE_PRODUCT_DBR_ADD_BUDGET = "UPDATE product_sum SET daily_budget_remaining = daily_budget_remaining + ? WHERE prod_inst_id = ? AND update_date = ?";
    private static final String UPDATE_PRODUCT_DBR_USING_TOTAL = "UPDATE product_sum SET daily_budget_remaining = ? - (ns_click_cost + vendor_click_cost + total_lead_cost) "
        + "WHERE prod_inst_id = ? AND update_date = ?";

    private double monthlyBudgetRemaining;
    private double dailyBudgetRemaining;
    private double dailyTotal;

    private Connection connection;

    /**
     * Constructor.
     */
    public ProductSummaryData(String logComponent) {
        super(logComponent);
        return;
    }

    /**
     * Constructor.
     */
    public ProductSummaryData(Log logger) {
        super(logger);
        return;
    }

    /**
     * Constructor.
     */
    public ProductSummaryData(BaseLoggable baseLoggable) {
        super(baseLoggable);
        return;
    }

    @Override
    public void init(Connection conn, String prodInstId) throws BudgetManagerException {
        // Initialize for today.
        this.init(conn, prodInstId, Calendar.getInstance());
    }

    public boolean init(Connection conn, String prodInstId, Calendar cal) throws BudgetManagerException {
        logTag = getLogTag(prodInstId);
        logInfo(logTag, "PRODUCT SUMMARY BEAN -> init(): *** ENTER ***");

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        // TR71978 - Return whether or not data was found for the specified date.
        boolean dataFoundForDate = false;

        try {
            if (conn == null || conn.isClosed()) {
                logError(logTag, "Error initializing PRODUCT SUMMARY data: Provided connection is NULL or CLOSED!");
                throw new BudgetManagerException(BudgetManagerException.DATABASE_ERROR, "Error initializing PRODUCT SUMMARY data: Provided connection is NULL or CLOSED!");
            }

            pstmt = conn.prepareStatement(GET_PRODUCT_SUMMARY_DATA);

            pstmt.setString(1, prodInstId);
            pstmt.setDate(2, new Date(cal.getTimeInMillis()));
            logDebug(logTag, "PRODUCT SUMMARY BEAN -> init(): About to execute GET_PRODUCT_SUMMARY_DATA: " + pstmt);
            rs = pstmt.executeQuery();

            if (rs == null) {
                logError(logTag, "PRODUCT SUMMARY BEAN -> init(): Error retrieving PRODUCT SUMMARY data from the DB: Null Result Set!");
                throw new SQLException("Error retrieving PRODUCT data from the DB: Null Result Set!");
            }

            if (rs.next()) {
                this.connection = conn;
                setProdInstId(prodInstId);
                this.monthlyBudgetRemaining = rs.getDouble(1);
                this.dailyBudgetRemaining = rs.getDouble(2);
                this.updateDate = rs.getDate(3);
                this.dailyTotal = rs.getDouble(4);
                logInfo(logTag, this.toString());

                Calendar updateDateCal = Calendar.getInstance();
                updateDateCal.setTime(this.updateDate);
                dataFoundForDate =
                    (updateDateCal.get(Calendar.YEAR) == cal.get(Calendar.YEAR)) &&
                    (updateDateCal.get(Calendar.MONTH) == cal.get(Calendar.MONTH)) &&
                    (updateDateCal.get(Calendar.DAY_OF_MONTH) == cal.get(Calendar.DAY_OF_MONTH));
            } else {
                logInfo(logTag, "PRODUCT SUMMARY BEAN -> init(): Product Not Found! Defaulting values...");
                this.connection = conn;
                setProdInstId(prodInstId);
                this.monthlyBudgetRemaining = 0.0;
                this.dailyBudgetRemaining = 0.0;
                this.updateDate = new Date(Calendar.getInstance().getTimeInMillis());
                logInfo(logTag, this.toString());
            }

            logInfo(logTag, "PRODUCT SUMMARY BEAN -> init(): *** COMPLETE ***");

            return dataFoundForDate;
        } catch (SQLException sqle) {
            logError(logTag, sqle);
            throw new BudgetManagerException(BudgetManagerException.DATABASE_ERROR, "Error occurred accessing DB in PRODUCT SUMMARY BEAN!", ""+sqle, sqle);
        } catch (Exception e) {
            logError(logTag, e);
            throw new BudgetManagerException(BudgetManagerException.UNKNOWN_ERROR, "Unexpected Error occurred in PRODUCT SUMMARY BEAN!", ""+e, e);
         } finally {
            BaseHelper.close(pstmt, rs);
        }
    }

    /**
     * Insert the product_sum record. This method can only be safely called when setting up the new budget for the day, thus it only inserts, does not update.
     * Use the update methods for specific scenarios.
     */
    @Override
    public void persist() throws BudgetManagerException {
        PreparedStatement pstmt = null;

        try {
            if (connection == null || connection.isClosed()) {
                logError(logTag, "Error persisting PRODUCT SUMMARY data: Provided connection is NULL or CLOSED!");
                throw new BudgetManagerException(BudgetManagerException.DATABASE_ERROR, "Error persisting PRODUCT SUMMARY data: Provided connection is NULL or CLOSED!");
            }

            pstmt = connection.prepareStatement(INSERT_PRODUCT_SUMMARY_DATA);
            pstmt.setString(1, getProdInstId());
            pstmt.setDate(2, updateDate);
            pstmt.setDouble(3, monthlyBudgetRemaining);
            pstmt.setDouble(4, dailyBudgetRemaining);

            logDebug(logTag, "PRODUCT SUMMARY -> persist(): About to execute PERSIST_PRODUCT_SUMMARY_DATA: " + pstmt);
            pstmt.executeUpdate();

            logInfo(logTag, "PRODUCT SUMMARY -> persist(): *** COMPLETE ***");
        } catch (SQLException sqle) {
            logError(logTag, sqle);
            throw new BudgetManagerException(BudgetManagerException.DATABASE_ERROR, "Error occurred accessing DB in PRODUCT SUMMARY BEAN!", ""+sqle, sqle);
        } catch (Exception e) {
            logError(logTag, e);
            throw new BudgetManagerException(BudgetManagerException.UNKNOWN_ERROR, "Unexpected Error occurred in PRODUCT SUMMARY BEAN!", ""+e, e);
         } finally {
            BaseHelper.close(pstmt);
        }
    }

    /**
     * Update the product_sum.monthly_budget_remaining. The MBR must be modified by an amount and not directly set.
     *
     * There was code that was querying the values, modifying them, then setting the new values. That way is faulty because the MBR and DBR could be updated after it was queried (debits!).
     *
     * DO NOT directly update daily_budget_remaining and monthly_budget_remaining. They must be incremented/decremented in the database to assure atomicity across operations
     * such as debits and budget upgrades/downgrades.
     *
     * @param pdbConn
     * @param mbrAmountToAdd The amount to add to monthly_budget_remaining. Can be negative.
     * @return
     * @throws BudgetManagerException
     */
    public void modifyMonthlyBudgetRemaining(double mbrAmountToAdd) throws BudgetManagerException {
        String logTag = getLogTag(getProdInstId());
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            pstmt = connection.prepareStatement(UPDATE_PRODUCT_MBR_ADD_BUDGET);
            int i = 1;
            pstmt.setDouble(i++, mbrAmountToAdd);
            pstmt.setString(i++, getProdInstId());
            pstmt.setDate(i++, getUpdateDate());
            logInfo(logTag, "modifyBudgets: About to execute UPDATE_PRODUCT_MBR_ADD_BUDGET:");
            logInfo(logTag, pstmt.toString());
            pstmt.executeUpdate();
        }
        catch (Exception e) {
            logError(logTag, e);
            throw new BudgetManagerException(BudgetManagerException.UNKNOWN_ERROR, "Unexpected Error occurred during modifyBudgets.", e.getMessage(), e);
        }
        finally {
            BaseHelper.close(pstmt, rs);
        }
    }

    /**
     * Update the product_sum.daihly_budget_remaining. The DBR must be modified by an amount and not directly set.
     *
     * There was code that was querying the values, modifying them, then setting the new values. That way is faulty because the MBR and DBR could be updated after it was queried (debits!).
     *
     * DO NOT directly update daily_budget_remaining and monthly_budget_remaining. They must be incremented/decremented in the database to assure atomicity across operations
     * such as debits and budget upgrades/downgrades.
     *
     * @param pdbConn
     * @param dbrAmountToAdd The amount to add to daily_budget_remaining. Can be negative.
     * @return
     * @throws BudgetManagerException
     */
    public void modifyDailyBudgetRemaining(double dbrAmountToAdd) throws BudgetManagerException {
        String logTag = getLogTag(getProdInstId());
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            pstmt = connection.prepareStatement(UPDATE_PRODUCT_DBR_ADD_BUDGET);
            int i = 1;
            pstmt.setDouble(i++, dbrAmountToAdd);
            pstmt.setString(i++, getProdInstId());
            pstmt.setDate(i++, getUpdateDate());
            logInfo(logTag, "modifyBudgets: About to execute UPDATE_PRODUCT_DBR_ADD_BUDGET:");
            logInfo(logTag, pstmt.toString());
            pstmt.executeUpdate();
        }
        catch (Exception e) {
            logError(logTag, e);
            throw new BudgetManagerException(BudgetManagerException.UNKNOWN_ERROR, "Unexpected Error occurred during modifyBudgets.", e.getMessage(), e);
        }
        finally {
            BaseHelper.close(pstmt, rs);
        }
    }

    /**
     * Update the product_sum.daily_budget_remaining. The DBR is modified by subtracting current daily costs from a new total daily budget.
     *
     * There was code that was querying the values, modifying them, then setting the new values. That way is faulty because the MBR and DBR could be updated after it was queried (debits!).
     *
     * DO NOT directly update daily_budget_remaining and monthly_budget_remaining. They must be incremented/decremented in the database to assure atomicity across operations
     * such as debits and budget upgrades/downgrades.
     *
     * @param pdbConn
     * @param totalDailyBudget The total daily budget.
     * @return
     * @throws BudgetManagerException
     */
    public void modifyTotalDailyBudgetRemaining(double totalDailyBudget) throws BudgetManagerException {
        String logTag = getLogTag(getProdInstId());
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            totalDailyBudget = totalDailyBudget < 0 ? 0 : totalDailyBudget;
            pstmt = connection.prepareStatement(UPDATE_PRODUCT_DBR_USING_TOTAL);
            int i = 1;
            pstmt.setDouble(i++, totalDailyBudget);
            pstmt.setString(i++, getProdInstId());
            pstmt.setDate(i++, getUpdateDate());
            logInfo(logTag, "modifyBudgets:> About to execute UPDATE_PRODUCT_DBR_USING_TOTAL:");
            logInfo(logTag, pstmt.toString());
            pstmt.executeUpdate();
        }
        catch (Exception e) {
            logError(logTag, e);
            throw new BudgetManagerException(BudgetManagerException.UNKNOWN_ERROR, "Unexpected Error occurred during modifyBudgets.", e.getMessage(), e);
        }
        finally {
            BaseHelper.close(pstmt, rs);
        }
    }

    public double getMonthlyBudgetRemaining() {
        return monthlyBudgetRemaining;
    }

    public void setMonthlyBudgetRemaining(double monthlyBudgetRemaining) {
        this.monthlyBudgetRemaining = monthlyBudgetRemaining;
    }

    public double getDailyBudgetRemaining() {
        return dailyBudgetRemaining;
    }

    public void setDailyBudgetRemaining(double dailyBudgetRemaining) {
        this.dailyBudgetRemaining = dailyBudgetRemaining;
    }

    /** Get the total spend for this day. */
    public double getDailyTotal() { return dailyTotal; }

    /** Get the daily budget for this day. */
    public double getDailyBudget() { return dailyTotal + dailyBudgetRemaining; }

    /**
     * Get an array of ProductSummaryData objects from the given calendar up to today. Days with no data are be omitted.
     */
    public static ProductSummaryData[] getProductSummaryDataUpToToday(BaseLoggable baseLoggable, Connection conn, String prodInstId, Calendar cal)
            throws BudgetManagerException {
        Calendar[] calendars = CalendarUtil.getCalendarsUpToToday(cal);
        Collection<ProductSummaryData> list = new ArrayList<ProductSummaryData>();
        for (Calendar c : calendars) {
            ProductSummaryData productSum = new ProductSummaryData(baseLoggable);
            if (productSum.init(conn, prodInstId, c)) { // omit default if no data found
                list.add(productSum);
            }
        }
        return list.toArray(new ProductSummaryData[list.size()]);
    }

    /**
     * Simple unit test.
     */
    public static void main(String[] args) {
        Connection pdbConn = null;
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            pdbConn = DriverManager.getConnection("jdbc:mysql://eng2.dev.netsol.com:4300/adagent?user=adagent&password=adagent"); // prod pdb

            String prodInstId="WN.DEV.BING.0002";
            ProductSummaryData productSummaryData = new ProductSummaryData(prodInstId);
            productSummaryData.init(pdbConn, prodInstId, Calendar.getInstance());
            productSummaryData.persist();
            double mbrAmountToRemove = -30;
            productSummaryData.modifyMonthlyBudgetRemaining(mbrAmountToRemove);
            double totalDailyBudget = 3.70;
            productSummaryData.modifyTotalDailyBudgetRemaining(totalDailyBudget);
            double dbrAmountToRemove = -1;
            productSummaryData.modifyDailyBudgetRemaining(dbrAmountToRemove);
        }
        catch(Throwable e) {
            e.printStackTrace(System.out);
        }
        finally {
            try {
                if (pdbConn != null) {
                    pdbConn.close();
                }
            }
            catch (Throwable e) {
                e.printStackTrace(System.out);
            }
        }
    }
}
