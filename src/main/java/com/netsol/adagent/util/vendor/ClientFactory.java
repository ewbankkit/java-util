/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.vendor;

import org.apache.commons.httpclient.HttpClient;

import com.netsol.vendor.beans.Credentials;

/**
 * Client factory.
 */
public class ClientFactory implements com.netsol.vendor.client.ClientFactory {
    public static final String sccsID = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:37 ClientFactory.java NSI";

    private final HttpClient httpClient;

    /**
     * Constructor.
     */
    public ClientFactory(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public com.netsol.vendor.client.AccountClient getAccountClient(Credentials credentials) {
        return new com.netsol.adagent.util.vendor.AccountClient(credentials, httpClient);
    }

    public com.netsol.vendor.client.CampaignCategoryClient getCampaignCategoryClient(Credentials credentials) {
        return new com.netsol.adagent.util.vendor.CampaignCategoryClient(credentials, httpClient);
    }

    public com.netsol.vendor.client.CampaignClient getCampaignClient(Credentials credentials) {
        return new com.netsol.adagent.util.vendor.CampaignClient(credentials, httpClient);
    }

    public com.netsol.vendor.client.CampaignGeographyClient getCampaignGeographyClient(Credentials credentials) {
        return new com.netsol.adagent.util.vendor.CampaignGeographyClient(credentials, httpClient);
    }

    public com.netsol.vendor.client.ReportingClient getReportingClient(Credentials credentials) {
        return new com.netsol.adagent.util.vendor.ReportingClient(credentials, httpClient);
    }
}
