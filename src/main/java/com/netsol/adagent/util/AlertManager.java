/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util;

import static com.netsol.adagent.util.beans.Alert.ENTITY_PRODUCT;
import static com.netsol.adagent.util.beans.Alert.LANDING_PAGE_ALERT;
import static com.netsol.adagent.util.beans.Alert.MESSAGE_ALERT;
import static com.netsol.adagent.util.beans.Alert.STATUS_DELETED;
import static com.netsol.adagent.util.beans.BaseData.stringsEqual;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.netsol.adagent.util.beans.Alert;
import com.netsol.adagent.util.beans.Alert.AlertType;
import com.netsol.adagent.util.dbhelpers.BaseHelper;

public final class AlertManager {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:53:48 AlertManager.java NSI";

	private static final Log logger = LogFactory.getLog(AlertManager.class);
	private static final DbHelper dbHelper = new DbHelper(logger);

	private static final int NUMBER_OF_DAYS = 1;

	/**
	 * Constructor.
	 */
	private AlertManager() {}

    public static Long addAlert(String logTag, Connection connection, Alert alert, String updatedByUser) throws SQLException {
        // Always add message and landing page alerts.
        String alertType = alert.getType();
        if (!stringsEqual(alertType, MESSAGE_ALERT) && !stringsEqual(alertType, LANDING_PAGE_ALERT)) {
            if (dbHelper.doesSimilarAlertExist(logTag, connection, alert, NUMBER_OF_DAYS)) {
                logger.info("A similar alert exists");
                return null;
            }
        }

        return dbHelper.insertAlert(logTag, connection, alert, updatedByUser);
    }

    public static void deleteAllAlerts(String logTag, Connection connection, String prodInstId, String updatedByUser) throws SQLException {
        dbHelper.deleteAllAlerts(logTag, connection, prodInstId, null, updatedByUser);
    }

    public static void deleteAllAlerts(String logTag, Connection connection, String prodInstId, AlertType alertType, String updatedByUser) throws SQLException {
        dbHelper.deleteAllAlerts(logTag, connection, prodInstId, alertType.value, updatedByUser);
    }

    public static void updateAlertStatus(String logTag, Connection connection, long alertId, String status, String updatedByUser) throws SQLException {
        dbHelper.updateAlertStatus(logTag, connection, alertId, status, updatedByUser);
    }

    private static class DbHelper extends BaseHelper {
        /**
         * Constructor.
         */
        public DbHelper(Log logger) {
            super(logger);
        }

        public void deleteAllAlerts(String logTag, Connection connection, String prodInstId, String alertType, String updatedByUser) throws SQLException {
            final String SQL =
                "UPDATE" +
                "  alert " +
                "SET" +
                "  `status` = ?," +
                "  updated_by_user = ?," +
                "  updated_date = NOW() " +
                "WHERE" +
                "  prod_inst_id = ? AND" +
                "  `status` <> ? AND" +
                "  IF(? IS NULL, TRUE, alert_type = ?);";

            PreparedStatement statement = null;
            try {
                statement = connection.prepareStatement(SQL);
                int parameterIndex = 1;
                statement.setString(parameterIndex++, STATUS_DELETED);
                statement.setString(parameterIndex++, updatedByUser);
                statement.setString(parameterIndex++, prodInstId);
                statement.setString(parameterIndex++, STATUS_DELETED);
                statement.setString(parameterIndex++, alertType);
                statement.setString(parameterIndex++, alertType);
                logSqlStatement(logTag, statement);
                statement.executeUpdate();
            }
            finally {
                close(statement);
            }
        }

        public boolean doesSimilarAlertExist(String logTag, Connection connection, Alert alert, int numberOfDays) throws SQLException {
            final String SQL =
                "SELECT" +
                "  COUNT(*) " +
                "FROM" +
                "  alert " +
                "WHERE" +
                "  prod_inst_id = ? AND" +
                "  priority = ? AND" +
                "  alert_type = ? AND" +
                "  entity_type = ? AND" +
                "  IF(?, TRUE, entity_id = ?) AND" +
                "  created_date > DATE_SUB(CURDATE(), INTERVAL ? DAY) AND" +
                "  `status` <> ?;";

            PreparedStatement statement = null;
            ResultSet resultSet = null;
            try {
                statement = connection.prepareStatement(SQL);
                int parameterIndex = 1;
                statement.setString(parameterIndex++, alert.getProdInstId());
                statement.setInt(parameterIndex++, alert.getPriority());
                statement.setString(parameterIndex++, alert.getType());
                statement.setString(parameterIndex++, alert.getEntityType());
                statement.setBoolean(parameterIndex++, stringsEqual(alert.getEntityType(), ENTITY_PRODUCT));
                statement.setLong(parameterIndex++, alert.getEntityId());
                statement.setInt(parameterIndex++, numberOfDays);
                statement.setString(parameterIndex++, STATUS_DELETED);
                logSqlStatement(logTag, statement);
                resultSet = statement.executeQuery();
                return countGreaterThanZero(resultSet);
            }
            finally {
                close(statement, resultSet);
            }
        }

        public Long insertAlert(String logTag, Connection connection, Alert alert, String updatedByUser) throws SQLException {
            final String SQL =
                "INSERT INTO alert" +
                "  (prod_inst_id, priority, alert_type, origin_system," +
                "   `status`, entity_type, entity_id, alert_detail," +
                "   created_date, updated_by_user, updated_date) " +
                "VALUES" +
                "  (?, ?, ?, ?," +
                "   ?, ?, ?, ?," +
                "   NOW(), ?, NOW());";

            PreparedStatement statement = null;
            try {
                statement = connection.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);
                int parameterIndex = 1;
                statement.setString(parameterIndex++, alert.getProdInstId());
                statement.setInt(parameterIndex++, alert.getPriority());
                statement.setString(parameterIndex++, alert.getType());
                statement.setString(parameterIndex++, alert.getOrigin());
                statement.setString(parameterIndex++, alert.getStatus());
                statement.setString(parameterIndex++, alert.getEntityType());
                long entityId = alert.getEntityId();
                statement.setObject(parameterIndex++, (entityId < 0L) ? null : Long.valueOf(entityId));
                statement.setString(parameterIndex++, alert.getDetails());
                statement.setString(parameterIndex++, updatedByUser);
                logSqlStatement(logTag, statement);
                statement.executeUpdate();
                return getAutoIncrementId(statement);
            }
            finally {
                close(statement);
            }
        }

        public void updateAlertStatus(String logTag, Connection connection, long alertId, String status, String updatedByUser) throws SQLException {
            final String SQL =
                "UPDATE" +
                "  alert " +
                "SET" +
                "  `status` = ?," +
                "  updated_by_user = ?," +
                "  updated_date = NOW() " +
                "WHERE" +
                "  alert_id = ?;";

            PreparedStatement statement = null;
            try {
                statement = connection.prepareStatement(SQL);
                int parameterIndex = 1;
                statement.setString(parameterIndex++, status);
                statement.setString(parameterIndex++, updatedByUser);
                statement.setLong(parameterIndex++, alertId);
                logSqlStatement(logTag, statement);
                statement.executeUpdate();
            }
            finally {
                close(statement);
            }
        }
    }
}
