/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.beans;

import java.util.Calendar;
import java.util.Date;

import com.netsol.adagent.util.CalendarUtil;

/**
 * Represents batch job status data.
 */
public class BatchJobStatusData extends BaseData {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:53:50 BatchJobStatusData.java NSI";

    public static final String BATCH_SERVER_SHUTDOWN_MESSAGE = "Batch server was shut down";
    public static final String DEFAULT_MESSAGE = "Please contact Engineering";

    private String batchJobId;
    private Long batchJobRunId;
    private Date batchJobStatusDate;
    private long batchJobStatusId;
    private String error;
    private String message;
    private Date processingDate;
    private String prodInstId;
    private String status;

    public void setBatchJobId(String batchJobId) {
        this.batchJobId = batchJobId;
    }

    public String getBatchJobId() {
        return batchJobId;
    }

    // A null batch job run ID can only be set when the object is created.
    public void setBatchJobRunId(long batchJobRunId) {
        this.batchJobRunId = Long.valueOf(batchJobRunId);
    }

    public Long getBatchJobRunId() {
        return batchJobRunId;
    }

    public void setBatchJobStatusDate(Date batchJobStatusDate) {
        this.batchJobStatusDate = batchJobStatusDate;
    }

    public Date getBatchJobStatusDate() {
        return batchJobStatusDate;
    }

    public void setBatchJobStatusId(long batchJobStatusId) {
        this.batchJobStatusId = batchJobStatusId;
    }

    public long getBatchJobStatusId() {
        return batchJobStatusId;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setProcessingDate(Calendar processingDate) {
        setProcessingDate(CalendarUtil.calendarToDate(processingDate));
    }

    public void setProcessingDate(Date processingDate) {
        this.processingDate = processingDate;
    }

    public Date getProcessingDate() {
        return processingDate;
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

    public static String getMessage(String error, String message) {
        return (message == null) ? ((error == null) ? null : BatchJobStatusData.DEFAULT_MESSAGE) : message;
    }
}
