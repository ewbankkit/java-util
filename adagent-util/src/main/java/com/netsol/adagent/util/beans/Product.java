/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.beans;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;

import com.netsol.adagent.util.BudgetManagerException;
import com.netsol.adagent.util.CalendarUtil;
import com.netsol.adagent.util.codes.ProductFeatureStatus;
import com.netsol.adagent.util.dbhelpers.BaseHelper;
import com.netsol.adagent.util.dbhelpers.ProductFeatureHelper;
import com.netsol.adagent.util.log.BaseLoggable;

public class Product extends BaseBudgetManagerData {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:04 Product.java NSI";

    public enum RenewalSourceType  { internal, external };
    public enum RenewalCycleType  { anniversary, firstOfMonth };

    // These fields have to be declared 'public' since their values may be retrieved by reflection via dirtyFields.
    public Boolean adjustBudget;
    public Double baseTarget;
    public Double currentTarget;
    public Double currentMarginAmount;
    public Date expirationDate;
    public Double spendAggressiveness;
    public Date startDate;
    public String updatedBySystem;
    public String updatedByUser;

    // These fields have to be declared 'public' since their values may be retrieved by reflection via dirtyPricingFields.
    public Double convRate;
    public Integer convRateWeeks;
    public Double cpcMarkup;
    public Double multiplier;
    public Integer tier;

    private Date activationDate;
    private String amountType;
    private Double avgTicket;
    private Boolean cpcSensitive;
    private Timestamp pricingUpdateDate;
    private String status;
    private Integer termQty;
    private Timestamp updatedDate;
    private Integer channelId;
    private Long prodId;
    private int[] featureIds;

    private RenewalSourceType renewalSourceType;
    private RenewalCycleType renewalCycleType;
    private Boolean rolloverBudget;

    private Map<String, String> dirtyFields;
    private Map<String, String> dirtyPricingFields;

    /**
     * Constructor.
     */
    public Product(String logComponent) {
        super(logComponent);
    }

    /**
     * Constructor.
     */
    public Product(Log logger) {
        super(logger);
    }

    /**
     * Constructor.
     */
    public Product(BaseLoggable baseLoggable) {
        super(baseLoggable);
    }

