/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util;

import static com.netsol.adagent.util.beans.BaseBudgetManagerData.getLogTag;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.impl.SimpleLog;

import com.netsol.adagent.util.advendor.CampaignUtil;
import com.netsol.adagent.util.advendor.CampaignUtil.CampaignData;
import com.netsol.adagent.util.beans.BudgetCycleData;
import com.netsol.adagent.util.beans.BudgetCycleProductData;
import com.netsol.adagent.util.beans.Campaign;
import com.netsol.adagent.util.beans.CampaignList;
import com.netsol.adagent.util.beans.CampaignSummaryData;
import com.netsol.adagent.util.beans.DebitableItem;
import com.netsol.adagent.util.beans.GenericClick;
import com.netsol.adagent.util.beans.MaxBidData;
import com.netsol.adagent.util.beans.PPCAdClick;
import com.netsol.adagent.util.beans.PPCLead;
import com.netsol.adagent.util.beans.PpcProductDetail;
import com.netsol.adagent.util.beans.Product;
import com.netsol.adagent.util.beans.ProductDebitConfig;
import com.netsol.adagent.util.beans.ProductSummaryData;
import com.netsol.adagent.util.beans.SuperpagesClick;
import com.netsol.adagent.util.beans.Target;
import com.netsol.adagent.util.beans.TargetVendor;
import com.netsol.adagent.util.beans.VendorBudgetAllocations;
import com.netsol.adagent.util.beans.VendorCredentials;
import com.netsol.adagent.util.budgetadj.BudgetAdjustment;
import com.netsol.adagent.util.budgetadj.BudgetAdjustmentFactory;
import com.netsol.adagent.util.codes.CampaignStatus;
import com.netsol.adagent.util.codes.FeatureId;
import com.netsol.adagent.util.codes.LeadTypePrefix;
import com.netsol.adagent.util.codes.ProductStatus;
import com.netsol.adagent.util.codes.VendorId;
import com.netsol.adagent.util.dbhelpers.AdScheduleHelper;
import com.netsol.adagent.util.dbhelpers.BaseHelper;
import com.netsol.adagent.util.dbhelpers.BudgetManagerHelper;
import com.netsol.adagent.util.dbhelpers.PpcProductDetailHelper;
import com.netsol.adagent.util.dbhelpers.TargetHelper;
import com.netsol.adagent.util.dbhelpers.VendorCredentialsHelper;
import com.netsol.adagent.util.log.BaseLoggable;
import com.netsol.adagent.util.productlifecycle.BudgetAdjustmentEvent;
import com.netsol.adagent.util.productlifecycle.BudgetRenewalEvent;
import com.netsol.adagent.util.productlifecycle.ProductLifeCycleEventFactory;
import com.netsol.adagent.util.productlifecycle.ProductLifeCycleEventHelper;
import com.netsol.adagent.util.productlifecycle.ProvisioningRenewalEvent;
import com.netsol.adagent.vendor.client.factory.AdAgentVendorClientFactory;
import com.netsol.adagent.vendor.client.intf.AdAgentVendorClientProcessor;
import com.netsol.adagent.vendor.client.intf.Credential;
import com.netsol.vendor.beans.Credentials;
import com.netsol.vendor.client.ClientFactories;

/**
 * Perform budget-related operations.
 *
 * Note: The constructors are temporarily taking vendor service objects until BM becomes its own service in the very near future. It's a necessary hack for right now.
 *
 * @author Adam S. Vernon (mostly rewritten)
 * @author mho (original version)
 * @since 27 Mar 2008
 */
public final class BudgetManager extends BaseLoggable {
    public static final String sccsId = "@(#) adagent-util_d16.18.0.4 09/21/12 13:50:47 BudgetManager.java NSI";

    private static final String updatedByUser = "Budget Manager";

    private final AdScheduleHelper adScheduleHelper;
    private final BudgetManagerHelper budgetManagerHelper;
    private AdAgentVendorClientProcessor adAgentVendorClientProcessor;
    private Credential adAgentVendorCredential;
    private Credentials adAgentWSCredentials;
    private boolean stubMode;

    private final static DateFormat format = new ThreadSafeDateFormat(new SimpleDateFormat("y-M-d"));
    private final static Map<Integer, String> valueColumnMapping = new MapBuilder<Integer, String>().
        put(Integer.valueOf(LeadTypePrefix.PHONE_LEAD.getId()), LeadTypePrefix.PHONE_LEAD.getPrefix()).
        put(Integer.valueOf(LeadTypePrefix.FORM_LEAD.getId()), LeadTypePrefix.FORM_LEAD.getPrefix()).
        put(Integer.valueOf(LeadTypePrefix.EMAIL_LEAD.getId()), LeadTypePrefix.EMAIL_LEAD.getPrefix()).
        put(Integer.valueOf(LeadTypePrefix.HIGH_VALUE_PAGE_LEAD.getId()), LeadTypePrefix.HIGH_VALUE_PAGE_LEAD.getPrefix()).
        put(Integer.valueOf(LeadTypePrefix.SHOPPING_CART_LEAD.getId()), LeadTypePrefix.SHOPPING_CART_LEAD.getPrefix()).unmodifiableMap();

    /**
     * Constructor.
     */
    public BudgetManager(String logComponent, AdAgentVendorClientProcessor adAgentVendorClientProcessor, Credential adAgentVendorCredential, Credentials adAgentWSCredentials) {
        this(logComponent, null, adAgentVendorClientProcessor, adAgentVendorCredential, adAgentWSCredentials);
    }

    /**
     * Constructor.
     */
    public BudgetManager(Log logger, AdAgentVendorClientProcessor adAgentVendorClientProcessor, Credential adAgentVendorCredential, Credentials adAgentWSCredentials) {
        this(null, logger, adAgentVendorClientProcessor, adAgentVendorCredential, adAgentWSCredentials);
    }

    /**
     * Constructor.
     */
    private BudgetManager(String logComponent, Log logger, AdAgentVendorClientProcessor adAgentVendorClientProcessor, Credential adAgentVendorCredential, Credentials adAgentWSCredentials) {
        super(logComponent, logger);
        adScheduleHelper = new AdScheduleHelper(this);
        budgetManagerHelper = new BudgetManagerHelper(this);
        this.adAgentVendorClientProcessor = adAgentVendorClientProcessor;
        this.adAgentVendorCredential = adAgentVendorCredential;
        this.adAgentWSCredentials = adAgentWSCredentials;
        logInfo(null, sccsId);
    }

    public void setStubMode() {
        stubMode = true;
    }

    public boolean isStubMode() {
        return stubMode;
    }

    /**
     * Debit a click.
     */
    public void debitClick(Connection gdbConn, Connection pdbConn, String prodInstId, Long hitId, Date clickDate, long nsCampaignId,
            long nsAdGroupId, long nsKeywordId, long nsAdId, BudgetAdjustment.System system, int vendorId)
        throws BudgetManagerException {

        if (stubMode) {
            return;
        } else {
            PPCAdClick click;
            if (vendorId == VendorId.GENERIC_CLICK_SOURCE) {
                click = new GenericClick(this);
            }
            else if (vendorId == VendorId.SUPERPAGES) {
                click = new SuperpagesClick(this);
            }
            else {
                click = new PPCAdClick(this);
            }

            clickDate = new BudgetManagerHelper(this).shiftClickOrLeadDate(gdbConn, prodInstId, vendorId, clickDate);
            click.setDate(clickDate);
            click.setNsAdGroupId(nsAdGroupId);
            click.setNsAdId(nsAdId);
            click.setNsCampaignId(nsCampaignId);
            click.setNsKeywordId(nsKeywordId);
            click.setProdInstId(prodInstId);
            click.setSystem(system);
            click.setHitId(hitId);
            click.setVendorId(vendorId);

            debit(gdbConn, pdbConn, click);
        }
    }

    /**
     * Debit a lead.
     */
    public void debitLead(Connection gdbConn, Connection pdbConn, String prodInstId, long leadId,
            BudgetAdjustment.System system) throws BudgetManagerException {
        if (stubMode) {
            return;
        } else {
            debitLead(gdbConn, pdbConn, prodInstId, leadId, 0.0, system);
        }
    }

    /**
     * Debit a lead.
     */
    public void debitLead(Connection gdbConn, Connection pdbConn, String prodInstId, long leadId, double tollCost,
            BudgetAdjustment.System system) throws BudgetManagerException {
        if (stubMode) {
            return;
        } else {
            String logTag = getLogTag(prodInstId);

            try {
                Product product = new Product(this);
                product.init(gdbConn, pdbConn, prodInstId);

                // Only perform debit actions for PPC/PCT leads.
                if (!FeatureId.anyFeatureIsEnabled(product.getFeatureIds(), FeatureId.PPC, FeatureId.PCT)) {
                    return;
                }

                // init the lead so we can operate
                PPCLead lead = new PPCLead(this);
                lead.init(pdbConn, product.getChannelId(), prodInstId, leadId, tollCost);
                lead.setSystem(system);
                logInfo(logTag, "Initialized PPCLead: {nsCampaignId=" + lead.getNsCampaignId() + ", nsAdGroupId=" + lead.getNsAdGroupId() +
                        ", nsAdId=" + lead.getNsAdId() + ", nsKeywordId=" + lead.getNsKeywordId() + "}");

                debit(gdbConn, pdbConn, lead);
            } catch (BudgetManagerException bme) {
                throw bme;
            } catch (Exception e) {
                logError(logTag, e);
                throw new BudgetManagerException(BudgetManagerException.UNKNOWN_ERROR, "Unknown Error", ""+e, e);
            }
        }
    }

    /**
     * Debit a debitable item.
     */
    public void debit(Connection gdbConn, Connection pdbConn, DebitableItem item) throws BudgetManagerException {
        if (stubMode) {
            return;
        } else {
            String prodInstId = item.getProdInstId();
            String logTag = getLogTag(prodInstId);

            // Initialize product data bean
            Product product = new Product(this);
            product.init(gdbConn, pdbConn, prodInstId);

            // Make sure the product is ACTIVE or PENDING_DELETE.
            if (!product.getStatus().equals(ProductStatus.ACTIVE) && !product.getStatus().equals(ProductStatus.PENDING_DELETE)) {
                logError(logTag, "Debit was called on a non-active product!");
                throw new BudgetManagerException(BudgetManagerException.DEBIT_INACTIVE_PRODUCT_ERROR, "Debit was called on a non-active product!");
            }

            // If product summary data does not exist for the click date, then we adjust for that day.
            ProductSummaryData productSummaryData = new ProductSummaryData(this);
            boolean dataFoundForToday = productSummaryData.init(pdbConn, prodInstId, item.getDateAsCalendar());
            if (!dataFoundForToday) {
                logInfo(logTag, "debit -> entering lazy adjust...");
                adjustForDay(gdbConn, pdbConn, prodInstId, item.getDateAsCalendar(), item.getSystem());
            }

            if (!item.validate(pdbConn)) {
                // it's not available for debiting so return
                return;
            }

            // calculate the costs for the item
            item.calculateCosts(pdbConn, product);

            // debit the costs of the item from the ns_[entity]_sum tables, increment the count values,
            // do any additional persistence logic
            item.debit(pdbConn);

            // insert the budget_adj record
            item.insertBudgetAdjustment(pdbConn);

            // Only perform overage and threshold checks for PPC-based products.
            if (FeatureId.featureIsEnabled(product.getFeatureIds(), FeatureId.PPC)) {
                // check click and lead count thresholds
                checkThresholds(pdbConn, product);
                // now validate budget remaining
                checkBudgetOverage(pdbConn, product, item);
            }
        }
    }

    /**
     * Adjust budgets for the specified date (usually today). This method is designed to only be run once per day.
     */
    public void adjustForDay(Connection gdbConn, Connection pdbConn, String prodInstId, Calendar dayToAdjust, BudgetAdjustment.System system) throws BudgetManagerException {
        adjustForDay(gdbConn, pdbConn, prodInstId, dayToAdjust, system, false);
    }

    /**
     * Adjust budgets for the specified date (usually today). This method is designed to only be run once per day.
     */
    public void adjustForDay(Connection gdbConn, Connection pdbConn, String prodInstId, Calendar dayToAdjust, BudgetAdjustment.System system, boolean isActivate)
        throws BudgetManagerException {

        if (stubMode) {
            return;
        }
        else {
            String logTag = getLogTag(prodInstId);

            // Initialize product.
            Product product = new Product(this);
            product.init(gdbConn, pdbConn, prodInstId);

            // Determine if adjust is needed:
            // Get the most recent product summary row and check to see if adjust has already been run.
            ProductSummaryData productSummaryData = new ProductSummaryData(this);
            boolean dataFoundForToday = productSummaryData.init(pdbConn, prodInstId, dayToAdjust);
            logInfo(logTag, "adjustForDay: MBR for " + prodInstId + "(" + format.format(productSummaryData.getUpdateDate()) + "): " + productSummaryData.getMonthlyBudgetRemaining());
            if (dataFoundForToday) {
                logInfo(logTag, "adjustForDay: product_sum data already exists; Product will NOT be adjusted.");
                return;
            }

            // We are adjusting. Create the BudgetAdjustmentEvent.
            BudgetAdjustmentEvent budgetAdjustmentEvent = ProductLifeCycleEventFactory.createBudgetAdjustmentEvent();
            budgetAdjustmentEvent.setAdjustmentDate(dayToAdjust.getTime());
            budgetAdjustmentEvent.setProdInstId(prodInstId);
            budgetAdjustmentEvent.setOldMonthlyBudgetRemaining(productSummaryData.getMonthlyBudgetRemaining());
            budgetAdjustmentEvent.setOldDailyBudgetRemaining(productSummaryData.getDailyBudgetRemaining());

            // Activation.
            if (isActivate) {
                productSummaryData.setMonthlyBudgetRemaining(product.getCurrentTarget());
            }

            // Determine if a budget renewal is needed. If the expiration date is in the past, a budget renewal will be attempted.
            if (product.isExpired()) {
                budgetAdjustmentEvent.setRenewal(true);
                // Renew the budget. The product's current target will be updated.
                if (renewBudget(gdbConn, pdbConn, prodInstId, system)) {
                    // Re-query the product to get the new current target.
                    product = new Product(this);
                    product.init(gdbConn, pdbConn, prodInstId);
                    productSummaryData.setMonthlyBudgetRemaining(product.getCurrentTarget());
                }
            }

            // Create the product_sum record. We don't know the daily budget until after campaigns are adjusted, so we initialize it to zero.
            productSummaryData.setUpdateDate(dayToAdjust);
            productSummaryData.setDailyBudgetRemaining(0);
            productSummaryData.persist();

            // Create the ns_campaign_sum records. While I don't think this step is necessary, this is the legacy behavior, and I'm preserving it for consistency.
            CampaignList campaigns = budgetManagerHelper.getCampaigns(pdbConn, product.getProdInstId(), new String[] { CampaignStatus.ACTIVE, CampaignStatus.SYSTEM_PAUSE});
            if (campaigns != null && !campaigns.isEmpty()) {
                if (campaigns != null && !campaigns.isEmpty()) {
                    CampaignSummaryData campaignSummary = new CampaignSummaryData(this);
                    campaignSummary.init(pdbConn, prodInstId);
                    campaignSummary.setCampaigns(campaigns);
                    campaignSummary.setUpdateDate(productSummaryData.getUpdateDate());
                    campaignSummary.persist();
                }
            }

            // Adjust budgets and statuses, but only if we are adjusting today.
            if (CalendarUtil.isSameDay(dayToAdjust, Calendar.getInstance())) {
                try {
                    adjustBudgetsAndStatuses(gdbConn, pdbConn, prodInstId);
                }
                catch (Exception e) {
                    throw new BudgetManagerException(BudgetManagerException.UNKNOWN_ERROR, "adjustForDay: An unexpected error occurred in adjustBudgetsAndStatuses.", e.getMessage(), e);
                }

                // Re-query ther product summary to get the MBR and DBR.
                productSummaryData = new ProductSummaryData(this);
                productSummaryData.init(pdbConn, prodInstId, dayToAdjust);
            }

            // Record the event.
            budgetAdjustmentEvent.setNewMonthlyBudgetRemaining(productSummaryData.getMonthlyBudgetRemaining());
            budgetAdjustmentEvent.setNewDailyBudgetRemaining(productSummaryData.getDailyBudgetRemaining());
            new ProductLifeCycleEventHelper(this).insertProductLifeCycleEvent(prodInstId, pdbConn, budgetAdjustmentEvent);
        }
    }

    /**
     * Calculate a product's tier.
     */
    public void calculateTier(Connection gdbConn, Connection pdbConn, String prodInstId, boolean calcConvRate) throws BudgetManagerException {
        if (stubMode) {
            return;
        } else {
            String logTag = getLogTag(prodInstId);

            // get pricing info
            Product product = new Product(this);
            product.init(gdbConn, pdbConn, prodInstId);
            logInfo(logTag, "calculateTier -> avgTicket: " + product.getAvgTicket());
            logInfo(logTag, "calculateTier -> convRate: " + product.getConvRate());
            logInfo(logTag, "calculateTier -> convRateWeeks: " + product.getConvRateWeeks());

            // calculate new conversion rate if necessary
            if (calcConvRate) { budgetManagerHelper.setCurrentConversionRate(pdbConn, product); }

            // now, get the tier value
            budgetManagerHelper.setTierValues(pdbConn, product);

            // now save all the pricing data
            product.setUpdatedBySystem("BM.calculateTier");
            product.setUpdatedByUser(updatedByUser);
            product.persist(gdbConn, pdbConn);
        }
    }

    /**
     * Activate a product.
     */
    public void activateProduct(Connection gdbConn, Connection pdbConn, String prodInstId) throws BudgetManagerException {
        if (stubMode) {
            return;
        } else {
            // Remove this code soon.
            // first, calculate the tier values
            calculateTier(gdbConn, pdbConn, prodInstId, false);
            // then, calculate the cpc markup value
            calculateCpcMarkup(gdbConn, pdbConn, prodInstId);

            adjustForDay(gdbConn, pdbConn, prodInstId, Calendar.getInstance(), BudgetAdjustment.System.WS_ADAGENT, true);
        }
    }

    /**
     * Reactivate a product.
     */
    public void reactivateProduct(Connection gdbConn, Connection pdbConn, String prodInstId, Calendar reactivationDate,
            BudgetAdjustment.System system) throws BudgetManagerException {
        if (stubMode) {
            return;
        } else {
            String logTag = getLogTag(prodInstId);
            logInfo(logTag, "Reactivating "+prodInstId+" on "+format.format(reactivationDate.getTime()));
            adjustForDay(gdbConn, pdbConn, prodInstId, reactivationDate, system);
        }
    }

