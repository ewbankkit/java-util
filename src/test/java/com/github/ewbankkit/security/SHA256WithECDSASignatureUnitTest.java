//
// Kit's Java Utils.
//

package com.github.ewbankkit.security;

import com.google.common.io.ByteSource;
import org.junit.Test;

import java.security.KeyPair;
import java.util.Random;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Test SHA256WithECDSA signature.
 */
public final class SHA256WithECDSASignatureUnitTest {
    @Test
    public void testSignVerify() throws Exception {
        byte[] data = new byte[256];
        new Random().nextBytes(data);

        KeyPair keyPair = SHA256WithECDSASignature.generateKeyPair(256);

        SHA256WithECDSASignature signer = new SHA256WithECDSASignature(keyPair);
        byte[] signature = signer.sign(ByteSource.wrap(data));
        assertNotNull(signature);

        boolean verified = signer.verify(ByteSource.wrap(data), signature);
        assertTrue(verified);
    }
}