    public void init(Connection gdbConn, Connection pdbConn, String prodInstId) throws BudgetManagerException {
        logTag = getLogTag(prodInstId);
        logInfo(logTag, "PRODUCT BEAN -> init(): *** ENTER ***");

        final String GET_PRODUCT_SQL =
            "SELECT" +
            "  p.base_target," +
            "  p.current_target," +
            "  p.current_margin_amount, " +
            "  p.start_date," +
            "  p.expiration_date," +
            "  p.activation_date," +
            "  p.status," +
            "  p.term_qty," +
            "  p.amount_type," +
            "  p.spend_aggressiveness," +
            "  p.adjust_budget," +
            "  p.updated_date," +
            "  p.channel_id, " +
            "  p.prod_id, " +
            "  p.renewal_source_type, " +
            "  p.renewal_cycle_type, " +
            "  p.rollover_budget " +
            "FROM" +
            "  product AS p " +
            "WHERE" +
            "  p.prod_inst_id = ?;";

        final String GET_PRODUCT_PRICING_SQL =
            "SELECT" +
            "  pp.tier," +
            "  pp.avg_ticket," +
            "  pp.cpc_markup," +
            "  pp.multiplier," +
            "  pp.conv_rate," +
            "  pp.conv_rate_weeks," +
            "  pp.cpc_sensitive," +
            "  pp.update_date " +
            "FROM" +
            "  product_pricing AS pp " +
            "WHERE" +
            "  pp.prod_inst_id = ?;";

        PreparedStatement pstmtProduct = null;
        ResultSet rsProduct = null;
        PreparedStatement pstmtProductPricing = null;
        ResultSet rsProductPricing = null;

        try {
            if (gdbConn == null || gdbConn.isClosed()) {
                logError(logTag, "Error initializing PRODUCT data: Provided GDB connection is NULL or CLOSED!");
                throw new BudgetManagerException(BudgetManagerException.DATABASE_ERROR, "Error initializing PRODUCT data: Provided GDB connection is NULL or CLOSED!");
            }

            if (pdbConn == null || pdbConn.isClosed()) {
                logError(logTag, "Error initializing PRODUCT data: Provided PDB connection is NULL or CLOSED!");
                throw new BudgetManagerException(BudgetManagerException.DATABASE_ERROR, "Error initializing PRODUCT data: Provided PDB connection is NULL or CLOSED!");
            }

            pstmtProduct = gdbConn.prepareStatement(GET_PRODUCT_SQL);

            pstmtProduct.setString(1, prodInstId);
            logDebug(logTag, "PRODUCT BEAN -> init(): About to execute GET_PRODUCT_SQL: " + pstmtProduct);
            rsProduct = pstmtProduct.executeQuery();

            if (rsProduct == null) {
                logError(logTag, "PRODUCT BEAN -> init(): Error retrieving PRODUCT data from the DB: Null Result Set!");
                throw new SQLException("Error retrieving PRODUCT data from the DB: Null Result Set!");
            }

            if (rsProduct.next()) {
                setProdInstId(prodInstId);
                baseTarget = rsProduct.getDouble("base_target");
                currentTarget = rsProduct.getDouble("current_target");
                currentMarginAmount = rsProduct.getDouble("current_margin_amount");
                startDate = rsProduct.getDate("start_date");
                expirationDate = rsProduct.getDate("expiration_date");
                activationDate = rsProduct.getDate("activation_date");
                status = rsProduct.getString("status");
                termQty = rsProduct.getInt("term_qty");
                amountType = rsProduct.getString("amount_type");
                spendAggressiveness = rsProduct.getDouble("spend_aggressiveness");
                adjustBudget = rsProduct.getBoolean("adjust_budget");
                updatedDate = rsProduct.getTimestamp("updated_date");
                channelId = rsProduct.getInt("channel_id");
                prodId = rsProduct.getLong("prod_id");
                String rst = rsProduct.getString("renewal_source_type");
                renewalSourceType = rst != null ? RenewalSourceType.valueOf(rst) : null;
                String rct = rsProduct.getString("renewal_cycle_type");
                renewalCycleType = rct != null ? RenewalCycleType.valueOf(rct) : null;
                rolloverBudget = rsProduct.getBoolean("rollover_budget");
            } else {
                logError(logTag, "PRODUCT BEAN -> init(): Error retrieving PRODUCT data from the DB: Product Not Found!");
                throw new SQLException("Error retrieving PRODUCT data from the DB: Product Not Found!");
            }

            pstmtProductPricing = pdbConn.prepareStatement(GET_PRODUCT_PRICING_SQL);

            pstmtProductPricing.setString(1, prodInstId);
            logDebug(logTag, "PRODUCT BEAN -> init(): About to execute GET_PRODUCT_PRICING_SQL: " + pstmtProductPricing);
            rsProductPricing = pstmtProductPricing.executeQuery();

            if ((rsProductPricing != null) && rsProductPricing.next()) {
                tier = rsProductPricing.getInt("tier");
                avgTicket = rsProductPricing.getDouble("avg_ticket");
                cpcMarkup = rsProductPricing.getDouble("cpc_markup");
                multiplier = rsProductPricing.getDouble("multiplier");
                convRate = rsProductPricing.getDouble("conv_rate");
                convRateWeeks = rsProductPricing.getInt("conv_rate_weeks");
                cpcSensitive = rsProductPricing.getBoolean("cpc_sensitive");
                pricingUpdateDate = rsProductPricing.getTimestamp("update_date");
            }

            logDebug(logTag, "PRODUCT BEAN -> init(): About to get non-deleted feature IDs");
            featureIds = new ProductFeatureHelper(this).getFeatureIdsExcludingStatuses(logTag, pdbConn, prodInstId, ProductFeatureStatus.DELETED);

            logInfo(logTag, toString());

            dirtyFields = new HashMap<String, String>();
            dirtyPricingFields = new HashMap<String, String>();

            logInfo(logTag, "PRODUCT BEAN -> init(): *** COMPLETE ***");
        } catch (SQLException sqle) {
            logError(logTag, sqle);
            throw new BudgetManagerException(BudgetManagerException.DATABASE_ERROR, "Error occurred accessing DB in PRODUCT BEAN!", ""+sqle, sqle);
        } catch (Exception e) {
            logError(logTag, e);
            throw new BudgetManagerException(BudgetManagerException.UNKNOWN_ERROR, "Unexpected Error occurred in PRODUCT BEAN!", ""+e, e);
        } finally {
            BaseHelper.close(pstmtProduct, pstmtProductPricing);
            BaseHelper.close(rsProduct, rsProductPricing);
        }
    }

