//
// Kit's Java Utils.
//

package com.github.ewbankkit.security;

import com.github.ewbankkit.io.FileProperties;
import com.google.common.base.Optional;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.security.cert.X509Certificate;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Test certificate functionality.
 */
public final class X509CertificatesUnitTest {
    @Before
    public void before() throws Exception {
        System.setProperty("common.env", "test");
    }

    @Test
    public void testStoreProperties1() throws Exception {
        Optional<FileProperties> optionalProperties = FileProperties.fromConfig("NoNoNo");
        assertFalse(optionalProperties.isPresent());
    }

    @Ignore("Ignored until implementing passbook and we know where to put the properties files")
    @Test
    public void testStoreProperties2() throws Exception {
        Optional<FileProperties> optionalProperties = FileProperties.fromConfig("appleWWDRCA.certificate");
        assertTrue(optionalProperties.isPresent());
        String s = optionalProperties.get().toString();
        assertNotNull(s);
    }

    @Ignore("Ignored until implementing passbook and we know where to put the properties files")
    @Test
    public void testStoreProperties3() throws Exception {
        Optional<FileProperties> optionalProperties = FileProperties.fromConfig("appleWWDRCA.certificate");
        assertTrue(optionalProperties.isPresent());
        List<X509Certificate> certificates = X509Certificates.fromProperties(optionalProperties.get());
        assertNotNull(certificates);
        assertFalse(certificates.isEmpty());
    }
}
