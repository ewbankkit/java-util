/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.dbhelpers;

import static com.netsol.adagent.util.beans.BaseData.arrayFromString;
import static com.netsol.adagent.util.beans.BaseData.arrayIsEmpty;
import static com.netsol.adagent.util.beans.BaseData.coalesce;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;

import com.netsol.adagent.util.beans.VendorReport;

/**
 * DB helpers for vendor reports.
 */
public class VendorReportHelper extends BaseHelper {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:57 VendorReportHelper.java NSI";

    public VendorReportHelper(String logComponent) {
        super(logComponent);
    }

    /**
     * Get any report for the specified NS report ID.
     */
    public VendorReport getVendorReport(String logTag, Connection connection, long nsReportId) throws SQLException {
        final String SQL =
            "SELECT" +
            "  ns_report_id," +
            "  vendor_id," +
            "  vendor_report_id," +
            "  vendor_account_id," +
            "  report_type_id," +
            "  report_start_date," +
            "  report_end_date," +
            "  requested_date," +
            "  completed_date," +
            "  report_url," +
            "  file_size," +
            "  download_start_date," +
            "  download_end_date," +
            "  processing_start_date," +
            "  processing_end_date," +
            "  row_count," +
            "  processed_row_count," +
            "  prod_inst_ids," +
            "  error " +
            "FROM" +
            "  vendor_report " +
            "WHERE" +
            "  ns_report_id = ?;";

        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(SQL);
            statement.setLong(1, nsReportId);
            logSqlStatement(logTag, statement);
            resultSet = statement.executeQuery();
            return singleValue(resultSet, VendorReportFactory.INSTANCE);
        }
        finally {
            close(statement, resultSet);
        }
    }

    /**
     * Insert a report.
     * Return the NS report ID.
     */
    public Long insertVendorReport(String logTag, Connection connection, VendorReport vendorReport) throws SQLException {
        final String SQL =
            "INSERT INTO vendor_report" +
            "  (vendor_id, vendor_account_id, report_type_id, report_start_date, report_end_date, requested_date, prod_inst_ids) " +
            "VALUES" +
            "  (?, ?, ?, ?, ?, NOW(), ?);";

        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, vendorReport.getVendorId());
            statement.setInt(2, vendorReport.getVendorAccountId());
            statement.setInt(3, vendorReport.getReportTypeId());
            statement.setTimestamp(4, toSqlTimestamp(vendorReport.getReportStartDate()));
            statement.setTimestamp(5, toSqlTimestamp(vendorReport.getReportEndDate()));
            statement.setString(6, arrayIsEmpty(vendorReport.getProdInstIds()) ? null : Arrays.toString(vendorReport.getProdInstIds()));
            logSqlStatement(logTag, statement);
            statement.executeUpdate();
            return getAutoIncrementId(statement);
        }
        finally {
            close(statement);
        }
    }

    /**
     * Update the report completed date.
     */
    public void updateVendorReport(String logTag, Connection connection, VendorReport vendorReport) throws SQLException {
        final String SQL =
            "UPDATE" +
            "  vendor_report " +
            "SET" +
            "  %1$s " +
            "WHERE" +
            "  ns_report_id = ?;";

        String sql = String.format(SQL, vendorReport.getUpdateValuesSnippet());

        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(sql);
            int parameterIndex = vendorReport.setUpdateParameters(statement, 1);
            statement.setLong(parameterIndex++, vendorReport.getNsReportId());
            logSqlStatement(logTag, statement);
            statement.executeUpdate();
        }
        finally {
            close(statement);
        }
    }

    /**
     * Factory class used to create VendorReport objects from a result set.
     */
    private static class VendorReportFactory implements Factory<VendorReport> {
        public static final VendorReportFactory INSTANCE = new VendorReportFactory();

        /**
         * Constructor.
         */
        private VendorReportFactory() {}

        /**
         * Return a new instance with values from the result set.
         */
        public VendorReport newInstance(ResultSet resultSet) throws SQLException {
            VendorReport vendorReport = new VendorReport();
            vendorReport.setCompletedDate(resultSet.getTimestamp("completed_date"));
            vendorReport.setDownloadEndDate(resultSet.getTimestamp("download_end_date"));
            vendorReport.setDownloadStartDate(resultSet.getTimestamp("download_start_date"));
            vendorReport.setError(resultSet.getString("error"));
            vendorReport.setFileSize(resultSet.getLong("file_size"));
            vendorReport.setNsReportId(resultSet.getLong("ns_report_id"));
            vendorReport.setProcessedRowCount(resultSet.getLong("processed_row_count"));
            vendorReport.setProcessingEndDate(resultSet.getTimestamp("processing_end_date"));
            vendorReport.setProcessingStartDate(resultSet.getTimestamp("processing_start_date"));
            vendorReport.setProdInstIds(arrayFromString(coalesce(resultSet.getString("prod_inst_ids"), "null")));
            vendorReport.setReportEndDate(resultSet.getTimestamp("report_end_date"));
            vendorReport.setReportStartDate(resultSet.getTimestamp("report_start_date"));
            vendorReport.setReportTypeId(resultSet.getInt("report_type_id"));
            vendorReport.setReportUrl(resultSet.getString("report_url"));
            vendorReport.setRequestedDate(resultSet.getTimestamp("requested_date"));
            vendorReport.setRowCount(resultSet.getLong("row_count"));
            vendorReport.setVendorAccountId(resultSet.getInt("vendor_account_id"));
            vendorReport.setVendorId(resultSet.getInt("vendor_id"));
            vendorReport.setVendorReportId(resultSet.getLong("vendor_report_id"));
            return vendorReport;
        }
    }
}