    public void persist(Connection gdbConn, Connection pdbConn) throws BudgetManagerException {
        logInfo(logTag, "PRODUCT BEAN -> persist(): *** ENTER ***");

        PreparedStatement pstmt = null;
        PreparedStatement pstmt2 = null;

        try {
            if (gdbConn == null || gdbConn.isClosed()) {
                logError(logTag, "Error persisting PRODUCT data: Connection is NULL or CLOSED!");
                throw new BudgetManagerException(BudgetManagerException.DATABASE_ERROR, "Error persisting PRODUCT data: Connection is NULL or CLOSED!");
            }

            if (!dirtyFields.isEmpty()) {
                StringBuilder sb = new StringBuilder("UPDATE product SET ");

                for (String field : dirtyFields.keySet()) {
                    sb.append(dirtyFields.get(field)).append(" = ?").append(", ");
                }
                sb.append(" updated_date = NOW() ");
                sb.append(" WHERE prod_inst_id = '").append(getProdInstId()).append("'");
                sb.append(" AND updated_date = ?");

                pstmt = gdbConn.prepareStatement(sb.toString());
                int numFields = 1;
                for (String field : dirtyFields.keySet()) {
                    String currTypeName = getClass().getDeclaredField(field).getType().getName();
                    if (currTypeName.equals("java.lang.Double")) pstmt.setDouble(numFields, (Double)(getClass().getField(field).get(this)));
                    else if (currTypeName.equals("java.lang.Integer")) pstmt.setInt(numFields, (Integer)(getClass().getField(field).get(this)));
                    else if (currTypeName.equals("java.lang.String")) pstmt.setString(numFields, (String)(getClass().getField(field).get(this)));
                    else if (currTypeName.equals("java.lang.Boolean")) pstmt.setBoolean(numFields, (Boolean)(getClass().getField(field).get(this)));
                    else if (currTypeName.equals("java.sql.Date")) pstmt.setDate(numFields, (Date)(getClass().getField(field).get(this)));
                    else if (currTypeName.equals("java.sql.Timestamp")) pstmt.setTimestamp(numFields, (Timestamp)(getClass().getField(field).get(this)));
                    numFields++;
                }
                pstmt.setTimestamp(numFields, updatedDate);
                logDebug(logTag, "PRODUCT BEAN -> persist(): About to execute update product: " + pstmt2);
                pstmt.executeUpdate();
            }

            if (!dirtyPricingFields.isEmpty()) {
                StringBuilder sb2 = new StringBuilder("UPDATE product_pricing SET ");
                for (String field : dirtyPricingFields.keySet()) {
                    sb2.append(dirtyPricingFields.get(field)).append(" = ?").append(", ");
                }
                sb2.deleteCharAt(sb2.length()-2);
                sb2.append(" WHERE prod_inst_id = '").append(getProdInstId()).append("'");
                sb2.append(" AND update_date = ?");

                pstmt2 = pdbConn.prepareStatement(sb2.toString());
                int numPricingFields = 1;
                for (String field : dirtyPricingFields.keySet()) {
                    String currTypeName = getClass().getDeclaredField(field).getType().getName();
                    if (currTypeName.equals("java.lang.Double")) pstmt2.setDouble(numPricingFields, (Double)(getClass().getField(field).get(this)));
                    else if (currTypeName.equals("java.lang.Integer")) pstmt2.setInt(numPricingFields, (Integer)(getClass().getField(field).get(this)));
                    else if (currTypeName.equals("java.lang.Boolean")) pstmt2.setBoolean(numPricingFields, (Boolean)(getClass().getField(field).get(this)));
                    numPricingFields++;
                }
                pstmt2.setTimestamp(numPricingFields, pricingUpdateDate);
                logDebug(logTag, "PRODUCT BEAN -> persist(): About to execute update product_pricing: " + pstmt2);
                pstmt2.executeUpdate();
            }

            logInfo(logTag, "PRODUCT BEAN -> persist(): *** COMPLETE ***");
        } catch (SQLException sqle) {
            logError(logTag, sqle);
            throw new BudgetManagerException(BudgetManagerException.DATABASE_ERROR, "Error occurred accessing DB in PRODUCT BEAN!", ""+sqle, sqle);
        } catch (Exception e) {
            logError(logTag, e);
            throw new BudgetManagerException(BudgetManagerException.UNKNOWN_ERROR, "Unexpected Error occurred in PRODUCT BEAN!", ""+e, e);
         } finally {
            BaseHelper.close(pstmt, pstmt2);
        }
    }

