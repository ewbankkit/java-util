package com.github.ewbankkit.util;

import java.sql.Connection;

import com.github.ewbankkit.util.dbhelpers.BaseHelper;

import junit.framework.TestCase;

/**
 * Test case base class to perform basic set up and tear down of resources.
 *
 * @author Adam Vernon
 */
public abstract class BaseTestCase extends TestCase {

	/** PDB connection. */
	protected Connection pdbConn = null;
	/** GDB connection. */
	protected Connection gdbConn = null;

	/** Constructor. */
	public BaseTestCase(String name) {
		super(name);
	}

	//
	// TestCase implementation:
	//

	public void setUp() {
		try {
			pdbConn = TestDbHelper.getPdbConnection();
			gdbConn = TestDbHelper.getGdbConnection();
		}
		catch(Throwable e) {
			e.printStackTrace(System.out);
		}
	}

	public void tearDown() {
	    BaseHelper.rollback(gdbConn);
	    BaseHelper.close(gdbConn);
	    BaseHelper.rollback(pdbConn);
	    BaseHelper.close(pdbConn);
	}
}
