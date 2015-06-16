/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.vendor;

import org.apache.commons.httpclient.HttpClient;

import com.netsol.vendor.beans.Credentials;
import com.netsol.vendor.beans.NSSuperPagesCampaignData;
import com.netsol.vendor.beans.Status;
import com.netsol.vendor.client.Paths;

/* package-private */ class CampaignClient extends BaseClient implements com.netsol.vendor.client.CampaignClient {
    public static final String sccsID = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:36 CampaignClient.java NSI";

    public CampaignClient(Credentials credentials, HttpClient httpClient) {
        super(credentials, httpClient);
    }

    public String addCampaign(NSSuperPagesCampaignData nsSuperPagesCampaignData) throws Exception {
        return restProcessor.post(rootUrl + Paths.campaignsPath(nsSuperPagesCampaignData.getProdInstId()), nsSuperPagesCampaignData, String.class);
    }

    public void syncCampaigns(String prodInstId) throws Exception {
        restProcessor.get(rootUrl + Paths.campaignsPath(prodInstId));
    }

    public void updateCampaign(NSSuperPagesCampaignData nsSuperPagesCampaignData) throws Exception {
        updateCampaignAtSuperPages(nsSuperPagesCampaignData);
        updateNsCampaign(nsSuperPagesCampaignData);
    }

    public void updateCampaignAtSuperPages(NSSuperPagesCampaignData nsSuperPagesCampaignData) throws Exception {
        restProcessor.put(
                rootUrl + Paths.campaignPath(nsSuperPagesCampaignData.getProdInstId(), nsSuperPagesCampaignData.getNsCampaignId()),
                nsSuperPagesCampaignData);
    }

    public void updateCampaignStatus(String prodInstId, long nsCampaignId, String status) throws Exception {
        Status s = new Status();
        s.setStatus(status);
        restProcessor.put(rootUrl + Paths.campaignStatusPath(prodInstId, nsCampaignId), s);
    }

    public void updateNsCampaign( NSSuperPagesCampaignData nsSuperPagesCampaignData) throws Exception {
        restProcessor.put(
                rootUrl + Paths.nsCampaignPath(nsSuperPagesCampaignData.getProdInstId(), nsSuperPagesCampaignData.getNsCampaignId()),
                nsSuperPagesCampaignData);
    }
}
