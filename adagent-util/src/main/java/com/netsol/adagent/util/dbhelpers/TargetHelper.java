/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.dbhelpers;

import static com.netsol.adagent.util.beans.BaseData.arrayIsEmpty;
import static com.netsol.adagent.util.beans.BaseData.coalesce;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.logging.Log;

import com.netsol.adagent.util.beans.Target;
import com.netsol.adagent.util.beans.TargetVendor;
import com.netsol.adagent.util.log.BaseLoggable;

public class TargetHelper extends BaseHelper {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:55 TargetHelper.java NSI";

    /**
     * Constructor.
     */
    public TargetHelper(String logComponent) {
        super(logComponent);
    }

    /**
     * Constructor.
     */
    public TargetHelper(String logComponent, boolean logSqlStatements) {
        super(logComponent, logSqlStatements);
    }

    /**
     * Constructor.
     */
    public TargetHelper(Log logger) {
        super(logger);
    }

    /**
     * Constructor.
     */
    public TargetHelper(BaseLoggable baseLoggable) {
        super(baseLoggable);
    }

    /**
     * Return any target for the specified target ID.
     */
    public Target getTarget(String logTag, Connection connection, String prodInstId, long targetId) throws SQLException {
        final String SQL =
            TargetFactory.SQL_SELECT_EXPRESSION +
            "WHERE target_id = ? AND IF(? IS NULL, TRUE, prod_inst_id = ?);";

        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(SQL);
            int parameterIndex = 1;
            statement.setLong(parameterIndex++, targetId);
            statement.setString(parameterIndex++, prodInstId);
            statement.setString(parameterIndex++, prodInstId);
            logSqlStatement(logTag, statement);
            resultSet = statement.executeQuery();
            Target target = singleValue(resultSet, TargetFactory.INSTANCE);
            if (target != null) {
                target.setTargetVendors(getTargetVendors(logTag, connection, prodInstId, targetId));
            }
            return target;
        }
        finally {
            close(statement, resultSet);
        }
    }

    /**
     * Return any target for the specified target ID.
     */
    public Target getCampaignTarget(String logTag, Connection connection, String prodInstId, long nsCampaignId) throws SQLException {
        final String SQL =
            TargetFactory.SQL_SELECT_BY_CAMPAIGN;
           

        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(SQL);
            int parameterIndex = 1;
            statement.setString(parameterIndex++, prodInstId);
            statement.setLong(parameterIndex++, nsCampaignId);            
            
            logSqlStatement(logTag, statement);
            resultSet = statement.executeQuery();
            Target target = singleValue(resultSet, TargetFactory.INSTANCE);
            if (target != null) {
                target.setTargetVendors(getTargetVendors(logTag, connection, prodInstId, target.getTargetId()));
            }
            return target;
        }
        finally {
            close(statement, resultSet);
        }
    }
    
    /**
     * Return all target vendors for the specified target ID.
     */
    public TargetVendor[] getTargetVendors(String logTag, Connection connection, String prodInstId, long targetId) throws SQLException {
        final String SQL =
            TargetVendorFactory.SQL_SELECT_EXPRESSION +
            "WHERE target_id = ? AND IF(? IS NULL, TRUE, prod_inst_id = ?);";

        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(SQL);
            int parameterIndex = 1;
            statement.setLong(parameterIndex++, targetId);
            statement.setString(parameterIndex++, prodInstId);
            statement.setString(parameterIndex++, prodInstId);
            logSqlStatement(logTag, statement);
            resultSet = statement.executeQuery();
            List<TargetVendor> targetList = newList(resultSet, TargetVendorFactory.INSTANCE);
            return targetList.toArray(new TargetVendor[targetList.size()]);
        }
        finally {
            close(statement, resultSet);
        }
    }

    /**
     * Return any targets for the specified product instance ID.
     */
    public List<Target> getTargetsExcludingStatuses(String logTag, Connection connection, String prodInstId, Target.Status... excludedTargetStatuses) throws SQLException {
        final String SQL =
            TargetFactory.SQL_SELECT_EXPRESSION +
            "WHERE prod_inst_id = ? AND IF(?, TRUE, status NOT IN (" + getInClauseValuesSnippet(excludedTargetStatuses.length) + "));";

        excludedTargetStatuses = coalesce(excludedTargetStatuses, new Target.Status[0]);

        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(SQL);
            int parameterIndex = 1;
            statement.setString(parameterIndex++, prodInstId);
            statement.setBoolean(parameterIndex++, arrayIsEmpty(excludedTargetStatuses));
            parameterIndex = setInClauseParameters(statement, parameterIndex, excludedTargetStatuses);
            logSqlStatement(logTag, statement);
            resultSet = statement.executeQuery();
            List<Target> targetList = newList(resultSet, TargetFactory.INSTANCE);
            for (Target target : targetList) {
                target.setTargetVendors(getTargetVendors(logTag, connection, prodInstId, target.getTargetId()));
            }
            return targetList;
        }
        finally {
            close(statement, resultSet);
        }
    }

    /** 
     * Return targets that are valid for a given cycle date ranage. Targets are valid if there is any date overlap.
     */
    public List<Target> getTargetsValidForCycleDates(String logTag, Connection connection, String prodInstId, Calendar startDate, Calendar expirationDate) throws SQLException {
    	List<Target> targetList = getTargetsExcludingStatuses(logTag, connection, prodInstId, new Target.Status[] { Target.Status.DELETED });
    	// The above list is unmodifiable, so I have to build a new one.
    	List<Target> targetArrayList = new ArrayList<Target>(); 
    	if (targetList != null && !targetList.isEmpty()) {
    		for (Target target : targetList) {
    			if (target.doesTargetDateRangeOverlapCurrentCycle(startDate, expirationDate)) {
    				targetArrayList.add(target);
    			}
    		}
    	}
    	return targetArrayList;
    }
    
    /**
     * Insert a target.
     */
    public void insertTarget(String logTag, Connection connection, Target target) throws SQLException {
        final String TARGET_SQL =
            "INSERT INTO target" +
            "  (prod_inst_id, market_geography_id, market_sub_category_id, name, status, target_type, start_date, end_date, budget, seo_budget," +
            "   margin, spend_aggressiveness, fulfillment_id, crm_id, created_date, updated_date, updated_by_user, updated_by_system) " +
            "VALUES" +
            "  (?, ?, ?, ?, ?, ?, ?, ?, ?, ?," +
            "   ?, ?, ?, ?, NOW(), NOW(), ?, ?);";
        final String TARGET_VENDOR_SQL = "INSERT INTO target_vendor" +
            " (prod_inst_id, target_id, vendor_id, budget, spend_aggressiveness, external_id, created_date, updated_date," +
            " updated_by_user, updated_by_system)" +
            "VALUES" +
            " (?, ?, ?, ?, ?, ?, NOW(), NOW(), ?, ?);";

        PreparedStatement statement = null, statement2 = null;
        try {
            statement = connection.prepareStatement(TARGET_SQL, Statement.RETURN_GENERATED_KEYS);
            int parameterIndex = 1;
            statement.setString(parameterIndex++, target.getProdInstId());
            statement.setLong(parameterIndex++, target.getMarketGeographyId());
            statement.setLong(parameterIndex++, target.getMarketSubCategoryId());
            statement.setString(parameterIndex++, target.getName());
            statement.setString(parameterIndex++, target.getStatus().toString());
            statement.setString(parameterIndex++, target.getTargetType());
            statement.setDate(parameterIndex++, toSqlDate(target.getStartDate()));
            statement.setDate(parameterIndex++, toSqlDate(target.getEndDate()));
            statement.setDouble(parameterIndex++, target.getBudget());
            statement.setDouble(parameterIndex++, target.getSeoBudget());
            statement.setFloat(parameterIndex++, target.getMargin());
            statement.setObject(parameterIndex++, target.getSpendAggressiveness());
            statement.setObject(parameterIndex++, target.getFulfillmentId());
            statement.setString(parameterIndex++, target.getCrmId());
            statement.setString(parameterIndex++, target.getUpdatedByUser());
            statement.setString(parameterIndex++, target.getUpdatedBySystem());

            logSqlStatement(logTag, statement);
            statement.executeUpdate();
            Long targetId = getAutoIncrementId(statement);
            if (targetId != null) {
                target.setTargetId(targetId.longValue());
            }

            if (target.getTargetVendors() != null) {
                statement2 = connection.prepareStatement(TARGET_VENDOR_SQL);
                for (TargetVendor targetVendor : target.getTargetVendors()) {
                    targetVendor.setTargetId(target.getTargetId());
                    targetVendor.setProdInstId(target.getProdInstId());
                    targetVendor.setUpdatedBySystem(target.getUpdatedBySystem());
                    targetVendor.setUpdatedByUser(target.getUpdatedByUser());

                    int index = 1;
                    statement2.setString(index++, targetVendor.getProdInstId());
                    statement2.setLong(index++, targetVendor.getTargetId());
                    statement2.setLong(index++, targetVendor.getVendorId());
                    statement2.setDouble(index++, targetVendor.getBudget());
                    statement2.setObject(index++, targetVendor.getSpendAggressiveness());
                    statement2.setString(index++, targetVendor.getExternalId());
                    statement2.setString(index++, targetVendor.getUpdatedByUser());
                    statement2.setString(index++, targetVendor.getUpdatedBySystem());
                    logSqlStatement(logTag, statement2);
                    statement2.executeUpdate();
                }
            }
        }
        finally {
            close(statement);
            close(statement2);
        }
    }

    /**
     * Update the specified target.
     */
    public void updateTarget(String logTag, Connection connection, Target target) throws SQLException {
        final String TARGET_SQL =
            "UPDATE" +
            "  target " +
            "SET" +
            "  %1$s," +
            "  updated_date = NOW() " +
            "WHERE" +
            "  target_id = ? AND" +
            "  prod_inst_id = ?;";

        final String TARGET_VENDOR_SQL = "INSERT INTO target_vendor" +
            " (prod_inst_id, target_id, vendor_id, budget, spend_aggressiveness, external_id, created_date, updated_date," +
            " updated_by_user, updated_by_system) " +
            "VALUES" +
            " (?, ?, ?, ?, ?, ?, NOW(), NOW(), ?, ?) " +
            "ON DUPLICATE KEY UPDATE %1$s, updated_date = NOW();";

        PreparedStatement statement = null, statement2 = null;
        try {
            statement = connection.prepareStatement(String.format(TARGET_SQL, target.getUpdateValuesSnippet()));
            int parameterIndex = target.setUpdateParameters(statement, 1);
            statement.setLong(parameterIndex++, target.getTargetId());
            statement.setString(parameterIndex++, target.getProdInstId());
            logSqlStatement(logTag, statement);
            statement.executeUpdate();

            if (target.getTargetVendors() != null) {

                for (TargetVendor targetVendor : target.getTargetVendors()) {
                    targetVendor.setTargetId(target.getTargetId());
                    targetVendor.setProdInstId(target.getProdInstId());
                    targetVendor.setUpdatedBySystem(target.getUpdatedBySystem());
                    targetVendor.setUpdatedByUser(target.getUpdatedByUser());

                    try{
                        statement2 = connection.prepareStatement(String.format(TARGET_VENDOR_SQL, targetVendor.getUpdateValuesSnippet()));

                        int index = 1;
                        statement2.setString(index++, targetVendor.getProdInstId());
                        statement2.setLong(index++, targetVendor.getTargetId());
                        statement2.setLong(index++, targetVendor.getVendorId());
                        statement2.setDouble(index++, targetVendor.getBudget());
                        statement2.setObject(index++, targetVendor.getSpendAggressiveness());
                        statement2.setString(index++, targetVendor.getExternalId());
                        statement2.setString(index++, targetVendor.getUpdatedByUser());
                        statement2.setString(index++, targetVendor.getUpdatedBySystem());

                        index = targetVendor.setUpdateParameters(statement2, index);
                        logSqlStatement(logTag, statement2);
                        statement2.executeUpdate();
                    }finally{
                        close(statement2);
                    }
                }
            }
        }
        finally {
            close(statement);
        }
    }

    /**
     * Factory class used to create Target objects from a result set.
     */
    private static class TargetFactory implements Factory<Target> {
        public static final TargetFactory INSTANCE = new TargetFactory();

        public static String SQL_SELECT_EXPRESSION =
            "SELECT" +
            "  t.target_id," +
            "  t.prod_inst_id," +
            "  mc.market_category_id," +
            "  mc.name market_category_name," +
            "  msc.market_sub_category_id," +
            "  msc.name market_sub_category_name," +
            "  mg.market_geography_id," +
            "  mg.name market_geography_name," +
            "  t.name," +
            "  t.status," +
            "  t.target_type," +
            "  t.start_date," +
            "  t.end_date," +
            "  t.budget," +
            "  t.seo_budget," +
            "  t.margin," +
            "  t.spend_aggressiveness," +
            "  t.crm_id," +
            "  t.fulfillment_id " +
            "FROM" +
            "  target AS t" +
            "  INNER JOIN market_sub_category AS msc ON msc.market_sub_category_id = t.market_sub_category_id" +
            "  INNER JOIN market_category AS mc ON mc.market_category_id = msc.market_category_id" +
            "  INNER JOIN market_geography AS mg ON mg.market_geography_id = t.market_geography_id ";
        
        public static String SQL_SELECT_BY_CAMPAIGN =
                SQL_SELECT_EXPRESSION + 
                " join ns_campaign nc on nc.target_id = t.target_id " +
                " where t.prod_inst_id = ?  and nc.ns_campaign_id = ?";
        

        /**
         * Constructor.
         */
        private TargetFactory() {}


        /**
         * Return a new instance with values from the result set.
         */
        public Target newInstance(ResultSet resultSet) throws SQLException {
            Target target= new Target();
            target.setBudget(resultSet.getDouble("budget"));
            target.setCrmId(resultSet.getString("crm_id"));
            target.setFulfillmentId(getLongValue(resultSet, "fulfillment_id"));
            target.setMargin(resultSet.getFloat("margin"));
            target.setMarketGeographyId(resultSet.getLong("market_geography_id"));
            target.setMarketGeographyName(resultSet.getString("market_geography_name"));
            target.setMarketSubCategoryId(resultSet.getLong("market_sub_category_id"));
            target.setMarketSubCategoryName(resultSet.getString("market_sub_category_name"));
            target.setMarketCategoryId(resultSet.getLong("market_category_id"));
            target.setMarketCategoryName(resultSet.getString("market_category_name"));
            target.setName(resultSet.getString("name"));
            target.setProdInstId(resultSet.getString("prod_inst_id"));
            target.setSeoBudget(resultSet.getDouble("seo_budget"));
            target.setSpendAggressiveness(getFloatValue(resultSet, "spend_aggressiveness"));
            target.setStartDate(resultSet.getDate("start_date"));
            target.setEndDate(resultSet.getDate("end_date"));
            target.setStatus(getEnumValue(resultSet, "status", Target.Status.class));
            target.setTargetId(resultSet.getLong("target_id"));
            target.setTargetType(resultSet.getString("target_type"));
            return target;
        }
    }

    /**
     * Factory class used to create TargetVendor objects from a result set.
     */
    private static class TargetVendorFactory implements Factory<TargetVendor> {
        public static final TargetVendorFactory INSTANCE = new TargetVendorFactory();

        public static String SQL_SELECT_EXPRESSION =
            "SELECT" +
            "  prod_inst_id," +
            "  target_id," +
            "  vendor_id," +
            "  budget," +
            "  spend_aggressiveness," +
            "  external_id " +
            "FROM" +
            "  target_vendor ";

        /**
         * Constructor.
         */
        private TargetVendorFactory() {}

        /**
         * Return a new instance with values from the result set.
         */
        public TargetVendor newInstance(ResultSet resultSet) throws SQLException {
            TargetVendor targetVendor= new TargetVendor();
            targetVendor.setProdInstId(resultSet.getString("prod_inst_id"));
            targetVendor.setTargetId(resultSet.getLong("target_id"));
            targetVendor.setVendorId(resultSet.getLong("vendor_id"));
            targetVendor.setBudget(resultSet.getDouble("budget"));
            targetVendor.setSpendAggressiveness(getFloatValue(resultSet, "spend_aggressiveness"));
            targetVendor.setExternalId(resultSet.getString("external_id"));
            return targetVendor;
        }
    }
}