    /**
     * Renew budget for the specified date. The product table will be update with the new:
     * - start and expiration dates
     * - base target, current target and current margin amount
     *
     * @return true if the product was successfully renewed, false otherwise.
     */
    private boolean renewBudget(Connection gdbConn, Connection pdbConn, String prodInstId, BudgetAdjustment.System system) throws BudgetManagerException {
        if (stubMode) {
            return true;
        } else {
            String logTag = getLogTag(prodInstId);

            // Make sure renew was not called on a non-expired product.
            Product product = new Product(this);
            product.init(gdbConn, pdbConn, prodInstId);
            if (!product.isExpired()) {
                return false;
            }
            Date oldExpirationDate = product.getExpirationDate();

            // There are multiple scenarios for renewal.
            // First, the renewal source can be either external or internal. In either case, we determine if a renewal is actually needed.
            // Second, the renewal can have an anniverary or first-of-month cycle type, which affects the start and expiration date calculations.
            // For each case, we will calculate the new expiration date , start date and budget (base target).
            java.sql.Date newExpirationDate = null;
            Double newBaseTarget = product.getBaseTarget();
            if (product.getRenewalSourceType().equals(Product.RenewalSourceType.external)) {
                // For external renewals, we look in the product_lifecycle_event table for the new expiration date and budget.
                ProductLifeCycleEventHelper eventHelper = new ProductLifeCycleEventHelper(getCurrentLogComponent());
                ProvisioningRenewalEvent renewalEvent = null;
                try {
                    renewalEvent = eventHelper.getLatestProvisioningRenewalEvent(logTag, pdbConn, prodInstId);
                }
                catch(SQLException e) {
                    throw new BudgetManagerException(BudgetManagerException.UNKNOWN_ERROR, "renewBudget: An unexpected error occurred while querying renewal event for product " + prodInstId,
                            e.getMessage(), e);
                }

                // Make sure that the external renewal event has happened and we have good data.
                if (renewalEvent != null && renewalEvent.getNewExpirationDate() != null && renewalEvent.getNewBaseTarget() != null) {
                    // Check to make sure that the most recent GFE renewal event is not an old one. This scenario could occur if the new RAS renewal does not happen, such as when the
                    // credit card is bad.
                    Calendar newExpDateCal = CalendarUtil.dateToCalendar(renewalEvent.getNewExpirationDate());
                    Calendar oldExpDateCal = CalendarUtil.dateToCalendar(product.getExpirationDate());
                    if (!CalendarUtil.isSameDay(newExpDateCal, oldExpDateCal) && newExpDateCal.after(oldExpDateCal)) {
                        newExpirationDate = DateUtil.toSqlDate(renewalEvent.getNewExpirationDate());
                        newBaseTarget = renewalEvent.getNewBaseTarget();
                    }
                    else {
                        logWarning(logTag, "renewBudget: External renew product is expired, but renewal event is missing or has bad data. renewalEvent=" + renewalEvent);
                        return false;
                    }
                }
                else {
                    logWarning(logTag, "renewBudget: External renew product is expired, but renewal event is missing or has bad data. renewalEvent=" + renewalEvent);
                    return false;
                }
            }
            else {
                // For internal anniversary renewals, the new expiration date is 1 month after the old expiration date.
                // if it's a first of month renewal, this date will just get overwritten below.
                // As of this writing, there are no internal anniversary renewals, but this code will work when called upon.
                Calendar newExpDateCal = Calendar.getInstance();
                newExpDateCal.setTime(oldExpirationDate);
                newExpDateCal.add(Calendar.MONTH, 1);
                newExpirationDate = CalendarUtil.calendarToSqlDate(newExpDateCal);
                // Base target does not change for internal renewals.
            }

            // Next we calculate the new start date.
            java.sql.Date newStartDate = null;
            if (product.getRenewalCycleType().equals(Product.RenewalCycleType.anniversary)) {
                // Anniversary renewal.
                // The start date rules are a bit complicated because not all months are the same length, and there could have been a gap in renewals.
                // Generally, it's the new expiration date minus 1 month plus 1 day. If that calculation results in a date that is less than or equal to the old
                // expiration date, then we set the new start date to the old expiration date plus 1 day. This algorithm handles all cases.
                Calendar startDateCal = Calendar.getInstance();
                startDateCal.setTime(newExpirationDate);
                for (int i = 0; i < product.getTermQty(); i++) { // Term may be quarterly (3 months).
                	startDateCal.add(Calendar.MONTH, -1);   
                }

                startDateCal.add(Calendar.DAY_OF_MONTH, 1);

                Calendar oldExpDateCal = Calendar.getInstance();
                oldExpDateCal.setTime(oldExpirationDate);
                if (CalendarUtil.isSameDay(startDateCal, oldExpDateCal) || startDateCal.before(oldExpDateCal)) {
                    startDateCal = Calendar.getInstance();
                    startDateCal.setTime(oldExpirationDate);
                    startDateCal.add(Calendar.DAY_OF_MONTH, 1);
                }
                newStartDate = CalendarUtil.calendarToSqlDate(startDateCal);
            }
            else {
                // First of month renewal.
                // Set the start date to the first of the current month.
                Calendar startDateCal = Calendar.getInstance();
                startDateCal.set(Calendar.DAY_OF_MONTH, 1);
                newStartDate = CalendarUtil.calendarToSqlDate(startDateCal);

                // Set the expiration date to the last day of the current month.
                // This code will override what was set above in the internal anniversary block.
                Calendar newExpDateCal = Calendar.getInstance();
                newExpDateCal.set(Calendar.DAY_OF_MONTH, newExpDateCal.getActualMaximum(Calendar.DAY_OF_MONTH));
                newExpirationDate = CalendarUtil.calendarToSqlDate(newExpDateCal);
            }

            // Calculate the new budget.

            double currentTarget = newBaseTarget;
            if (product.getRolloverBudget()) {

                // Rollover budget: Add the current monthly budget remaining to the budget.
                ProductSummaryData productSummaryData = new ProductSummaryData(this);
                productSummaryData.init(pdbConn, prodInstId);
                currentTarget += productSummaryData.getMonthlyBudgetRemaining();
                logInfo(logTag, "renewBudget: rollover budget amount: " + productSummaryData.getMonthlyBudgetRemaining());
            }

            // Get the target margin amount and subtract it from the budget.
            TargetHelper targetHelper = new TargetHelper(this);
            List<Target> targetList = null;
            try {
                targetList = targetHelper.getTargetsValidForCycleDates(logTag, pdbConn, prodInstId, CalendarUtil.dateToCalendar(product.getStartDate()),
                    CalendarUtil.dateToCalendar(product.getExpirationDate()));
            }
            catch (Exception e) {
                throw new BudgetManagerException(BudgetManagerException.UNKNOWN_ERROR, "renewBudget -> An unexpected error occurred while querying targets for product " + prodInstId,
                       e.getMessage(), e);
            }

            double totalTargetMarginAmount = 0;
            for (Target target: targetList) {
                totalTargetMarginAmount += target.calculateActualMonthlyMarginAmount();
            }
            currentTarget -= totalTargetMarginAmount;

            logInfo(logTag, "renewBudget: totalTargetMarginAmount: " + totalTargetMarginAmount);
            logInfo(logTag, "renewBudget: currentTarget: " + currentTarget);

            // Update product.
            product.setBaseTarget(newBaseTarget);
            product.setCurrentTarget(currentTarget);
            product.setCurrentMarginAmount(totalTargetMarginAmount);
            product.setStartDate(newStartDate);
            product.setExpirationDate(newExpirationDate);
            product.setUpdatedBySystem("BM.renewBudget"); // I don't like these literal values, but I'm leaving it as-is for backward consistency (for now at least).
            product.setUpdatedByUser(updatedByUser);
            product.persist(gdbConn, pdbConn);

            BudgetRenewalEvent budgetRenewalEvent = ProductLifeCycleEventFactory.createBudgetRenewalEvent();
            budgetRenewalEvent.setProdInstId(prodInstId);
            budgetRenewalEvent.setOldCurrentTarget(product.getCurrentTarget());
            budgetRenewalEvent.setOldStartDate(product.getStartDate());
            budgetRenewalEvent.setNewStartDate(newStartDate);
            budgetRenewalEvent.setNewCurrentTarget(product.getCurrentTarget());

            // Calculate the new cpc markup range. Remove this soon.
            calculateCpcMarkup(gdbConn, pdbConn, prodInstId);

            // Record the lifecycle event.
            new ProductLifeCycleEventHelper(this).insertProductLifeCycleEvent(prodInstId, pdbConn, budgetRenewalEvent);

            try {
                // Insert the budget_adj record.
                BudgetAdjustmentFactory factory = BudgetAdjustmentFactory.getInstance();
                BudgetAdjustment budgetAdjustment = factory.getRenewBudgetAdjustment(prodInstId, new Date(), system, product.getBaseTarget().doubleValue(),
                        currentTarget, 0d);
                budgetAdjustment.insert(pdbConn);
            } catch (Exception e) {
                // Log the error, but don't throw an exception since we would not want the renew to fail because of this error.
                logError(logTag, e);
            }
            return true;
        }
    }

    /**
     * Modify monthly budget.
     */
    public void modifyMonthlyBudget(Connection gdbConn, Connection pdbConn, String prodInstId, double amountToAdjust,
            boolean isUpgrade, boolean isAddOn, BudgetAdjustment.System system, String user) throws BudgetManagerException {
        if (stubMode) {
            return;
        } else {
            String logTag = getLogTag(prodInstId);

            // Initialize the product.
            Product product = new Product(this);
            product.init(gdbConn, pdbConn, prodInstId);

            // In the near future, review this termQty logic to determine if we still need it. I want to remove it, but I'm ignoring it for now.
            logInfo(logTag, "modifyBudget -> expDate: "+format.format(product.getExpirationDate()));
            logInfo(logTag, "modifyBudget -> termQty: "+product.getTermQty());
            if (product.getTermQty() > 1 && !isAddOn) {
                // this calendar will start one month before exp date.
                Calendar test = Calendar.getInstance();
                test.setTimeInMillis(product.getExpirationDate().getTime());
                test.add(Calendar.MONTH, -1);

                int x = 0;
                while ((x <= product.getTermQty()) && !(Calendar.getInstance().getTimeInMillis() >= test.getTimeInMillis())) {
                    test.add(Calendar.MONTH, -1);
                    x++;
                }
                // x is now number of full months til expiration (can be zero)
                // so now you have to subtract it from the termQty to get months passed (rounded up)
                x = product.getTermQty() - x;
                // now you increase the amount to adjust by x to compensate for months that may have
                // passed in the cycle.
                amountToAdjust *= x;
                logInfo(logTag, "modifyBudget -> new adjust amt: "+amountToAdjust);
            }

            // Update the current target.
            amountToAdjust = isUpgrade ? amountToAdjust : -amountToAdjust;
            product.setCurrentTarget(product.getCurrentTarget() + amountToAdjust);
            product.setUpdatedBySystem("BM.modifyBudget");
            product.setUpdatedByUser(updatedByUser);
            product.persist(gdbConn, pdbConn);

            // Update monthly budget remaining.
            ProductSummaryData productSummary = new ProductSummaryData(this);
            productSummary.init(pdbConn, prodInstId);
            productSummary.modifyMonthlyBudgetRemaining(amountToAdjust);

            // Adjust budgets and statuses.
            try {
                adjustBudgetsAndStatuses(gdbConn, pdbConn, prodInstId);
            }
            catch (Exception e) {
                throw new BudgetManagerException(BudgetManagerException.UNKNOWN_ERROR, "An unexpected error occurred in adjustBudgetsAndStatuses.", e.getMessage(), e);
            }
            finally {
                // It's important to still capture the addon amount even if adjustBudgetsAndStatuses fails.
                try {
                    // Re-query the product summary data.
                    productSummary = new ProductSummaryData(this);
                    productSummary.init(pdbConn, prodInstId);

                    // Insert the budget_adj record.
                    BudgetAdjustmentFactory factory = BudgetAdjustmentFactory.getInstance();
                    BudgetAdjustment budgetAdjustment = null;
                    if (isAddOn) {
                        // Add-on or Remove add-on
                        budgetAdjustment = factory.getAddonBudgetAdjustment(prodInstId, productSummary.getUpdateDate(), system, user, amountToAdjust,
                                productSummary.getMonthlyBudgetRemaining(), productSummary.getDailyBudgetRemaining());
                    }
                    else {
                        // Upgrade or downgrade
                        budgetAdjustment = factory.getUpgradeDowngradeBudgetAdjustment(prodInstId, productSummary.getUpdateDate(), system, user,
                        		amountToAdjust, productSummary.getMonthlyBudgetRemaining(), productSummary.getDailyBudgetRemaining());
                    }
                    budgetAdjustment.insert(pdbConn);
                }
                catch (Exception e) {
                    // Log the error, but don't throw an exception since we would not want a debit to fail because of this error.
                    logError(logTag, e);
                }
            }
        }
    }

    /**
     * Upgrade monthly budget.
     */
    public void upgradeMonthlyBudget(Connection gdbConn, Connection pdbConn, String prodInstId, double amountToAdjust,
            BudgetAdjustment.System system, String user) throws BudgetManagerException {
        if (stubMode) {
            return;
        } else {
            String logTag = getLogTag(prodInstId);

            logInfo(logTag, "Applying immediate upgrade to "+prodInstId+": "+amountToAdjust);
            modifyMonthlyBudget(gdbConn, pdbConn, prodInstId, amountToAdjust, true, false, system, user);
        }
    }

    /**
     * Add one-time monthly budget.
     */
    public void addOneTimeMonthlyBudget(Connection gdbConn, Connection pdbConn, String prodInstId, double amountToAdjust,
            BudgetAdjustment.System system, String user) throws BudgetManagerException {
        if (stubMode) {
            return;
        } else {
            String logTag = getLogTag(prodInstId);

            logInfo(logTag, "Adding one time add-on for "+prodInstId+": "+amountToAdjust);
            modifyMonthlyBudget(gdbConn, pdbConn, prodInstId, amountToAdjust, true, true, system, user);
        }
    }

    /**
     * Remove one-time monthly budget.
     */
    public void removeOneTimeMonthlyBudget(Connection gdbConn, Connection pdbConn, String prodInstId, double amountToAdjust,
            BudgetAdjustment.System system, String user) throws BudgetManagerException {
        if (stubMode) {
            return;
        } else {
            String logTag = getLogTag(prodInstId);

            logInfo(logTag, "Removing one time add-on for "+prodInstId+": "+amountToAdjust);
            modifyMonthlyBudget(gdbConn, pdbConn, prodInstId, amountToAdjust, false, true, system, user);
        }
    }

