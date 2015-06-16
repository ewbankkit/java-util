/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.dbhelpers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.apache.commons.logging.Log;


import com.netsol.adagent.util.beans.CallTargetNumber;
import com.netsol.adagent.util.beans.CallTrackingNumber;
import com.netsol.adagent.util.beans.Pair;

/**
 * DB helpers for call tracking numbers.
 */
public class CallTrackingNumberHelper extends BaseHelper {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:42 CallTrackingNumberHelper.java NSI";

  
    public CallTrackingNumberHelper(String logComponent) {
        super(logComponent);
    }   
    public CallTrackingNumberHelper(Log logger) {
        super(logger);
    }



	/**
	 * Query all call tracking numbers from DB, including the
	 * target, vanity and tracking numbers, ad group id, and CallSource campaign
	 * id.
	 *
	 * @param gdbConn
	 * @param prodInstId
	 * @return an array of CallTrackingNumber objects
	 * @throws SQLException
	 */
	public CallTargetNumber[] getCallTrackingNumberDetails(Connection pdbConn, String prodInstId) throws SQLException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ArrayList<Pair<CallTargetNumber, ArrayList<CallTrackingNumber>>> callTargetNumbers = new ArrayList<Pair<CallTargetNumber, ArrayList<CallTrackingNumber>>>();
		
		try {
			final String query =
					" select tn.target_number_id, tn.target_number, trn.vendor_id, tn.tracking_scope, tn.vanity_number, tn.is_local, trn.tracking_number, trn.ns_campaign_id, trn.ns_ad_group_id" +
					"  from target_number tn" +
					"   join tracking_number trn on tn.prod_inst_id = trn.prod_inst_id and tn.target_number_id = trn.target_number_id " +
					"  where tn.prod_inst_id = ? " +
					" order by 1, 2";

			pstmt = pdbConn.prepareStatement(query);
			pstmt.setString(1, prodInstId);
			rs = pstmt.executeQuery();

			ArrayList<CallTrackingNumber> trackingNumbers = null;
			CallTargetNumber target = null;
			
			while (rs.next()) {
				String targetNumber = rs.getString("target_number");
				String scope = rs.getString("tracking_scope");

				if(target == null ||
						!targetNumber.equals(target.getTargetNumber()) ||
						!scope.equals(target.getTrackingScope())){
					target = new CallTargetNumber();					
					target.setProdInstId(prodInstId);
					target.setTargetNumberId(rs.getLong("target_number_id"));
					target.setLocal(rs.getBoolean("is_local"));
					target.setTrackingScope(scope);
					target.setTargetNumber(rs.getString("target_number"));
					target.setVanityNumber(rs.getString("vanity_number"));
					
					trackingNumbers = new ArrayList<CallTrackingNumber>();
					callTargetNumbers.add(Pair.from(target, trackingNumbers));
					
				}

				CallTrackingNumber number = new CallTrackingNumber();
				number.setTargetNumberId(rs.getLong("target_number_id"));
				number.setNsCampaignId(BaseHelper.getLongValue(rs, "ns_campaign_id"));
				number.setNsAdGroupId(BaseHelper.getLongValue(rs, "ns_ad_group_id"));
				number.setVendorId(BaseHelper.getIntegerValue(rs, "vendor_id"));
				number.setTrackingNumber(rs.getString("tracking_number"));
				trackingNumbers.add(number);

				this.logDebug(prodInstId," getCallTrackingNumberDetails -> " + number );

			}


		} finally {
			if (pstmt != null) {
				pstmt.close();
			}
		}
		
		CallTargetNumber[] targets = new CallTargetNumber[callTargetNumbers.size()];
		int i = 0;
		for(Pair<CallTargetNumber, ArrayList<CallTrackingNumber>> p: callTargetNumbers){			
			targets[i] = p.getFirst();
			targets[i].setTrackingNumbers(p.getSecond().toArray(new CallTrackingNumber[0]));
			i++;
		}
		
