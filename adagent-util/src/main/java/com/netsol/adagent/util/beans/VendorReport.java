/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.beans;

import java.util.Date;

import com.netsol.adagent.util.codes.VendorReportType;

/**
 * Represents a vendor report record.
 */
public class VendorReport extends BaseDataWithUpdateTracking {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:12 VendorReport.java NSI";

    @ColumnName("completed_date")
    private Date completedDate;
    @ColumnName("download_end_date")
    private Date downloadEndDate;
    @ColumnName("download_start_date")
    private Date downloadStartDate;
    @ColumnName("error")
    private String error;
    @ColumnName("file_size")
    private long fileSize;
    private long nsReportId;
    @ColumnName("processed_row_count")
    private long processedRowCount;
    @ColumnName("processing_end_date")
    private Date processingEndDate;
    @ColumnName("processing_start_date")
    private Date processingStartDate;
    private String[] prodInstIds;
    private Date reportEndDate;
    private Date reportStartDate;
    private int reportTypeId;
    @ColumnName("report_url")
    private String reportUrl;
    private Date requestedDate;
    @ColumnName("row_count")
    private long rowCount;
    private int vendorAccountId;
    private int vendorId;
    @ColumnName("vendor_report_id")
    private long vendorReportId;

    public void setCompletedDate(Date completedDate) {
        setTrackedField("completedDate", completedDate);
    }

    public Date getCompletedDate() {
        return completedDate;
    }

    public void setDownloadEndDate(Date downloadEndDate) {
        setTrackedField("downloadEndDate", downloadEndDate);
    }

    public Date getDownloadEndDate() {
        return downloadEndDate;
    }

    public void setDownloadStartDate(Date downloadStartDate) {
        setTrackedField("downloadStartDate", downloadStartDate);
    }

    public Date getDownloadStartDate() {
        return downloadStartDate;
    }

    public void setError(String error) {
        setTrackedField("error", error);
    }

    public String getError() {
        return error;
    }

    public void setFileSize(long fileSize) {
        setTrackedField("fileSize", fileSize);
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setNsReportId(long nsReportId) {
        this.nsReportId = nsReportId;
    }

    public long getNsReportId() {
        return nsReportId;
    }

    public void setProcessedRowCount(long processedRowCount) {
        setTrackedField("processedRowCount", processedRowCount);
    }

    public long getProcessedRowCount() {
        return processedRowCount;
    }

    public void setProcessingEndDate(Date processingEndDate) {
        setTrackedField("processingEndDate", processingEndDate);
    }

    public Date getProcessingEndDate() {
        return processingEndDate;
    }

    public void setProcessingStartDate(Date processingStartDate) {
        setTrackedField("processingStartDate", processingStartDate);
    }

    public Date getProcessingStartDate() {
        return processingStartDate;
    }

    public void setProdInstIds(String[] prodInstIds) {
        this.prodInstIds = prodInstIds;
    }

    public String[] getProdInstIds() {
        return prodInstIds;
    }

    public void setReportEndDate(Date reportEndDate) {
        this.reportEndDate = reportEndDate;
    }

    public Date getReportEndDate() {
        return reportEndDate;
    }

    public void setReportStartDate(Date reportStartDate) {
        this.reportStartDate = reportStartDate;
    }

    public Date getReportStartDate() {
        return reportStartDate;
    }

    public void setReportTypeId(int reportTypeId) {
        this.reportTypeId = reportTypeId;
    }

    public int getReportTypeId() {
        return this.reportTypeId;
    }

    public void setReportUrl(String reportUrl) {
        setTrackedField("reportUrl", reportUrl);
    }

    public String getReportUrl() {
        return reportUrl;
    }

    public void setRequestedDate(Date requestedDate) {
        this.requestedDate = requestedDate;
    }

    public Date getRequestedDate() {
        return requestedDate;
    }

    public void setRowCount(long rowCount) {
        setTrackedField("rowCount", rowCount);
    }

    public long getRowCount() {
        return rowCount;
    }

    public void setVendorAccountId(int vendorAccountId) {
        this.vendorAccountId = vendorAccountId;
    }

    public int getVendorAccountId() {
        return vendorAccountId;
    }

    public void setVendorId(int vendorId) {
        this.vendorId = vendorId;
    }

    public int getVendorId() {
        return vendorId;
    }

    public void setVendorReportId(long vendorReportId) {
        setTrackedField("vendorReportId", vendorReportId);
    }

    public long getVendorReportId() {
        return vendorReportId;
    }

    /**
     * Vendor report types.
     */
    public enum ReportType {
        AD_GROUP_SUMMARY(VendorReportType.AD_GROUP_SUMMARY, "Ad Group Performance Report"),
        AD_SUMMARY(VendorReportType.AD_SUMMARY,             "Ad Performance Report"),
        CAMPAIGN_SUMMARY(VendorReportType.CAMPAIGN_SUMMARY, "Campaign Performance Report"),
        KEYWORD_SUMMARY(VendorReportType.KEYWORD_SUMMARY,   "Keyword Performance Report"),
        CATEGORY_GEOGRAPHY_SUMMARY(VendorReportType.CATEGORY_GEOGRAPHY_SUMMARY,   "Superpage Category Geography Performance Report");

        private final int reportTypeId;
        private final String reportTypeName;

        /**
         * Constructor.
         */
        private ReportType(int reportTypeId, String reportTypeName) {
            this.reportTypeId = reportTypeId;
            this.reportTypeName = reportTypeName;
        }

        @Override
        public String toString() {
            return Integer.toString(reportTypeId);
        }

        public int reportTypeId() {
            return reportTypeId;
        }

        public String reportTypeName() {
            return reportTypeName;
        }

        public static ReportType fromReportTypeId(int reportTypeId) {
            if (reportTypeId == AD_GROUP_SUMMARY.reportTypeId) {
                return AD_GROUP_SUMMARY;
            }
            else if (reportTypeId == AD_SUMMARY.reportTypeId) {
                return AD_SUMMARY;
            }
            else if (reportTypeId == CAMPAIGN_SUMMARY.reportTypeId) {
                return CAMPAIGN_SUMMARY;
            }
            else if (reportTypeId == KEYWORD_SUMMARY.reportTypeId) {
                return KEYWORD_SUMMARY;
            }
            throw new IllegalArgumentException(Integer.toString(reportTypeId));
        }
    }
}
