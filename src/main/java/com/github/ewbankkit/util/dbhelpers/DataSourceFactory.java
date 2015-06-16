/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.github.ewbankkit.util.dbhelpers;

import javax.sql.DataSource;

/**
 * Interface implemented by data source factories.
 */
public interface DataSourceFactory {
    public final static String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:43 DataSourceFactory.java NSI";

    /**
     * Return a new data source instance.
     */
    public abstract DataSource newDataSource(String url);
}