    /**
     * Is the product expired?
     * @return True if today is not the expiration date and today is after the expiration date.
     */
    public boolean isExpired() {
        Calendar expirationCal = Calendar.getInstance();
        expirationCal.setTimeInMillis(expirationDate.getTime());
        Calendar today = Calendar.getInstance();
        return (!(CalendarUtil.isSameDay(expirationCal, today)) && today.after(expirationCal));
    }

    private Calendar getNearestEndOfCycle(Calendar from) {
        return CalendarUtil.getNearestEndOfCycle(from, CalendarUtil.dateToCalendar(expirationDate));
    }

    private Calendar getNearestEndOfCycle() {
        return CalendarUtil.getNearestEndOfCycle(CalendarUtil.dateToCalendar(expirationDate));
    }

    public double getDaysUntilExpiration(Calendar from) {
        return (double)CalendarUtil.getDaysBetween(from, getNearestEndOfCycle(from));
    }

    public double getNumberOfDaysInCurrentCycle() {
        return (double)CalendarUtil.getDaysBetween(CalendarUtil.dateToCalendar(startDate), getNearestEndOfCycle());
    }

    public double getStandardSpend() {
        return currentTarget / getNumberOfDaysInCurrentCycle();
    }

    public double getTargetMonthlyBudgetRemaining(Calendar from) {
        return getStandardSpend() * getDaysUntilExpiration(from);
    }

    public double getValuePerClick(Connection pdbConn) throws BudgetManagerException {
        if (convRate != 0.0) {
            return avgTicket * Math.min(convRate, budgetManagerHelper.getMaxConversionRate(pdbConn, getChannelId()));
        } else {
            // if there was no conversion rate value, use the default
            return avgTicket * budgetManagerHelper.getDefaultConversionRate(pdbConn, getChannelId());
        }
    }

    public Double getBaseTarget() {
        return baseTarget;
    }

    public Double getCurrentTarget() {
        return currentTarget;
    }

