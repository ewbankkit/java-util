/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.dbhelpers;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import org.apache.commons.logging.Log;

import com.netsol.adagent.util.beans.Pair;

/**
 * DB helpers for product type default configuration.
 */
public class ProductTypeDefaultConfigHelper extends BaseHelper {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:54 ProductTypeDefaultConfigHelper.java NSI";

    /**
     * Constructor.
     */
    public ProductTypeDefaultConfigHelper(String logComponent) {
        super(logComponent);
    }

    /**
     * Constructor.
     */
    public ProductTypeDefaultConfigHelper(Log logger) {
        super(logger);
    }

    public Map<String, String> getDefaultConfig(String logTag, Connection connection, long prodId) throws SQLException {
        final String SQL =
            "SELECT `name`, `value` FROM product_type_default_config WHERE prod_id = ?;";

        return newMapFromSingleParameter(logTag, connection, Long.valueOf(prodId), SQL, new Factory<Pair<String, String>>() {
            public Pair<String, String> newInstance(ResultSet resultSet) throws SQLException {
                return Pair.from(resultSet.getString("name"), resultSet.getString("value"));
            }});
    }
}
