/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.advendor;

import static com.netsol.adagent.util.beans.BaseData.toIterable;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.netsol.adagent.util.ArrayBuilder;
import com.netsol.adagent.util.beans.BaseData;
import com.netsol.adagent.util.beans.NsCampaign;
import com.netsol.adagent.util.beans.Pair;
import com.netsol.adagent.util.codes.CampaignStatus;
import com.netsol.adagent.util.codes.UserId;
import com.netsol.adagent.util.codes.VendorId;
import com.netsol.adagent.util.dbhelpers.NsEntityHelper;
import com.netsol.adagent.vendor.client.factory.AdAgentVendorClientFactory;
import com.netsol.adagent.vendor.client.intf.AdAgentVendorClientProcessor;
import com.netsol.adagent.vendor.client.intf.CampaignRequest;
import com.netsol.adagent.vendor.client.intf.Credential;
import com.netsol.adagent.vendor.client.intf.NSCampaign;
import com.netsol.vendor.beans.Credentials;
import com.netsol.vendor.client.ClientFactories;

/**
 * Campaign utilities.
 */
public final class CampaignUtil {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:53:48 CampaignUtil.java NSI";

    private static final Log logger = LogFactory.getLog(CampaignUtil.class);
    private static final NsEntityHelper nsEntityHelper = new NsEntityHelper(logger);

    private AdAgentVendorClientProcessor adAgentVendorClientProcessor;
    private Credential adAgentVendorCredential;
    private Credentials adAgentWSCredentials;

    public void setAdAgentVendorClientProcessor(AdAgentVendorClientProcessor adAgentVendorClientProcessor) {
        this.adAgentVendorClientProcessor = adAgentVendorClientProcessor;
    }

    public void setAdAgentVendorCredential(Credential adAgentVendorCredential) {
        this.adAgentVendorCredential = adAgentVendorCredential;
    }

    public void setAdAgentWSCredentials(Credentials adAgentWSCredentials) {
        this.adAgentWSCredentials = adAgentWSCredentials;
    }

    /**
     * Update the status at the vendor for all the specified product's campaigns.
     * Log and swallow any vendor exceptions.
     */
    public void updateAllCampaignStatusesAtVendor(String logTag, Connection connection, String prodInstId, String status, String updatedByUser) throws SQLException {
        ArrayBuilder<String> excludedCampaignStatuses = new ArrayBuilder<String>();
        excludedCampaignStatuses.add(CampaignStatus.DELETED);
        if (CampaignStatus.SYSTEM_PAUSE.equals(status)) {
            excludedCampaignStatuses.add(CampaignStatus.MANUAL_PAUSE);
        }

        Collection<NsCampaign> nsCampaigns =
            nsEntityHelper.getNsAdCampaignsExcludingStatuses(
                    logTag,
                    connection,
                    prodInstId,
                    excludedCampaignStatuses.toArray(String.class));
        for (NsCampaign nsCampaign : nsCampaigns) {
            CampaignData campaignData = new CampaignData();
            campaignData.setNsCampaignId(nsCampaign.getNsCampaignId());
            campaignData.setProdInstId(nsCampaign.getProdInstId());
            campaignData.setStatus(status);
            campaignData.setVendorId(nsCampaign.getVendorId());
            updateCampaignAtVendor(campaignData, updatedByUser);
        }
    }

    /**
     * Update the specified campaign at the vendor.
     * Log and swallow any vendor exception.
     */
    public void updateCampaignAtVendor(CampaignData campaignData, String updatedByUser) {
        if ((adAgentVendorClientProcessor == null) || (adAgentVendorCredential == null) || (adAgentWSCredentials == null)) {
            throw new IllegalStateException();
        }

        try {
            newCampaignUpdater(campaignData.getVendorId()).updateCampaign(campaignData, updatedByUser);
        }
        catch (Exception ex) {
            logger.error("CampaignUtil.updateCampaignStatusAtVendor", ex);
        }
    }

    /**
     * Update the specified campaigns at the vendor.
     * Log and swallow any vendor exception.
     */
    public void updateCampaignsAtVendor(Iterable<CampaignData> campaignDatas, String updatedByUser) {
        for (CampaignData campaignData : campaignDatas) {
            updateCampaignAtVendor(campaignData, updatedByUser);
        }
    }

