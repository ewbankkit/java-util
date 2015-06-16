/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.beans;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.netsol.adagent.util.codes.LeadType;

/**
 * Represents PPC product details.
 */
public class PpcProductDetail extends BaseData {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:04 PpcProductDetail.java NSI";
    private static final Log log = LogFactory.getLog(PpcProductDetail.class);
    
    private Long clickThreshold;
    private boolean debitCpcMarkup;
    private boolean debitEmailLeadCost;
    private boolean debitFormLeadCost;
    private boolean debitHighValuePageLeadCost;
    private boolean debitPhoneLeadCost;
    private boolean debitShoppingCartLeadCost;
    private boolean debitUnansweredPhoneLeadCost;
    private double cpcMarkup;
    private double emailLeadCost;
    private double formLeadCost;
    private double highValuePageLeadCost;
    private double phoneLeadCost;
    private double shoppingCartLeadCost;
    private double unansweredPhoneLeadCost;   
    private Long leadThreshold;
    private Double maxBudget;
    private boolean optimizeAdGroups;
    private boolean optimizeAds;
    private boolean optimizeBids;
    private boolean optimizeBudget;
    private boolean optimizeKeywords;
    private boolean optimizeQualityScore;
    private String prodInstId;
    private Double subscriptionFee;
    private String updatedBySystem;
    private String updatedByUser;

    public void setClickThreshold(Long clickThreshold) {
        this.clickThreshold = clickThreshold;
    }

    public Long getClickThreshold() {
        return clickThreshold;
    }

    public void setDebitCpcMarkup(boolean debitCpcMarkup) {
        this.debitCpcMarkup = debitCpcMarkup;
    }

    public boolean isDebitCpcMarkup() {
        return debitCpcMarkup;
    }

    public void setDebitEmailLeadCost(boolean debitEmailLeadCost) {
        this.debitEmailLeadCost = debitEmailLeadCost;
    }

    public boolean isDebitEmailLeadCost() {
        return debitEmailLeadCost;
    }

    public void setDebitFormLeadCost(boolean debitFormLeadCost) {
        this.debitFormLeadCost = debitFormLeadCost;
    }

    public boolean isDebitFormLeadCost() {
        return debitFormLeadCost;
    }

    public void setDebitHighValuePageLeadCost(boolean debitHighValuePageLeadCost) {
        this.debitHighValuePageLeadCost = debitHighValuePageLeadCost;
    }

    public boolean isDebitHighValuePageLeadCost() {
        return debitHighValuePageLeadCost;
    }

    public void setDebitPhoneLeadCost(boolean debitPhoneLeadCost) {
        this.debitPhoneLeadCost = debitPhoneLeadCost;
    }

    public boolean isDebitPhoneLeadCost() {
        return debitPhoneLeadCost;
    }

    public void setDebitShoppingCartLeadCost(boolean debitShoppingCartLeadCost) {
        this.debitShoppingCartLeadCost = debitShoppingCartLeadCost;
    }

    public boolean isDebitShoppingCartLeadCost() {
        return debitShoppingCartLeadCost;
    }

    public void setDebitUnansweredPhoneLeadCost(boolean debitUnansweredPhoneLeadCost) {
        this.debitUnansweredPhoneLeadCost = debitUnansweredPhoneLeadCost;
    }

    public boolean isDebitUnansweredPhoneLeadCost() {
        return debitUnansweredPhoneLeadCost;
    }

    public void setLeadThreshold(Long leadThreshold) {
        this.leadThreshold = leadThreshold;
    }

    public Long getLeadThreshold() {
        return leadThreshold;
    }

    public Double getMaxBudget() {
        return maxBudget;
    }

    public void setMaxBudget(Double maxBudget) {
        this.maxBudget = maxBudget;
    }

    public void setOptimizeAdGroups(boolean optimizeAdGroups) {
        this.optimizeAdGroups = optimizeAdGroups;
    }

    public boolean isOptimizeAdGroups() {
        return optimizeAdGroups;
    }

    public void setOptimizeAds(boolean optimizeAds) {
        this.optimizeAds = optimizeAds;
    }

    public boolean isOptimizeAds() {
        return optimizeAds;
    }

    public void setOptimizeBids(boolean optimizeBids) {
        this.optimizeBids = optimizeBids;
    }

    public boolean isOptimizeBids() {
        return optimizeBids;
    }

    public void setOptimizeBudget(boolean optimizeBudget) {
        this.optimizeBudget = optimizeBudget;
    }

    public boolean isOptimizeBudget() {
        return optimizeBudget;
    }

