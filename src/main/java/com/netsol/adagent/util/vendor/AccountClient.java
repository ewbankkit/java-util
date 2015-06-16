/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.vendor;

import org.apache.commons.httpclient.HttpClient;

import com.netsol.vendor.beans.AccountBean;
import com.netsol.vendor.beans.Budget;
import com.netsol.vendor.beans.Credentials;
import com.netsol.vendor.beans.Status;
import com.netsol.vendor.client.Paths;

/* package-private */ class AccountClient extends BaseClient implements com.netsol.vendor.client.AccountClient {
    public static final String sccsID = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:36 AccountClient.java NSI";

    /**
     * Constructor.
     */
    public AccountClient(Credentials credentials, HttpClient httpClient) {
        super(credentials, httpClient);
    }

    public String addAccount(AccountBean account) throws Exception {
        return restProcessor.post(rootUrl + Paths.accountsPath(), account, String.class);
    }

    public AccountBean getAccountDetail(String prodInstId) throws Exception {
        return restProcessor.get(rootUrl + Paths.accountPath(prodInstId), AccountBean.class);
    }

    public AccountBean[] getAccounts() throws Exception {
        return restProcessor.get(rootUrl + Paths.accountsPath(), AccountBean[].class);
    }

    public void updateAccountBudget(String prodInstId, double budget) throws Exception {
        Budget b = new Budget();
        b.setBudget(budget);
        restProcessor.put(rootUrl + Paths.accountBudgetPath(prodInstId), b);
    }

    public void updateAccountStatus(String prodInstId, String status) throws Exception {
        Status s = new Status();
        s.setStatus(status);
        restProcessor.put(rootUrl + Paths.accountStatusPath(prodInstId), s);
    }
}
