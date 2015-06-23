//
// Kit's Java Utils.
//

package com.capitalone.cardcompanion.common.security;

import com.google.common.io.BaseEncoding;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Encrypt and decrypt using AES in CBC mode.
 * Use XOR-cycle to add ciphertext entropy.
 * Not thread-safe.
 */
final class AESCBCCipher {
    private static final Charset CHARSET        = StandardCharsets.UTF_8;
    private static final String  TRANSFORMATION = "AES/CBC/PKCS5Padding";

    private final Cipher ecipher;
    private final Cipher dcipher;

    /**
     * Constructor.
     */
    public AESCBCCipher(Key key, IvParameterSpec iv) throws GeneralSecurityException {
        ecipher = Cipher.getInstance(TRANSFORMATION);
        dcipher = Cipher.getInstance(TRANSFORMATION);
        ecipher.init(Cipher.ENCRYPT_MODE, key, iv);
        dcipher.init(Cipher.DECRYPT_MODE, key, iv);
    }

    /**
     * Encrypt the specified string. Returns a base64url encoded representation.
     */
    public String encrypt(String string) throws GeneralSecurityException {
        return encrypt(string, BaseEncoding.base64Url());
    }

    /**
     * Encrypt the specified string.
     */
    public String encrypt(String string, BaseEncoding baseEncoding) throws GeneralSecurityException {
        byte[] head = new byte[8];
        byte[] tail = new byte[8];
        Random random = ThreadLocalRandom.current();
        random.nextBytes(head);
        random.nextBytes(tail);

        byte[] plaintext = string.getBytes(CHARSET);
        XORCycle.apply(plaintext, head);
        XORCycle.apply(plaintext, tail);
        byte[] data = new byte[head.length + plaintext.length + tail.length];
        System.arraycopy(head, 0, data, 0, head.length);
        System.arraycopy(plaintext, 0, data, head.length, plaintext.length);
        System.arraycopy(tail, 0, data, head.length + plaintext.length, tail.length);

        return baseEncoding.encode(ecipher.doFinal(data));
    }

    /**
     * Decrypts the specified string. The string is assumed to be base64url encoded.
     */
    public String decrypt(String string) throws GeneralSecurityException {
        return decrypt(string, BaseEncoding.base64Url());
    }

    /**
     * Decrypts the specified string.
     */
    public String decrypt(String string, BaseEncoding baseEncoding) throws GeneralSecurityException {
        byte[] data = dcipher.doFinal(baseEncoding.decode(string));

        byte[] head = new byte[8];
        byte[] tail = new byte[8];
        byte[] plaintext = new byte[data.length - (head.length + tail.length)];
        System.arraycopy(data, 0, head, 0, head.length);
        System.arraycopy(data, head.length, plaintext, 0, plaintext.length);
        System.arraycopy(data, head.length + plaintext.length, tail, 0, tail.length);
        XORCycle.apply(plaintext, tail);
        XORCycle.apply(plaintext, head);

        return new String(plaintext, CHARSET);
    }
}
