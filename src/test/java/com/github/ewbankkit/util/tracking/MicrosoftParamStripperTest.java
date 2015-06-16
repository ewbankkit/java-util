/**
 * Kit's Java Utils.
 */

package com.github.ewbankkit.util.tracking;

import static org.junit.Assert.assertEquals;

import com.github.ewbankkit.util.codes.VendorId;
import org.junit.BeforeClass;
import org.junit.Test;

import com.netsol.adagent.util.tracking.ParamStripper;
import com.netsol.adagent.util.tracking.ParamTools;

public class MicrosoftParamStripperTest {
    private static ParamStripper paramStripper;

    @BeforeClass
    public static void setUp() {
        paramStripper = ParamTools.getParamStripper(VendorId.MICROSOFT);
    }

    @Test
    public void testStripParams1() {
        assertEquals(
                "http://www.microsoft.com/",
                paramStripper.stripParams("http://www.microsoft.com/?MSADID=666&MSKWID=666&adGroup=99")
            );
    }

    @Test
    public void testStripParams2() {
        assertEquals(
                "http://www.microsoft.com/",
                paramStripper.stripParams("http://www.microsoft.com/?MSADID=666&adGroup=99&MSKWID=666&MSKWMT=p")
            );
    }

    @Test
    public void testStripParams3() {
        assertEquals(
                "http://www.microsoft.com/",
                paramStripper.stripParams("http://www.microsoft.com/?MSKWMT=p")
            );
    }

    @Test
    public void testStripParams4() {
        assertEquals(
                "http://www.microsoft.com/",
                paramStripper.stripParams("http://www.microsoft.com/?MSKWMT=p&MSADID=666&MSKWID=666")
            );
    }

    @Test
    public void testStripParams5() {
        assertEquals(
                "http://www.microsoft.com/?a=1&b=2&c=4",
                paramStripper.stripParams("http://www.microsoft.com/?MSADID=666&MSKWID=666&a=1&b=2&adGroup=99&c=4")
            );
    }

    @Test
    public void testStripParams6() {
        assertEquals(
                "http://www.microsoft.com/?a=1&b=2&c=4",
                paramStripper.stripParams("http://www.microsoft.com/?a=1&MSADID=666&b=2&MSKWID=666&c=4&MSKWMT=p")
            );
    }

    @Test
    public void testStripParams7() {
        assertEquals(
                "http://www.microsoft.com/?a=1&b=2&c=4",
                paramStripper.stripParams("http://www.microsoft.com/?a=1&b=2&c=4&MSADID={AdId}&MSKWID={OrderItemId}&MSKWMT={MatchType}")
            );
    }
}
