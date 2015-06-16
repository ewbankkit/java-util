/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.quest;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Collection;

import com.netsol.adagent.util.dbhelpers.BaseHelper;
import com.netsol.adagent.util.log.BaseLoggable;

/**
 * Quest job submitter.
 */
/* package-private */ abstract class JobSubmitter<T> extends BaseLoggable {
    public final static String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:20 JobSubmitter.java NSI";

    protected final QuestQueueHelper questQueueHelper;

    private static final int PRIORITY = 2;
    private static final int SUBMIT_TYPE = 2;

    /**
     * Constructor.
     */
    protected JobSubmitter(BaseLoggable baseLoggable) {
        super(baseLoggable);

        this.questQueueHelper = new QuestQueueHelper(this);

        return;
    }

    /**
     * Submit a job.
     * Return the job ID.
     */
    public long submitJob(String logTag, Connection connection, Collection<T> requests, String jobName, int workflowId, int requestType, Long startTime) throws SQLException {
        long jobId = 0L;
        if (startTime == null) {
            jobId = this.questQueueHelper.createJob(logTag, connection, jobName, workflowId, JobSubmitter.PRIORITY);
        }
        else {
            jobId = this.questQueueHelper.createJobWithStartDate(logTag, connection, jobName, workflowId, JobSubmitter.PRIORITY, startTime.longValue());
        }
        this.questQueueHelper.setSubmitType(logTag, connection, jobId, JobSubmitter.SUBMIT_TYPE);
        for (T request : requests) {
            this.addRequest(logTag, connection, jobId, request);
        }
        this.questQueueHelper.startJob(logTag, connection, jobId, requestType);

        return jobId;
    }

    /**
     * Add a request to a job.
     */
    protected abstract void addRequest(String logTag, Connection connection, long jobId, T request) throws SQLException;

    protected static class QuestQueueHelper extends BaseHelper {
        /**
         * Constructor.
         */
        public QuestQueueHelper(BaseLoggable baseLoggable) {
            super(baseLoggable);

            return;
        }

        /**
         * Add a request to a job.
         */
        public void addRequest(String logTag, Connection connection, long jobId, String requestData) throws SQLException {
            final String SQL = "{CALL add_request_to_staged_job(?, ?)}";

            CallableStatement statement = null;
            try {
                statement = connection.prepareCall(SQL);
                statement.setLong(1, jobId);
                statement.setString(2, requestData);
                this.logSqlStatement(logTag, statement);
                statement.execute();

                return;
            }
            finally {
                BaseHelper.close(statement);
            }
        }

        /**
         * Create a job.
         * Return the new job ID.
         */
        public long createJob(String logTag, Connection connection, String jobName, int workflowId, int priority) throws SQLException {
            final String SQL = "{CALL create_job(?, ?, ?, ?, ?)}";

            CallableStatement statement = null;
            try {
                statement = connection.prepareCall(SQL);
                statement.setInt(1, workflowId);
                statement.setNull(2, Types.INTEGER); // No child workflow.
                statement.setString(3, jobName);
                statement.setInt(4, priority);
                statement.registerOutParameter(5, Types.BIGINT);
                this.logSqlStatement(logTag, statement);
                statement.execute();

                long jobId = statement.getLong(5);
                this.logInfo(logTag, "Created job ID: " + Long.toString(jobId));

                return jobId;
            }
            finally {
                BaseHelper.close(statement);
            }
        }

        /**
         * Create a job with a future start date.
         * Return the new job ID.
         */
        public long createJobWithStartDate(String logTag, Connection connection, String jobName, int workflowId, int priority, long startTime) throws SQLException {
            final String SQL = "{CALL create_job_with_start_date(?, ?, ?, ?, ?, ?)}";

            CallableStatement statement = null;
            try {
                statement = connection.prepareCall(SQL);
                statement.setInt(1, workflowId);
                statement.setNull(2, Types.INTEGER); // No child workflow.
                statement.setString(3, jobName);
                statement.setInt(4, priority);
                statement.setTimestamp(5, BaseHelper.toSqlTimestamp(startTime));
                statement.registerOutParameter(6, Types.BIGINT);
                this.logSqlStatement(logTag, statement);
                statement.execute();

                long jobId = statement.getLong(6);
                this.logInfo(logTag, "Created job ID: " + Long.toString(jobId));

                return jobId;
            }
            finally {
                BaseHelper.close(statement);
            }
        }

        /**
         * Set the job's submit type.
         */
        public void setSubmitType(String logTag, Connection connection, long jobId, int submitType) throws SQLException {
            final String SQL =
                "UPDATE" +
                "  job_info " +
                "SET" +
                "  submit_type = ? " +
                "WHERE" +
                "  job_id = ?;";

            PreparedStatement statement = null;
            try {
                statement = connection.prepareStatement(SQL);
                statement.setInt(1, submitType);
                statement.setLong(2, jobId);
                this.logSqlStatement(logTag, statement);
                statement.execute();

                return;
            }
            finally {
                BaseHelper.close(statement);
            }
        }

        /**
         * Start a job.
         */
        public void startJob(String logTag, Connection connection, long jobId, int requestType) throws SQLException {
            final String SQL = "{CALL start_staged_job(?, ?)}";

            CallableStatement statement = null;
            try {
                statement = connection.prepareCall(SQL);
                statement.setLong(1, jobId);
                statement.setInt(2, requestType);
                this.logSqlStatement(logTag, statement);
                statement.execute();

                this.logInfo(logTag, "Started job ID: " + Long.toString(jobId));

                return;
            }
            finally {
                BaseHelper.close(statement);
            }
        }
    }
}
