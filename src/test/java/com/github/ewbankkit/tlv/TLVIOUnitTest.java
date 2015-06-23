//
// Kit's Java Utils.
//

package com.github.ewbankkit.tlv;

import com.google.common.base.Optional;
import com.google.common.io.ByteSource;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Test TLV I/O.
 */
public final class TLVIOUnitTest {
    @Before
    public void before() throws Exception {
        System.setProperty("common.env", "test");
    }

    @Test
    public void testParse1() throws Exception {
        Optional<? extends Map<Integer, TLV>> stuff = TLVIO.read(ByteSource.wrap(new byte[] {}));
        assertNotNull(stuff);
        assertTrue(stuff.isPresent());
        assertTrue(stuff.get().isEmpty());
    }

    @Test
    public void testParse2() throws Exception {
        Optional<? extends Map<Integer, TLV>> stuff = TLVIO.read(ByteSource.wrap(new byte[] {(byte)0x82, 0x02, 0x19, (byte)0x80}));
        assertNotNull(stuff);
        assertTrue(stuff.isPresent());
        assertEquals(1, stuff.get().size());
        assertEquals(2, stuff.get().get(0x82).getLength());
    }

    @Test
    public void testParse3() throws Exception {
        Optional<? extends Map<Integer, TLV>> stuff = TLVIO.read(ByteSource.wrap(new byte[] {(byte)0x82, 0x02, 0x19, (byte)0x80, (byte)0x9F, 0x5F, 0x01, (byte)0xC0}));
        assertNotNull(stuff);
        assertTrue(stuff.isPresent());
        assertEquals(2, stuff.get().size());
        assertEquals(1, stuff.get().get(0x9F5F).getLength());
    }
}
