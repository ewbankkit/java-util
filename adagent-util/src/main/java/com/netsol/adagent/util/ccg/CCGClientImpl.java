/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.ccg;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.PartSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.netsol.adagent.util.beans.BaseData;
import com.netsol.adagent.util.beans.LeadAndVisitorSummaryData;
import com.netsol.ccg.factory.CCGClientFactory;
import com.netsol.ccg.intf.Attachment;
import com.netsol.ccg.intf.Credential;
import com.netsol.ccg.intf.CustCommGatewayException;
import com.netsol.ccg.intf.CustomEmail;
import com.netsol.ccg.intf.CustomEmailGenerator;
import com.netsol.ccg.intf.CustomTemplateEmail;
import com.netsol.ccg.intf.EmailGenerator;
import com.netsol.ccg.intf.LSVSubmissionInfo;
import com.netsol.ccg.intf.NameValuePair;
import com.netsol.ccg.intf.PPCSummaryReport;
import com.netsol.ccg.intf.PPCSummaryStatistic;
import com.netsol.ccg.intf.RequestData;

/* package-private */ class CCGClientImpl implements CCGClient {
    public static final String sccsID = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:20 CCGClientImpl.java NSI";

    private final Log log = LogFactory.getLog(this.getClass());
    private final CCGConnectionCfg conf;

    CCGClientImpl(CCGConnectionCfg conf){
        this.conf = conf;
    }

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
            )
    throws CCGClientException
    {
        CustomEmail customEmail = CCGClientFactory.getCustomEmailInstance();
        customEmail.setDisableTracking(true);
        customEmail.setFromEmailAddress(fromEmailAddr);
        customEmail.setFromName(fromName);
        customEmail.setRcptEmailAddresses(recipientEmailAddrs.toArray(new String[recipientEmailAddrs.size()]));
        customEmail.setReplyTo(replyTo);
        customEmail.setSubject(subject);
        customEmail.setTextBody(textBody);
        if (log.isTraceEnabled()) {
            log.trace(BaseData.toString(customEmail));
        }

        try {
            long requestId = getCustomEmailGenerator().sendCustomEmail(
                    getCredential(),
                    customEmail,
                    getRequestData("Adagent JavaScript Interceptor", 1234L));
            log.debug("CCG request ID: " + Long.toString(requestId));
            return requestId;
        }
        catch (CustCommGatewayException e) {
            throw new CCGClientException(e);
        }
    }

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
            )
    throws CCGClientException
    {
        //Tags that contain all dynamic content
        final String
            URL_REPORTS = "URL_REPORTS",
            CUSTOM_MESSAGE = "CUSTOM_MESSAGE";

        final int TEMPLATE_ID = 600;

        NameValuePair[] nvps = getTags(
            URL_REPORTS, siteUrl,
            CUSTOM_MESSAGE, customMessage);

        String[] emails = recipientEmailAddrs.toArray( new String[recipientEmailAddrs.size()] );

        DateFormat df = new SimpleDateFormat("ddMMyyyy.HHmmss");
        String diskFileName = fileName + "_" + prodInstId + "_" + df.format(new Date());

        Attachment att = CCGClientFactory.getAttachmentInstance();
        att.setFileName(fileName);
        att.setLocation(diskFileName);
        att.setCompressFile(false);
        att.setFormat("application");


        CustomTemplateEmail customTemplateEmail = CCGClientFactory.getCustomTemplateEmailInstance();
        customTemplateEmail.setNameValuePairs(nvps);
        customTemplateEmail.setRcptEmailAddresses(emails);
        customTemplateEmail.setTemplateId(TEMPLATE_ID);
        customTemplateEmail.setAttachments(new Attachment[]{att});


        uploadAttachment(reportContents, diskFileName);
        log.info("Uploaded file to inthub service: "+diskFileName);


        long directRequestId = -1;
        try{

            directRequestId = getCustomEmailGenerator().sendCustomTemplateEmail(
                    getCredential(),
                    customTemplateEmail,
                    getRequestData("AMP", prodInstId));
        }catch(Exception e){
            throw new CCGClientException(e);
        }

        return directRequestId;
    }

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
            )
    throws CCGClientException
    {
        LSVSubmissionInfo lsvSubmissionInfo = CCGClientFactory.getLsvSubmissionInfoInstance();
        lsvSubmissionInfo.setBusinessName(businessName);
        lsvSubmissionInfo.setBusinessUrl(businessUrl);
        lsvSubmissionInfo.setProdInstId(prodInstId);
        lsvSubmissionInfo.setThinkLocalUrl(thinkLocalUrl);
        if (log.isTraceEnabled()) {
            log.trace(BaseData.toString(lsvSubmissionInfo));
        }

        try {
            long requestId = getEmailGenerator().sendLSVSubmissionNotice(
                    getCredential(),
                    lsvSubmissionInfo,
                    getRequestData("LSV Submission Notice", prodInstId));
            log.debug("CCG request ID: " + Long.toString(requestId));
            return requestId;
        }
        catch (CustCommGatewayException e) {
            throw new CCGClientException(e);
        }
    }
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
           )
    throws CCGClientException
    {
       Map<Integer, Float> statistics = new HashMap<Integer, Float>();
       statistics.put(Integer.valueOf(PPCSummaryStatistic.PHONE_CALL_COUNT_CD), Float.valueOf(leadAndVisitorSummaryData.getPhoneLeadCount()));
       statistics.put(Integer.valueOf(PPCSummaryStatistic.EMAIL_COUNT_CD), Float.valueOf(leadAndVisitorSummaryData.getEmailLeadCount()));
       statistics.put(Integer.valueOf(PPCSummaryStatistic.FORM_SUBMISSION_COUNT_CD), Float.valueOf(leadAndVisitorSummaryData.getFormLeadCount()));
       statistics.put(Integer.valueOf(PPCSummaryStatistic.ONLINE_SALES_COUNT_CD), Float.valueOf(leadAndVisitorSummaryData.getShoppingCartLeadCount()));
       statistics.put(Integer.valueOf(PPCSummaryStatistic.TOTAL_LEAD_CD), Float.valueOf(leadAndVisitorSummaryData.getTotalLeadCount()));
       statistics.put(Integer.valueOf(PPCSummaryStatistic.AVERAGE_DAILY_LEAD_CD), Float.valueOf(leadAndVisitorSummaryData.getAverageDailyLeadCount()));
       statistics.put(Integer.valueOf(PPCSummaryStatistic.TOTAL_VISITOR_CD), Float.valueOf(leadAndVisitorSummaryData.getTotalVisitCount()));
       statistics.put(Integer.valueOf(PPCSummaryStatistic.AVERAGE_DAILY_VISITOR_CD), Float.valueOf(leadAndVisitorSummaryData.getAverageDailyVisitCount()));

       PPCSummaryStatistic[] ppcSummaryStats = CCGClientFactory.getPPCSummaryStatisticArrayInstance(statistics.size());
       int i = 0;
       for (Map.Entry<Integer, Float> statisticEntry : statistics.entrySet()) {
           PPCSummaryStatistic ppcSummaryStat = CCGClientFactory.getPPCSummaryStatisticInstance();
           ppcSummaryStat.setStatistic(statisticEntry.getValue().floatValue());
           ppcSummaryStat.setStatisticCode(statisticEntry.getKey().intValue());
           ppcSummaryStats[i++] = ppcSummaryStat;
       }

       PPCSummaryReport ppcSummaryReport = CCGClientFactory.getPPCSummaryReportInstance();
       ppcSummaryReport.setEndDate(endDate);
       ppcSummaryReport.setProdInstId(prodInstId);
       ppcSummaryReport.setStartDate(startDate);
       ppcSummaryReport.setSummaryStats(ppcSummaryStats);
       if (log.isTraceEnabled()) {
           log.trace(BaseData.toString(ppcSummaryReport));
       }

       try {
           long requestId = getEmailGenerator().sendPPCSummaryReport(
                   getCredential(),
                   ppcSummaryReport,
                   getRequestData("PPC Adagent Summary Report", prodInstId));
           log.debug("CCG request ID: " + Long.toString(requestId));
           return requestId;
       }
       catch (CustCommGatewayException e) {
           throw new CCGClientException(e);
       }
    }

    private void uploadAttachment(final InputStream reportContents, final String diskFileName)
    throws CCGClientException {
       HttpClient client = new HttpClient();
       PostMethod filePost = new PostMethod(conf.getUploadServiceUrl());
       client.getHttpConnectionManager().getParams().setConnectionTimeout(conf.getTimeoutMillis());


       try {

           final byte[] bytes = readBytes(reportContents);

           Part[] parts = {
                   new FilePart(diskFileName, new PartSource(){
                       public InputStream createInputStream() throws IOException {
                           return new ByteArrayInputStream(bytes);
                       }
                       public String getFileName() {
                           return diskFileName;
                       }
                       public long getLength() {
                           return bytes.length;
                       }
                   })
           };
           filePost.setRequestEntity( new MultipartRequestEntity(parts, filePost.getParams()) );

           int status = client.executeMethod(filePost);
           if (status != HttpStatus.SC_OK) {
              throw new CCGClientException("Unable to post file, http response code: "+ status);
           }

       } catch (Exception ex) {
          throw new CCGClientException("Unable to post file, exception: "+ ex, ex);
       } finally {
           filePost.releaseConnection();
       }

    }

    private byte[] readBytes(InputStream is) throws IOException{

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        byte[] byteArray = null;

        int r = 0;
        byte[] buf = new byte[1024];
        while( (r = is.read(buf, 0, buf.length)) > 0 ){
            bytes.write(buf, 0, r);
        }
        byteArray =  bytes.toByteArray();

        return byteArray;
    }

    private NameValuePair[] getTags(String ...tags) throws CCGClientException{
        if( (tags.length & 0x1) != 0){
            throw new CCGClientException("tags must be passed in pairs");
        }

        List<NameValuePair> nvps = new ArrayList<NameValuePair>();

        for(int i=0; i<tags.length; i+=2){
            String name = tags[i];
            String val = tags[i+1];
            val = (val == null || val.trim().length() < 1 ? null : val.trim());

            if(val != null){
                NameValuePair nvp = CCGClientFactory.getNameValuePairInstance();
                nvp.setName(name);
                nvp.setValue(val);
                nvps.add(nvp);
            }
        }

        return nvps.toArray(new NameValuePair[nvps.size()]);

    }

    private EmailGenerator getEmailGenerator() throws CustCommGatewayException {
        CCGClientFactory.setStubMode(conf.isStubMode());
        return CCGClientFactory.getEmailGeneratorInstance(conf.getEmailGenUrl(), conf.getTimeoutMillis());
    }

    private CustomEmailGenerator getCustomEmailGenerator() throws CustCommGatewayException {
        CCGClientFactory.setStubMode(conf.isStubMode());
        return CCGClientFactory.getCustomEmailGeneratorInstance(conf.getCustomEmailGenUrl(), conf.getTimeoutMillis());
    }

    private Credential getCredential(){
        Credential credential = CCGClientFactory.getCredentialInstance();
        credential.setUserName(conf.getUserName());
        credential.setPassword(conf.getPassword());

        return credential;
    }

    private RequestData getRequestData(String clientTrackingName, long clientId) {
        RequestData requestData = CCGClientFactory.getRequestDataInstance();

        requestData.setTrackingHeaderData(clientTrackingName);
        requestData.setClientRef("");
        requestData.setClientID(clientId);
        return requestData;
    }

    private RequestData getRequestData(String clientTrackingName, String clientRef) {
        RequestData requestData = CCGClientFactory.getRequestDataInstance();

        requestData.setTrackingHeaderData(clientTrackingName);
        requestData.setClientRef(clientRef);
        return requestData;
    }
}
