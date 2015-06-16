package com.netsol.adagent.util;

import java.sql.SQLException;

import com.netsol.adagent.util.beans.VendorBudgetAllocations;
import com.netsol.adagent.util.codes.VendorId;
import com.netsol.adagent.util.log.BaseLoggable;
import com.netsol.adagent.util.log.SimpleLoggable;

/**
 * Unit test for vendor budget allocation data access.
 * 
 * @author Adam S. Vernon
 */
public class VendorBudgetAllocationsTest extends BaseTestCase {
    static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:07 VendorBudgetAllocationsTest.java NSI";

    private String prodInstId = "WN.PP.33344444";
    
	/**
	 * Constructor
	 */
	public VendorBudgetAllocationsTest(String name) {
		super(name);
	}
	
	/**
	 * Unit test.
	 */
	public void testAll() throws SQLException {
		// TODO - assertions 
		
		// Delete whatever exists.
		VendorBudgetAllocations.deleteVendorBudgetAllocations(pdbConn, prodInstId);
		
		BaseLoggable baseLoggable = new SimpleLoggable("");
		
		// Query to make sure there is nothing.
		VendorBudgetAllocations allocations = VendorBudgetAllocations.getVendorBudgetAllocations(baseLoggable, pdbConn, prodInstId);
		System.out.println("after delete:");
		System.out.println(allocations);

		// Test the hasAllocations method when we have no allocations.
		System.out.println("hasAllocations (none): " + allocations.hasAllocations());
		
		// Insert an allocation.
		allocations = new VendorBudgetAllocations(baseLoggable, prodInstId);
		allocations.setAllocation(VendorId.GOOGLE, 1);
		allocations.insertOrUpdate(pdbConn);
		
		// Query to make sure there is an allocation.
		allocations = VendorBudgetAllocations.getVendorBudgetAllocations(baseLoggable, pdbConn, prodInstId);
		System.out.println("after insert:");
		System.out.println(allocations);
		
		// Test the hasAllocations method when we have an allocation.
		System.out.println("hasAllocations (have one): " + allocations.hasAllocations());
		
		// Test setAllocation.
		allocations.setAllocation(VendorId.GOOGLE, 0.5);
		
		// Test updating the allocation.
		allocations.insertOrUpdate(pdbConn);
		
		// Query to  check the update.
		allocations = VendorBudgetAllocations.getVendorBudgetAllocations(baseLoggable, pdbConn, prodInstId);
		System.out.println("after update:");
		System.out.println(allocations);
		
		// Test the getAllocation method on an existing allocation.
		System.out.println("getAllocation (existing): " + allocations.getAllocation(VendorId.GOOGLE));
		
		// Test the getAllocation method on an non-existent allocation.
		System.out.println("getAllocation (non-existent): " + allocations.getAllocation(42));
		
		// Delete the test data.
		VendorBudgetAllocations.deleteVendorBudgetAllocations(pdbConn, prodInstId);
	}
}
