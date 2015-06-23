//
// Kit's Java Utils.
//

package com.capitalone.cardcompanion.common.security;

import com.capitalone.cardcompanion.common.Config;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.io.BaseEncoding;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

/**
 * Secret key operations.
 */
public final class SecretKeys {
    private final static Logger LOGGER = LoggerFactory.getLogger(SecretKeys.class);

    /**
     * Return any configured secret key (assumed to be base64url encoded).
     */
    public static Optional<Key> fromConfig(String configKey, String algorithm) {
        Preconditions.checkNotNull(configKey);
        Preconditions.checkNotNull(algorithm);

        Optional<String> secretKey = Config.getInstance().getString(configKey);
        if (secretKey.isPresent()) {
            byte[] key = BaseEncoding.base64Url().decode(secretKey.get());
            int keyLength = key.length;
            try {
                int len = KeyGenerator.getInstance(algorithm).generateKey().getEncoded().length;
                if (keyLength >= len) {
                    return Optional.of((Key)new SecretKeySpec(key, 0, len, algorithm));
                }
                else {
                    LOGGER.error("Invalid key length; Expected at least {} bytes, received {} bytes", len, keyLength);
                }
            }
            catch (NoSuchAlgorithmException ex) {
                LOGGER.error(String.format("Unable to obtain %s secret key", algorithm), ex);
            }
        }
        else {
            LOGGER.error("Missing {} secret key configuration value", algorithm);
        }

        return Optional.absent();
    }

    private SecretKeys() {}
}