    /**
     * Update the specified campaigns at the vendor.
     * Log and swallow any vendor exception.
     */
    public void updateCampaignsAtVendor(CampaignData[] campaignDatas, String updatedByUser) {
        updateCampaignsAtVendor(toIterable(campaignDatas), updatedByUser);
    }

    /**
     * Update the statuses of the specified campaigns at the vendor.
     * Log and swallow any vendor exception.
     */
    public void updateCampaignStatusesAtVendor(String prodInstId, Iterable<Pair<Long, Integer>> nsCampaignIdsAndVendorIds, String status, String updatedByUser) {
        ArrayBuilder<CampaignData> campaignDatas = new ArrayBuilder<CampaignData>();
        for (Pair<Long, Integer> nsCampaignIdAndVendorId : nsCampaignIdsAndVendorIds) {
            CampaignData campaignData = new CampaignData();
            campaignData.setNsCampaignId(nsCampaignIdAndVendorId.getFirst().longValue());
            campaignData.setProdInstId(prodInstId);
            campaignData.setStatus(status);
            campaignData.setVendorId(nsCampaignIdAndVendorId.getSecond().intValue());
        }
        updateCampaignsAtVendor(campaignDatas.toArray(CampaignData.class), updatedByUser);
    }

    public static class CampaignData extends BaseData {
        private double dailyBudget;
        private long nsCampaignId;
        private String prodInstId;
        private String status;
        private int vendorId;

        public void setDailyBudget(double dailyBudget) {
            this.dailyBudget = dailyBudget;
        }

        public double getDailyBudget() {
            return dailyBudget;
        }

        public void setNsCampaignId(long nsCampaignId) {
            this.nsCampaignId = nsCampaignId;
        }

        public long getNsCampaignId() {
            return nsCampaignId;
        }

        public void setProdInstId(String prodInstId) {
            this.prodInstId = prodInstId;
        }

        public String getProdInstId() {
            return prodInstId;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getStatus() {
            return status;
        }

        public void setVendorId(int vendorId) {
            this.vendorId = vendorId;
        }

        public int getVendorId() {
            return vendorId;
        }
    }

    private CampaignUpdater newCampaignUpdater(int vendorId) {
        switch (vendorId) {
        case VendorId.SUPERPAGES:
            return new SuperPagesCampaignUpdater();

        default:
            return new AdVendorCampaignUpdater();
        }
    }

    private static interface CampaignUpdater {
        public abstract void updateCampaign(CampaignData campaignData, String updatedByUser) throws Exception;
    }

    private class AdVendorCampaignUpdater implements CampaignUpdater {
        public void updateCampaign(CampaignData campaignData, String updatedByUser) throws Exception {
            NSCampaign campaign = AdAgentVendorClientFactory.getNSCampaignInstance();
            campaign.setDailyBudget(campaignData.getDailyBudget());
            campaign.setNsCampaignId(campaignData.getNsCampaignId());
            campaign.setStatus(campaignData.getStatus());
            campaign.setVendorId(campaignData.getVendorId());
            CampaignRequest campaignRequest = AdAgentVendorClientFactory.getCampaignRequestInstance();
            campaignRequest.setNsCampaign(campaign);
            campaignRequest.setOriginatorUserId(UserId.INTERNAL_USER_ID);
            campaignRequest.setProductInstanceId(campaignData.getProdInstId());
            campaignRequest.setUpdatedByUser(updatedByUser);

            logger.info("Updating campaign:" + BaseData.toString(campaign));
            adAgentVendorClientProcessor.updateCampaign(adAgentVendorCredential, campaignRequest);
        }
    }

    private class SuperPagesCampaignUpdater implements CampaignUpdater {
        public void updateCampaign(CampaignData campaignData, String updatedByUser) throws Exception {
            logger.info("Updating campaign: " + campaignData.toString());
            // SuperPages campaigns do not have daily budgets.
            ClientFactories.getClientFactory().getCampaignClient(adAgentWSCredentials).updateCampaignStatus(
                    campaignData.getProdInstId(),
                    campaignData.getNsCampaignId(),
                    campaignData.getStatus());
        }
    }
}
