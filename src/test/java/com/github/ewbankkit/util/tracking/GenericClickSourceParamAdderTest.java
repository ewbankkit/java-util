/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.github.ewbankkit.util.tracking;

import static org.junit.Assert.assertEquals;

import org.junit.BeforeClass;
import org.junit.Test;

import com.github.ewbankkit.util.codes.VendorId;
import com.netsol.adagent.util.tracking.ParamAdder;
import com.netsol.adagent.util.tracking.ParamTools;

public class GenericClickSourceParamAdderTest {
    private static ParamAdder paramAdder;

    @BeforeClass
    public static void setUp() {
        paramAdder = ParamTools.getParamAdder(VendorId.GENERIC_CLICK_SOURCE, 42L);
    }

    @Test
    public void testAddParams1() {
        assertEquals(
                "http://www.google.com/?_nsag=42",
                paramAdder.addParams("http://www.google.com/")
            );
    }

    @Test
    public void testAddParams2() {
        assertEquals(
                "http://www.google.com/?_nsag=42",
                paramAdder.addParams("http://www.google.com/?")
            );
    }

    @Test
    public void testAddParams3() {
        assertEquals(
                "http://www.google.com/?_nsag=42",
                paramAdder.addParams("http://www.google.com")
            );
    }

    @Test
    public void testAddParams4() {
        assertEquals(
                "http://www.google.com/?param1=value1&_nsag=42",
                paramAdder.addParams("http://www.google.com/?param1=value1")
            );
    }
}
