/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.github.ewbankkit.util.geo;

import static com.github.ewbankkit.util.beans.BaseData.firstElement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;

import org.apache.commons.logging.Log;

import com.github.ewbankkit.util.beans.Quadruple;
import com.github.ewbankkit.util.dbhelpers.BaseHelper;
import com.github.ewbankkit.util.log.BaseLoggable;
import com.github.ewbankkit.util.log.SimpleLoggable;

public class ZipCodeCalculator extends BaseLoggable {
	public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:59 ZipCodeCalculator.java NSI";

	private static final double DEFAULT_RADIUS_KILOMETERS = toKilometers(10D);
	private static final double KILOMETERS_PER_MILE = 1.609344D;
	private static final double RADIUS_OF_EARTH_IN_KM = 6371D;

	private final ZipCodesHelper zipCodesHelper;

    /**
     * Constructor.
     */
    public ZipCodeCalculator(String logComponent) {
        this(new SimpleLoggable(logComponent));
    }

    /**
     * Constructor.
     */
    public ZipCodeCalculator(String logComponent, boolean logSqlStatements) {
        this(new SimpleLoggable(logComponent), Boolean.valueOf(logSqlStatements));
    }

    /**
     * Constructor.
     */
    public ZipCodeCalculator(Log logger) {
        this(new SimpleLoggable(logger));
    }

    /**
     * Constructor.
     */
    public ZipCodeCalculator(BaseLoggable baseLoggable) {
        this(baseLoggable, null);
    }

    /**
     * Constructor.
     */
    private ZipCodeCalculator(BaseLoggable baseLoggable, Boolean logSqlStatements) {
        super(baseLoggable);
        zipCodesHelper = (logSqlStatements == null) ? new ZipCodesHelper(this) : new ZipCodesHelper(this, logSqlStatements.booleanValue());
    }

    /**
     * Return the city, state, latitude and longitude for the specified ZIP code.
     */
    public Quadruple<String, String, Double, Double> getCityStateLatitudeAndLongitude(String logTag, Connection connection, String zipCode) throws SQLException {
        return firstElement(getCitiesStatesLatitudesAndLongitudes(logTag, connection, Collections.singleton(zipCode)));
    }

    /**
     * Return the cities, states, latitudes and longitudes for the specified ZIP codes.
     */
    public Collection<Quadruple<String, String, Double, Double>> getCitiesStatesLatitudesAndLongitudes(String logTag, Connection connection, Collection<String> zipCodes) throws SQLException {
        return zipCodesHelper.getCitiesStatesLatitudesAndLongitudes(logTag, connection, zipCodes);
    }

    /**
     * Return all ZIP codes within the default radius of the specified latitude and longitude.
     */
    public Collection<String> getZipCodesWithinRadius(String logTag, Connection connection, double latitude, double longitude) throws SQLException {
        return getZipCodesWithinRadiusKilometers(logTag, connection, latitude, longitude, DEFAULT_RADIUS_KILOMETERS);
    }

    /**
     * Return all ZIP codes within the specified radius (in kilometers) of the specified latitude and longitude.
     */
    public Collection<String> getZipCodesWithinRadiusKilometers(String logTag, Connection connection, double latitude, double longitude, double radius) throws SQLException {
        return zipCodesHelper.getZipCodesWithinRadius(logTag, connection, latitude, longitude, radius);
    }

    /**
     * Return the closest ZIP code within the default radius of the specified latitude and longitude.
     */
    public String getClosestZipCodeWithinRadius(String logTag, Connection connection, double latitude, double longitude) throws SQLException {
        return getClosestZipCodeWithinRadiusKilometers(logTag, connection, latitude, longitude, DEFAULT_RADIUS_KILOMETERS);
    }
    /**
     * Return the closest ZIP code within the specified radius (in kilometers) of the specified latitude and longitude.
     */
    public String getClosestZipCodeWithinRadiusKilometers(String logTag, Connection connection, double latitude, double longitude, double radius) throws SQLException {
        return firstElement(getZipCodesWithinRadiusKilometers(logTag, connection, latitude, longitude, radius));
    }

	public static double toKilometers(double miles) {
	    return miles * KILOMETERS_PER_MILE;
	}

   public static int toMeters(double miles) {
        return (int)(toKilometers(miles) * 1000D);
    }

	public static int toMicroDegrees(double degrees) {
	    return (int)(degrees * 1000000D);
	}

	public static double toMiles(double kilometers) {
	    return kilometers / KILOMETERS_PER_MILE;
	}

	/**
     * DB helper.
     */
    private static class ZipCodesHelper extends BaseHelper {
        /**
         * Constructor.
         */
        public ZipCodesHelper(BaseLoggable baseLoggable) {
            super(baseLoggable);
        }

        /**
         * Constructor.
         */
        public ZipCodesHelper(BaseLoggable baseLoggable, boolean logSqlStatements) {
            super(baseLoggable, logSqlStatements);
        }

        /**
         * Return the cities, states, latitudes and longitudes for the specified ZIP codes.
         */
        public Collection<Quadruple<String, String, Double, Double>> getCitiesStatesLatitudesAndLongitudes(String logTag, Connection connection, Collection<String> zipCodes) throws SQLException {
            final String SQL =
                "SELECT city," +
                "       state," +
                "       latitude," +
                "       longitude " +
                "FROM   zip_codes " +
                "WHERE  zip_code IN (%1$s);";

            return newListFromParameters(logTag, connection, String.format(SQL, getInClauseValuesSnippet(zipCodes.size())),
                    new Factory<Quadruple<String, String, Double, Double>>() {
                        public Quadruple<String, String, Double, Double> newInstance(ResultSet resultSet) throws SQLException {
                            return Quadruple.from(
                                    resultSet.getString("city"),
                                    resultSet.getString("state"),
                                    getDoubleValue(resultSet, "latitude"),
                                    getDoubleValue(resultSet, "longitude"));
                        }},
                    new InClauseParameters(zipCodes));
        }

        /**
         * Return the ZIP codes within the specified radius (in KM) of the specified latitude and longitude.
         * The ZIP codes are returned closest first.
         */
        public Collection<String> getZipCodesWithinRadius(String logTag, Connection connection, double latitude, double longitude, double radius) throws SQLException {
            // Use the Spherical Law of Cosines to find the distance between two points on the surface of a sphere.
            final String SQL =
                "SELECT t2.zip_code AS zip_code," +
                "       t2.distance AS distance " +
                "FROM   (SELECT zip_code," +
                "               ACOS(SIN(RADIANS(latitude)) * SIN(t1.lat2) +" +
                "               COS(RADIANS(latitude)) * COS(t1.lat2) *" +
                "               COS(t1.long2 - RADIANS(longitude))) * ? AS distance" +
                "        FROM   zip_codes," +
                "               (SELECT RADIANS(?) AS lat2," +
                "                       RADIANS(?) AS long2" +
                "                FROM   dual) AS t1) AS t2 " +
                "WHERE  distance <= ? " +
                "ORDER  BY distance ASC;";

            PreparedStatement statement = null;
            ResultSet resultSet = null;
            try {
                statement = connection.prepareStatement(SQL);
                statement.setDouble(1, RADIUS_OF_EARTH_IN_KM);
                statement.setDouble(2, latitude);
                statement.setDouble(3, longitude);
                statement.setDouble(4, radius);
                logSqlStatement(logTag, statement);
                resultSet = statement.executeQuery();
                return newList(resultSet, new StringFactory("zip_code") {});
            }
            finally {
                close(statement, resultSet);
            }
        }
    }
}
