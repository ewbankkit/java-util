/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.vendor;

import static com.netsol.adagent.util.beans.BaseData.coalesce;
import static com.netsol.adagent.util.beans.BaseData.toIterable;

import org.apache.commons.httpclient.HttpClient;

import com.netsol.adagent.util.tracking.QueryString;
import com.netsol.vendor.beans.BaseLocation;
import com.netsol.vendor.beans.Credentials;
import com.netsol.vendor.beans.NSCampaignGeoData;
import com.netsol.vendor.client.Paths;


/* package-private */ class CampaignGeographyClient extends BaseClient implements com.netsol.vendor.client.CampaignGeographyClient {
    public static final String sccsID = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:37 CampaignGeographyClient.java NSI";

    public CampaignGeographyClient(Credentials credentials, HttpClient httpClient) {
        super(credentials, httpClient);
    }

    public void addCampaignGeographies(String prodInstId, long nsCampaignId, NSCampaignGeoData[] nsCampaignGeoData) throws Exception {
        restProcessor.post(rootUrl + Paths.campaignGeographiesPath(prodInstId, nsCampaignId), nsCampaignGeoData);
    }

    public void deleteCampaignGeographies(String prodInstId, long nsCampaignId, String[] nsGeoIds) throws Exception {
        QueryString queryString = new QueryString().addMulti("id", toIterable(nsGeoIds));
        restProcessor.delete(rootUrl + Paths.campaignGeographiesPath(prodInstId, nsCampaignId) + "?" + queryString.toUrlEncodedString(characterEncoding));
    }

    public BaseLocation[] getLocalGeosByZipCode(String zipcode) throws Exception {
        return coalesce(restProcessor.get(rootUrl + Paths.geographiesByZipCodePath(zipcode), BaseLocation[].class), new BaseLocation[0]);
    }

    public void syncCampaignGeographies(String prodInstId, long nsCampaignId) throws Exception {
        restProcessor.get(rootUrl + Paths.campaignGeographiesPath(prodInstId, nsCampaignId));
    }
}
