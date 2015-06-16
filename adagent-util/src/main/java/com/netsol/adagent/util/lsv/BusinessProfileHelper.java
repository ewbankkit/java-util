/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.lsv;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import org.apache.commons.logging.Log;

import com.netsol.adagent.util.dbhelpers.BaseHelper;

// Business profile DB helper.
public class BusinessProfileHelper extends BaseHelper {
    public final static String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:10 BusinessProfileHelper.java NSI";

    private static final String LOGO_IMAGE_URL = "https://data.netsolads.com/services/PreviewImage?prod_inst_id=%pid%&business_profile_logo_id=%logoid%";

    /**
     * Constructor.
     */
    public BusinessProfileHelper(String logComponent) {
        super(logComponent);
    }

    /**
     * Constructor.
     */
    public BusinessProfileHelper(Log logger) {
        super(logger);
    }

    /**
     * Delete the business profiles for the specified product instance ID.
     */
    public void deleteBusinessProfile(String logTag, Connection connection, String prodInstId) throws SQLException {
       final String SQL = "DELETE FROM business_profile WHERE prod_inst_id = ?;";
       deleteForProdInstId(logTag, connection, prodInstId, SQL);
    }

    /**
     * Return the business profiles for the specified product instance ID.
     */
    public BusinessProfile getBusinessProfile(String logTag, Connection connection, String prodInstId) throws SQLException {
        final String SQL =
            "SELECT" +
            "  p.business_profile_id," +
            "  p.prod_inst_id," +
            "  p.business_name," +
            "  p.business_description," +
            "  p.street_addr_1," +
            "  p.street_addr_2," +
            "  p.city," +
            "  p.state," +
            "  p.zip," +
            "  p.country_code," +
            "  p.phone," +
            "  p.site_url," +
            "  p.keywords," +
            "  p.email," +
            "  CONVERT(p.categories, SIGNED INTEGER) AS category_id," +
            "  p.specialities," +
            "  p.brands," +
            "  p.payment_methods," +
            "  p.hours_of_operation," +
            "  p.alternate_phone," +
            "  p.fax," +
            "  p.first_name," +
            "  p.last_name," +
            "  p.business_headline," +
            "  p.service_area," +
            "  p.timezone," +
            "  p.discounts," +
            "  p.additional_info,"+
            "  p.languages_spoken," +
            "  p.year_established," +
            "  IF(EXISTS(SELECT * FROM business_profile_logo AS l WHERE l.prod_inst_id = p.prod_inst_id)," +
            "     REPLACE(REPLACE(?, '%pid%', p.prod_inst_id), '%logoid%', l.business_profile_logo_id)," +
            "     NULL) AS logo_image, " +
            "  p.last_publish_date " +
            "FROM" +
            "  business_profile AS p " +
            "LEFT OUTER JOIN business_profile_logo AS l" +
            "  ON l.prod_inst_id = p.prod_inst_id " +
            "WHERE" +
            "  p.prod_inst_id = ?;";

        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(SQL);
            statement.setString(1, LOGO_IMAGE_URL);
            statement.setString(2, prodInstId);
            logSqlStatement(logTag, statement);
            resultSet = statement.executeQuery();
            return firstValue(resultSet, BusinessProfileFactory.INSTANCE);
        }
        finally {
            close(statement, resultSet);
        }
    }

    /**
     * Insert a business profile.
     */
    public void insertBusinessProfile(String logTag, Connection connection, BusinessProfile businessProfile) throws SQLException {
        final String SQL =
            "INSERT INTO business_profile(" +
            "  prod_inst_id," +
            "  business_name," +
            "  business_description," +
            "  street_addr_1," +
            "  street_addr_2," +
            "  city," +
            "  state," +
            "  zip," +
            "  country_code," +
            "  phone," +
            "  site_url," +
            "  keywords," +
            "  email," +
            "  categories," +
            "  specialities," +
            "  brands," +
            "  payment_methods," +
            "  hours_of_operation," +
            "  first_name," +
            "  last_name," +
            "  fax," +
            "  alternate_phone," +
            "  business_headline," +
            "  service_area," +
            "  timezone," +
            "  discounts," +
            "  additional_info," +
            "  languages_spoken," +
            "  year_established," +
            "  last_publish_date " +
            ") " +
            "VALUES (" +
            "  ?," +
            "  ?," +
            "  ?," +
            "  ?," +
            "  ?," +
            "  ?," +
            "  ?," +
            "  ?," +
            "  ?," +
            "  ?," +
            "  ?," +
            "  ?," +
            "  ?," +
            "  ?," +
            "  ?," +
            "  ?," +
            "  ?," +
            "  ?," +
            "  ?," +
            "  ?," +
            "  ?," +
            "  ?," +
            "  ?," +
            "  ?," +
            "  ?," +
            "  ?," +
            "  ?," +
            "  ?," +
            "  ?," +
            "  IF(?, NOW(), DEFAULT(last_publish_date)) " +
            ");";

        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(SQL);
            statement.setString(1, businessProfile.getProdInstId());
            statement.setString(2, businessProfile.getBusinessName());
            statement.setString(3, businessProfile.getBusinessDescription());
            statement.setString(4, businessProfile.getStreetAddress1());
            statement.setString(5, businessProfile.getStreetAddress2());
            statement.setString(6, businessProfile.getCity());
            statement.setString(7, businessProfile.getState());
            statement.setString(8, businessProfile.getZip());
            statement.setString(9, businessProfile.getCountryCode());
            statement.setString(10, businessProfile.getPhone());
            statement.setString(11, businessProfile.getSiteUrl());
            statement.setString(12, businessProfile.getKeywords());
            statement.setString(13, businessProfile.getEmail());
            statement.setString(14, Integer.toString(businessProfile.getCategoryId()));
            statement.setString(15, businessProfile.getSpecialities());
            statement.setString(16, businessProfile.getBrands());
            statement.setString(17, businessProfile.getPaymentMethods());
            statement.setString(18, businessProfile.getHoursOfOperation());
            statement.setString(19, businessProfile.getFirstName());
            statement.setString(20, businessProfile.getLastName());
            statement.setString(21, businessProfile.getFax());
            statement.setString(22, businessProfile.getAlternatePhone());
            statement.setString(23, businessProfile.getBusinessHeadline());
            statement.setString(24, businessProfile.getServiceArea());
            statement.setString(25, businessProfile.getTimeZone());
            statement.setString(26, businessProfile.getDiscounts());
            statement.setString(27, businessProfile.getAdditionalInfo());
            statement.setString(28, businessProfile.getLanguagesSpoken());
            statement.setString(29, businessProfile.getYearEstablished());
            statement.setBoolean(30, businessProfile.isPublish());
            logSqlStatement(logTag, statement);
            statement.executeUpdate();
        }
        finally {
            close(statement);
        }
    }

    /**
     * Update a business profile.
     */
    public void updateBusinessProfile(String logTag, Connection connection, BusinessProfile businessProfile) throws SQLException {
        final String SQL =
            "UPDATE" +
            "  business_profile " +
            "SET" +
            "  prod_inst_id = ?," +
            "  business_name = ?," +
            "  business_description = ?," +
            "  street_addr_1 = ?," +
            "  street_addr_2 = ?," +
            "  city = ?," +
            "  state = ?," +
            "  zip = ?," +
            "  country_code = ?," +
            "  phone = ?," +
            "  site_url = ?," +
            "  keywords = ?," +
            "  email = ?," +
            "  categories = ?," +
            "  specialities = ?," +
            "  brands = ?," +
            "  payment_methods = ?," +
            "  hours_of_operation = ?," +
            "  first_name = ?," +
            "  last_name = ?," +
            "  fax = ?," +
            "  alternate_phone = ?," +
            "  business_headline = ?," +
            "  service_area = ?," +
            "  timezone = ?," +
            "  discounts = ?," +
            "  additional_info = ?," +
            "  languages_spoken = ?," +
            "  year_established = ?," +
            "  last_publish_date = IF(?, NOW(), last_publish_date) " +
            "WHERE" +
            "  prod_inst_id = ?;";

        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(SQL);
            statement.setString(1, businessProfile.getProdInstId());
            statement.setString(2, businessProfile.getBusinessName());
            statement.setString(3, businessProfile.getBusinessDescription());
            statement.setString(4, businessProfile.getStreetAddress1());
            statement.setString(5, businessProfile.getStreetAddress2());
            statement.setString(6, businessProfile.getCity());
            statement.setString(7, businessProfile.getState());
            statement.setString(8, businessProfile.getZip());
            statement.setString(9, businessProfile.getCountryCode());
            statement.setString(10, businessProfile.getPhone());
            statement.setString(11, businessProfile.getSiteUrl());
            statement.setString(12, businessProfile.getKeywords());
            statement.setString(13, businessProfile.getEmail());
            statement.setString(14, Integer.toString(businessProfile.getCategoryId()));
            statement.setString(15, businessProfile.getSpecialities());
            statement.setString(16, businessProfile.getBrands());
            statement.setString(17, businessProfile.getPaymentMethods());
            statement.setString(18, businessProfile.getHoursOfOperation());
            statement.setString(19, businessProfile.getFirstName());
            statement.setString(20, businessProfile.getLastName());
            statement.setString(21, businessProfile.getFax());
            statement.setString(22, businessProfile.getAlternatePhone());
            statement.setString(23, businessProfile.getBusinessHeadline());
            statement.setString(24, businessProfile.getServiceArea());
            statement.setString(25, businessProfile.getTimeZone());
            statement.setString(26, businessProfile.getDiscounts());
            statement.setString(27, businessProfile.getAdditionalInfo());
            statement.setString(28, businessProfile.getLanguagesSpoken());
            statement.setString(29, businessProfile.getYearEstablished());
            statement.setBoolean(30, businessProfile.isPublish());
            statement.setString(31, businessProfile.getProdInstId());
            logSqlStatement(logTag, statement);
            statement.executeUpdate();
        }
        finally {
            close(statement);
        }
    }

    /**
     * Get the first publish date for the business profile.
     */
    public Date getFirstPublishDate(Connection connection, String prodInstId) throws SQLException {
        final String SQL = "SELECT last_publish_date FROM business_profile_hist WHERE prod_inst_id = ? AND " +
        		"last_publish_date IS NOT NULL ORDER BY business_profile_hist_id ASC LIMIT 1;";
        return singleValueFromProdInstId(null, connection, prodInstId, SQL, new DateFactory("last_publish_date") {});
    }

    /**
     * Get the last publish date for the business profile.
     */
    public Date getLastPublishDate(Connection connection, String prodInstId) throws SQLException {
        final String SQL = "SELECT last_publish_date FROM business_profile WHERE prod_inst_id = ?;";
        return singleValueFromProdInstId(null, connection, prodInstId, SQL, new DateFactory("last_publish_date") {});
    }

    /**
     * Factory class used to business profile objects from a result set.
     */
    private static class BusinessProfileFactory implements Factory<BusinessProfile> {
        public static final BusinessProfileFactory INSTANCE = new BusinessProfileFactory();

        /**
         * Constructor.
         */
        private BusinessProfileFactory() {}

        /**
         * Return a new instance with values from the result set.
         */
        public BusinessProfile newInstance(ResultSet resultSet) throws SQLException {
            BusinessProfile businessProfile = new BusinessProfile();
            businessProfile.setBusinessProfileId(resultSet.getInt("business_profile_id"));
            businessProfile.setProdInstId(resultSet.getString("prod_inst_id"));
            businessProfile.setBusinessName(resultSet.getString("business_name"));
            businessProfile.setBusinessDescription(resultSet.getString("business_description"));
            businessProfile.setStreetAddress1(resultSet.getString("street_addr_1"));
            businessProfile.setStreetAddress2(resultSet.getString("street_addr_2"));
            businessProfile.setCity(resultSet.getString("city"));
            businessProfile.setState(resultSet.getString("state"));
            businessProfile.setZip(resultSet.getString("zip"));
            businessProfile.setCountryCode(resultSet.getString("country_code"));
            businessProfile.setPhone(resultSet.getString("phone"));
            businessProfile.setSiteUrl(resultSet.getString("site_url"));
            businessProfile.setKeywords(resultSet.getString("keywords"));
            businessProfile.setEmail(resultSet.getString("email"));
            businessProfile.setCategoryId(resultSet.getInt("category_id"));
            businessProfile.setSpecialities(resultSet.getString("specialities"));
            businessProfile.setBrands(resultSet.getString("brands"));
            businessProfile.setPaymentMethods(resultSet.getString("payment_methods"));
            businessProfile.setHoursOfOperation(resultSet.getString("hours_of_operation"));
            businessProfile.setAlternatePhone(resultSet.getString("alternate_phone"));
            businessProfile.setFax(resultSet.getString("fax"));
            businessProfile.setFirstName(resultSet.getString("first_name"));
            businessProfile.setLastName(resultSet.getString("last_name"));
            businessProfile.setBusinessHeadline(resultSet.getString("business_headline"));
            businessProfile.setServiceArea(resultSet.getString("service_area"));
            businessProfile.setTimeZone(resultSet.getString("timezone"));
            businessProfile.setDiscounts(resultSet.getString("discounts"));
            businessProfile.setAdditionalInfo(resultSet.getString("additional_info"));
            businessProfile.setLanguagesSpoken(resultSet.getString("languages_spoken"));
            businessProfile.setYearEstablished(resultSet.getString("year_established"));
            businessProfile.setLogoImage(resultSet.getString("logo_image"));
            businessProfile.setLastPublishDate(resultSet.getTimestamp("last_publish_date"));
            return businessProfile;
        }
    }
}
