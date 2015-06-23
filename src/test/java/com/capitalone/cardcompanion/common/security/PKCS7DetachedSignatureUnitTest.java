//
// Kit's Java Utils.
//

package com.capitalone.cardcompanion.common.security;

import com.capitalone.cardcompanion.common.io.FileProperties;
import com.google.common.base.Optional;
import com.google.common.io.ByteSource;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Test PKCS7DetachedSignature.
 */
public final class PKCS7DetachedSignatureUnitTest {
    @Before
    public void before() throws Exception {
        System.setProperty("common.env", "test");
    }

    @Ignore("Ignored until implementing passbook and we know where to put the properties files")
    @Test
    public void testSignVerify() throws Exception {
        byte[] data = new byte[256];
        new Random().nextBytes(data);

        Optional<KeyStoreProperties> optionalProperties1 = KeyStoreProperties.fromConfig("passbook.keystore");
        assertTrue(optionalProperties1.isPresent());
        KeyStoreProperties properties = optionalProperties1.get();
        KeyStore keystore = KeyStores.fromProperties(properties);
        Optional<X509CertificateAndPrivateKey> optionalX509CertificateAndPrivateKey = KeyStores.getX509CertificateAndPrivateKey(keystore, properties.getOptionalKeyPassword());
        assertNotNull(optionalX509CertificateAndPrivateKey);
        assertTrue(optionalX509CertificateAndPrivateKey.isPresent());
        X509Certificate signingCert = optionalX509CertificateAndPrivateKey.get().getX509Certificate();

        Optional<FileProperties> optionalProperties2 = FileProperties.fromConfig("appleWWDRCA.certificate");
        assertTrue(optionalProperties2.isPresent());
        List<X509Certificate> additionalCerts = X509Certificates.fromProperties(optionalProperties2.get());

        PKCS7DetachedSignature signer = new PKCS7DetachedSignature(optionalX509CertificateAndPrivateKey.get().getPrivateKey(), signingCert, additionalCerts);
        ByteSource signature = signer.sign(data);
        assertNotNull(signature);
        data = signature.read();
        assertNotNull(data);
        assertFalse(data.length == 0);
    }
}
