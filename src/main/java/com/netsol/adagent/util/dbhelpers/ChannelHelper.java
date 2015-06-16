/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.dbhelpers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.netsol.adagent.util.codes.PartnerCode;

/**
 * DB helpers for channels.
 */
public class ChannelHelper extends BaseHelper {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:42 ChannelHelper.java NSI";
    
    /**
     * Constructor.
     */
    public ChannelHelper(String logComponent) {
        super(logComponent);
        
        return;
    }
    
    /**
     * Return any channel ID corresponding to the specified partner code. 
     */
    public Long getChannelId(String logTag, Connection connection, long partnerCode) throws SQLException {
        final String GET_CHANNEL_ID_SQL =
            "SELECT" +
            "  channel_id " +
            "FROM" +
            "  channel " +
            "WHERE" +
            "  partner_code = ?;";
        
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            statement = connection.prepareStatement(GET_CHANNEL_ID_SQL);
            statement.setLong(1, partnerCode);
            this.logSqlStatement(logTag, statement);
            resultSet = statement.executeQuery();
            return BaseHelper.singleValue(resultSet, ChannelIdFactory.INSTANCE);
        }
        finally {
            BaseHelper.close(statement, resultSet);
        }
    }
    
    /**
     * Factory class used to create channel IDs from a result set. 
     */
    private static class ChannelIdFactory extends LongIdFactory {
        public static final ChannelIdFactory INSTANCE = new ChannelIdFactory();
        
        /**
         * Constructor.
         */
        private ChannelIdFactory() {
            super("channel_id");
            
            return;
        }
    }
    
    // Test harness.
    public static void main(String[] args) {
        try {
            Connection connection = null;
            try {
                connection = BaseHelper.createDevGdbConnection();
                ChannelHelper channelHelper = new ChannelHelper(null);
                System.out.println(String.valueOf(channelHelper.getChannelId(null, connection, PartnerCode.NETSOL)));
            }
            finally {
                BaseHelper.close(connection);
            }
        }
        catch (Throwable t) {
            t.printStackTrace();
        }
        
        return;
    }
}
