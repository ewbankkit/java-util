package com.netsol.adagent.util.beans;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;

import com.netsol.adagent.util.BudgetManagerException;
import com.netsol.adagent.util.dbhelpers.BaseHelper;
import com.netsol.adagent.util.log.BaseLoggable;

/**
 * Vendor budget allocation data for a product.
 * 
 * @author Adam S. Vernon
 */
public class VendorBudgetAllocations extends BaseLoggable {
	public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:54:11 VendorBudgetAllocations.java NSI";
	
	private String prodInstId;
	
	private Map<Integer, Double> map;
	
	public VendorBudgetAllocations(BaseLoggable baseLoggable, String prodInstId) {
	    super(baseLoggable);
	    
		this.prodInstId = prodInstId;
		map = new HashMap<Integer, Double>();
	}
	
	public VendorBudgetAllocations(Log log, String prodInstId) {
        super(log);
        
        this.prodInstId = prodInstId;
        map = new HashMap<Integer, Double>();
    }

	public VendorBudgetAllocations(String logTag, String prodInstId) {
	    super(logTag);
	    
		this.prodInstId = prodInstId;
		map = new HashMap<Integer, Double>();
	}
	
	/**
	 * @return the prodInstId
	 */
	public String getProdInstId() {
		return prodInstId;
	}

	/**
	 * @param prodInstId the prodInstId to set
	 */
	public void setProdInstId(String prodInstId) {
		this.prodInstId = prodInstId;
	}

	/**
	 * @return the allocation
	 */
	public Double getAllocation(int vendorId) {
		return map.get(vendorId);
	}

	/**
	 * Adds the allocation or updates the allocation value if it already has been set for this vendor.
	 * 
	 * @param allocation the allocation to set
	 */
	public void setAllocation(int vendorId, double allocation) {
		map.put(vendorId, allocation);
	}

	/**
	 * Check to see if there are allocations.
	 * 
	 * @return true if there are allocations, false otherwise.
	 */
	public boolean hasAllocations() {
		return map.size() > 0;
	}
	
	/**
	 * Get the vendor IDs with allocations.
	 * 
	 *  @return a set of vendor IDs.
	 */
	private Set<Integer> getVendorIds() {
		return map.keySet();
	}
	
	/**
	 * 
	 * @param allocations
	 * @return true if the allocations are the same, false otherwise.
	 */
	public boolean isEqual(VendorBudgetAllocations allocations) {
		// Get a list of all vendor IDs used across both collections.
		Set<Integer> vendorIds = allocations.getVendorIds();
		for (Integer i : getVendorIds()) {
			if (!vendorIds.contains(i)) {
				vendorIds.add(i);
			}
		}
		
		for (Integer i : vendorIds) {
			double allocation = (getAllocation(i) != null ? getAllocation(i) : 0d);
			double newAllocation = (allocations.getAllocation(i) != null ? allocations.getAllocation(i) : 0d); 
			if (allocation != newAllocation) {
				// Return false if we find a difference.
				return false;
			}
		}
		
		// Return true if the allocations are the same. 
		return true;
	}
	
	
	/**
	 * Override toString.
	 */
	public String toString() {
		if (hasAllocations()) {
			StringBuilder sb = new StringBuilder();
			Set<Integer> vendorIds = map.keySet();
			for (Integer vendorId : vendorIds) {
				sb.append("vendorId=" + vendorId + ", allocation=" + map.get(vendorId) + "\n");
			}
			return sb.toString();
		}	
		else {	
			return "empty";
		}
	}
	
	//
	// Database
	//
	
	/**
	 * SQL to insert or update vendor budget allocations.
	 */
	private static final String INSERT_OR_UPDATE_VENDOR_BUDGET_ALLOCATIONS = "INSERT INTO vendor_budget_allocation (prod_inst_id, vendor_id, allocation) "
		+ "VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE allocation = ?";
	
	/**
	 * SQL to query vendor budget allocations for a product.
	 */
	private static final String GET_VENDOR_BUDGET_ALLOCATIONS = "SELECT vendor_id, allocation FROM vendor_budget_allocation WHERE prod_inst_id = ?";	
	
