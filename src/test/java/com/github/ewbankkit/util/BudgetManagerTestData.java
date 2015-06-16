package com.github.ewbankkit.util;

import java.util.Date;

import com.github.ewbankkit.util.codes.ChannelId;
import com.github.ewbankkit.util.codes.VendorId;

public class BudgetManagerTestData {

    // Product data:
    private int channelId;
    private String prodInstId;
    private double dailyBudgetRemaining;
    private double monthlyBudgetRemaining;

    // Click data:
    private int vendorId;
    private long hitId;
    private Date clickDate;
    private long nsCampaignId;
    private long nsAdGroupId;
    private long nsAdId;
    private long nsKeywordId;

    /**
     * Constructor.
     */
    public BudgetManagerTestData(int channelId, String prodInstId, double dailyBudgetRemaining, double monthlyBudgetRemaining,
            long hitId, int vendorId, long nsCampaignId, long nsAdGroupId, long nsAdId, long nsKeywordId) {
        this.channelId = channelId;
        this.prodInstId = prodInstId;
        this.dailyBudgetRemaining = dailyBudgetRemaining;
        this.monthlyBudgetRemaining = monthlyBudgetRemaining;
        this.hitId = hitId;
        this.clickDate = new Date();
        this.vendorId = vendorId;
        this.nsCampaignId = nsCampaignId;
        this.nsAdGroupId = nsAdGroupId;
        this.nsAdId = nsAdId;
        this.nsKeywordId = nsKeywordId;
    }

    //
    // Factory methods:
    //

    public static BudgetManagerTestData getNSTestData() {
        BudgetManagerTestData testData= new BudgetManagerTestData(ChannelId.NETSOL, "WN.DEV.BING.0002", 10.0, 20.0,
                5614, VendorId.GOOGLE, 4765, 7882, 18890, 100000000);
        return testData;
    }


    //
    // Get/Set methods:
    //


    /**
     * @return the channelId
     */
    public int getChannelId() {
        return channelId;
    }
    /**
     * @param channelId the channelId to set
     */
    public void setChannelId(int channelId) {
        this.channelId = channelId;
    }
    /**
     * @return the prodInstId
     */
    public String getProdInstId() {
        return prodInstId;
    }
    /**
     * @param prodInstId the prodInstId to set
     */
    public void setProdInstId(String prodInstId) {
        this.prodInstId = prodInstId;
    }
    /**
     * @return the dailyBudgetRemaining
     */
    public double getDailyBudgetRemaining() {
        return dailyBudgetRemaining;
    }
    /**
     * @param dailyBudgetRemaining the dailyBudgetRemaining to set
     */
    public void setDailyBudgetRemaining(double dailyBudgetRemaining) {
        this.dailyBudgetRemaining = dailyBudgetRemaining;
    }
    /**
     * @return the monthlyBudgetRemaining
     */
    public double getMonthlyBudgetRemaining() {
        return monthlyBudgetRemaining;
    }
    /**
     * @param monthlyBudgetRemaining the monthlyBudgetRemaining to set
     */
    public void setMonthlyBudgetRemaining(double monthlyBudgetRemaining) {
        this.monthlyBudgetRemaining = monthlyBudgetRemaining;
    }
    /**
     * @return the hitId
     */
    public long getHitId() {
        return hitId;
    }
    /**
     * @param hitId the hitId to set
     */
    public void setHitId(long hitId) {
        this.hitId = hitId;
    }
    /**
     * @return the clickDate
     */
    public Date getClickDate() {
        return clickDate;
    }
    /**
     * @param clickDate the clickDate to set
     */
    public void setClickDate(Date clickDate) {
        this.clickDate = clickDate;
    }
    /**
     * @return the nsCampaignId
     */
    public long getNsCampaignId() {
        return nsCampaignId;
    }
    /**
     * @param nsCampaignId the nsCampaignId to set
     */
    public void setNsCampaignId(long nsCampaignId) {
        this.nsCampaignId = nsCampaignId;
    }
    /**
     * @return the nsAdGroupId
     */
    public long getNsAdGroupId() {
        return nsAdGroupId;
    }
    /**
     * @param nsAdGroupId the nsAdGroupId to set
     */
    public void setNsAdGroupId(long nsAdGroupId) {
        this.nsAdGroupId = nsAdGroupId;
    }
    /**
     * @return the nsAdId
     */
    public long getNsAdId() {
        return nsAdId;
    }
    /**
     * @param nsAdId the nsAdId to set
     */
    public void setNsAdId(long nsAdId) {
        this.nsAdId = nsAdId;
    }
    /**
     * @return the nsKeywordId
     */
    public long getNsKeywordId() {
        return nsKeywordId;
    }
    /**
     * @param nsKeywordId the nsKeywordId to set
     */
    public void setNsKeywordId(long nsKeywordId) {
        this.nsKeywordId = nsKeywordId;
    }
    /**
     * @return the vendorId
     */
    public int getVendorId() {
        return vendorId;
    }
    /**
     * @param vendorId the vendorId to set
     */
    public void setVendorId(int vendorId) {
        this.vendorId = vendorId;
    }
}
