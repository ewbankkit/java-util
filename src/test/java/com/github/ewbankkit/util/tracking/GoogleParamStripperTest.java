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

public class GoogleParamStripperTest {
    private static ParamStripper paramStripper;

    @BeforeClass
    public static void setUp() {
        paramStripper = ParamTools.getParamStripper(VendorId.GOOGLE);
    }

    @Test
    public void testStripParams1() {
        assertEquals(
                "http://www.google.com/",
                paramStripper.stripParams("http://www.google.com/?keywords=test&adGroup=99")
            );
    }

    @Test
    public void testStripParams2() {
        assertEquals(
                "http://www.google.com/",
                paramStripper.stripParams("http://www.google.com/?keywords=test&creative=1234")
            );
    }

    @Test
    public void testStripParams3() {
        assertEquals(
                "http://www.google.com/",
                paramStripper.stripParams("http://www.google.com/?creative=1234&keywords=test")
            );
    }

    @Test
    public void testStripParams4() {
        assertEquals(
                "http://www.google.com/",
                paramStripper.stripParams("http://www.google.com/?creative=1234&keywords=test&matchtype=b")
            );
    }

    @Test
    public void testStripParams5() {
        assertEquals(
                "http://www.google.com/?xyz=boop",
                paramStripper.stripParams("http://www.google.com/?matchtype=p&creative=1234&xyz=boop&keywords=test")
            );
    }

    @Test
    public void testStripParams6() {
        assertEquals(
                "http://www.google.com/?a=1&b=2&c=4",
                paramStripper.stripParams("http://www.google.com/?keywords=test&a=1&b=2&c=4&adGroup=99")
            );
    }
    @Test
    public void testStripParams7() {
        assertEquals(
                "http://www.google.com/?a=1&b=2&c=4",
                paramStripper.stripParams("http://www.google.com/?a=1&b=2&keywords=test&c=4&adGroup=99&matchtype=e")
            );
    }

    @Test
    public void testStripParams8() {
        assertEquals(
                "http://www.google.com/?a=1&b=2&c=4",
                paramStripper.stripParams("http://www.google.com/?a=1&creative=1234&b=2&c=4&keywords=test")
            );
    }

    @Test
    public void testStripParams9() {
        assertEquals(
                "http://www.google.com/?a=1&b=2&c=4",
                paramStripper.stripParams("http://www.google.com/?a=1&creative={creative}&b=2&c=4&keywords=test")
            );
    }

    @Test
    public void testStripParams10() {
        assertEquals(
                "http://www.google.com/?a=1&b=2&c=4",
                paramStripper.stripParams("http://www.google.com/?a=1&creative={creative}&b=2&c=4&{copy:adGroup}")
            );
    }
}
