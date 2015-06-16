/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.dbhelpers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;

import com.netsol.adagent.util.beans.BatchJobStatusData;

/**
 * DB helpers for batch job statuses.
 */
public class BatchJobStatusHelper extends BaseHelper {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:41 BatchJobStatusHelper.java NSI";

    public BatchJobStatusHelper(String logComponent, boolean logSqlStatements) {
        super(logComponent, logSqlStatements);
    }

    public BatchJobStatusHelper(Log logger, boolean logSqlStatements) {
        super(logger, logSqlStatements);
    }

    public List<BatchJobStatusData> getBatchJobsStatusList(String logTag, Connection connection, String prodInstId, Date startDate, Date endDate) throws SQLException {
        final String SQL =
            "SELECT" +
            "  batch_job_run_id," +
            "  batch_job_id," +
            "  prod_inst_id," +
            "  status," +
            "  message," +
            "  error," +
            "  processing_date," +
            "  updated_date " +
            "FROM" +
            "  batch_job_status " +
            "WHERE" +
            "  prod_inst_id IS NULL OR prod_inst_id = ? AND" +
            "  processing_date BETWEEN ? AND ?;";

        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(SQL);
            statement.setString(1, prodInstId);
            statement.setDate(2, toSqlDate(startDate));
            statement.setDate(3, toSqlDate(endDate));
            logSqlStatement(logTag, statement);
            resultSet = statement.executeQuery();
            return newList(resultSet, new Factory<BatchJobStatusData>() {
                public BatchJobStatusData newInstance(ResultSet resultSet) throws SQLException {
                    BatchJobStatusData batchJobStatusData = new BatchJobStatusData();
                    batchJobStatusData.setBatchJobRunId(resultSet.getLong("batch_job_run_id"));
                    batchJobStatusData.setBatchJobId(resultSet.getString("batch_job_id"));
                    batchJobStatusData.setProdInstId(resultSet.getString("prod_inst_id"));
                    batchJobStatusData.setStatus(resultSet.getString("status"));
                    batchJobStatusData.setMessage(resultSet.getString("message"));
                    batchJobStatusData.setError(resultSet.getString("error"));
                    batchJobStatusData.setProcessingDate(resultSet.getDate("processing_date"));
                    batchJobStatusData.setBatchJobStatusDate(resultSet.getDate("updated_date"));
                    return batchJobStatusData;
                }});
        }
        finally {
            close(statement, resultSet);
        }
    }

    public void insertBatchJobStatusData(String logTag, Connection connection, BatchJobStatusData batchJobStatusData) throws SQLException {
        final String SQL1 =
            "INSERT INTO batch_job_status" +
            "  (batch_job_run_id, batch_job_id, prod_inst_id, status, message, error, processing_date) " +
            "VALUES" +
            "  (?, ?, ?, ?, ?, ?, ?);";

        PreparedStatement statement1 = null;
        try {
            statement1 = connection.prepareStatement(SQL1, Statement.RETURN_GENERATED_KEYS);
            boolean batchJobRunIdSpecified = (batchJobStatusData.getBatchJobRunId() != null);
            if (batchJobRunIdSpecified) {
                statement1.setLong(1, batchJobStatusData.getBatchJobRunId().longValue());
            }
            else {
                statement1.setNull(1, Types.BIGINT);
            }
            statement1.setString(2, batchJobStatusData.getBatchJobId());
            statement1.setString(3, batchJobStatusData.getProdInstId());
            statement1.setString(4, batchJobStatusData.getStatus());
            statement1.setString(5, batchJobStatusData.getMessage());
            statement1.setString(6, batchJobStatusData.getError());
            statement1.setDate(7, toSqlDate(batchJobStatusData.getProcessingDate()));
            logSqlStatement(logTag, statement1);
            statement1.executeUpdate();
            Long batchJobStatusId = BaseHelper.getAutoIncrementId(statement1);
            if (batchJobStatusId != null) {
                batchJobStatusData.setBatchJobStatusId(batchJobStatusId.longValue());

                // If no batch job run ID was specified, use the generated batch job status ID value.
                if (!batchJobRunIdSpecified) {
                    batchJobStatusData.setBatchJobRunId(batchJobStatusData.getBatchJobStatusId());

                    final String SQL2 =
                        "UPDATE" +
                        "  batch_job_status " +
                        "SET" +
                        "  batch_job_run_id = ? " +
                        "WHERE" +
                        "  batch_job_status_id = ?;";

                    PreparedStatement statement2 = null;
                    try {
                        statement2 = connection.prepareStatement(SQL2);
                        statement2.setLong(1, batchJobStatusData.getBatchJobRunId().longValue());
                        statement2.setLong(2, batchJobStatusData.getBatchJobStatusId());
                        logSqlStatement(logTag, statement2);
                        statement2.executeUpdate();
                    }
                    finally {
                        close(statement2);
                    }
                }
            }
        }
        finally {
            close(statement1);
        }
    }
}
