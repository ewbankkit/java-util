//
// Kit's Java Utils.
//

package com.github.ewbankkit.base;

import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Test UUID utilities.
 */
public final class UUIDsUnitTest {
    @Test
    public void testToByteArray1() throws Exception {
        UUID uuid = UUID.randomUUID();
        byte[] bytes = UUIDs.toByteArray(uuid);

        assertNotNull(bytes);
        assertEquals(16, bytes.length);
    }

    @Test
    public void testToByteArray2() throws Exception {
        UUID uuid1 = UUID.randomUUID();
        byte[] bytes = UUIDs.toByteArray(uuid1);
        UUID uuid2 = UUIDs.fromByteArray(bytes);

        assertNotNull(uuid2);
        assertEquals(uuid1, uuid2);
    }
}
