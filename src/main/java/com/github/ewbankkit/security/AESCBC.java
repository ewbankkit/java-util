//
// Kit's Java Utils.
//

package com.github.ewbankkit.security;

import com.github.ewbankkit.Config;
import com.google.common.base.Optional;
import com.google.common.io.BaseEncoding;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.concurrent.ThreadSafe;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Encrypt and decrypt using AES in CBC mode.
 * Thread-safe.
 */
@ThreadSafe
public final class AESCBC {
    private static final Logger LOGGER = LoggerFactory.getLogger(AESCBC.class);

    private static final String                    ALGORITHM  = "AES";
    private static final EncDec                    DECRYPT    = new EncDec() {
        @Override
        public String apply(AESCBCCipher cipher, String string) throws GeneralSecurityException {
            return cipher.decrypt(string);
        }
    };
    private static final EncDec                    ENCRYPT    = new EncDec() {
        @Override
        public String apply(AESCBCCipher cipher, String string) throws GeneralSecurityException {
            return cipher.encrypt(string);
        }
    };
    private static final Optional<IvParameterSpec> IV         = getInitializationVector();
    private static final Optional<Key>             SECRET_KEY = getSecretKey();

    private AESCBC() {}

    /**
     * Encrypt the specified string. Returns a base64url encoded representation.
     */
    public static String encrypt(String string) throws GeneralSecurityException {
        return apply(ENCRYPT, string);
    }

    /**
     * Decrypts the specified string. The string is assumed to be base64url encoded.
     */
    public static String decrypt(String string) throws GeneralSecurityException {
        return apply(DECRYPT, string);
    }

    /**
     * Generates a new IV of the specified length.
     */
    static IvParameterSpec generateIV(int len) {
        byte[] iv = new byte[len];
        new SecureRandom().nextBytes(iv);
        return new IvParameterSpec(iv);
    }

    /**
     * Generates a new key of the specified length.
     */
    static Key generateKey(int len) throws GeneralSecurityException {
        KeyGenerator keygen = KeyGenerator.getInstance(ALGORITHM);
        keygen.init(len);
        return keygen.generateKey();
    }

    /**
     * Apply the specified encryption or decryption operation.
     */
    private static String apply(EncDec endDec, String string) throws GeneralSecurityException {
        if (!SECRET_KEY.isPresent()) {
            throw new GeneralSecurityException("Missing AES secret key");
        }
        if (!IV.isPresent()) {
            throw new GeneralSecurityException("Missing AES initialization vector");
        }
        return endDec.apply(new AESCBCCipher(SECRET_KEY.get(), IV.get()), string);
    }

    /**
     * Return any configured initialization vector (assumed to be base16 encoded).
     */
    private static Optional<IvParameterSpec> getInitializationVector() {
        Optional<String> initializationVector = Config.getInstance().getString("AES.initializationVector");
        if (initializationVector.isPresent()) {
            byte[] iv = BaseEncoding.base16().decode(initializationVector.get());
            return Optional.of(new IvParameterSpec(iv));
        }
        else {
            LOGGER.error("Missing AES initialization vector configuration value");
        }

        return Optional.absent();
    }

    /**
     * Return any configured secret key (assumed to be base64url encoded).
     */
    private static Optional<Key> getSecretKey() {
        Optional<String> secretKey = Config.getInstance().getString("AES.secretKey");
        if (secretKey.isPresent()) {
            byte[] key = BaseEncoding.base64Url().decode(secretKey.get());
            int keyLength = key.length;
            try {
                int len = KeyGenerator.getInstance(ALGORITHM).generateKey().getEncoded().length;
                if (keyLength >= len) {
                    return Optional.of((Key)new SecretKeySpec(key, 0, len, ALGORITHM));
                }
                else {
                    LOGGER.error(String.format("Invalid key length; Expected at least %d bytes, received %d bytes", len, keyLength));
                }
            }
            catch (NoSuchAlgorithmException ex) {
                LOGGER.error("Unable to obtain AES secret key", ex);
            }
        }
        else {
            LOGGER.error("Missing AES secret key configuration value");
        }

        return Optional.absent();
    }

    /**
     * Encapsulates encryption or decryption.
     */
    private static interface EncDec {
        /**
         * Apply the operation.
         */
        public abstract String apply(AESCBCCipher cipher, String string) throws GeneralSecurityException;
    }
}