    public void setOptimizeKeywords(boolean optimizeKeywords) {
        this.optimizeKeywords = optimizeKeywords;
    }

    public boolean isOptimizeKeywords() {
        return optimizeKeywords;
    }

    public void setOptimizeQualityScore(boolean optimizeQualityScore) {
        this.optimizeQualityScore = optimizeQualityScore;
    }

    public boolean isOptimizeQualityScore() {
        return optimizeQualityScore;
    }

    public String getProdInstId() {
        return prodInstId;
    }

    public void setProdInstId(String prodInstId) {
        this.prodInstId = prodInstId;
    }

    public Double getSubscriptionFee() {
        return subscriptionFee;
    }

    public void setSubscriptionFee(Double subscriptionFee) {
        this.subscriptionFee = subscriptionFee;
    }

    public void setUpdatedBySystem(String updatedBySystem) {
        this.updatedBySystem = updatedBySystem;
    }

    public String getUpdatedBySystem() {
        return updatedBySystem;
    }

    public String getUpdatedByUser() {
        return updatedByUser;
    }

    public void setUpdatedByUser(String updatedByUser) {
        this.updatedByUser = updatedByUser;
    }

    public double getCpcMarkup() {
        return cpcMarkup;
    }

    public void setCpcMarkup(double cpcMarkup) {
        this.cpcMarkup = cpcMarkup;
    }

    public double getEmailLeadCost() {
        return emailLeadCost;
    }

    public void setEmailLeadCost(double emailLeadCost) {
        this.emailLeadCost = emailLeadCost;
    }

    public double getFormLeadCost() {
        return formLeadCost;
    }

    public void setFormLeadCost(double formLeadCost) {
        this.formLeadCost = formLeadCost;
    }

    public double getHighValuePageLeadCost() {
        return highValuePageLeadCost;
    }

    public void setHighValuePageLeadCost(double highValuePageLeadCost) {
        this.highValuePageLeadCost = highValuePageLeadCost;
    }

    public double getPhoneLeadCost() {
        return phoneLeadCost;
    }

    public void setPhoneLeadCost(double phoneLeadCost) {
        this.phoneLeadCost = phoneLeadCost;
    }

    public double getShoppingCartLeadCost() {
        return shoppingCartLeadCost;
    }

    public void setShoppingCartLeadCost(double shoppingCartLeadCost) {
        this.shoppingCartLeadCost = shoppingCartLeadCost;
    }

    public double getUnansweredPhoneLeadCost() {
        return unansweredPhoneLeadCost;
    }

    public void setUnansweredPhoneLeadCost(double unansweredPhoneLeadCost) {
        this.unansweredPhoneLeadCost = unansweredPhoneLeadCost;
    }
    
    /**
     * Determine if a particular lead type cost is debited.
     *
     * @param leadTypeId
     * @return
     */
    public boolean isLeadTypeDebited(int leadTypeId) {
        boolean isDebited = false;
        switch (leadTypeId) {
        case LeadType.PHONE_LEAD: isDebited = debitPhoneLeadCost; break;
        case LeadType.EMAIL_LEAD: isDebited = debitEmailLeadCost; break;
        case LeadType.FORM_LEAD: isDebited = debitFormLeadCost; break;
        case LeadType.HIGH_VALUE_PAGE_LEAD: isDebited = debitHighValuePageLeadCost; break;
        case LeadType.SHOPPING_CART_LEAD: isDebited = debitShoppingCartLeadCost; break;
        case LeadType.UNANSWERED_PHONE_LEAD: isDebited = debitUnansweredPhoneLeadCost; break;
        default: log.warn("Unknown lead type: " + Integer.toString(leadTypeId)); break;
        }
        return isDebited;
    }
    
    /**
     * Get the cost for a particular lead type.
     *
     * @param leadTypeId
     * @return
     */
    public double getLeadTypeCost(int leadTypeId) {
        double cost = 0d;
        switch (leadTypeId) {
        case LeadType.PHONE_LEAD: cost = phoneLeadCost; break;
        case LeadType.EMAIL_LEAD: cost = emailLeadCost; break;
        case LeadType.FORM_LEAD: cost = formLeadCost; break;
        case LeadType.HIGH_VALUE_PAGE_LEAD: cost = highValuePageLeadCost; break;
        case LeadType.SHOPPING_CART_LEAD: cost = shoppingCartLeadCost; break;
        case LeadType.UNANSWERED_PHONE_LEAD: cost = unansweredPhoneLeadCost; break;
        default: log.warn("Unknown lead type: " + Integer.toString(leadTypeId)); break;
        }
        return cost;
    }
}
