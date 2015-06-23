//
// Kit's Java Utils.
//

package com.github.ewbankkit.security;

import com.google.common.base.Preconditions;
import com.google.common.io.ByteProcessor;
import com.google.common.io.ByteSource;

import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.Key;

/**
 * Generate a MAC using HMAC SHA-256 .
 * Not thread-safe.
 */
public final class HmacSHA256Mac {
    public static final String ALGORITHM = "HmacSHA256";

    private final Mac mac;

    /**
     * Constructor.
     */
    public HmacSHA256Mac(Key key) throws GeneralSecurityException {
        Preconditions.checkNotNull(key);

        mac = Mac.getInstance(ALGORITHM);
        mac.init(key);
    }

    /**
     * Generates a MAC for the specified data.
     */
    public byte[] mac(ByteSource byteSource) throws GeneralSecurityException {
        Preconditions.checkNotNull(byteSource);

        try {
            return byteSource.read(new ByteProcessor<byte[]>() {
                /**
                 * This method will be called for each chunk of bytes in an input stream.
                 */
                @Override
                public boolean processBytes(@SuppressWarnings("NullableProblems") byte[] buf, int off, int len) throws IOException {
                    mac.update(buf, off, len);
                    return true;
                }

                /**
                 * Return the result of processing all the bytes.
                 */
                @Override
                public byte[] getResult() {
                    return mac.doFinal();
                }
            });
        }
        catch (Exception ex) {
            throw new GeneralSecurityException(ex);
        }
    }

    /**
     * Generates a new secret key.
     */
    public static SecretKey generateSecretKey() throws GeneralSecurityException {
        return KeyGenerator.getInstance(ALGORITHM).generateKey();
    }
}
