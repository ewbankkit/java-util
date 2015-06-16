/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.dbhelpers;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.netsol.adagent.util.beans.VendorTimeZone;

public class VendorTimeZoneHelper extends BaseHelper {
    public final static String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:57 VendorTimeZoneHelper.java NSI";

    /**
     * Constructor.
     */
    public VendorTimeZoneHelper(String logComponent) {
        super(logComponent);
    }

    public VendorTimeZone getSingleVendorTimeZoneByVendorId(String logTag, Connection connection, int vendorId) throws SQLException {
        final String SQL =
            VendorTimeZoneFactory.SQL_SELECT_EXPRESSION + "WHERE vtz.vendor_id = ?;";

        return singleValueFromParameters(logTag, connection, SQL, VendorTimeZoneFactory.INSTANCE, vendorId);
    }

    public VendorTimeZone getVendorTimeZoneByVendorIdAndProdInstId(String logTag, Connection connection, int vendorId, String prodInstId) throws SQLException {
        final String SQL =
            VendorTimeZoneFactory.SQL_SELECT_EXPRESSION +
            "INNER JOIN vendor_credential AS vc ON vc.vendor_time_zone_id = vtz.vendor_time_zone_id " +
            "WHERE vtz.vendor_id = ? AND vc.prod_inst_id = ?;";

        return singleValueFromParameters(logTag, connection, SQL, VendorTimeZoneFactory.INSTANCE, vendorId, prodInstId);
    }

    public VendorTimeZone getVendorTimeZoneByVendorIdAndName(String logTag, Connection connection, int vendorId, String name) throws SQLException {
        final String SQL =
            VendorTimeZoneFactory.SQL_SELECT_EXPRESSION + "WHERE vtz.vendor_id = ? AND vtz.`name` = ?;";

        return singleValueFromParameters(logTag, connection, SQL, VendorTimeZoneFactory.INSTANCE, vendorId, name);
    }

    public VendorTimeZone getVendorTimeZoneByVendorTimeZoneId(String logTag, Connection connection, Long vendorTimeZoneId) throws SQLException {
        final String SQL =
            VendorTimeZoneFactory.SQL_SELECT_EXPRESSION + "WHERE vtz.vendor_time_zone_id = ?;";

        return singleValueFromSingleParameter(logTag, connection, vendorTimeZoneId, SQL, VendorTimeZoneFactory.INSTANCE);
    }

    /**
     * Factory class used to create vendor time zone objects from a result set.
     */
    private static class VendorTimeZoneFactory implements Factory<VendorTimeZone> {
        public static final VendorTimeZoneFactory INSTANCE = new VendorTimeZoneFactory();

        public static String SQL_SELECT_EXPRESSION =
            "SELECT" +
            "  vtz.vendor_time_zone_id," +
            "  vtz.vendor_id," +
            "  vtz.`name`," +
            "  vtz.java_name " +
            "FROM" +
            "  vendor_time_zone AS vtz ";

        /**
         * Constructor.
         */
        private VendorTimeZoneFactory() {}

        /**
         * Return a new instance with values from the result set.
         */
        public VendorTimeZone newInstance(ResultSet resultSet) throws SQLException {
            VendorTimeZone vendorTimeZone = new VendorTimeZone();
            vendorTimeZone.setJavaName(resultSet.getString("java_name"));
            vendorTimeZone.setName(resultSet.getString("name"));
            vendorTimeZone.setVendorId(resultSet.getInt("vendor_id"));
            vendorTimeZone.setVendorTimeZoneId(resultSet.getLong("vendor_time_zone_id"));
            return vendorTimeZone;
        }
    }
}
