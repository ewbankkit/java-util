/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.vendor;

import org.apache.commons.httpclient.HttpClient;

import com.netsol.vendor.beans.Credentials;
import com.netsol.vendor.beans.Reporting;
import com.netsol.vendor.client.Paths;

/* package-private */ class ReportingClient extends BaseClient implements com.netsol.vendor.client.ReportingClient {
    public static final String sccsID = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:38 ReportingClient.java NSI";

    public ReportingClient(Credentials credentials, HttpClient httpClient) {
        super(credentials, httpClient);
    }

    public String getReports(Reporting reporting) throws Exception {
        return restProcessor.post(rootUrl + Paths.reportsPath(), reporting, String.class);
    }

    public String getReportURL(String reportId) throws Exception {
        return restProcessor.get(rootUrl + Paths.reportPath(reportId), String.class);
    }
}
