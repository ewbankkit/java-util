/**
 * Kit's Java Utils.
 */

package com.github.ewbankkit.util.tracking;

import static org.junit.Assert.assertEquals;

import org.junit.BeforeClass;
import org.junit.Test;

import com.github.ewbankkit.util.codes.VendorId;
import com.netsol.adagent.util.tracking.ParamAdder;
import com.netsol.adagent.util.tracking.ParamTools;

public class MicrosoftParamAdderTest {
    private static ParamAdder paramAdder;

    @BeforeClass
    public static void setUp() {
        paramAdder = ParamTools.getParamAdder(VendorId.MICROSOFT, 42L);
    }

    @Test
    public void testStripParams1() {
        assertEquals(
                "http://www.google.com/?MSADID={AdId}&MSKWID={OrderItemId}&MSKWMT={MatchType}&adGroup=42",
                paramAdder.addParams("http://www.google.com/")
            );
    }

    @Test
    public void testStripParams2() {
        assertEquals(
                "http://www.google.com/?param1=value1&MSADID={AdId}&MSKWID={OrderItemId}&MSKWMT={MatchType}&adGroup=42",
                paramAdder.addParams("http://www.google.com/?param1=value1")
            );
    }
}