    /**
     * Reconcile budget.
     */
    public void reconcileBudget(Connection gdbConn, Connection pdbConn, String prodInstId, Calendar today, int numDays, double tollCost) throws BudgetManagerException {
        if (stubMode) {
            return;
        } else {
            String logTag = getLogTag(prodInstId);

            logInfo(logTag, "Reconciling budget for " + prodInstId + " on " + format.format(today.getTime()) + " for " + numDays + " days");
            String getMinDateSql = "SELECT MIN(update_date) min_date FROM product_sum WHERE prod_inst_id = ?";
            String getCostsSql = "SELECT v.ns_keyword_id, v.update_date, IFNULL(v.cost - s.vendor_click_cost , v.cost) difference, IFNULL(CAST(v.clicks AS SIGNED) - s.click_count, v.clicks) click_diff FROM vendor_keyword v LEFT JOIN ns_keyword_sum s ON (v.prod_inst_id = s.prod_inst_id AND v.ns_keyword_id = s.ns_keyword_id AND v.update_date = s.update_date) WHERE v.prod_inst_id = ? AND v.update_date BETWEEN ? AND ?";
            String getIdsSql = "SELECT k.ns_ad_group_id, ag.ns_campaign_id FROM ns_keyword k, ns_ad_group ag WHERE k.prod_inst_id = ? AND k.ns_keyword_id = ? AND k.prod_inst_id = ag.prod_inst_id AND k.ns_ad_group_id = ag.ns_ad_group_id";
            String getCpcMarkupSql = "SELECT cpc_markup FROM product_pricing WHERE prod_inst_id = ?";
            String updateProductClickCostsSql = "UPDATE product_sum SET daily_budget_remaining = daily_budget_remaining - ?, vendor_click_cost = vendor_click_cost + ?, ns_click_cost = ns_click_cost + ?, click_count = click_count + ? WHERE prod_inst_id = ? AND update_date = ?";
            String updateMonthlyBudgetSql = "UPDATE product_sum SET monthly_budget_remaining = monthly_budget_remaining - ? WHERE prod_inst_id = ? AND update_date >= ?";
            String updateCampaignClickCostsSql = "INSERT INTO ns_campaign_sum (prod_inst_id, ns_campaign_id, update_date, vendor_click_cost, ns_click_cost, click_count) VALUES (?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE vendor_click_cost = vendor_click_cost + VALUES(vendor_click_cost), ns_click_cost = ns_click_cost + VALUES(ns_click_cost), click_count = click_count + VALUES(click_count)";
            String updateAdGroupClickCostsSql = "INSERT INTO ns_ad_group_sum (prod_inst_id, ns_ad_group_id, update_date, vendor_click_cost, ns_click_cost, click_count) VALUES (?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE vendor_click_cost = vendor_click_cost + VALUES(vendor_click_cost), ns_click_cost = ns_click_cost + VALUES(ns_click_cost), click_count = click_count + VALUES(click_count)";
            String updateKeywordClickCostsSql = "INSERT INTO ns_keyword_sum (prod_inst_id, ns_keyword_id, update_date, vendor_click_cost, ns_click_cost, click_count) VALUES (?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE vendor_click_cost = vendor_click_cost + VALUES(vendor_click_cost), ns_click_cost = ns_click_cost + VALUES(ns_click_cost), click_count = click_count + VALUES(click_count)";
            String getAdCostsSql = "SELECT v.ns_ad_id, v.update_date, IFNULL(v.cost - s.vendor_click_cost, v.cost) difference, IFNULL(CAST(v.clicks AS SIGNED) - s.click_count, v.clicks) click_diff FROM vendor_ad v LEFT JOIN ns_ad_sum s ON (v.prod_inst_id = s.prod_inst_id AND v.ns_ad_id = s.ns_ad_id AND v.update_date = s.update_date) WHERE v.prod_inst_id = ? AND v.update_date BETWEEN ? AND ?";
            String updateAdClickCostsSql = "INSERT INTO ns_ad_sum (prod_inst_id, ns_ad_id, update_date, vendor_click_cost, ns_click_cost, click_count) VALUES (?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE vendor_click_cost = vendor_click_cost + VALUES(vendor_click_cost), ns_click_cost = ns_click_cost + VALUES(ns_click_cost), click_count = click_count + VALUES(click_count)";
            String getExtraCostsSql = "SELECT s.ns_keyword_id, s.update_date, s.vendor_click_cost, s.ns_click_cost, s.click_count FROM ns_keyword_sum s LEFT JOIN vendor_keyword v ON (v.prod_inst_id = s.prod_inst_id AND v.ns_keyword_id = s.ns_keyword_id AND v.update_date = s.update_date) WHERE s.prod_inst_id = ? AND s.update_date BETWEEN ? AND ? AND v.cost IS NULL";
            String getExtraAdCostsSql = "SELECT s.ns_ad_id, s.update_date, s.vendor_click_cost, s.ns_click_cost, s.click_count FROM ns_ad_sum s LEFT JOIN vendor_ad v ON (v.prod_inst_id = s.prod_inst_id AND v.ns_ad_id = s.ns_ad_id AND v.update_date = s.update_date) WHERE s.prod_inst_id = ? AND s.update_date BETWEEN ? AND ? AND v.cost IS NULL";

            PreparedStatement getMinDatePstmt = null;
            PreparedStatement getCostsPstmt = null;
            PreparedStatement verifyCostsPstmt = null;
            PreparedStatement getIdsPstmt = null;
            PreparedStatement getCpcMarkupPstmt = null;
            PreparedStatement updateProductClickCostsPstmt = null;
            PreparedStatement updateMonthlyBudgetPstmt = null;
            PreparedStatement updateCampaignClickCostsPstmt = null;
            PreparedStatement updateAdGroupClickCostsPstmt = null;
            PreparedStatement updateKeywordClickCostsPstmt = null;
            PreparedStatement getAdCostsPstmt = null;
            PreparedStatement updateAdClickCostsPstmt = null;
            PreparedStatement getExtraCostsPstmt = null;
            PreparedStatement verifyExtraCostsPstmt = null;
            PreparedStatement getExtraAdCostsPstmt = null;
            PreparedStatement getUnprocessedLeadsPstmt = null;

            ResultSet getMinDateRs = null;
            ResultSet getCostsRs = null;
            ResultSet verifyCostsRs = null;
            ResultSet getIdsRs = null;
            ResultSet getCpcMarkupRs = null;
            ResultSet getAdCostsRs = null;
            ResultSet getExtraCostsRs = null;
            ResultSet verifyExtraCostsRs = null;
            ResultSet getExtraAdCostsRs = null;
            ResultSet getUnprocessedLeadsRs = null;

            try {
                // Get the NS_KEYWORD_IDs to process by finding differences in cost/vendor_click_cost within the last numDays days.

                // first move the calendar back one day so we don't mess up stuff for today
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(today.getTimeInMillis());
                cal.add(Calendar.DATE, -1);

                // now make a calendar for numDays days before that
                Calendar someDaysAgo = Calendar.getInstance();
                someDaysAgo.setTimeInMillis(cal.getTimeInMillis());
                someDaysAgo.add(Calendar.DATE, -1*numDays);

                // now get the min date from the db
                getMinDatePstmt = pdbConn.prepareStatement(getMinDateSql);
                getMinDatePstmt.setString(1, prodInstId);
                logInfo(logTag, "reconcileBudget -> minDate: "+getMinDatePstmt);
                getMinDateRs = getMinDatePstmt.executeQuery();
                if (getMinDateRs == null) {
                    logError(logTag, "reconcileBudget -> error getting min date from the db! Null result set");
                    throw new BudgetManagerException(BudgetManagerException.DATABASE_ERROR, "error getting min date from the db! Null result set");
                }
                Date rsDate = null;
                if (getMinDateRs.next()) {
                    rsDate = getMinDateRs.getDate(1);
                }
                if (rsDate == null) {
                    logInfo(logTag, "reconcileBudget -> NO ROWS IN PRODUCT SUM FOR "+prodInstId+"! Exiting...");
                    return;
                }

                Calendar minDate = Calendar.getInstance();
                minDate.setTime(rsDate);
                logInfo(logTag, "reconcileBudget -> minDate: "+format.format(minDate.getTime()));

                if (someDaysAgo.getTimeInMillis() < minDate.getTimeInMillis()) {
                    logInfo(logTag, "reconcileBudget -> "+numDays+" ago is less than minDate... setting START to minDate.");
                    someDaysAgo.setTimeInMillis(minDate.getTimeInMillis());
                }

                logInfo(logTag, "reconcileBudget -> Product: "+prodInstId);
                logInfo(logTag, "reconcileBudget -> START: "+format.format(someDaysAgo.getTime()));
                logInfo(logTag, "reconcileBudget -> END: "+format.format(cal.getTime()));

                // get cpc markup first
                double markupRate = 0.0;
                // Get the product debit configuration.
                ProductDebitConfig debitConfig = null;
                try {
                    debitConfig = new ProductDebitConfig(pdbConn, prodInstId);
                }
                catch (Exception e) {
                    // Don't allow the recomciler to fail. Assume debit flags are turned on if there is a bad data condition.
                    logInfo(logTag, "reconcileBudget -> ProductDebitConfig data not found");
                    logError(logTag, e);
                }

                // Calculate the markup.
                if ((debitConfig != null) && Boolean.TRUE.equals(debitConfig.getDebitCpcMarkup())) {
                    getCpcMarkupPstmt = pdbConn.prepareStatement(getCpcMarkupSql);
                    getCpcMarkupPstmt.setString(1, prodInstId);
                    logInfo(logTag, "reconcileBudget -> Getting CPC Markup: "+getCpcMarkupPstmt);
                    getCpcMarkupRs = getCpcMarkupPstmt.executeQuery();

                    if (getCpcMarkupRs != null && getCpcMarkupRs.next()) {
                        markupRate = getCpcMarkupRs.getDouble("cpc_markup");
                    }
                }
                logInfo(logTag, "reconcileBudget -> CPC Markup: "+markupRate);

                // Build keyword mismatches
                // First get mismatches between the sum and vendor table. Vendor table is authoritative for clicks.
                getCostsPstmt = pdbConn.prepareStatement(getCostsSql);
                getCostsPstmt.setString(1, prodInstId);
                getCostsPstmt.setDate(2, new java.sql.Date(someDaysAgo.getTimeInMillis()));
                getCostsPstmt.setDate(3, new java.sql.Date(cal.getTimeInMillis()));
                logInfo(logTag, "reconcileBudget -> Getting costs: "+getCostsPstmt);
                getCostsRs = getCostsPstmt.executeQuery();

                // Then get rows that exist in sum but not vendor so we can zero any click values out.
                getExtraCostsPstmt = pdbConn.prepareStatement(getExtraCostsSql);
                getExtraCostsPstmt.setString(1, prodInstId);
                getExtraCostsPstmt.setDate(2, new java.sql.Date(someDaysAgo.getTimeInMillis()));
                getExtraCostsPstmt.setDate(3, new java.sql.Date(cal.getTimeInMillis()));
                logInfo(logTag, "reconcileBudget -> Getting extra costs: "+getExtraCostsPstmt);
                getExtraCostsRs = getExtraCostsPstmt.executeQuery();

                class IdDateDiff {
                    long id;
                    java.sql.Date date;
                    double diff;
                    double markupDiff;
                    int clickDiff;

                    boolean hasDiff() {
                        return diff != 0.00d || markupDiff != 0.00d || clickDiff != 0;
                    }

                    public String toString() {
                        StringBuilder sb = new StringBuilder("reconcileBudget -> ***** ID_DATE_DIFF *****\n");
                        sb.append("        id -> ").append(id).append("\n");
                        sb.append("      date -> ").append(format.format(date)).append("\n");
                        sb.append("      diff -> ").append(diff).append("\n");
                        sb.append("markupDiff -> ").append(markupDiff).append("\n");
                        sb.append(" clickDiff -> ").append(clickDiff).append("\n");
                        return sb.toString();
                    }
                }

                // Finally put the two result sets together into a collection of the above obj
                logInfo(logTag, "reconcileBudget -> Collecting keyword adjustments needed...");
                ArrayList<IdDateDiff> diffArray = new ArrayList<IdDateDiff>();

                while (getCostsRs != null && getCostsRs.next()) {
                    IdDateDiff tmp = new IdDateDiff();
                    tmp.id = getCostsRs.getLong("ns_keyword_id");
                    tmp.date = getCostsRs.getDate("update_date");
                    tmp.diff = getCostsRs.getDouble("difference");
                    tmp.markupDiff = getCostsRs.getDouble("difference") * markupRate;
                    tmp.clickDiff = getCostsRs.getInt("click_diff");

                    if (tmp.hasDiff()) {
                        logInfo(logTag, tmp.toString());
                        diffArray.add(tmp);
                    }
                }

                while (getExtraCostsRs != null && getExtraCostsRs.next()) {
                    IdDateDiff tmp = new IdDateDiff();
                    tmp.id = getExtraCostsRs.getLong("ns_keyword_id");
                    tmp.date = getExtraCostsRs.getDate("update_date");
                    tmp.diff = getExtraCostsRs.getDouble("vendor_click_cost") * -1;
                    tmp.markupDiff = getExtraCostsRs.getDouble("ns_click_cost") * -1;
                    tmp.clickDiff = getExtraCostsRs.getInt("click_count") * -1;
                    if (tmp.hasDiff()) {
                        logInfo(logTag, tmp.toString());
                        diffArray.add(tmp);
                    }
                }

                // Build ad mismatches
                // First get mismatches between the sum and vendor table. Vendor table is authoritative for clicks.
                getAdCostsPstmt = pdbConn.prepareStatement(getAdCostsSql);
                getAdCostsPstmt.setString(1, prodInstId);
                getAdCostsPstmt.setDate(2, new java.sql.Date(someDaysAgo.getTimeInMillis()));
                getAdCostsPstmt.setDate(3, new java.sql.Date(cal.getTimeInMillis()));
                logInfo(logTag, "reconcileBudget -> Getting ad costs: "+getAdCostsPstmt);
                getAdCostsRs = getAdCostsPstmt.executeQuery();

                // Then get rows that exist in sum but not vendor so we can zero any click values out.
                getExtraAdCostsPstmt = pdbConn.prepareStatement(getExtraAdCostsSql);
                getExtraAdCostsPstmt.setString(1, prodInstId);
                getExtraAdCostsPstmt.setDate(2, new java.sql.Date(someDaysAgo.getTimeInMillis()));
                getExtraAdCostsPstmt.setDate(3, new java.sql.Date(cal.getTimeInMillis()));
                logInfo(logTag, "reconcileBudget -> Getting extra ad costs: "+getExtraAdCostsPstmt);
                getExtraAdCostsRs = getExtraAdCostsPstmt.executeQuery();

                // Finally put the two result sets into a collection of the above obj
                logInfo(logTag, "reconcileBudget -> Collecting ad adjustments needed...");
                ArrayList<IdDateDiff> adDiffArray = new ArrayList<IdDateDiff>();

                while (getAdCostsRs != null && getAdCostsRs.next()) {
                    IdDateDiff tmp = new IdDateDiff();
                    tmp.id = getAdCostsRs.getLong("ns_ad_id");
                    tmp.date = getAdCostsRs.getDate("update_date");
                    tmp.diff = getAdCostsRs.getDouble("difference");
                    tmp.markupDiff = getAdCostsRs.getDouble("difference") * markupRate;
                    tmp.clickDiff = getAdCostsRs.getInt("click_diff");
                    if (tmp.hasDiff()) {
                        logInfo(logTag, tmp.toString());
                        adDiffArray.add(tmp);
                    }
                }

                while (getExtraAdCostsRs != null && getExtraAdCostsRs.next()) {
                    IdDateDiff tmp = new IdDateDiff();
                    tmp.id = getExtraAdCostsRs.getLong("ns_ad_id");
                    tmp.date = getExtraAdCostsRs.getDate("update_date");
                    tmp.diff = getExtraAdCostsRs.getDouble("vendor_click_cost") * -1;
                    tmp.markupDiff = getExtraAdCostsRs.getDouble("ns_click_cost") * -1;
                    tmp.clickDiff = getExtraAdCostsRs.getInt("click_count") * -1;
                    if (tmp.hasDiff()) {
                        logInfo(logTag, tmp.toString());
                        adDiffArray.add(tmp);
                    }
                }

                if (diffArray.size() == 0 && adDiffArray.size() == 0) {
                    // no keywords to process
                    throw new BudgetManagerException(BudgetManagerException.NO_KEYWORDS_TO_RECONCILE, "No keywords to recondile for " + prodInstId);
                }

                // cycle through all the keywords to process
                for (IdDateDiff idd : diffArray) {
                    if (idd.hasDiff()) {
                        long nsCampaignId = 0L;
                        long nsAdGroupId = 0L;

                        // get the ad group and campaign entity ids
                        getIdsPstmt = pdbConn.prepareStatement(getIdsSql);
                        getIdsPstmt.setString(1, prodInstId);
                        getIdsPstmt.setLong(2, idd.id);
                        getIdsRs = getIdsPstmt.executeQuery();

                        if (getIdsRs != null && getIdsRs.next()) {
                            nsCampaignId = getIdsRs.getLong("ns_campaign_id");
                            nsAdGroupId = getIdsRs.getLong("ns_ad_group_id");
                        }

                        logInfo(logTag, "reconcileBudget -> Reconciling campaign "+nsCampaignId+", ad group "+nsAdGroupId+", keyword "+idd.id+" for day "+format.format(idd.date)+" with $"+idd.diff+" ($"+idd.markupDiff+") to reconcile");

                        // Add the reconciliation value and markup (which can be negative) to:
                        // VENDOR_CLICK_COST and NS_CLICK_COST in NS_KEYWORD_SUM
                        updateKeywordClickCostsPstmt = pdbConn.prepareStatement(updateKeywordClickCostsSql);
                        updateKeywordClickCostsPstmt.setString(1, prodInstId);
                        updateKeywordClickCostsPstmt.setLong(2, idd.id);
                        updateKeywordClickCostsPstmt.setDate(3, idd.date);
                        updateKeywordClickCostsPstmt.setDouble(4, idd.diff);
                        updateKeywordClickCostsPstmt.setDouble(5, idd.markupDiff);
                        updateKeywordClickCostsPstmt.setInt(6, idd.clickDiff);
                        logInfo(logTag, "reconcileBudget -> Update NS_KEYWORD_SUM: "+updateKeywordClickCostsPstmt);
                        updateKeywordClickCostsPstmt.executeUpdate();

                        // VENDOR_CLICK_COST and NS_CLICK_COST in NS_AD_GROUP_SUM
                        updateAdGroupClickCostsPstmt = pdbConn.prepareStatement(updateAdGroupClickCostsSql);
                        updateAdGroupClickCostsPstmt.setString(1, prodInstId);
                        updateAdGroupClickCostsPstmt.setLong(2, nsAdGroupId);
                        updateAdGroupClickCostsPstmt.setDate(3, idd.date);
                        updateAdGroupClickCostsPstmt.setDouble(4, idd.diff);
                        updateAdGroupClickCostsPstmt.setDouble(5, idd.markupDiff);
                        updateAdGroupClickCostsPstmt.setInt(6, idd.clickDiff);
                        logInfo(logTag, "reconcileBudget -> Update NS_AD_GROUP_SUM: "+updateAdGroupClickCostsPstmt);
                        updateAdGroupClickCostsPstmt.executeUpdate();

                        // VENDOR_CLICK_COST and NS_CLICK_COST in NS_CAMPAIGN_SUM
                        updateCampaignClickCostsPstmt = pdbConn.prepareStatement(updateCampaignClickCostsSql);
                        updateCampaignClickCostsPstmt.setString(1, prodInstId);
                        updateCampaignClickCostsPstmt.setLong(2, nsCampaignId);
                        updateCampaignClickCostsPstmt.setDate(3, idd.date);
                        updateCampaignClickCostsPstmt.setDouble(4, idd.diff);
                        updateCampaignClickCostsPstmt.setDouble(5, idd.markupDiff);
                        updateCampaignClickCostsPstmt.setInt(6, idd.clickDiff);
                        logInfo(logTag, "reconcileBudget -> Update NS_CAMPAIGN_SUM: "+updateCampaignClickCostsPstmt);
                        updateCampaignClickCostsPstmt.executeUpdate();

                        // DAILY_BUDGET_REMAINING, VENDOR_CLICK_COST and NS_CLICK_COST in the PRODUCT_SUM table for that day.
                        updateProductClickCostsPstmt = pdbConn.prepareStatement(updateProductClickCostsSql);
                        updateProductClickCostsPstmt.setDouble(1, idd.diff + idd.markupDiff);
                        updateProductClickCostsPstmt.setDouble(2, idd.diff);
                        updateProductClickCostsPstmt.setDouble(3, idd.markupDiff);
                        updateProductClickCostsPstmt.setInt(4, idd.clickDiff);
                        updateProductClickCostsPstmt.setString(5, prodInstId);
                        updateProductClickCostsPstmt.setDate(6, idd.date);
                        logInfo(logTag, "reconcileBudget -> Update Daily Budget and PRODUCT_SUM: "+updateProductClickCostsPstmt);
                        updateProductClickCostsPstmt.executeUpdate();

                        // MONTHLY_BUDGET_REMAINING in the PRODUCT_SUM table for that day, and all days up to the current day.
                        updateMonthlyBudgetPstmt = pdbConn.prepareStatement(updateMonthlyBudgetSql);
                        updateMonthlyBudgetPstmt.setDouble(1, idd.diff + idd.markupDiff);
                        updateMonthlyBudgetPstmt.setString(2, prodInstId);
                        updateMonthlyBudgetPstmt.setDate(3, idd.date);
                        logInfo(logTag, "reconcileBudget -> Update Monthly Budget Remaining: "+updateMonthlyBudgetPstmt);
                        updateMonthlyBudgetPstmt.executeUpdate();

                        // Insert budget_adj records for each day where an update was made.
                        try {
                            ProductSummaryData productSum = new ProductSummaryData(this);
                            Calendar date = java.util.Calendar.getInstance();
                            date.setTimeInMillis(idd.date.getTime());
                            productSum.init(pdbConn, prodInstId, date);

                            CampaignList campaignList = budgetManagerHelper.getSingleCampaignInfo(pdbConn, prodInstId, nsCampaignId);
                            Campaign campaign = campaignList.getCampaigns().get(0);

                            BudgetAdjustmentFactory factory = BudgetAdjustmentFactory.getInstance();
                            BudgetAdjustment budgetAdjustment = factory.getReconcileClicksBudgetAdjustment(prodInstId,
                                    productSum.getUpdateDate(),    idd.diff, idd.markupDiff, idd.clickDiff, nsCampaignId, nsAdGroupId, idd.id,
                                    productSum.getMonthlyBudgetRemaining(), productSum.getDailyBudgetRemaining(), campaign.getDailyBudget());
                            budgetAdjustment.insert(pdbConn);
                        } catch(Exception e) {
                            // Log the error, but don't throw an exception since we would not want a debit to fail because of this error.
                            logError(logTag, e);
                        }
                    }
                }

                // now we have to reconcile the ns_ad_sum table
                // process all the ads that need to change
                for (IdDateDiff adIdd : adDiffArray) {
                    if (adIdd.hasDiff()) {
                        logInfo(logTag, "reconcileBudget -> Reconciling ad "+adIdd.id+" on date "+format.format(adIdd.date)+" for $"+adIdd.diff+" ($"+adIdd.markupDiff+")");

                        updateAdClickCostsPstmt = pdbConn.prepareStatement(updateAdClickCostsSql);
                        updateAdClickCostsPstmt.setString(1, prodInstId);
                        updateAdClickCostsPstmt.setLong(2, adIdd.id);
                        updateAdClickCostsPstmt.setDate(3, adIdd.date);
                        updateAdClickCostsPstmt.setDouble(4, adIdd.diff);
                        updateAdClickCostsPstmt.setDouble(5, adIdd.markupDiff);
                        updateAdClickCostsPstmt.setInt(6, adIdd.clickDiff);
                        logInfo(logTag, "reconcileBudget -> Update AD_SUM: "+updateAdClickCostsPstmt);
                        updateAdClickCostsPstmt.executeUpdate();
                    }
                }

                // now verify that keywords should match

                // Build keyword mismatches
                // First get mismatches between the sum and vendor table. Vendor table is authoritative for clicks.
                verifyCostsPstmt = pdbConn.prepareStatement(getCostsSql);
                verifyCostsPstmt.setString(1, prodInstId);
                verifyCostsPstmt.setDate(2, new java.sql.Date(someDaysAgo.getTimeInMillis()));
                verifyCostsPstmt.setDate(3, new java.sql.Date(cal.getTimeInMillis()));
                logInfo(logTag, "reconcileBudget -> Verify costs: "+verifyCostsPstmt);
                verifyCostsRs = verifyCostsPstmt.executeQuery();

                // Then get rows that exist in sum but not vendor so we can zero any click values out.
                verifyExtraCostsPstmt = pdbConn.prepareStatement(getExtraCostsSql);
                verifyExtraCostsPstmt.setString(1, prodInstId);
                verifyExtraCostsPstmt.setDate(2, new java.sql.Date(someDaysAgo.getTimeInMillis()));
                verifyExtraCostsPstmt.setDate(3, new java.sql.Date(cal.getTimeInMillis()));
                logInfo(logTag, "reconcileBudget -> Getting extra costs: "+verifyExtraCostsPstmt);
                verifyExtraCostsRs = verifyExtraCostsPstmt.executeQuery();

                while (verifyCostsRs != null && verifyCostsRs.next()) {
                    IdDateDiff tmp = new IdDateDiff();
                    tmp.id = verifyCostsRs.getLong("ns_keyword_id");
                    tmp.date = verifyCostsRs.getDate("update_date");
                    tmp.diff = verifyCostsRs.getDouble("difference");
                    tmp.markupDiff = verifyCostsRs.getDouble("difference") * markupRate;
                    tmp.clickDiff = verifyCostsRs.getInt("click_diff");
                    if (tmp.hasDiff()) {
                        logError(logTag, "VALIDATION FAILED: "+tmp.toString());
                    }
                }

                while (verifyExtraCostsRs != null && verifyExtraCostsRs.next()) {
                    IdDateDiff tmp = new IdDateDiff();
                    tmp.id = verifyExtraCostsRs.getLong("ns_keyword_id");
                    tmp.date = verifyExtraCostsRs.getDate("update_date");
                    tmp.diff = verifyExtraCostsRs.getDouble("vendor_click_cost") * -1;
                    tmp.markupDiff = verifyExtraCostsRs.getDouble("ns_click_cost") * -1;
                    tmp.clickDiff = verifyExtraCostsRs.getInt("click_count") * -1;
                    if (tmp.hasDiff()) {
                        logError(logTag, "VALIDATION FAILED: "+tmp.toString());
                    }
                }

                // Adjust budgets and statuses.
                try {

                    adjustBudgetsAndStatuses(gdbConn, pdbConn, prodInstId);
                }
                catch (Exception e) {
                    throw new BudgetManagerException(BudgetManagerException.UNKNOWN_ERROR, "reconcileBudget: An unexpected error occurred in adjustBudgetsAndStatuses.", e.getMessage(), e);
                }

            } catch (SQLException e) {
                logError(logTag, e);
                throw new BudgetManagerException(BudgetManagerException.DATABASE_ERROR, "Generic DB Error", "" + e, e);
            } finally {
                BaseHelper.close(getMinDateRs,getCostsRs,verifyCostsRs,getIdsRs,getCpcMarkupRs,
                        getAdCostsRs,getExtraAdCostsRs,getExtraCostsRs,verifyExtraCostsRs,getUnprocessedLeadsRs);
                BaseHelper.close(getMinDatePstmt,getCostsPstmt,verifyCostsPstmt,getIdsPstmt,getCpcMarkupPstmt,updateProductClickCostsPstmt,updateMonthlyBudgetPstmt,
                        updateCampaignClickCostsPstmt,updateAdGroupClickCostsPstmt,updateKeywordClickCostsPstmt,getAdCostsPstmt,updateAdClickCostsPstmt,
                        getExtraCostsPstmt,verifyExtraCostsPstmt,getExtraAdCostsPstmt,getUnprocessedLeadsPstmt);
            }
        }
    }