		return targets;
	}
	
	
	  /**
     * Insert the specified call tracking number.
     */
    public void insertTargetNumber(String logTag, Connection pdbConn, CallTargetNumber targetNumber, String updatedBy) throws SQLException {
        final String SQL =
            "INSERT INTO target_number(" +            
            "  prod_inst_id, " +           
            "  target_number, " +
            "  vanity_number, " +
            "  is_local, " +
            "  tracking_scope, "  +           
            "  created_date, updated_date, updated_by_user) " +
            "VALUES" +
            "  (?, ? ,?, ?, ?, now(), now(), ?)";
            

        PreparedStatement statement = null;
        ResultSet rs = null;
        try {
            statement = pdbConn.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS );
            int p=1;
            
            statement.setString(p++, targetNumber.getProdInstId());
            statement.setString(p++, targetNumber.getTargetNumber());
            statement.setString(p++, targetNumber.getVanityNumber());
            statement.setBoolean(p++, targetNumber.isLocal());
            statement.setString(p++, targetNumber.getTrackingScope());
            statement.setString(p++, updatedBy);
            logSqlStatement(logTag, statement);
            statement.execute();
            
            rs = statement.getGeneratedKeys();
            if(rs.next());
            targetNumber.setTargetNumberId(rs.getLong(1));
        }
        finally {
            close(statement, rs);
        }
    }

    /**
     * Insert the specified call tracking number.
     */
    public void insertTrackingNumber(String logTag, Connection connection, String prodInstId, CallTrackingNumber callTrackingNumber, String updatedBy) throws SQLException {
        final String SQL =
            "INSERT INTO tracking_number(" +
            "  target_number_id, " +
            "  prod_inst_id, " +           
            "  ns_campaign_id, " +
            "  ns_ad_group_id, " +
            "  vendor_id, " +
            "  vendor_entity_id1, "  +              
            "  tracking_number, " +
            "  created_date, updated_date, updated_by_user) " +
            "VALUES" +
            "  (?, ? ,?, ?, ?, 0, ?, now(), now(), ?)";
            

        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(SQL);
            int p=1;
            statement.setLong(p++, callTrackingNumber.getTargetNumberId());
            statement.setString(p++, prodInstId);
            statement.setObject(p++, callTrackingNumber.getNsCampaignId());
            statement.setObject(p++, callTrackingNumber.getNsAdGroupId());
            statement.setInt(p++, callTrackingNumber.getVendorId());
            statement.setString(p++, callTrackingNumber.getTrackingNumber());
            statement.setString(p++, updatedBy);
            
            logSqlStatement(logTag, statement);
            statement.execute();
        }
        finally {
            close(statement);
        }
    }
	

	/**
	 * Delete row from call_tracking table in pdb
	 */
	public void deleteTargetAndTrackingNumbers(Connection pdbConn, String prodInstId) throws SQLException {
		PreparedStatement ps = null;

		try {
			ps = pdbConn.prepareStatement("delete from tracking_number where prod_inst_id = ?");
			ps.setString(1, prodInstId);			
			logDebug(prodInstId, "Delete tracking_number -> " + ps.toString());
			ps.execute();
		} finally {
			BaseHelper.close(ps);
		}
		
		try {
			ps = pdbConn.prepareStatement("delete from target_number where prod_inst_id = ?");
			ps.setString(1, prodInstId);			
			logDebug(prodInstId, "Delete target_number -> " + ps.toString());
			ps.execute();
		} finally {
			BaseHelper.close(ps);
		}
	}

	
	/**
	 * Delete row from call_tracking table in pdb
	 */
	public void deleteTargetAndTrackingNumbers(Connection pdbConn, String prodInstId, Long nsCampaignId, Long nsAdGroupId) throws SQLException {
		PreparedStatement ps = null;

		try {
			ps = pdbConn.prepareStatement(
					"delete from tracking_number " +
					"where prod_inst_id = ? " +
					"and ns_campaign_id <=> ? " +
					"and ns_ad_group_id <=> ? ");
			ps.setString(1, prodInstId);
			ps.setObject(2, nsCampaignId);
			ps.setObject(3, nsAdGroupId);
			logDebug(prodInstId, "Delete tracking_number -> " + ps.toString());
			ps.execute();
		} finally {
			BaseHelper.close(ps);
		}
		
		try {
			ps = pdbConn.prepareStatement(
					"delete tgt from target_number tgt " +
					" left join tracking_number tr on tgt.prod_inst_id = tr.prod_inst_id  and tgt.target_number_id = tr.target_number_id " +
					" where tgt.prod_inst_id = ? and tr.target_number_id is null");
			ps.setString(1, prodInstId);			
			logDebug(prodInstId, "Delete target_number -> " + ps.toString());
			ps.execute();
		} finally {
			BaseHelper.close(ps);
		}
	}
	
	public boolean hasActiveTrackingNumbers(String logTag, Connection connection, String prodInstId) throws SQLException {
        final String SQL =
            "SELECT" +
            "  COUNT(*) > 0 " +
            "FROM" +
            "  tracking_number " +
            "WHERE" +
            "  prod_inst_id = ?";

        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(SQL);
            statement.setString(1, prodInstId);
            logSqlStatement(logTag, statement);
            resultSet = statement.executeQuery();
            // Default is false.
            return firstValue(resultSet, 1, false);
        }
        finally {
            close(statement, resultSet);
        }
    }
  
}
