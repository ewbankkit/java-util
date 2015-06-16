/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.ccg;

import java.io.InputStream;
import java.util.Calendar;
import java.util.Collection;

import com.netsol.adagent.util.beans.LeadAndVisitorSummaryData;

public interface CCGClient {
    public static final String sccsID = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:19 CCGClient.java NSI";

    /**
     *
     * @param fromEmailAddr - from e-mail address
     * @param fromName - from name
     * @param recipientEmailAddrs - Collection of all recipients to receive the lead
     * @param replyTo - reply to
     * @param subject - subject
     * @param textBody - text body
     * @return CCG Request ID for tracking
     * @throws CCGClientException
     */
    public long sendEmailLead(String fromEmailAddr,
            String fromName,
            Collection<String> recipientEmailAddrs,
            String replyTo,
            String subject,
            String textBody
            ) throws CCGClientException;

    /**
     *
     * @param recipientEmailAddrs - Collection of all recipients to receive the report
     * @param customMessage - a custom message to be inserted into email body
     * @param siteUrl - customer's website url
     * @param pdf - InputStream for the attachment's contents
     * @param fileName - Filename to be used on the email attachment
     * @param prodInstId - product instance ID for the product
     * @return - CCG Request ID for tracking
     */
    public long sendEmailReport(Collection<String> recipientEmailAddrs,
            String customMessage,
            String siteUrl,
            InputStream reportContents,
            String fileName,
            String prodInstId
            ) throws CCGClientException;

    /**
     *
     * @param businessName - Business name
     * @param businessUrl - Business URL
     * @param thinkLocalUrl - ThinkLocal URL
     * @param prodInstId - product instance ID for the product
     * @return - CCG Request ID for tracking
     * @throws CCGClientException
     */
    public long sendLSVSubmissionNotice(String businessName,
            String businessUrl,
            String thinkLocalUrl,
            String prodInstId
            ) throws CCGClientException;

    /**
     *
     * @param startDate - Report start date
     * @param endDate - Report end date
     * @param leadAndVisitorSummaryData - Lead and visitor summary data
     * @param prodInstId - product instance ID for the product
     * @return - CCG Request ID for tracking
     * @throws CCGClientException
     */
    public long sendPPCSummaryReport(Calendar startDate,
            Calendar endDate,
            LeadAndVisitorSummaryData leadAndVisitorSummaryData,
            String prodInstId
            ) throws CCGClientException;
}
