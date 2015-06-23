//
// Kit's Java Utils.
//

package com.capitalone.cardcompanion.common.security;

import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Test XOR-cycle.
 */
public final class XORCycleUnitTest {
    @Test
    public void testEncryptDecrypt1() throws Exception {
        byte[] data1 = StandardCharsets.UTF_8.encode("A test string").array();
        byte[] data2 = data1.clone();
        byte[] K = {0x11, 0x22, 0x33, 0x44};

        XORCycle.apply(data2, K);
        assertFalse(Arrays.equals(data1, data2));
        XORCycle.apply(data2, K);
        assertTrue(Arrays.equals(data1, data2));
    }
}