    /**
     * Calculate bid cap. This method is only used by optimization, is a candidate for removal.
     */
    public void calculateBidcap(Connection pdbConn, String prodInstId, long nsAdGroupId, double leniency, Calendar today) throws BudgetManagerException {
        if (stubMode) {
            return;
        } else {
            String logTag = getLogTag(prodInstId);

            logInfo(logTag, "Calculating bidcap for "+prodInstId+"-ag"+nsAdGroupId+" with leniency "+leniency+" on "+format.format(today.getTime()));
            // simulate the ad group budget
            // first get the campaign's budget
            double campaignBudget = budgetManagerHelper.getCampaignBudgetByAdGroup(pdbConn, prodInstId, nsAdGroupId);
            logInfo(logTag, "calculateBidcap -> campaignBudget: "+campaignBudget);

            // now get the number of ad groups
            double numAdGroups = budgetManagerHelper.getCountOfActiveAdGroups(pdbConn, prodInstId);
            logInfo(logTag, "calculateBidcap -> numAdGroups: "+numAdGroups);

            // now simulate the ad group budget
            double adGroupBudget = campaignBudget / numAdGroups;
            logInfo(logTag, "calculateBidcap -> adGroupBudget: "+adGroupBudget);

            // get the number of keywords in the ad group
            double numKeywords = budgetManagerHelper.getCountOfActiveKeywords(pdbConn, prodInstId, nsAdGroupId);
            logInfo(logTag, "calculateBidcap -> numKeywords: "+numKeywords);

            // get the cost of most expensive lead configured for product
            double maxLeadCost = budgetManagerHelper.getMaxLeadCost(pdbConn, prodInstId);
            logInfo(logTag, "calculateBidcap -> maxLeadCost: "+maxLeadCost);

            // get the target conversion rate
            Map<String, Double> adGroupValues = budgetManagerHelper.getAdGroupTargetConversionRate(pdbConn, prodInstId, nsAdGroupId);
            double targetConvRate = adGroupValues.get("target_conversion_rate");
            logInfo(logTag, "calculateBidcap -> targetConvRate: "+targetConvRate);

            double adGroupCpc = adGroupValues.get("ad_group_cpc") == -1.0 ? adGroupBudget : adGroupValues.get("ad_group_cpc");
            logInfo(logTag, "calculateBidcap -> adGroupCpc: "+adGroupCpc);

            // figure out budget per keyword, using the leniency
            double keywordBudget = adGroupBudget / (numKeywords / leniency);
            // subtract max lead cost from budget per keyword
            keywordBudget -= (keywordBudget > maxLeadCost * 2) ? maxLeadCost : 0.0;
            logInfo(logTag, "calculateBidcap -> keywordBudget: "+keywordBudget);

            // set bidcap = budget per keyword * the target conv rate
            double bidcap = Math.min(keywordBudget > 10.0 ? keywordBudget * targetConvRate : Math.max(keywordBudget, 1.0), adGroupCpc);
            logInfo(logTag, "calculateBidcap -> bidcap: "+bidcap);

            // now update the bid caps
            budgetManagerHelper.updateBidcapForAdGroup(pdbConn, prodInstId, nsAdGroupId, bidcap);
        }
    }

    /**
     * Refund a lead
     */
    public void refundLead(Connection pdbConn, String prodInstId, long leadId, String user) throws BudgetManagerException {
        if (stubMode) {
            return;
        } else {
            String logTag = getLogTag(prodInstId);

            logInfo(logTag, "refundLead -> refunding lead " + leadId + " for product " + prodInstId);

            String getLeadInfoSql = "SELECT refund_status_id, lead_type_id, lead_date, generic_decimal2 AS lead_cost, ns_campaign_id, ns_ad_group_id, ns_keyword_id, ns_ad_id FROM leads WHERE prod_inst_id = ? AND lead_id = ?";

            StringBuilder refundProductSql = new StringBuilder("UPDATE product_sum SET daily_budget_remaining = daily_budget_remaining + ?, total_lead_count = total_lead_count - 1, total_lead_cost = total_lead_cost - ?, ");
            StringBuilder refundCampaignSql = new StringBuilder("UPDATE ns_campaign_sum SET total_lead_count = total_lead_count - 1, total_lead_cost = total_lead_cost - ?, ");

            String tmpCountSql = " - 1, ";
            String tmpCostSql = " - ? WHERE prod_inst_id = ? AND update_date = ?";
            String tmpCampaignSql =    " AND ns_campaign_id = ?";

            String refundAdGroupSql = "UPDATE ns_ad_group_sum SET total_lead_count = total_lead_count - 1, total_lead_cost = total_lead_cost - ? WHERE prod_inst_id = ? AND ns_ad_group_id = ? AND update_date = ?";
            String refundKeywordSql = "UPDATE ns_keyword_sum SET total_lead_count = total_lead_count - 1, total_lead_cost = total_lead_cost - ? WHERE prod_inst_id = ? AND ns_keyword_id = ? AND update_date = ?";
            String refundAdSql = "UPDATE ns_ad_sum SET total_lead_count = total_lead_count - 1, total_lead_cost = total_lead_cost - ? WHERE prod_inst_id = ? AND ns_ad_id = ? AND update_date = ?";
            String refundBudgetSql = "UPDATE product_sum SET monthly_budget_remaining = monthly_budget_remaining + ? WHERE prod_inst_id = ? AND update_date >= ?";

            String updateLeadStatusSql = "UPDATE leads SET refund_status_id = 'refunded', updated_by = 'BM.refundLead', updated_date = NOW() WHERE lead_id = ?";

            PreparedStatement getLeadInfoPstmt = null;
            PreparedStatement refundProductPstmt = null;
            PreparedStatement refundCampaignPstmt = null;
            PreparedStatement refundAdGroupPstmt = null;
            PreparedStatement refundKeywordPstmt = null;
            PreparedStatement refundAdPstmt = null;
            PreparedStatement refundBudgetPstmt = null;
            PreparedStatement updateLeadStatusPstmt = null;

            ResultSet getLeadInfoRs = null;

            try {
                int leadType = 0;
                java.sql.Date leadDate;
                long campaignId = 0L;
                long adGroupId = 0L;
                long keywordId = 0L;
                long adId = 0L;
                double leadCost = 0.0;
                String refundStatus = null;

                // get the lead info (type, date, value, ids)
                getLeadInfoPstmt = pdbConn.prepareStatement(getLeadInfoSql);
                getLeadInfoPstmt.setString(1, prodInstId);
                getLeadInfoPstmt.setLong(2, leadId);
                getLeadInfoRs = getLeadInfoPstmt.executeQuery();
                if (getLeadInfoRs != null && getLeadInfoRs.next()) {
                    refundStatus = getLeadInfoRs.getString("refund_status_id");
                    leadType = getLeadInfoRs.getInt("lead_type_id");
                    leadDate = getLeadInfoRs.getDate("lead_date");
                    leadCost = getLeadInfoRs.getDouble("lead_cost");
                    campaignId = getLeadInfoRs.getLong("ns_campaign_id");
                    adGroupId = getLeadInfoRs.getLong("ns_ad_group_id");
                    keywordId = getLeadInfoRs.getLong("ns_keyword_id");
                    adId = getLeadInfoRs.getLong("ns_ad_id");

                    if (refundStatus == null || refundStatus.equals("refunded")) {
                        logInfo(logTag, "refundLead -> already refunded lead " + leadId + " for product " + prodInstId);
                        return;
                    }

                    // rerate the lead to 0 value (to remove the value from all levels)
                    rerateLead(pdbConn, prodInstId, leadId, 0, "");

                    // refund count, cost and value to all levels
                    String leadTypeName = valueColumnMapping.get(Integer.valueOf(leadType));

                    // product level
                    refundProductSql.append(leadTypeName + "count = " + leadTypeName + "count" + tmpCountSql);
                    refundProductSql.append(leadTypeName + "cost = " + leadTypeName + "cost" + tmpCostSql);
                    refundProductPstmt = pdbConn.prepareStatement(refundProductSql.toString());
                    refundProductPstmt.setDouble(1, leadCost);
                    refundProductPstmt.setDouble(2, leadCost);
                    refundProductPstmt.setDouble(3, leadCost);
                    refundProductPstmt.setString(4, prodInstId);
                    refundProductPstmt.setDate(5, leadDate);
                    logInfo(logTag, "refundLead -> about to execute product level refund: " + refundProductPstmt);
                    refundProductPstmt.executeUpdate();

                    // refund cost to monthly budget remaining for all rows for given prod inst where date > lead date
                    refundBudgetPstmt = pdbConn.prepareStatement(refundBudgetSql);
                    refundBudgetPstmt.setDouble(1, leadCost);
                    refundBudgetPstmt.setString(2, prodInstId);
                    refundBudgetPstmt.setDate(3, leadDate);
                    logInfo(logTag, "refundLead -> about to execute monthly budget refund: " + refundBudgetPstmt);
                    refundBudgetPstmt.executeUpdate();

                    // campaign level
                    if (campaignId != 0) {
                        refundCampaignSql.append(leadTypeName + "count = " + leadTypeName + "count" + tmpCountSql);
                        refundCampaignSql.append(leadTypeName + "cost = " + leadTypeName + "cost" + tmpCostSql + tmpCampaignSql);
                        refundCampaignPstmt = pdbConn.prepareStatement(refundCampaignSql.toString());
                        refundCampaignPstmt.setDouble(1, leadCost);
                        refundCampaignPstmt.setDouble(2, leadCost);
                        refundCampaignPstmt.setString(3, prodInstId);
                        refundCampaignPstmt.setDate(4, leadDate);
                        refundCampaignPstmt.setLong(5, campaignId);
                        logInfo(logTag, "refundLead -> about to execute campaign level refund: " + refundCampaignPstmt);
                        refundCampaignPstmt.executeUpdate();
                    }

                    // ad group level
                    if (adGroupId != 0) {
                        refundAdGroupPstmt = pdbConn.prepareStatement(refundAdGroupSql);
                        refundAdGroupPstmt.setDouble(1, leadCost);
                        refundAdGroupPstmt.setString(2, prodInstId);
                        refundAdGroupPstmt.setLong(3, adGroupId);
                        refundAdGroupPstmt.setDate(4, leadDate);
                        logInfo(logTag, "refundLead -> about to execute ad group level refund: " + refundAdGroupPstmt);
                        refundAdGroupPstmt.executeUpdate();
                    }

                    // keyword level
                    if (keywordId != 0) {
                        refundKeywordPstmt = pdbConn.prepareStatement(refundKeywordSql);
                        refundKeywordPstmt.setDouble(1, leadCost);
                        refundKeywordPstmt.setString(2, prodInstId);
                        refundKeywordPstmt.setLong(3, keywordId);
                        refundKeywordPstmt.setDate(4, leadDate);
                        logInfo(logTag, "refundLead -> about to execute keyword level refund: " + refundKeywordPstmt);
                        refundKeywordPstmt.executeUpdate();
                    }

                    // ad group level
                    if (adGroupId != 0) {
                        refundAdPstmt = pdbConn.prepareStatement(refundAdSql);
                        refundAdPstmt.setDouble(1, leadCost);
                        refundAdPstmt.setString(2, prodInstId);
                        refundAdPstmt.setLong(3, adId);
                        refundAdPstmt.setDate(4, leadDate);
                        logInfo(logTag, "refundLead -> about to execute ad level refund: " + refundAdPstmt);
                        refundAdPstmt.executeUpdate();
                    }

                    // finally, update the lead status
                    updateLeadStatusPstmt = pdbConn.prepareStatement(updateLeadStatusSql);
                    updateLeadStatusPstmt.setLong(1, leadId);
                    logInfo(logTag, "refundLead -> about to update lead status: " + updateLeadStatusPstmt);
                    updateLeadStatusPstmt.executeUpdate();

                    if (campaignId != 0) {
                        try {
                            ProductSummaryData productSum = new ProductSummaryData(this);
                            productSum.init(pdbConn, prodInstId);

                            CampaignList campaignList = budgetManagerHelper.getSingleCampaignInfo(pdbConn, prodInstId, campaignId);
                            Campaign campaign = campaignList.getCampaigns().get(0);

                            BudgetAdjustmentFactory factory = BudgetAdjustmentFactory.getInstance();
                            BudgetAdjustment budgetAdjustment = factory.getLeadRefundBudgetAdjustment(prodInstId, leadDate,
                                    BudgetAdjustment.System.DATA_SERVICES, user, leadId, leadType, leadCost, campaignId, adGroupId,
                                    adId, keywordId, productSum.getMonthlyBudgetRemaining(), productSum.getDailyBudgetRemaining(),
                                    campaign.getDailyBudget());
                            budgetAdjustment.insert(pdbConn);
                        } catch (Exception e) {
                            // Log the error, but don't throw an exception since we would not want a debit to fail because of this error.
                            logError(logTag, e);
                        }
                    }
                }

                logInfo(logTag, "refundLead -> refund successful!");
            } catch (SQLException e) {
                logError(logTag, e);
                throw new BudgetManagerException(BudgetManagerException.DATABASE_ERROR, "Generic DB Error", ""+e, e);
            } finally {
                BaseHelper.close(getLeadInfoRs);
                BaseHelper.close(getLeadInfoPstmt, updateLeadStatusPstmt, refundProductPstmt, refundCampaignPstmt,
                        refundAdGroupPstmt, refundKeywordPstmt, refundAdPstmt, refundBudgetPstmt);
            }
        }
    }

