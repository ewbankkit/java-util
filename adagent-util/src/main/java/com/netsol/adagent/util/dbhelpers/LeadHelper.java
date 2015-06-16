/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.dbhelpers;

/**
 * Base class for lead DB helpers.
 */
public abstract class LeadHelper extends BaseHelper {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:52 LeadHelper.java NSI";

    /**
     * Constructor.
     */
    protected LeadHelper(String logComponent) {
        super(logComponent);
    }

    /**
     * Constructor.
     */
    protected LeadHelper(String logComponent, boolean logSqlStatements) {
        super(logComponent, logSqlStatements);
    }

    /**
     * Factory class used to create a lead ID from a result set.
     */
    protected static class LeadIdFactory extends LongFactory {
        public static final LeadIdFactory INSTANCE = new LeadIdFactory();

        /**
         * Constructor.
         */
        private LeadIdFactory() {
            super("lead_id");
        }
    }
}
