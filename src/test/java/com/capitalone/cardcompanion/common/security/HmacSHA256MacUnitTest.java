//
// Kit's Java Utils.
//

package com.capitalone.cardcompanion.common.security;

import com.google.common.io.BaseEncoding;
import com.google.common.io.ByteSource;
import org.junit.Test;

import javax.crypto.SecretKey;
import java.util.Arrays;
import java.util.Random;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

/**
 * Test SHA1WithRSA encryption.
 */
public final class HmacSHA256MacUnitTest {
    @Test
    public void testMac() throws Exception {
        byte[] data = new byte[256];
        new Random().nextBytes(data);

        SecretKey sk = HmacSHA256Mac.generateSecretKey();

        HmacSHA256Mac hmac = new HmacSHA256Mac(sk);
        byte[] mac = hmac.mac(ByteSource.wrap(data));
        assertNotNull(mac);
        assertFalse(Arrays.equals(data, mac));
    }

    @Test
    public void testSecretKey() throws Exception {
        SecretKey sk = HmacSHA256Mac.generateSecretKey();
        assertNotNull(sk);

        String encoded = BaseEncoding.base64Url().encode(sk.getEncoded());
        assertNotNull(encoded);

        System.out.println(encoded);
    }
}
