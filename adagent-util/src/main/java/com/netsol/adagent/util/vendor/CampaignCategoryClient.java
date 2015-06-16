/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.vendor;

import static com.netsol.adagent.util.beans.BaseData.coalesce;
import static com.netsol.adagent.util.beans.BaseData.toIterable;

import org.apache.commons.httpclient.HttpClient;

import com.netsol.adagent.util.tracking.QueryString;
import com.netsol.vendor.beans.Credentials;
import com.netsol.vendor.beans.NSCampaignCategoryData;
import com.netsol.vendor.client.Paths;

/* package-private */ class CampaignCategoryClient extends BaseClient implements com.netsol.vendor.client.CampaignCategoryClient {
    public static final String sccsID = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:36 CampaignCategoryClient.java NSI";

    /**
     * Constructor.
     */
    public CampaignCategoryClient(Credentials credentials, HttpClient httpClient) {
        super(credentials, httpClient);
    }

    public void addCampaignCategories(String prodInstId, long nsCampaignId, NSCampaignCategoryData[] categories) throws Exception {
        restProcessor.post(rootUrl + Paths.campaignCategoriesPath(prodInstId, nsCampaignId), categories);
    }

    public void deleteCampaignCategories(String prodInstId, long nsCampaignId, String[] nsCategoryIds) throws Exception {
        QueryString queryString = new QueryString().addMulti("id", toIterable(nsCategoryIds));
        restProcessor.delete(rootUrl + Paths.campaignCategoriesPath(prodInstId, nsCampaignId) + "?" + queryString.toUrlEncodedString(characterEncoding));
    }

    public NSCampaignCategoryData[] getCategoriesByKeywords(String[] keywords) throws Exception {
        QueryString queryString = new QueryString().addMulti("keyword", toIterable(keywords));
        return coalesce(restProcessor.get(
                rootUrl + Paths.categoriesByKeywordsPath() + "?" + queryString.toUrlEncodedString(characterEncoding),
                NSCampaignCategoryData[].class), new NSCampaignCategoryData[0]);
    }

    public void syncCampaignCategories(String prodInstId, long nsCampaignId) throws Exception {
        restProcessor.get(rootUrl + Paths.campaignCategoriesPath(prodInstId, nsCampaignId));
    }

    public void updateCampaignCategoryBid(String prodInstId, long nsCampaignId, NSCampaignCategoryData[] categories) throws Exception {
        restProcessor.put(rootUrl + Paths.campaignCategoriesPath(prodInstId, nsCampaignId), categories);
    }
}