    /**
     *
     * @param pdbConn
     * @param prodInstId
     * @param leadId
     * @param newRating
     * @param comments
     * @throws BudgetManagerException
     */
    public void rerateLead(Connection pdbConn, String prodInstId, long leadId, int newRating, String comments) throws BudgetManagerException {
        if (stubMode) {
            return;
        } else {
            String logTag = getLogTag(prodInstId);

            logInfo(logTag, "Rerating lead "+leadId+" for "+prodInstId+" to "+newRating+", "+comments);

            String getLeadInfoSql = "SELECT lead_type_id, lead_date, lead_value, ns_campaign_id, ns_ad_group_id, ns_keyword_id, ns_ad_id FROM leads WHERE prod_inst_id = ? AND lead_id = ?";
            String updateLeadInfoSql = "UPDATE leads SET lead_value = ?, comments = ?, updated_by = 'BM.rerateLead', updated_date = NOW() WHERE prod_inst_id = ? AND lead_id = ?";
            StringBuilder updateProductInfoSql = new StringBuilder("UPDATE product_sum SET total_lead_value = total_lead_value + ?, ");
            StringBuilder updateCampaignInfoSql = new StringBuilder("UPDATE ns_campaign_sum SET total_lead_value = total_lead_value + ?, ");
            String updateProductCampaignSqlMiddle1 = "value = ";
            String updateProductCampaignSqlMiddle2 = "value + ?";
            String updateProductCampaignSqlEnd = " WHERE prod_inst_id = ? AND update_date = ?";
            String updateCampaignSqlEnd = " AND ns_campaign_id = ?";
            String updateAdGroupInfoSql = "UPDATE ns_ad_group_sum SET total_lead_value = total_lead_value + ? WHERE prod_inst_id = ? AND update_date = ? AND ns_ad_group_id = ?";
            String updateKeywordInfoSql = "UPDATE ns_keyword_sum SET total_lead_value = total_lead_value + ? WHERE prod_inst_id = ? AND update_date = ? AND ns_keyword_id = ?";
            String updateAdInfoSql = "UPDATE ns_ad_sum SET total_lead_value = total_lead_value + ? WHERE prod_inst_id = ? AND update_date = ? AND ns_ad_id = ?";

            PreparedStatement getLeadInfoPstmt = null;
            PreparedStatement updateLeadInfoPstmt = null;
            PreparedStatement updateProductInfoPstmt = null;
            PreparedStatement updateCampaignInfoPstmt = null;
            PreparedStatement updateAdGroupInfoPstmt = null;
            PreparedStatement updateKeywordInfoPstmt = null;
            PreparedStatement updateAdInfoPstmt = null;

            ResultSet getLeadInfoRs = null;

            try {
                // local vars needed to rerate lead
                int leadType = 0;
                int leadValue = 0;
                java.sql.Date leadDate = null;
                long nsCampaignId = 0L;
                long nsAdGroupId = 0L;
                long nsKeywordId = 0L;
                long nsAdId = 0L;

                // first, get the necessary lead info to start
                getLeadInfoPstmt = pdbConn.prepareStatement(getLeadInfoSql);
                getLeadInfoPstmt.setString(1, prodInstId);
                getLeadInfoPstmt.setLong(2, leadId);
                logInfo(logTag, "rerateLead -> getLeadInfo: "+getLeadInfoPstmt);
                getLeadInfoRs = getLeadInfoPstmt.executeQuery();
                if (getLeadInfoRs != null) {
                    if (getLeadInfoRs.next()) {
                        leadType = getLeadInfoRs.getInt("lead_type_id");
                        leadValue = getLeadInfoRs.getInt("lead_value");
                        leadDate = getLeadInfoRs.getDate("lead_date");
                        nsCampaignId = getLeadInfoRs.getLong("ns_campaign_id");
                        nsAdGroupId = getLeadInfoRs.getLong("ns_ad_group_id");
                        nsKeywordId = getLeadInfoRs.getLong("ns_keyword_id");
                        nsAdId = getLeadInfoRs.getLong("ns_ad_id");

                        logInfo(logTag, "rerateLead -> leadType: "+leadType);
                        logInfo(logTag, "rerateLead -> leadValue: "+leadValue);
                        logInfo(logTag, "rerateLead -> leadDate: "+format.format(leadDate));
                        logInfo(logTag, "rerateLead -> campaign: "+nsCampaignId);
                        logInfo(logTag, "rerateLead -> ad group: "+nsAdGroupId);
                        logInfo(logTag, "rerateLead -> keyword; "+nsKeywordId);
                        logInfo(logTag, "rerateLead -> ad: "+nsAdId);

                        // Lead info populated -- first get the difference
                        int difference = newRating - leadValue;
                        logInfo(logTag, "rerateLead -> difference: "+difference);

                        // Get the appropriate lead type prefix
                        String leadTypePrefix = valueColumnMapping.get(Integer.valueOf(leadType));
                        logInfo(logTag, "rerateLead -> leadTypeName: "+leadTypePrefix);

                        // Now update the lead info
                        updateLeadInfoPstmt = pdbConn.prepareStatement(updateLeadInfoSql);
                        updateLeadInfoPstmt.setInt(1, newRating);
                        updateLeadInfoPstmt.setString(2, comments);
                        updateLeadInfoPstmt.setString(3, prodInstId);
                        updateLeadInfoPstmt.setLong(4, leadId);
                        logInfo(logTag, "rerateLead -> updateLeadInfo: "+updateLeadInfoPstmt);
                        updateLeadInfoPstmt.executeUpdate();

                        // Now, update product info
                        updateProductInfoSql.append(leadTypePrefix);
                        updateProductInfoSql.append(updateProductCampaignSqlMiddle1);
                        updateProductInfoSql.append(leadTypePrefix);
                        updateProductInfoSql.append(updateProductCampaignSqlMiddle2);
                        updateProductInfoSql.append(updateProductCampaignSqlEnd);
                        updateProductInfoPstmt = pdbConn.prepareStatement(updateProductInfoSql.toString());
                        updateProductInfoPstmt.setInt(1, difference);
                        updateProductInfoPstmt.setInt(2, difference);
                        updateProductInfoPstmt.setString(3, prodInstId);
                        updateProductInfoPstmt.setDate(4, leadDate);
                        logInfo(logTag, "rerateLead -> updateProduct: "+updateProductInfoPstmt);
                        updateProductInfoPstmt.executeUpdate();

                        // Now, update the ns_[entity]_sum tables if the ids are populated
                        if (nsCampaignId != 0) {
                            updateCampaignInfoSql.append(leadTypePrefix);
                            updateCampaignInfoSql.append(updateProductCampaignSqlMiddle1);
                            updateCampaignInfoSql.append(leadTypePrefix);
                            updateCampaignInfoSql.append(updateProductCampaignSqlMiddle2);
                            updateCampaignInfoSql.append(updateProductCampaignSqlEnd);
                            updateCampaignInfoSql.append(updateCampaignSqlEnd);
                            updateCampaignInfoPstmt = pdbConn.prepareStatement(updateCampaignInfoSql.toString());
                            updateCampaignInfoPstmt.setInt(1, difference);
                            updateCampaignInfoPstmt.setInt(2, difference);
                            updateCampaignInfoPstmt.setString(3, prodInstId);
                            updateCampaignInfoPstmt.setDate(4, leadDate);
                            updateCampaignInfoPstmt.setLong(5, nsCampaignId);
                            logInfo(logTag, "rerateLead -> updateCampaign: "+updateCampaignInfoPstmt);
                            updateCampaignInfoPstmt.executeUpdate();
                        }

                        if (nsAdGroupId != 0) {
                            updateAdGroupInfoPstmt = pdbConn.prepareStatement(updateAdGroupInfoSql);
                            updateAdGroupInfoPstmt.setInt(1, difference);
                            updateAdGroupInfoPstmt.setString(2, prodInstId);
                            updateAdGroupInfoPstmt.setDate(3, leadDate);
                            updateAdGroupInfoPstmt.setLong(4, nsAdGroupId);
                            logInfo(logTag, "rerateLead -> updateAdGroup: "+updateAdGroupInfoPstmt);
                            updateAdGroupInfoPstmt.executeUpdate();
                        }

                        if (nsKeywordId != 0) {
                            updateKeywordInfoPstmt = pdbConn.prepareStatement(updateKeywordInfoSql);
                            updateKeywordInfoPstmt.setInt(1, difference);
                            updateKeywordInfoPstmt.setString(2, prodInstId);
                            updateKeywordInfoPstmt.setDate(3, leadDate);
                            updateKeywordInfoPstmt.setLong(4, nsKeywordId);
                            logInfo(logTag, "rerateLead -> updateKeyword: "+updateKeywordInfoPstmt);
                            updateKeywordInfoPstmt.executeUpdate();
                        }

                        if (nsAdId != 0) {
                            updateAdInfoPstmt = pdbConn.prepareStatement(updateAdInfoSql);
                            updateAdInfoPstmt.setInt(1, difference);
                            updateAdInfoPstmt.setString(2, prodInstId);
                            updateAdInfoPstmt.setDate(3, leadDate);
                            updateAdInfoPstmt.setLong(4, nsAdId);
                            logInfo(logTag, "rerateLead -> updateAd: "+updateAdInfoPstmt);
                            updateAdInfoPstmt.executeUpdate();
                        }
                    } else {
                        // No lead found with this id for given prod inst!
                        String errMsg = "Retrieval of lead id: " + leadId + " for product: " + prodInstId + " returned empty result set!";
                        logError(logTag, errMsg);
                        throw new BudgetManagerException(BudgetManagerException.INVALID_SRC_DATA, errMsg);
                    }
                } else {
                    // Null result set?
                    String errMsg = "Retrieval of lead id: " + leadId + " for product: " + prodInstId + " returned null result set!";
                    logError(logTag, errMsg);
                    throw new BudgetManagerException(BudgetManagerException.INVALID_SRC_DATA, errMsg);
                }
            } catch (SQLException e) {
                logError(logTag, e);
                throw new BudgetManagerException(BudgetManagerException.DATABASE_ERROR, "Generic DB Error", ""+e, e);
            } finally {
                BaseHelper.close(getLeadInfoRs);
                BaseHelper.close(getLeadInfoPstmt, updateLeadInfoPstmt, updateProductInfoPstmt,
                        updateCampaignInfoPstmt, updateAdGroupInfoPstmt, updateKeywordInfoPstmt, updateAdInfoPstmt);
            }
        }
    }

    /**
     * Calculate CPC markup.
     */
    public void calculateCpcMarkup(Connection gdbConn, Connection pdbConn, String prodInstId) throws BudgetManagerException {
        if (stubMode) {
            return;
        } else {
            String logTag = getLogTag(prodInstId);

            logInfo(logTag, "calculateCpcMarkup -> Enter: ");
            Product product = new Product(this);
            product.init(gdbConn, pdbConn, prodInstId);
            product.setCpcMarkup(budgetManagerHelper.getCpcMarkup(gdbConn, product.getChannelId(), prodInstId, product.isCpcSensitive()));
            product.setUpdatedBySystem("BM.calculateCpcMarkup");
            product.setUpdatedByUser(updatedByUser);
            logInfo(logTag, "calculateCpcMarkup -> Persist: ");
            product.persist(gdbConn, pdbConn);
            logInfo(logTag, "calculateCpcMarkup -> Exit: COMPLETE!");
        }
    }

    /**
     * Check budget overage.
     */
    private void checkBudgetOverage(Connection conn, Product product, DebitableItem item) throws BudgetManagerException {
        if (stubMode) {
            return;
        } else {
            String logTag = getLogTag(product.getProdInstId());

            try {
                Calendar today = Calendar.getInstance();
                Map<String, Double> budgetInfo = budgetManagerHelper.getBudgetsRemaining(conn, item);

                // within 2.5% of the budget being consumed?
                double campaignBuffer = budgetInfo.get("campaign_daily_budget") == null ? 0.0 : (budgetInfo.get("campaign_daily_budget") * 0.025);

                logInfo(logTag, "checkOverage -> campaignBuffer: "+campaignBuffer);

                // first, monthly budget -- it trumps all budget overages
                if (budgetInfo.get("monthly_budget_remaining") <= 0.0) {
                    // there is no monthly budget remaining.
                    if (product.getDaysUntilExpiration(item.getDateAsCalendar()) > 7.0) {
                        // and there are more than 7 days left!
                        logInfo(logTag, "checkOverage -> Monthly budget has been consumed early!");
                        throw new BudgetManagerException(BudgetManagerException.MONTHLY_BUDGET_CONSUMED_EARLY, "Monthly Budget has been consumed for product: "
                            + item.getProdInstId() + " with AT LEAST 7 days remaining in the billing cycle!\n" + "Budget Overage: " + (budgetInfo.get("monthly_budget_remaining")*-1));
                    } else {
                        // the product works?!
                        logInfo(logTag, "checkOverage -> Monthly budget has been consumed!");
                        throw new BudgetManagerException(BudgetManagerException.MONTHLY_BUDGET_CONSUMED, "Monthly Budget has been consumed for product: "
                            + item.getProdInstId() + "\n" + "Budget Overage: " + (budgetInfo.get("monthly_budget_remaining")*-1));
                    }
                }

                // next, daily budget
                if (budgetInfo.get("daily_budget_remaining") <= 0.0) {
                    // there is no daily budget remaining
                    if (today.get(Calendar.HOUR_OF_DAY) <= 16) {
                        // and it's not even 4pm yet!
                        SimpleDateFormat format = new SimpleDateFormat("MMM d yyyy HH:mm z");
                        logInfo(logTag, "checkOverage -> Daily budget has been consumed early!");
                        throw new BudgetManagerException(BudgetManagerException.DAILY_BUDGET_CONSUMED_EARLY, "Daily budget for product: " + item.getProdInstId()
                                + " has been consumed before " + format.format(today.getTime()) + "!");
                    } else {
                        // the product works?!
                        logInfo(logTag, "checkOverage -> Daily budget has been consumed!");
                        throw new BudgetManagerException(BudgetManagerException.DAILY_BUDGET_CONSUMED, "Daily budget for product: " + item.getProdInstId()
                                + " has been consumed!");
                    }
                }

                // next, campaign budget:
                if (budgetInfo.get("campaign_daily_budget") != null && (budgetInfo.get("campaign_budget_remaining") <= campaignBuffer)
                        && (item.getNsCampaignId() > 0)) {
                    // almost out....!
                    logInfo(logTag, "checkOverage -> Campaign budget has reached buffer!");
                    throw new BudgetManagerException(BudgetManagerException.CAMPAIGN_BUDGET_CONSUMED, "Campaign " + item.getNsCampaignId() + "'s budget has been consumed for product: " + item.getProdInstId());
                }
            } catch (BudgetManagerException bme) {
                throw bme;
            } catch (Exception e) {
                logError(logTag, e);
                throw new BudgetManagerException(BudgetManagerException.UNKNOWN_ERROR, "Unknown Error", ""+e, e);
            }
        }
    }

    /**
     * Check thresholds.
     */
    private void checkThresholds(Connection conn, Product product) throws BudgetManagerException {
        if (stubMode) {
            return;
        }
        else {
            String prodInstId = product.getProdInstId();
            String logTag = getLogTag(prodInstId);
            try {
                PpcProductDetailHelper ppcProductDetailHelper = new PpcProductDetailHelper(this);
                PpcProductDetail ppcProductDetail = ppcProductDetailHelper.getPpcProductDetail(logTag, conn, prodInstId);
                if (ppcProductDetail != null) {

                    BudgetCycleProductData budgetCycleProductData = BudgetCycleProductData.getCurrentBudgetCycleProductData(conn, product);
                    int leadCount = budgetCycleProductData.getBudgetCycleData().getLeadCount();
                    int clickCount = budgetCycleProductData.getBudgetCycleData().getClickCount();

                    if (ppcProductDetail.getLeadThreshold() != null && leadCount >= ppcProductDetail.getLeadThreshold()) {
                        // The resulting behavior is the same as if the monthly budget was consumed.
                        throw new BudgetManagerException(BudgetManagerException.MONTHLY_BUDGET_CONSUMED, "Monthly lead threshold exceeded for product "
                                + prodInstId + ": leadCount=" + leadCount + ", leadThreshold=" + ppcProductDetail.getLeadThreshold());
                    }

                    if (ppcProductDetail.getClickThreshold() != null && clickCount >= ppcProductDetail.getClickThreshold()) {
                        // The resulting behavior is the same as if the monthly budget was consumed.
                        throw new BudgetManagerException(BudgetManagerException.MONTHLY_BUDGET_CONSUMED, "Monthly click threshold exceeded for product "
                                + prodInstId + ": clickCount=" + clickCount + ", clickThreshold=" + ppcProductDetail.getClickThreshold());
                    }
                }
            }
            catch (Exception e) {
                logError(logTag, e);
                throw new BudgetManagerException(BudgetManagerException.UNKNOWN_ERROR, "Unknown Error", ""+e, e);
            }
        }
    }

    /**
     * Gets the max bid data. The nsCampaignId is optional; if not supplied, only max bids for new campaigns will be returned.
     *
     * @param prodInstId
     * @param nsCampaignId optional
     * @return
     * @throws BudgetManagerException
     */
    public MaxBidData getMaxBidData(Connection gdbConn, Connection pdbConn, String prodInstId, Long nsCampaignId) throws BudgetManagerException {
        MaxBidData maxBidData = new MaxBidData();
        if (nsCampaignId != null) {
            // Set current campaign daily budget as max bid.
            CampaignList campaignList = budgetManagerHelper.getSingleCampaignInfo(pdbConn, prodInstId, nsCampaignId);
            Campaign campaign = campaignList.getCampaigns().get(0);
            maxBidData.setMaxBidForCampaign(campaign.getDailyBudget());
        }

        Calendar today = Calendar.getInstance();
        Product product = new Product(this);
        product.init(gdbConn, pdbConn, prodInstId);
        double daysUntilExpiration = product.getDaysUntilExpiration(today);

        ProductSummaryData productSummary = new ProductSummaryData(this);
        productSummary.init(pdbConn, prodInstId, today);
        double monthlyBudgetRemaining = productSummary.getMonthlyBudgetRemaining();
        double dailyBudgetRemaining = productSummary.getDailyBudgetRemaining();

        double budgetRemainingPerDay = (monthlyBudgetRemaining - dailyBudgetRemaining) / daysUntilExpiration;

        // Set the new campaign max bid values per vendor.
        String[] statuses = { CampaignStatus.ACTIVE, CampaignStatus.SYSTEM_PAUSE};
        CampaignList campaignList = budgetManagerHelper.getCampaigns(pdbConn, prodInstId, statuses);
        int numCampaigns = campaignList.getCampaigns().size();
        if (numCampaigns > 0) {
            // We have existing campaigns.
            VendorBudgetAllocations vba = new VendorBudgetAllocations(this, prodInstId);
            if (vba.hasAllocations()) {
                // Count campaigns per vendor.
                List<Campaign> campaigns = campaignList.getCampaigns();
                int googleCount = 0;
                int bingCount = 0;
                for (Campaign c : campaigns) {
                    if (c.getVendorId() == VendorId.GOOGLE) {
                        googleCount++;
                    }
                    else if (c.getVendorId() == VendorId.MICROSOFT) {
                        bingCount++;
                    }
                }

                // Google
                double allocation = vba.getAllocation(VendorId.GOOGLE);
                double newCampaignBudget = (allocation * budgetRemainingPerDay) / (googleCount + 1);
                maxBidData.setMaxBidNewGoogleCampaign(newCampaignBudget);

                // Bing
                allocation = vba.getAllocation(VendorId.MICROSOFT);
                newCampaignBudget = (allocation * budgetRemainingPerDay) / (bingCount + 1);
                maxBidData.setMaxBidNewBingCampaign(newCampaignBudget);
            }
            else {
                double newCampaignBudget = budgetRemainingPerDay / (numCampaigns + 1);
                maxBidData.setMaxBidNewGoogleCampaign(newCampaignBudget);
                maxBidData.setMaxBidNewBingCampaign(newCampaignBudget);
            }
        }
        else {
            // No existing campaigns - use remaining budget per day as max bid.
            maxBidData.setMaxBidNewGoogleCampaign(budgetRemainingPerDay);
            maxBidData.setMaxBidNewBingCampaign(budgetRemainingPerDay);
        }

        return maxBidData;
    }

