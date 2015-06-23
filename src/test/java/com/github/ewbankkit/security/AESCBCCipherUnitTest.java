//
// Kit's Java Utils.
//

package com.github.ewbankkit.security;

import org.junit.Test;

import javax.crypto.spec.IvParameterSpec;
import java.security.Key;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Test AES encryption.
 */
public final class AESCBCCipherUnitTest {
    @Test
    public void testEncryptDecrypt1() throws Exception {
        String string = "A test string";
        Key key = AESCBC.generateKey(128);
//        System.out.println(com.google.common.io.BaseEncoding.base64Url().encode(key.getEncoded()));
        IvParameterSpec ivspec = AESCBC.generateIV(16);
//        System.out.println(com.google.common.io.BaseEncoding.base16().encode(ivspec.getIV()));
        AESCBCCipher cipher = new AESCBCCipher(key, ivspec);
        String encrypted = cipher.encrypt(string);
        assertNotEquals(string, encrypted);
        assertEquals(string, cipher.decrypt(encrypted));
    }
}