	/**
	 * SQL to delete vendor budget allocations for a product.
	 */
	private static final String DELETE_VENDOR_BUDGET_ALLOCATIONS = "DELETE FROM vendor_budget_allocation WHERE prod_inst_id = ?";

	/**
	 * SQL to delete vendor budget allocations for a product and vendor.
	 */
	private static final String DELETE_VENDOR_BUDGET_ALLOCATION_FOR_VENDOR = "DELETE FROM vendor_budget_allocation WHERE prod_inst_id = ? and vendor_id = ?";

	/**
	 * Insert or update the vendor budget allocations.
	 * 
	 * @param conn a connection to PDB.
	 */
	public void insertOrUpdate(Connection conn) throws SQLException {
		if (map.size() > 0) {
			PreparedStatement pstmt = null;
			
			try {				
				Set<Integer> vendorIds = map.keySet();
				for (Integer vendorId : vendorIds) {
					double allocation = map.get(vendorId);
					pstmt = conn.prepareStatement(INSERT_OR_UPDATE_VENDOR_BUDGET_ALLOCATIONS);
					pstmt.setString(1, prodInstId);
					pstmt.setInt(2, vendorId);
					pstmt.setDouble(3, allocation);
					pstmt.setDouble(4, allocation);
	
					logInfo(getLogTag(prodInstId), pstmt.toString());
					pstmt.executeUpdate();
				}
			}
			catch (SQLException e) {
			    logError(getLogTag(prodInstId), e);
				throw e;
			} 
			finally {
				BaseHelper.close(pstmt);
			}
		}
	}
	
	/**
	 * Get the vendor budget allocations for a product. 
	 * 
	 * @param conn a connection to PDB.
	 * @param prodInstId
	 * @return a VendorBudgetAllocations object, which may contain no allocations. Null is never returned.
	 * @throws BudgetManagerException
	 */
	public static VendorBudgetAllocations getVendorBudgetAllocations(BaseLoggable baseLoggable, Connection conn, String prodInstId) throws SQLException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		VendorBudgetAllocations allocations = new VendorBudgetAllocations(baseLoggable, prodInstId);
		
		try {				
			pstmt = conn.prepareStatement(GET_VENDOR_BUDGET_ALLOCATIONS);
			pstmt.setString(1, prodInstId);

			allocations.logInfo(getLogTag(prodInstId), pstmt.toString());
			rs = pstmt.executeQuery();
			
			if (rs != null) {
				while (rs.next()) {
					allocations.setAllocation(rs.getInt(1), rs.getDouble(2));
				}
			}
			
			return allocations;
		} 
		catch (SQLException e) {
		    allocations.logError(getLogTag(prodInstId), e);
			throw e;
		} 
		finally {
			BaseHelper.close(pstmt);
		}
	}
	
	/**
	 * 
	 * @param conn a connection to PDB.
	 * @param prodInstId
	 * @throws SQLException
	 */
	public static void deleteVendorBudgetAllocations(Connection conn, String prodInstId) throws SQLException {
		PreparedStatement pstmt = null;
		try {				
			pstmt = conn.prepareStatement(DELETE_VENDOR_BUDGET_ALLOCATIONS);
			pstmt.setString(1, prodInstId);

			pstmt.executeUpdate();
		} 
		finally {
			BaseHelper.close(pstmt);
		}
	}
	
	/**
	 * 
	 * @param conn a connection to PDB.
	 * @param prodInstId
	 * @throws SQLException
	 */
	public static void deleteVendorBudgetAllocationForVendor(Connection conn, String prodInstId, int vendorId) throws SQLException {
		PreparedStatement pstmt = null;
		try {				
			pstmt = conn.prepareStatement(DELETE_VENDOR_BUDGET_ALLOCATION_FOR_VENDOR);
			pstmt.setString(1, prodInstId);
			pstmt.setInt(2, vendorId);

			pstmt.executeUpdate();
		} 
		finally {
			BaseHelper.close(pstmt);
		}
	}
	
    /**
     * Return the log tag for the specified product instance ID. 
     */
    private static String getLogTag(String prodInstId) {
        return prodInstId + "|VendorBudgetAllocations";
    }
}