    public Double getCurrentMarginAmount() {
        return currentMarginAmount;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public Date getActivationDate() {
        return activationDate;
    }

    public String getStatus() {
        return status;
    }

    public Integer getTermQty() {
        return termQty;
    }

    public String getAmountType() {
        return amountType;
    }

    public Double getSpendAggressiveness() {
        return spendAggressiveness;
    }

    public Boolean isAdjustBudget() {
        return adjustBudget;
    }

    public Timestamp getUpdatedDate() {
        return updatedDate;
    }

    @NotInToString
    public String getUpdatedByUser() {
        return updatedByUser;
    }

    @NotInToString
    public String getUpdatedBySystem() {
        return updatedBySystem;
    }

    public Integer getTier() {
        return tier;
    }

    public Double getAvgTicket() {
        return avgTicket;
    }

    public Double getCpcMarkup() {
        return cpcMarkup;
    }

    public Double getMultiplier() {
        return multiplier;
    }

    public Double getConvRate() {
        return convRate;
    }

    public Integer getConvRateWeeks() {
        return convRateWeeks;
    }

    public Integer getChannelId() {
        return channelId;
    }

    public Long getProdId() {
        return prodId;
    }

    public int[] getFeatureIds() {
        return featureIds;
    }

    public RenewalSourceType getRenewalSourceType() {
        return renewalSourceType;
    }

    public RenewalCycleType getRenewalCycleType() {
        return renewalCycleType;
    }

    public Boolean getRolloverBudget() {
        return rolloverBudget;
    }

    public Boolean isCpcSensitive() {
        return cpcSensitive;
    }

    public void setBaseTarget(Double baseTarget) {
        this.baseTarget = baseTarget;
        dirtyFields.put("baseTarget", "base_target");
    }

    public void setCurrentTarget(Double currentTarget) {
        this.currentTarget = currentTarget;
        dirtyFields.put("currentTarget", "current_target");
    }

    public void setCurrentMarginAmount(Double currentMarginAmount) {
        this.currentMarginAmount = currentMarginAmount;
        dirtyFields.put("currentMarginAmount", "current_margin_amount");
    }
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
        dirtyFields.put("startDate", "start_date");
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
        dirtyFields.put("expirationDate", "expiration_date");
    }

    public void setSpendAggressiveness(Double spendAggressiveness) {
        this.spendAggressiveness = spendAggressiveness;
        dirtyFields.put("spendAggressiveness", "spend_aggressiveness");
    }

    public void setAdjustBudget(Boolean adjustBudget) {
        this.adjustBudget = adjustBudget;
        dirtyFields.put("adjustBudget", "adjust_budget");
    }

    public void setUpdatedByUser(String updatedByUser) {
        this.updatedByUser = updatedByUser;
        dirtyFields.put("updatedByUser", "updated_by_user");
    }

    public void setUpdatedBySystem(String updatedBySystem) {
        this.updatedBySystem = updatedBySystem;
        dirtyFields.put("updatedBySystem", "updated_by_system");
    }

    public void setTier(Integer tier) {
        this.tier = tier;
        dirtyPricingFields.put("tier", "tier");
    }

    public void setCpcMarkup(Double cpcMarkup) {
        this.cpcMarkup = cpcMarkup;
        dirtyPricingFields.put("cpcMarkup", "cpc_markup");
    }

    public void setMultiplier(Double multiplier) {
        this.multiplier = multiplier;
        dirtyPricingFields.put("multiplier", "multiplier");
    }

    public void setConvRate(Double convRate) {
        this.convRate = convRate;
        dirtyPricingFields.put("convRate", "conv_rate");
    }

    public void setConvRateWeeks(Integer convRateWeeks) {
        this.convRateWeeks = convRateWeeks;
        dirtyPricingFields.put("convRateWeeks", "conv_rate_weeks");
    }

    //
    // Unit test.
    //

    public static void main(String[] args) throws Exception {
        Connection gdbConn = null;
        Connection pdbConn = null;
        try {
            gdbConn = BaseHelper.createDevGdbConnection();
            pdbConn = BaseHelper.createDevPdb1Connection();
            Product product = new Product(Product.class.getName());
            product.init(gdbConn, pdbConn, "WN.DEV.BING.0002");
            System.out.println(product);
        }
        finally {
            BaseHelper.close(gdbConn);
            BaseHelper.close(pdbConn);
        }
    }
}