    /**
     * Adjust campaign monthly budgets if necessary, such as when the base budget, vendor budget allocations, or target platform budgets change.
     *
     * @param pdbConn
     * @param product
     * @throws Exception
     */
    private void adjustCampaignMonthlyBudgets(Connection pdbConn, Product product, BudgetCycleProductData budgetCycleProductData) throws Exception {
        String prodInstId = product.getProdInstId();
        String logTag = getLogTag(prodInstId);
        logInfo(logTag, "starting adjustCampaignMonthlyBudgets...");
        final String updatedBySystem = "BM.adjustCampaignMonthlyBudgets";

        // Get the targets.
        TargetHelper targetHelper = new TargetHelper(this);
        List<Target> targetList = null;
        try {
            CampaignList campaigns = budgetManagerHelper.getCampaigns(pdbConn, product.getProdInstId(), new String[] { CampaignStatus.ACTIVE, CampaignStatus.SYSTEM_PAUSE, CampaignStatus.MANUAL_PAUSE});
            // Note: We can't use BudgetCycleProductData to sum campaign monthly budgets in this context because BudgetCycleProductData is not guaranteed to always include data for
            // manual pause campaigns, by design.

            if (campaigns != null && !campaigns.isEmpty()) {
                targetList = targetHelper.getTargetsValidForCycleDates(logTag, pdbConn, prodInstId, CalendarUtil.dateToCalendar(product.getStartDate()), CalendarUtil.dateToCalendar(product.getExpirationDate()));
                if (targetList != null && !targetList.isEmpty()) {
                    logInfo(logTag, "adjustCampaignMonthlyBudgets: target list is not empty, adjusting target campaigns.");
                    for (Target target : targetList) {
                        logInfo(logTag, "adjustCampaignMonthlyBudgets: target loop, targetId=" + target.getTargetId());
                        TargetVendor[] targetVendors = target.getTargetVendors();
                        if (targetVendors != null) {
                            for (TargetVendor targetVendor : targetVendors) {
                                logInfo(logTag, "adjustCampaignMonthlyBudgets: target vendor loop, vendorId=" + targetVendor.getVendorId());
                                // Sum the target vendor campaign monthly budgets.
                                double totalTargetVendorCampaignMonthlyBudget = 0;
                                for (Campaign campaign : campaigns.getCampaigns()) {
                                    if (campaign.getTargetId() != null && campaign.getTargetId() == target.getTargetId() && targetVendor.getVendorId() == campaign.getVendorId() && campaign.getMonthlyBudget() != null) {
                                        totalTargetVendorCampaignMonthlyBudget += campaign.getMonthlyBudget();
                                    }
                                }
                                logInfo(logTag, "adjustCampaignMonthlyBudgets: totalTargetVendorCampaignMonthlyBudget=" + totalTargetVendorCampaignMonthlyBudget + ", targetVendor.budget=" + targetVendor.getBudget());

                                if (totalTargetVendorCampaignMonthlyBudget > 0 && totalTargetVendorCampaignMonthlyBudget > targetVendor.getBudget()) {
                                    for (Campaign campaign : campaigns.getCampaigns()) {
                                        logInfo(logTag, "adjustCampaignMonthlyBudgets: target vendor campaign loop, nsCampaignId=" + campaign.getNsCampaignId());
                                        if (campaign.getTargetId() != null && campaign.getTargetId() == target.getTargetId() && targetVendor.getVendorId() == campaign.getVendorId() && campaign.getMonthlyBudget() != null) {
                                            campaign.setMonthlyBudget(targetVendor.getBudget() * (campaign.getMonthlyBudget() / totalTargetVendorCampaignMonthlyBudget));
                                            logInfo(logTag, "adjustCampaignMonthlyBudgets: new campaign.montlyBudget=" + campaign.getMonthlyBudget());
                                            campaign.persist(pdbConn, updatedBySystem);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Now adjust non-target campaigns.
                logInfo(logTag, "adjustCampaignMonthlyBudgets: adjusting non-target campaigns");
                double nonTargetMonthlyBudget = budgetCycleProductData.getTotalNonTargetMonthlyBudget();
                logInfo(logTag, "adjustCampaignMonthlyBudgets: nonTargetMonthlyBudget=" + nonTargetMonthlyBudget);
                VendorBudgetAllocations allocations = VendorBudgetAllocations.getVendorBudgetAllocations(this, pdbConn, prodInstId);
                if (allocations.hasAllocations()) {
                    logInfo(logTag, "adjustCampaignMonthlyBudgets: there are vendor budget allocations");
                    // Sum the non-target campaign monthly budgets per vendor.
                    Map<Integer, Double> vendorTotalCampaignMonthlyBudgetMap = new HashMap<Integer, Double>();
                    logInfo(logTag, "adjustCampaignMonthlyBudgets: totaling vendor non-target monthly budgets.");
                    for (Campaign campaign : campaigns.getCampaigns()) {
                        if (campaign.getTargetId() == null && campaign.getMonthlyBudget() != null) {
                            logInfo(logTag, "adjustCampaignMonthlyBudgets: found a non-target campaign with a non-null monthly budget, nsCampaignId=" + campaign.getNsCampaignId() + ", monthlyBudget=" + campaign.getMonthlyBudget());
                            Double vendorTotalCampaignMonthlyBudget = vendorTotalCampaignMonthlyBudgetMap.get(campaign.getVendorId());
                            if (vendorTotalCampaignMonthlyBudget == null) {
                                vendorTotalCampaignMonthlyBudget = 0d;
                            }
                            vendorTotalCampaignMonthlyBudget += campaign.getMonthlyBudget();
                            vendorTotalCampaignMonthlyBudgetMap.put(campaign.getVendorId(), vendorTotalCampaignMonthlyBudget);
                            logInfo(logTag, "adjustCampaignMonthlyBudgets: vendorId=" + campaign.getVendorId() + ", new vendorTotalCampaignMonthlyBudget=" + vendorTotalCampaignMonthlyBudget);
                        }
                    }

                    for (Campaign campaign : campaigns.getCampaigns()) {
                        if (campaign.getTargetId() == null && campaign.getMonthlyBudget() != null) {
                            logInfo(logTag, "adjustCampaignMonthlyBudgets: adjusting a non-target campaign with a non-null monthly budget, nsCampaignId=" + campaign.getNsCampaignId() + ", monthlyBudget=" + campaign.getMonthlyBudget());
                            Double allocation = allocations.getAllocation(campaign.getVendorId());
                            double vendorMonthlyBudget = nonTargetMonthlyBudget * (allocation == null ? 0 : allocation);
                            double vendorTotalCampaignMonthlyBudget = vendorTotalCampaignMonthlyBudgetMap.get(campaign.getVendorId());
                            logInfo(logTag, "adjustCampaignMonthlyBudgets: allocation=" + allocation + ", vendorMonthlyBudget=" + vendorMonthlyBudget + ", vendorTotalCampaignMonthlyBudget=" + vendorTotalCampaignMonthlyBudget);
                            if (vendorTotalCampaignMonthlyBudget > 0 && vendorTotalCampaignMonthlyBudget > vendorMonthlyBudget) {
                                campaign.setMonthlyBudget(vendorMonthlyBudget * (campaign.getMonthlyBudget() / vendorTotalCampaignMonthlyBudget));
                                logInfo(logTag, "adjustCampaignMonthlyBudgets: new campaign.monthlyBudget=" + campaign.getMonthlyBudget());
                                campaign.persist(pdbConn, updatedBySystem);
                            }
                        }
                    }
                }
                else {
                    logInfo(logTag, "adjustCampaignMonthlyBudgets: there are no vendor budget allocations");
                    // Sum the non-target campaign monthly budgets.
                    double nonTargetCampaignMonthlyBudget = 0;
                    for (Campaign campaign : campaigns.getCampaigns()) {
                        if (campaign.getTargetId() == null && campaign.getMonthlyBudget() != null) {
                            logInfo(logTag, "adjustCampaignMonthlyBudgets: found a non-target campaign with a non-null monthly budget, nsCampaignId=" + campaign.getNsCampaignId() + ", monthlyBudget=" + campaign.getMonthlyBudget());
                            nonTargetCampaignMonthlyBudget += campaign.getMonthlyBudget();
                        }
                    }
                    logInfo(logTag, "adjustCampaignMonthlyBudgets: nonTargetCampaignMonthlyBudget=" + nonTargetCampaignMonthlyBudget + ", nonTargetMonthlyBudget=" + nonTargetMonthlyBudget);

                    if (nonTargetCampaignMonthlyBudget > 0 && nonTargetCampaignMonthlyBudget > nonTargetMonthlyBudget) {
                        for (Campaign campaign : campaigns.getCampaigns()) {
                            if (campaign.getTargetId() == null && campaign.getMonthlyBudget() != null) {
                                logInfo(logTag, "adjustCampaignMonthlyBudgets: adjusting a non-target campaign with a non-null monthly budget, nsCampaignId=" + campaign.getNsCampaignId() + ", monthlyBudget=" + campaign.getMonthlyBudget());
                                campaign.setMonthlyBudget(nonTargetMonthlyBudget * (campaign.getMonthlyBudget() / nonTargetCampaignMonthlyBudget));
                                logInfo(logTag, "adjustCampaignMonthlyBudgets: new campaign.montlyBudget=" + campaign.getMonthlyBudget());
                                campaign.persist(pdbConn, updatedBySystem);
                            }
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            throw new BudgetManagerException(BudgetManagerException.UNKNOWN_ERROR, "adjustCampaignMonthlyBudgets: An unexpected error occurred " + prodInstId,
                    e.getMessage(), e);
        }
    }

    /**
     * Adjust budgets and statuses for today. Unlike adjustForDay, this method may be called more than once per day.
     */
    public void adjustBudgetsAndStatuses(Connection gdbConn, Connection pdbConn, String prodInstId) throws Exception {
        // TODO Refactor code out of this method into other objects/methods! 1. consider pushing VBA calcs into BudgetCycleProductData... superpages account budget... etc...
        // Initialize product and summary data.
        String logTag = getLogTag(prodInstId);
        logInfo(logTag, "starting adjustBudgetsAndStatuses...");
        final String updatedBySystem = "BM.adjustBudgetsAndStatuses";

        Calendar today = Calendar.getInstance();
        Product product = new Product(this);
        product.init(gdbConn, pdbConn, prodInstId);
        ProductSummaryData productSummaryData = new ProductSummaryData(this);
        productSummaryData.init(pdbConn, prodInstId);

        // Get budget cycle data for this product.
        BudgetCycleProductData budgetCycleProductData = BudgetCycleProductData.getCurrentBudgetCycleProductData(pdbConn, product);
        logInfo(logTag, "adjustBudgetsAndStatuses: budgetCycleProductData=" + budgetCycleProductData.toString());

        // Adjust campaign monthly budgets.
        adjustCampaignMonthlyBudgets(pdbConn, product, budgetCycleProductData);

        // Check to see if the current target is accurate. Since the current target is set at the time of renewal, and it includes rollover budget, the current target
        // could be inaccurate if reconcilliation changed the budget rollover amount.
        double currentTargetDiff = product.getCurrentTarget() - budgetCycleProductData.getTotalBudget();
        if (product.getRolloverBudget() && Math.abs(currentTargetDiff) > 0.01) { // After arithmetic, doubles are almost never exactly 0...
            logInfo(logTag, "adjustBudgetsAndStatuses: Product current_target (" + product.getCurrentTarget()  + ") did not match reconciled MBR (" + budgetCycleProductData.getMonthlyBudgetRemaining() + ") + current cost ("
                    + budgetCycleProductData.getBudgetCycleData().getTotalCost() + "). New current target is " + budgetCycleProductData.getTotalBudget());
            product.setCurrentTarget(budgetCycleProductData.getTotalBudget());
            product.setUpdatedBySystem(updatedBySystem);
            product.setUpdatedByUser(updatedByUser);
            product.persist(gdbConn, pdbConn);
        }

        // Check to see if the current margin amount has changed.
        // The diff will be positive if there is an increase in the margin amount.
        double totalTargetMarginAmount = budgetCycleProductData.getTotalTargetMarginAmount();
        double marginAmountDiff = totalTargetMarginAmount - product.getCurrentMarginAmount();
        if (Math.abs(marginAmountDiff) > 0.01) { // After arithmetic, doubles are almost never exactly 0...
            logInfo(logTag, "adjustBudgetsAndStatuses: Product totalTargetMarginAmount (" + totalTargetMarginAmount + ") did not match currentMarginAmount (" + product.getCurrentMarginAmount() + "), "
                    + "modifying currentTarget and monthlyBudgetRemaining by diff amount (" + marginAmountDiff + ").");
            // Update the product.
            product.setCurrentMarginAmount(totalTargetMarginAmount);
            product.setCurrentTarget(product.getCurrentTarget() - marginAmountDiff);
            product.setUpdatedBySystem(updatedBySystem);
            product.setUpdatedByUser(updatedByUser);
            product.persist(gdbConn, pdbConn);
            // Update the product summary. If the margin increased, we need to subtract the difference.
            productSummaryData.modifyMonthlyBudgetRemaining(-marginAmountDiff);
            // Re-query product summary data.
            productSummaryData = new ProductSummaryData(this);
            productSummaryData.init(pdbConn, prodInstId);

            // Re-query the budget cycle data since the monthly budget changed.
            budgetCycleProductData = BudgetCycleProductData.getCurrentBudgetCycleProductData(pdbConn, product);
            logInfo(logTag, "adjustBudgetsAndStatuses: requeried after margin update, budgetCycleProductData=" + budgetCycleProductData.toString());
        }

        // Set the SEO budget.
        budgetManagerHelper.updateRoadmapCycleBudget(pdbConn, prodInstId, budgetCycleProductData.getTotalTargetSEOMonthlyBudget());

        // Get the campaigns.
        CampaignList campaigns = budgetManagerHelper.getCampaigns(pdbConn, product.getProdInstId(), new String[] { CampaignStatus.ACTIVE, CampaignStatus.SYSTEM_PAUSE});
        double totalCampaignDailyBudget = 0;
        if (campaigns != null && !campaigns.isEmpty()) {
            logInfo(logTag, "adjustBudgetsAndStatuses: there are ACTIVE and/or SYSTEM_PAUSE campaigns.");
            // Check monthly budget remaining and click and lead thresholds.
            PpcProductDetailHelper ppcProductDetailHelper = new PpcProductDetailHelper(this);
            PpcProductDetail ppcProductDetail = ppcProductDetailHelper.getPpcProductDetail(logTag, pdbConn, prodInstId);
            if (productSummaryData.getMonthlyBudgetRemaining() <= 0.05 ||
                (ppcProductDetail.getLeadThreshold() != null && budgetCycleProductData.getBudgetCycleData().getLeadCount() >= ppcProductDetail.getLeadThreshold()) ||
                    (ppcProductDetail.getClickThreshold() != null && budgetCycleProductData.getBudgetCycleData().getClickCount() >= ppcProductDetail.getClickThreshold())) {
                logInfo(logTag, "adjustBudgetsAndStatuses: Monthly budget is exhaused or a threshold was exceeded. Pause all campaigns.");
                // Monthly budget is exhaused or a threshold was exceeded. Pause all campaigns.
                for (Campaign campaign : campaigns.getCampaigns()) {
                    campaign.setStatus(CampaignStatus.SYSTEM_PAUSE);
                }

                // We also have to set the Superpages budget. Since we are not spending, set the budget to the current cost (which could be $0).
                VendorCredentials vendorCredentials = getVendorCredentials(gdbConn, prodInstId, VendorId.SUPERPAGES);
                if (vendorCredentials != null) {
                    // Superpages account exists. Set the budget.
                    BaseHelper.commit(gdbConn);
                    BaseHelper.commit(pdbConn);
                    BudgetCycleData superpagesBudgetCycleData = budgetCycleProductData.getVendorBudgetCycleDataMap().get((long)VendorId.SUPERPAGES);
                    double superPagesAccountBudget = superpagesBudgetCycleData == null ? 0 : superpagesBudgetCycleData.getTotalCost();
                    logInfo(logTag, "adjustBudgetsAndStatuses: Monthly budget is exhaused or a threshold was exceeded. Setting Superpages account budget to " + superPagesAccountBudget);
                    updateSuperPagesAccountBudget(prodInstId, superPagesAccountBudget);
                }
            }
            else {
                // Get the vendor budget allocations. Vendor budget allocations only apply to non-target campaigns and Superpages.
                VendorBudgetAllocations allocations;
                try {
                    allocations = VendorBudgetAllocations.getVendorBudgetAllocations(this, pdbConn, prodInstId);
                }
                catch (SQLException e) {
                    throw new BudgetManagerException(BudgetManagerException.UNKNOWN_ERROR,
                            "adjustTargetsAndCampaigns: Error while querying vendor budget allocations for prodInstId: " + prodInstId);
                }

                // Campaigns can be run. Is the adjust budget flag on or off?
                if (!product.isAdjustBudget()) {
                    // Adjust budget flag is off. When the flag is off:
                    // - Daily campaign budgets are set manually by the analyst; don't modify campaign budgets.
                    // - Targets are ignored.
                    // - Spend aggressiveness is ignored; campaign budgets determine pacing.
                    // - Vendor budget allocations are ignored.
                    // - Superpages campaigns are special and will be treated differently (later in the code).
                    logInfo(logTag, "adjustBudgetsAndStatuses: adjustBudget is off.");
                    for (Campaign campaign : campaigns.getCampaigns()) {
                        if (campaign.getVendorId() != VendorId.SUPERPAGES){
                            // Check the daily budget remaining and either pause or activate.
                            BudgetCycleData campaignBudgetCycleData = budgetCycleProductData.getCampaignBudgetCycleDataMap().get(campaign.getNsCampaignId());
                            if (campaignBudgetCycleData != null && campaignBudgetCycleData.getDailyCost() > campaign.getDailyBudget()) {
                                // The target vendor has monthly budget remaining, but this campaign has no daily budget remaining. Pause it.
                                campaign.setStatus(CampaignStatus.SYSTEM_PAUSE);
                            }
                            else {
                                // The campaign has daily budget remaining.
                                campaign.setStatus(CampaignStatus.ACTIVE);
                            }
                        }
                    }
                }
                else {
                    // Adjust budget flag is on.
                    logInfo(logTag, "adjustBudgetsAndStatuses: adjustBudget is on. Building campaign data structures...");
                    // Build some useful data structures to facilitate adjustment of targets and campaigns.
                    // These data structures contain only non-Superpages campaigns. Superpages campaigns are dealt with separately.
                    // A map of non-Superpages campaign lists by target.
                    Map<Long, List<Campaign>> targetCampaignListMap = new HashMap<Long, List<Campaign>>();
                    // A map of maps (of non-Superpages campaign by vendor) by target.
                    Map<Long, Map<Long, List<Campaign>>> targetVendorCampaignListMap = new HashMap<Long, Map<Long, List<Campaign>>>();
                    // A list of non-target non-Superpages campaigns.
                    List<Campaign> nonTargetCampaignList = new ArrayList<Campaign>();
                    // A map of non-target non-Superpages campaign lists by vendor.
                    Map<Long, List<Campaign>> nonTargetVendorCampaignListMap = new HashMap<Long, List<Campaign>>();
                    for (Campaign campaign : campaigns.getCampaigns()) {
                        // We are only collecting non-Superpages campaigns.
                        if (campaign.getVendorId() != VendorId.SUPERPAGES){
                            Long targetId = campaign.getTargetId();
                            if (targetId != null) {
                                // Do we already have a campaign list for this target?
                                List<Campaign> targetCampaignList = targetCampaignListMap.get(targetId);
                                if (targetCampaignList == null) {
                                    targetCampaignList = new ArrayList<Campaign>();
                                    targetCampaignListMap.put(targetId, targetCampaignList);
                                }
                                targetCampaignList.add(campaign);

                                // Do we already have a target vendor campaign list map for this target?
                                Map<Long, List<Campaign>> vendorCampaignListMap = targetVendorCampaignListMap.get(targetId);
                                if (vendorCampaignListMap == null) {
                                    vendorCampaignListMap = new HashMap<Long, List<Campaign>>();
                                    targetVendorCampaignListMap.put(targetId, vendorCampaignListMap);
                                }

                                // Do we already have target vendor campaign list for this vendor?
                                List<Campaign> vendorCampaignList = vendorCampaignListMap.get((long)campaign.getVendorId());
                                if (vendorCampaignList == null) {
                                    vendorCampaignList = new ArrayList<Campaign>();
                                    vendorCampaignListMap.put((long)campaign.getVendorId(), vendorCampaignList);
                                }
                                vendorCampaignList.add(campaign);
                            }
                            else {
                                // Add to non-target campaign list.
                                nonTargetCampaignList.add(campaign);

                                // Do we already have a non-target vendor campaign list for this vendor?
                                List<Campaign> nonTargetVendorCampaignList = nonTargetVendorCampaignListMap.get((long)campaign.getVendorId());
                                if (nonTargetVendorCampaignList == null) {
                                    nonTargetVendorCampaignList = new ArrayList<Campaign>();
                                    nonTargetVendorCampaignListMap.put((long)campaign.getVendorId(), nonTargetVendorCampaignList);
                                }
                                nonTargetVendorCampaignList.add(campaign);
                            }
                        }
                    }
                    // end of building data structures

                    //
                    // Adjust targets and target campaigns.
                    //

                    // Get the targets.
                    TargetHelper targetHelper = new TargetHelper(this);
                    List<Target> targetList = null;
                    try {
                        logInfo(logTag, "adjustBudgetsAndStatuses: Querying targets...");
                        targetList = targetHelper.getTargetsValidForCycleDates(logTag, pdbConn, prodInstId, CalendarUtil.dateToCalendar(product.getStartDate()),
                               CalendarUtil.dateToCalendar(product.getExpirationDate()));
                    }
                    catch (Exception e) {
                        throw new BudgetManagerException(BudgetManagerException.UNKNOWN_ERROR, "adjustTargetsAndCampaigns: An unexpected error occurred while querying targets for product " + prodInstId,
                                e.getMessage(), e);
                    }

                    if (targetList != null && !targetList.isEmpty()) {
                        logInfo(logTag, "adjustBudgetsAndStatuses: There are targets.");
                        for (Target target : targetList) {
                            logInfo(logTag, "adjustBudgetsAndStatuses: target loop, target=" + target.getTargetId());
                            boolean pauseAllTargetCampaigns = false;
                            Long targetId = target.getTargetId();
                            // Get the target's total cost in the current cycle.
                            BudgetCycleData targetData = budgetCycleProductData.getTargetBudgetCycleDataMap().get(targetId);
                            double targetCost = 0;
                            if (targetData != null) {
                                targetCost = targetData.getTotalCost();
                            }
                            // Has the target exhausted its budget?
                            if (targetCost >= target.calculateActualMonthlyPPCBudget()) {
                                // Target PPC budget has been exhausted. System-pause all campaigns in the target.
                                logInfo(logTag, "adjustBudgetsAndStatuses: target has exhausted its monthly budget, pausing all campaigns.");
                                pauseAllTargetCampaigns = true;
                            }
                            else {
                                // Target has remaining PPC budget.
                                logInfo(logTag, "adjustBudgetsAndStatuses: target has monthly budget remaining");
                                if (target.getStatus().equals(Target.Status.ACTIVE)) {
                                    // Target is active. Now check the target date range.
                                    logInfo(logTag, "adjustBudgetsAndStatuses: target is active");
                                    if (target.isDayInTargetDateRange(today)) {
                                        // Today is in the valid date range for the target.
                                        logInfo(logTag, "adjustBudgetsAndStatuses: target date range is valid");
                                        TargetVendor[] targetVendors = target.getTargetVendors();
                                        if (targetVendors != null) {
                                            // This code assumes that for a given target/vendor combination for a campaign, there is a target_vendor record, which should always be the case.
                                            for (TargetVendor targetVendor : targetVendors) {
                                                long vendorId = targetVendor.getVendorId();
                                                logInfo(logTag, "adjustBudgetsAndStatuses: targetVendor loop, vendorId=" + vendorId);
                                                Map<Long, List<Campaign>> vendorCampaignListMap = targetVendorCampaignListMap.get(targetId);
                                                if (vendorCampaignListMap != null && !vendorCampaignListMap.isEmpty()) {
                                                    List<Campaign> targetVendorCampaignList = vendorCampaignListMap.get(vendorId);
                                                    if (targetVendorCampaignList != null && !targetVendorCampaignList.isEmpty()) {
                                                        Map<Long, BudgetCycleData> targetVendorBudgetCycleDataMap = budgetCycleProductData.getTargetVendorBudgetCycleDataMap().get(targetId);
                                                        // Get the total cost for this target vendor in the current cycle.
                                                        double targetVendorCurrentCost = 0;
                                                        if (targetVendorBudgetCycleDataMap != null && !targetVendorBudgetCycleDataMap.isEmpty()) {
                                                            BudgetCycleData targetVendorData = targetVendorBudgetCycleDataMap.get(vendorId);
                                                            targetVendorCurrentCost = targetVendorData.getTotalCost();
                                                        }
                                                        logInfo(logTag, "adjustBudgetsAndStatuses: targetVendorCurrentCost=" + targetVendorCurrentCost);
                                                        // Has this target vendor exhausted its budget?
                                                        double targetVendorMBR = targetVendor.getBudget() - targetVendorCurrentCost;
                                                        logInfo(logTag, "adjustBudgetsAndStatuses: targetVendorMBR=" + targetVendorMBR);
                                                        if (targetVendorMBR <= 0.01) {
                                                            logInfo(logTag, "adjustBudgetsAndStatuses: targetVendor is out of monthly budget, pausing campaigns.");
                                                            // Target vendor budget is exhausted. Pause all campaigns for this target vendor.
                                                            for (Campaign campaign : targetVendorCampaignList) {
                                                                campaign.setStatus(CampaignStatus.SYSTEM_PAUSE);
                                                            }
                                                        }
                                                        else {
                                                            logInfo(logTag, "adjustBudgetsAndStatuses: targetVendor has monthly budget");
                                                            // Target vendor has remaining monthly budget.
                                                            int targetVendorCampaignCount = targetVendorCampaignList.size();
                                                            logInfo(logTag, "adjustBudgetsAndStatuses: targetVendorCampaignCount=" + targetVendorCampaignCount);
                                                            if (targetVendorCampaignCount > 0) { // The list can't be empty in this case, but I check anyway since I divide by the size.
                                                                // Calculate the spend aggressiveness. Order of precedence: target vendor, target, product
                                                                float spendAggressiveness = targetVendor.getSpendAggressiveness() != null ? targetVendor.getSpendAggressiveness() :
                                                                    target.getSpendAggressiveness() != null ? target.getSpendAggressiveness() :
                                                                        budgetManagerHelper.getVendorSpendAggressivenessForProduct(pdbConn, prodInstId, vendorId);
                                                                logInfo(logTag, "adjustBudgetsAndStatuses: targetVendor effective spendAggressiveness=" + spendAggressiveness);

                                                                // First, loop over the campaigns to handle the difference between the ones with and without monthly budget set.
                                                                int campaignWithNoMonthlyBudgetCount = 0;
                                                                for (Campaign campaign : targetVendorCampaignList) {
                                                                    if (campaign.getMonthlyBudget() != null) {
                                                                        BudgetCycleData campaignBudgetCycleData = budgetCycleProductData.getCampaignBudgetCycleDataMap().get(campaign.getNsCampaignId());
                                                                        // Deduct the campaign MBR from the targetVendorMBR. The remainder will be available to campaigns
                                                                        // that do not have a monthly budget value set.
                                                                        targetVendorMBR -= (campaign.getMonthlyBudget() - campaignBudgetCycleData.getTotalCost());
                                                                    }
                                                                    else {
                                                                        // Count how many campaigns there are with no monthly budget value set.
                                                                        campaignWithNoMonthlyBudgetCount++;
                                                                    }
                                                                }
                                                                logInfo(logTag, "adjustBudgetsAndStatuses: targetVendorMBR w/campaign monthly budgets deducted=" + targetVendorMBR);
                                                                logInfo(logTag, "adjustBudgetsAndStatuses: campaignWithNoMonthlyBudgetCount=" + campaignWithNoMonthlyBudgetCount);

                                                                for (Campaign campaign : targetVendorCampaignList) {
                                                                    logInfo(logTag, "adjustBudgetsAndStatuses: targetVendor campaign loop, nsCampaignId=" + campaign.getNsCampaignId());
                                                                    BudgetCycleData campaignBudgetCycleData = budgetCycleProductData.getCampaignBudgetCycleDataMap().get(campaign.getNsCampaignId());
                                                                    double campaignMBR = 0;
                                                                    if (campaign.getMonthlyBudget() == null) {
                                                                        // We are dividing the target vendor budget equally among campaigns w/o monthly budget.
                                                                        campaignMBR = targetVendorMBR / (double)campaignWithNoMonthlyBudgetCount;
                                                                    }
                                                                    else {
                                                                        // Calculate the MBR using the campaign monthly budget.
                                                                        campaignMBR = campaign.getMonthlyBudget() - campaignBudgetCycleData.getTotalCost();
                                                                    }
                                                                    logInfo(logTag, "adjustBudgetsAndStatuses: targetVendor campaign loop, campaignMBR=" + campaignMBR);

                                                                    // Get the day count using the ad scheduling for the campaign.
                                                                    // We don't care whether the campaign is scheduled for today. The vendor handles that.
                                                                    double dayCount = adScheduleHelper.getNumberOfDaysRemainingInCycleWhenCampaignIsScheduled(gdbConn, pdbConn, prodInstId, campaign.getNsCampaignId());
                                                                    // Make sure the day count does not exceed the number of days before the target expires.
                                                                    Calendar endDate = target.getEndDateAsCalendar();
                                                                    dayCount = endDate == null ? dayCount : Math.min(dayCount, CalendarUtil.getDaysBetween(today, endDate));
                                                                    logInfo(logTag, "adjustBudgetsAndStatuses: targetVendor campaign loop, dayCount=" + dayCount);
                                                                    // Don't let the aggressiveness push the campaign daily budget beyond the campaign MBR.
                                                                    double campaignDailyBudget = Math.min(((campaignMBR / dayCount) * spendAggressiveness), campaignMBR);
                                                                    campaign.setDailyBudget(campaignDailyBudget);
                                                                    // Check the daily budget remaining and either pause or activate.
                                                                    double campaignDailyCost = campaignBudgetCycleData == null ? 0 : campaignBudgetCycleData.getDailyCost();
                                                                    logInfo(logTag, "adjustBudgetsAndStatuses: targetVendor campaign loop, campaignDailyBudget=" + campaignDailyBudget
                                                                        + ", campaignDailyCost=" + campaignDailyCost);
                                                                    if (campaignDailyCost > campaignDailyBudget) {
                                                                        // The target vendor has monthly budget remaining, but this campaign has no daily budget remaining. Pause it.
                                                                        logInfo(logTag, "adjustBudgetsAndStatuses: campaign daily budget exhausted, pausing campaign");
                                                                        campaign.setStatus(CampaignStatus.SYSTEM_PAUSE);
                                                                    }
                                                                    else {
                                                                        // The campaign has daily budget remaining.
                                                                        logInfo(logTag, "adjustBudgetsAndStatuses: campaign daily budget remaining, activating campaign");
                                                                        campaign.setStatus(CampaignStatus.ACTIVE);
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            } // end target vendors for loop
                                        } // end target vendors not null
                                    } // end if target date range is valid
                                    else {
                                        // Today is not in the valid date range for the target. Pause the target and system-pause every campaign in the target.
                                        logInfo(logTag, "adjustBudgetsAndStatuses: today is not in the valid date range for the target, pausing all target campaigns.");
                                        target.setStatus(Target.Status.PAUSED);
                                        targetHelper.updateTarget(logTag, pdbConn, target);
                                        pauseAllTargetCampaigns = true;
                                    }
                                } // end if target is active
                                else {
                                    // Target status is not active. System-pause every campaign in the target.
                                    logInfo(logTag, "adjustBudgetsAndStatuses: target is not active, pausing all target campaigns.");
                                    pauseAllTargetCampaigns = true;
                                }
                            } // end else target has remaining PPC budget

                            // Pause all the target campaigns if needed.
                            if (pauseAllTargetCampaigns) {
                                logInfo(logTag, "adjustBudgetsAndStatuses: pauseAllTargetCampaigns=true, setting target campaigns to SYSTEM_PAUSE");
                                List<Campaign> targetCampaignList = targetCampaignListMap.get(targetId);
                                if (targetCampaignList != null && !targetCampaignList.isEmpty()) {
                                    for (Campaign campaign : targetCampaignList) {
                                        campaign.setStatus(CampaignStatus.SYSTEM_PAUSE);
                                    }
                                }
                            }
                        } // end target loop
                    } // end target list not empty

                    //
                    // Adjust non-target campaigns.
                    //

                    // Are there any non-target campaigns?
                    if (!nonTargetCampaignList.isEmpty()) {
                        logInfo(logTag, "adjustBudgetsAndStatuses: adjusting non-target campaigns.");
                        // Calculate monthly budget remaining that is available for non-target campaigns. I allow overspent targets to take away from non-target campaigns.
                        double availableNonTargetCampaignMonthlyBudget = budgetCycleProductData.getAvailableNonTargetCampaignMonthlyBudget();
                        double nonTargetMBR = availableNonTargetCampaignMonthlyBudget - budgetCycleProductData.getNonTargetCampaignBudgetCycleData().getTotalCost();
                        logInfo(logTag, "adjustBudgetsAndStatuses: availableNonTargetCampaignMonthlyBudget=" + availableNonTargetCampaignMonthlyBudget + ", nonTargetMBR=" + nonTargetMBR);

                        // Has the non-target budget been exhausted?
                        if (nonTargetMBR > 0) {
                            // There is non-target budget available.
                            // Are there vendor budget allocations?
                            if (allocations != null && allocations.hasAllocations()) {
                                logInfo(logTag, "adjustBudgetsAndStatuses: non-target campaigns, there are VendorBudgetAllocations.");
                                // There are vendor budget allocations.
                                Set<Long> vendorIdSet = nonTargetVendorCampaignListMap.keySet();
                                for (Long vendorId : vendorIdSet) {
                                    logInfo(logTag, "adjustBudgetsAndStatuses: non-target campaign vendor loop, vendorId=" + vendorId);
                                    List<Campaign> vendorCampaignList = nonTargetVendorCampaignListMap.get(vendorId);
                                    if (vendorCampaignList != null && !vendorCampaignList.isEmpty()) {
                                        int vendorCampaignCount = vendorCampaignList.size();
                                        logInfo(logTag, "adjustBudgetsAndStatuses: vendorCampaignCount=" + vendorCampaignCount);
                                        if (vendorCampaignCount > 0) { // The size of this list will not be zero because the list would not even exist if there was nothing in it, but I'm checking anyway since I divide by the size.
                                            // Get the allocation % for this vendor.
                                            Double allocation = allocations.getAllocation(vendorId.intValue());
                                            allocation = allocation == null ? 0 : allocation;
                                            logInfo(logTag, "adjustBudgetsAndStatuses: allocation=" + allocation);
                                            // Get the total monthly spend for this vendor.
                                            double totalVendorMonthlySpend = availableNonTargetCampaignMonthlyBudget * allocation;
                                            logInfo(logTag, "adjustBudgetsAndStatuses: totalVendorMonthlySpend=" + totalVendorMonthlySpend);
                                            BudgetCycleData vendorBudgetCycleData = budgetCycleProductData.getNonTargetCampaignVendorBudgetCycleDataMap().get(vendorId);
                                            // Get the non-target vendor cost in the current cycle
                                            double totalVendorCost = 0;
                                            if (vendorBudgetCycleData != null) {
                                                totalVendorCost = vendorBudgetCycleData.getTotalCost();
                                            }
                                            logInfo(logTag, "adjustBudgetsAndStatuses: totalVendorCost=" + totalVendorCost);
                                            // Calculate MBR for this vendor.
                                            double vendorMBR = totalVendorMonthlySpend - totalVendorCost;
                                            logInfo(logTag, "adjustBudgetsAndStatuses: vendorMBR=" + vendorMBR);
                                            // Has the vendor exhausted its budget?
                                            if (vendorMBR > 0.01) {
                                                // This vendor has not exhausted its allocation.
                                                // First, loop over the campaigns to handle the difference between the ones with and without monthly budget set.
                                                int campaignWithNoMonthlyBudgetCount = 0;
                                                for (Campaign campaign : vendorCampaignList) {
                                                    if (campaign.getMonthlyBudget() != null) {
                                                        BudgetCycleData campaignBudgetCycleData = budgetCycleProductData.getCampaignBudgetCycleDataMap().get(campaign.getNsCampaignId());
                                                        // Calculate the modified monthly budget, accounting for month to month budget rollover.
                                                        double modifiedMonthlyBudget = (campaign.getMonthlyBudget() / budgetCycleProductData.getBaseBudget()) * budgetCycleProductData.getTotalBudget();
                                                        // Deduct the campaign MBR from the vendorMBR. The remainder will be available to campaigns that do not have a monthly budget value set.
                                                        vendorMBR -= (modifiedMonthlyBudget - campaignBudgetCycleData.getTotalCost());
                                                    }
                                                    else {
                                                        // Count how many campaigns there are with no monthly budget value set.
                                                        campaignWithNoMonthlyBudgetCount++;
                                                    }
                                                }
                                                logInfo(logTag, "adjustBudgetsAndStatuses: vendorMBR w/campaign monthly budgets deducted=" + vendorMBR);
                                                logInfo(logTag, "adjustBudgetsAndStatuses: campaignWithNoMonthlyBudgetCount=" + campaignWithNoMonthlyBudgetCount);

                                                for (Campaign campaign : vendorCampaignList) {
                                                    logInfo(logTag, "adjustBudgetsAndStatuses: campaign loop, nsCampaignId=" + campaign.getNsCampaignId());
                                                    BudgetCycleData campaignBudgetCycleData = budgetCycleProductData.getCampaignBudgetCycleDataMap().get(campaign.getNsCampaignId());
                                                    double campaignMBR = 0;
                                                    if (campaign.getMonthlyBudget() != null) {
                                                        // Calculate the modified monthly budget, accounting for month to month budget rollover.
                                                        double modifiedMonthlyBudget = (campaign.getMonthlyBudget() / budgetCycleProductData.getBaseBudget()) * budgetCycleProductData.getTotalBudget();
                                                        campaignMBR = modifiedMonthlyBudget - campaignBudgetCycleData.getTotalCost();
                                                    }
                                                    else {
                                                        campaignMBR = vendorMBR / campaignWithNoMonthlyBudgetCount;
                                                    }
                                                    logInfo(logTag, "adjustBudgetsAndStatuses: campaignMBR=" + campaignMBR);

                                                    // The vendor has remaining monthly budget.
                                                    double dayCount = adScheduleHelper.getNumberOfDaysRemainingInCycleWhenCampaignIsScheduled(gdbConn, pdbConn, prodInstId, campaign.getNsCampaignId());
                                                    logInfo(logTag, "adjustBudgetsAndStatuses: dayCount=" + dayCount);
                                                    // Don't let the aggressiveness push the campaign daily budget beyond the campaign MBR.
                                                    double spendAggressiveness = campaign.getSpendAggressiveness();
                                                    logInfo(logTag, "adjustBudgetsAndStatuses: spendAggressiveness=" + spendAggressiveness);
                                                    double campaignDailyBudget = Math.min(((campaignMBR / dayCount) * spendAggressiveness), campaignMBR);
                                                    logInfo(logTag, "adjustBudgetsAndStatuses: campaignDailyBudget=" + campaignDailyBudget);
                                                    campaign.setDailyBudget(campaignDailyBudget);
                                                    // Check the daily budget remaining and either pause or activate.
                                                    if (campaignBudgetCycleData != null && campaignBudgetCycleData.getDailyCost() > campaignDailyBudget) {
                                                        // The vendor has monthly budget remaining, but this campaign has no daily budget. Pause it.
                                                        logInfo(logTag, "adjustBudgetsAndStatuses: campaign daily budget exhausted, pausing campaign");
                                                        campaign.setStatus(CampaignStatus.SYSTEM_PAUSE);
                                                    }
                                                    else {
                                                        // The campaign has daily budget remaining.
                                                        logInfo(logTag, "adjustBudgetsAndStatuses: campaign daily budget remaining, activating campaign");
                                                        campaign.setStatus(CampaignStatus.ACTIVE);
                                                    }
                                                }
                                            }
                                            else {
                                                // This vendor has exhausted its allocation. Pause campaigns.
                                                logInfo(logTag, "adjustBudgetsAndStatuses: monthly vendor allocation is exhausted");
                                                for (Campaign campaign : vendorCampaignList) {
                                                    campaign.setStatus(CampaignStatus.SYSTEM_PAUSE);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            else {
                                // There are no vendor budget allocations.
                                logInfo(logTag, "adjustBudgetsAndStatuses: non-target campaigns, there are no VendorBudgetAllocations.");
                                int nonTargetCampaignCount = nonTargetCampaignList.size();
                                if (nonTargetCampaignCount > 0) { // I divide by this number...
                                    // First, loop over the campaigns to handle the difference between the ones with and without monthly budget set.
                                    int campaignWithNoMonthlyBudgetCount = 0;
                                    for (Campaign campaign : nonTargetCampaignList) {
                                        if (campaign.getMonthlyBudget() != null) {
                                            BudgetCycleData campaignBudgetCycleData = budgetCycleProductData.getCampaignBudgetCycleDataMap().get(campaign.getNsCampaignId());
                                            // Calculate the modified monthly budget, accounting for month to month budget rollover.
                                            double modifiedMonthlyBudget = (campaign.getMonthlyBudget() / budgetCycleProductData.getBaseBudget()) * budgetCycleProductData.getTotalBudget();
                                            // Deduct the campaign MBR from the nonTargetMBR. The remainder will be available to campaigns that do not have a monthly budget value set.
                                            nonTargetMBR -= (modifiedMonthlyBudget - campaignBudgetCycleData.getTotalCost());
                                        }
                                        else {
                                            // Count how many campaigns there are with no monthly budget value set.
                                            campaignWithNoMonthlyBudgetCount++;
                                        }
                                    }
                                    logInfo(logTag, "adjustBudgetsAndStatuses: nonTargetMBR w/campaign monthly budgets deducted=" + nonTargetMBR);
                                    logInfo(logTag, "adjustBudgetsAndStatuses: campaignWithNoMonthlyBudgetCount=" + campaignWithNoMonthlyBudgetCount);

                                    for (Campaign campaign : nonTargetCampaignList) {
                                        logInfo(logTag, "adjustBudgetsAndStatuses: campaign loop, nsCampaignId=" + campaign.getNsCampaignId());
                                        BudgetCycleData campaignBudgetCycleData = budgetCycleProductData.getCampaignBudgetCycleDataMap().get(campaign.getNsCampaignId());
                                        double campaignMBR = 0;
                                        if (campaign.getMonthlyBudget() != null) {
                                        	// Calculate the modified monthly budget, accounting for month to month budget rollover.
                                        	double modifiedMonthlyBudget = (campaign.getMonthlyBudget() / budgetCycleProductData.getBaseBudget()) * budgetCycleProductData.getTotalBudget();
                                            campaignMBR = modifiedMonthlyBudget - campaignBudgetCycleData.getTotalCost();
                                        }
                                        else {
                                            campaignMBR = nonTargetMBR / campaignWithNoMonthlyBudgetCount;
                                        }
                                        logInfo(logTag, "adjustBudgetsAndStatuses: campaignMBR=" + campaignMBR);

                                        int dayCount = adScheduleHelper.getNumberOfDaysRemainingInCycleWhenCampaignIsScheduled(gdbConn, pdbConn, prodInstId, campaign.getNsCampaignId());
                                        logInfo(logTag, "adjustBudgetsAndStatuses: dayCount=" + dayCount);
                                        logInfo(logTag, "adjustBudgetsAndStatuses: spendAggressiveness=" + campaign.getSpendAggressiveness());
                                        // Don't let the aggressiveness push the campaign daily budget beyond the campaign MBR.
                                        double campaignDailyBudget = Math.min(((campaignMBR / dayCount) * campaign.getSpendAggressiveness()), campaignMBR);
                                        logInfo(logTag, "adjustBudgetsAndStatuses: campaignDailyBudget=" + campaignDailyBudget);
                                        campaign.setDailyBudget(campaignDailyBudget);
                                        // Check the daily budget remaining and either pause or activate.
                                        if (campaignBudgetCycleData != null && campaignBudgetCycleData.getDailyCost() > campaignDailyBudget) {
                                            // This campaign has no daily budget remaining. Pause it.
                                            logInfo(logTag, "adjustBudgetsAndStatuses: campaign daily budget exhausted, pausing campaign");
                                            campaign.setStatus(CampaignStatus.SYSTEM_PAUSE);
                                        }
                                        else {
                                            // The campaign has daily budget remaining.
                                            logInfo(logTag, "adjustBudgetsAndStatuses: campaign daily budget remaining, activating campaign");
                                            campaign.setStatus(CampaignStatus.ACTIVE);
                                        }
                                    }
                                }
                            }
                        }
                        else {
                            // Non-target budget is exhausted. Pause all non-target campaigns.
                            logInfo(logTag, "adjustBudgetsAndStatuses: Non-target budget is exhausted. Pause all non-target campaigns.");
                            for (Campaign campaign : nonTargetCampaignList) {
                                campaign.setStatus(CampaignStatus.SYSTEM_PAUSE);
                            }
                        }
                    } // end non-target campaign list not empty
                } // end product isAdjustBudget

                //
                // Superpages
                //

                // Superpages campaigns are handled the same regardless of whether adjust budget is on or off.
                // We treat them differently from search engine campaigns because there is no campaign-level budget; the budget is only at the account level.
                // Build a list of Superpages campaigns.
                List<Campaign> superpagesCampaignList = new ArrayList<Campaign>();
                for (Campaign campaign : campaigns.getCampaigns()) {
                    // Is this a Superpages campaign?
                    if (campaign.getVendorId() == VendorId.SUPERPAGES){
                        // Superpages campaign.
                        superpagesCampaignList.add(campaign);
                    }
                }

                VendorCredentials vendorCredentials = getVendorCredentials(gdbConn, prodInstId, VendorId.SUPERPAGES);
                if (vendorCredentials != null) {
                    logInfo(logTag, "adjustBudgetsAndStatuses: Superpages account exists. Adjust Superpages campaigns.");
                    // Superpages account exists.
                    // Calculate total monthly budget for Superpages.
                    Double superpagesAllocation = allocations.getAllocation(VendorId.SUPERPAGES);
                    superpagesAllocation = superpagesAllocation == null ? 0 : superpagesAllocation;
                    logInfo(logTag, "adjustBudgetsAndStatuses: superpagesAllocation=" + superpagesAllocation);
                    double superpagesMonthlyBudget = budgetCycleProductData.getTotalTargetSuperpagesMonthlyBudget() + (budgetCycleProductData.getTotalNonTargetMonthlyBudget() * superpagesAllocation);
                    logInfo(logTag, "adjustBudgetsAndStatuses: superpagesMonthlyBudget=" + superpagesMonthlyBudget);

                    // Get current costs for Superpages.
                    double superpagesCurrentCycleCost = 0;
                    double superpagesCurrentCycleMarginAndLeadCost = 0;
                    BudgetCycleData superpagesBudgetCycleData = budgetCycleProductData.getVendorBudgetCycleDataMap().get((long)VendorId.SUPERPAGES);
                    if (superpagesBudgetCycleData != null) {
                        superpagesCurrentCycleCost = superpagesBudgetCycleData.getTotalCost();
                        superpagesCurrentCycleMarginAndLeadCost = superpagesBudgetCycleData.getClickMargin() + superpagesBudgetCycleData.getLeadCost();
                    }
                    logInfo(logTag, "adjustBudgetsAndStatuses: superpagesCurrentCycleCost=" + superpagesCurrentCycleCost);
                    logInfo(logTag, "adjustBudgetsAndStatuses: superpagesCurrentCycleMarginAndLeadCost=" + superpagesCurrentCycleMarginAndLeadCost);

                    // The Superpages budget will always be set to something if there is an account, possibly 0. We have to do this because if there was previously
                    // a Superpages budget, and it decreased, was removed or campaigns were stopped, then we have to update the budget.
                    // In any case, the daily budget should never be less than the current cycle spend.
                    double superpagesBudgetForToday = superpagesCurrentCycleCost;

                    // Are there any campaigns right now?
                    if (!superpagesCampaignList.isEmpty()) {
                        // We have to reduce the superpagesMonthlyBudget by the click margin and lead costs since these are hidden from Superpages.
                        // We'll just remove the current running total every time instead of trying to estimate it.
                        superpagesMonthlyBudget -= superpagesCurrentCycleMarginAndLeadCost;
                        // Calculate the Superpages MBR.
                        double superpagesMBR = superpagesMonthlyBudget - superpagesCurrentCycleCost;
                        logInfo(logTag, "adjustBudgetsAndStatuses: superpagesMBR=" + superpagesMBR);
                        logInfo(logTag, "adjustBudgetsAndStatuses: There is Superpages monthly budget remaining.");
                        // Query the Superpages spend aggressiveness. We only look at product-level spend aggressiveness for Superpages since the budget is at the account level.
                        float superpagesSpendAggressiveness = (new BudgetManagerHelper(this).getVendorSpendAggressivenessForProduct(pdbConn, prodInstId, VendorId.SUPERPAGES));
                        logInfo(logTag, "adjustBudgetsAndStatuses: superpagesSpendAggressiveness=" + superpagesSpendAggressiveness);
                        // Superpages has some budget to spend and campaigns to spend it.
                        // Essentially, I have written this algorithm to pace the Superpages budget since by default they will spend the account-level monthly budget ASAP.
                        // Build some variables to make this formula comprehensible and assist logging.
                        double dayOfMonth = Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + 1; // Add 1 because Superpages is likely to run a day behind...
                        double numberOfDaysInCurrentCycle = product.getNumberOfDaysInCurrentCycle();
                        double days = Math.min(dayOfMonth, numberOfDaysInCurrentCycle); // Don't let the +1 exceed days in cycle.
                        double budgetPerDay = superpagesMonthlyBudget / numberOfDaysInCurrentCycle;
                        // Don't allow the budget w/spend aggressiveness to exceed the monthly budget.
                        superpagesBudgetForToday = Math.min((days * budgetPerDay) * superpagesSpendAggressiveness, superpagesMonthlyBudget);
                        // Calculate the Superpages DBR and add it to the total campaign daily budget. Unlike the search engine vendors, this is only one number across all campaigns.
                        double superpagesDBR = superpagesBudgetForToday - superpagesCurrentCycleCost;
                        logInfo(logTag, "adjustBudgetsAndStatuses: days=" + days);
                        logInfo(logTag, "adjustBudgetsAndStatuses: budgetPerDay=" + budgetPerDay);
                        logInfo(logTag, "adjustBudgetsAndStatuses: superpagesBudgetForToday=" + superpagesBudgetForToday);
                        logInfo(logTag, "adjustBudgetsAndStatuses: superpagesDBR=" + superpagesDBR);
                        totalCampaignDailyBudget += superpagesDBR;

                        if (superpagesMBR > 0.01 && superpagesDBR > 0.01) {
                            logInfo(logTag, "adjustBudgetsAndStatuses:  There is budget remaining for today, activate campaigns.");
                            for (Campaign campaign : superpagesCampaignList) {
                                // Set the campaign daily budget to the total Superpages DBR. Superpages will balance it across campaigns as they see fit.
                                // Persist the campaign daily budget here since the Superpages vendor API does not have this field. If we don't do this, debits will cause
                                // unwanted pausing. We will also add the Superpages DBR to the product-level DBR.
                                campaign.setDailyBudget(superpagesDBR);
                                campaign.persist(pdbConn, updatedBySystem);
                                campaign.setStatus(CampaignStatus.ACTIVE);
                            }
                        }
                        else {
                            logInfo(logTag, "adjustBudgetsAndStatuses:  Superpages MBR or DBR is exhausted, pausing campaigns.");
                            for (Campaign campaign : superpagesCampaignList) {
                                campaign.setDailyBudget(0);
                                campaign.setStatus(CampaignStatus.SYSTEM_PAUSE);
                            }
                        }
                    }
                    // Set the Superpages account budget.
                    BaseHelper.commit(gdbConn);
                    BaseHelper.commit(pdbConn);
                    // Superpages requires the new budget to be greater than the current cost.
                    if (superpagesCurrentCycleCost + 0.01 > superpagesBudgetForToday) {
                        superpagesBudgetForToday = superpagesCurrentCycleCost + 0.01;
                        logInfo(logTag, "adjustBudgetsAndStatuses: Superpages budget must be greater than current cost. superpagesBudgetForToday=" + superpagesBudgetForToday);
                    }
                    logInfo(logTag, "adjustBudgetsAndStatuses: Setting Superpages account budget for today to " + superpagesBudgetForToday);
                    updateSuperPagesAccountBudget(prodInstId, superpagesBudgetForToday);
                } // end if there is a Superpages account
            } // end budget and thresholds not exceeded

            // Update the campaigns at the vendors. Since the services update the database, we have to commit transactions first.
            logInfo(logTag, "adjustBudgetsAndStatuses: updating campaigns at vendors...");
            List<CampaignData> campaignDataList = new ArrayList<CampaignData>();
            for (Campaign campaign : campaigns.getCampaigns()) {
                CampaignData campaignData = new CampaignData();
                campaignData.setDailyBudget(campaign.getDailyBudget());
                campaignData.setNsCampaignId(campaign.getNsCampaignId());
                campaignData.setProdInstId(prodInstId);
                campaignData.setStatus(campaign.getStatus());
                campaignData.setVendorId(campaign.getVendorId());
                campaignDataList.add(campaignData);

                // Total daily budget of ACTIVE non-Superpages campaigns.
                if (campaign.getStatus().equals(CampaignStatus.ACTIVE) && campaign.getVendorId() != VendorId.SUPERPAGES) {
                    totalCampaignDailyBudget += campaign.getDailyBudget();
                }
            }

            // Note: I'm not persisting the campaign data. The vendor web service will persist when successfully updated at the vendor, keeping the systems in synch.
            BaseHelper.commit(gdbConn);
            BaseHelper.commit(pdbConn);
            CampaignUtil campaignUtil = new CampaignUtil();
            campaignUtil.setAdAgentVendorClientProcessor(adAgentVendorClientProcessor); // The constructor should take these objects as parameters....
            campaignUtil.setAdAgentVendorCredential(adAgentVendorCredential);
            campaignUtil.setAdAgentWSCredentials(adAgentWSCredentials);
            campaignUtil.updateCampaignsAtVendor(campaignDataList, "Budget Manager");
        } // end campaign list not empty
        else {
            // There are no active or system-paused campaigns.
            logInfo(logTag, "adjustBudgetsAndStatuses: There are no ACTIVE or SYSTEM_PAUSE campaigns.");
            // The only thing we have to do is set the Superpages budget. Since we are not spending, set the budget to the current cost (which could be $0).
            // This piece of code needs to get factored out and shared...
            VendorCredentials vendorCredentials = getVendorCredentials(gdbConn, prodInstId, VendorId.SUPERPAGES);
            if (vendorCredentials != null) {
                // Set the Superpages account budget.
                BaseHelper.commit(gdbConn);
                BaseHelper.commit(pdbConn);
                BudgetCycleData superpagesBudgetCycleData = budgetCycleProductData.getVendorBudgetCycleDataMap().get((long)VendorId.SUPERPAGES);
                double superPagesAccountBudget = superpagesBudgetCycleData == null ? 0 : superpagesBudgetCycleData.getTotalCost();
                logInfo(logTag, "adjustBudgetsAndStatuses: Monthly budget is exhaused or a threshold was exceeded. Setting Superpages account budget to " + superPagesAccountBudget);
                updateSuperPagesAccountBudget(prodInstId, superPagesAccountBudget);
            }
        }

        // Update daily budget remaining. If there are no campaigns or no budget remaining, this amount will be $0.
        logInfo(logTag, "adjustBudgetsAndStatuses: modifying product_sum.daily_budget_remaining with totalCampaignDailyBudget=" + totalCampaignDailyBudget);
        productSummaryData.modifyTotalDailyBudgetRemaining(totalCampaignDailyBudget);

        logInfo(logTag, "adjustBudgetsAndStatuses complete.");
    }

    /**
     * Get the vendor credentials if the account exists.
     *
     * @param pdbConn
     * @param prodInstId
     * @param vendorId
     * @return null if the vendor account has not been created
     * @throws Exception
     */
    private VendorCredentials getVendorCredentials(Connection conn, String prodInstId, int vendorId) throws SQLException {
        VendorCredentialsHelper vch = new VendorCredentialsHelper(getCurrentLogComponent());
        return vch.getVendorCredentials(getLogTag(prodInstId), conn, prodInstId, vendorId);
    }

    /**
     * Update the account budget at Superpages.
     *
     * @param prodInstId
     * @param budget
     * @throws Exception
     */
    private void updateSuperPagesAccountBudget(String prodInstId, double budget) throws Exception {
        try {
            ClientFactories.getClientFactory().getAccountClient(adAgentWSCredentials).updateAccountBudget(prodInstId, budget);
        }
        catch (Throwable e) {
            // We're swallowing exceptions here to prevent basic operations from failing in the event that Superpages is down or there is an issue.
            logWarning(getLogTag(prodInstId), "An unexpected error occurred setting the Superpages account budget.");
            logError(getLogTag(prodInstId), e);
        }
    }

    public static void main(String[] args) throws Exception {
        Connection pdbConn = null;
        Connection gdbConn = null;
        PreparedStatement pstmt = null;
        try {
            pdbConn = BaseHelper.createDevPdb1Connection();
            gdbConn = BaseHelper.createDevGdbConnection();

            gdbConn.setAutoCommit(false);
            pdbConn.setAutoCommit(false);
            //pstmt = conn.prepareStatement("SET SQL_MODE = 'STRICT_ALL_TABLES'");
            //pstmt.execute();

            AdAgentVendorClientProcessor processor =
            AdAgentVendorClientFactory.getProcessorInstance("http://dapp1.dev.netsol.com:3250/AdAgentVendorService", 600000);

            Credential credential = AdAgentVendorClientFactory.getCredentialInstance();
            credential.setUserName("analystUI");
            credential.setPassword("zxqweinaq");

            Credentials credentials = new Credentials();
            String serviceUrl = "http://aaapp1.dev.netsol.com:3260/adagent-ws/rest/";
            String username = "ampuser";
            String password = "amppwd";
            credentials.setServiceUrl(serviceUrl);
            credentials.setUsername(username);
            credentials.setPassword(password);

            String prodInstId = "WN.DEV.BING.0002";
            BudgetManager bm = new BudgetManager(new SimpleLog("BM"), processor, credential, credentials);
            //bm.renewBudget(gdbConn, pdbConn, prodInstId, BudgetAdjustment.System.DAILY_BUDGET_BATCH); // let adjust renew in most cases unless testing something specific
            //reconcileBudget(gdbConn, conn, "WN.PP.256039627", Calendar.getInstance(), 30, 0.10d);
            //turnAdjustBackOn(gdbConn, conn, "WN.PP.254523370");
            //bm.activateProduct(gdbConn, pdbConn, "WN.DEV.BING.0002");
            bm.adjustForDay(gdbConn, pdbConn, prodInstId, Calendar.getInstance(), BudgetAdjustment.System.DAILY_BUDGET_BATCH);
            //bm.refundLead(conn, "WN.PPC.1089255", 4219L, "user");

            // Test a Superpages click. WN.DEV.BING.0003, hits_id=90461, superpages campaign id=100000992
            //bm.debitClick(gdbConn, pdbConn, prodInstId, 90461L, new Date(), 100000992L, 0L, 0L, 0L, BudgetAdjustment.System.AGGREGATOR, VendorId.SUPERPAGES);
            // Google PPC click
            //bm.debitClick(gdbConn, pdbConn, prodInstId, 90461L, new Date(), 4765L, 7882L, 100000054L, 18890L, BudgetAdjustment.System.AGGREGATOR, VendorId.GOOGLE);

            gdbConn.commit();
            pdbConn.commit();
            System.out.println("Done.");
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        finally {
            gdbConn.commit();
            pdbConn.commit();
            BaseHelper.close(pstmt);
            BaseHelper.close(gdbConn, pdbConn);
        }
    }
}
