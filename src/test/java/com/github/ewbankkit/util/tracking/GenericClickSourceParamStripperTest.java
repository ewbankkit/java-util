/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.github.ewbankkit.util.tracking;

import static org.junit.Assert.assertEquals;

import com.github.ewbankkit.util.codes.VendorId;
import org.junit.BeforeClass;
import org.junit.Test;

import com.netsol.adagent.util.tracking.ParamStripper;
import com.netsol.adagent.util.tracking.ParamTools;

public class GenericClickSourceParamStripperTest {
    private static ParamStripper paramStripper;

    @BeforeClass
    public static void setUp() throws Exception {
        paramStripper = ParamTools.getParamStripper(VendorId.GENERIC_CLICK_SOURCE);
    }

    @Test
    public void testStripParams1() {
        assertEquals(
                "http://www.google.com/",
                paramStripper.stripParams("http://www.google.com/?_nsag=42")
            );
    }

    @Test
    public void testStripParams2() {
        assertEquals(
                "http://www.google.com/?keywords=test",
                paramStripper.stripParams("http://www.google.com/?keywords=test")
            );
    }

    @Test
    public void testStripParams3() {
        assertEquals(
                "http://www.google.com/?keywords=test",
                paramStripper.stripParams("http://www.google.com/?keywords=test&_nsag=42")
            );
    }

    @Test
    public void testStripParams4() {
        assertEquals(
                "http://www.google.com/?a=1&keywords=test&c=4",
                paramStripper.stripParams("http://www.google.com/?a=1&_nsag=42&keywords=test&c=4")
            );
    }

    @Test
    public void testStripParams5() {
        assertEquals(
                "http://www.google.com/",
                paramStripper.stripParams("http://www.google.com")
            );
    }
}
