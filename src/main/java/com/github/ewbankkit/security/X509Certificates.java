//
// Kit's Java Utils.
//

package com.github.ewbankkit.security;

import com.github.ewbankkit.io.FileProperties;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.io.ByteSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

//import sun.security.x509.AccessDescription;
//import sun.security.x509.AuthorityInfoAccessExtension;
//import sun.security.x509.X509CertImpl;
//import java.io.IOException;

/**
 * X509 certificate utilities.
 */
public final class X509Certificates {
    private static final Logger LOGGER = LoggerFactory.getLogger(X509Certificates.class);

    /**
     * Creates certificates from a byte source.
     */
    public static List<X509Certificate> fromByteSource(ByteSource byteSource) throws CertificateException {
        Preconditions.checkNotNull(byteSource);

        CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
        try (InputStream stream = byteSource.openStream()) {
            return ImmutableList.copyOf(Iterables.filter(certificateFactory.generateCertificates(stream), X509Certificate.class));
        }
        catch (IOException ex) {
            throw new CertificateException(ex);
        }
    }

    /**
     * Creates certificates from properties.
     */
    public static List<X509Certificate> fromProperties(FileProperties properties) throws GeneralSecurityException {
        Preconditions.checkNotNull(properties);

        List<X509Certificate> certificates = fromByteSource(properties.getByteSource());
        if (LOGGER.isDebugEnabled()) {
            StringBuilder sb = new StringBuilder("Certificate (").append(properties.getFileOrResource()).append("):");
            for (X509Certificate certificate : certificates) {
                sb.append(toString(certificate));
            }
            LOGGER.debug(sb.toString());
        }

        return certificates;
    }

    /**
     * Utility method to test if a certificate is self-issued.
     * This is the case iff the subject and issuer X500Principals are equal.
     */
    public static boolean isSelfIssued(X509Certificate x509Certificate) {
        Preconditions.checkNotNull(x509Certificate);

        return x509Certificate.getSubjectX500Principal().equals(x509Certificate.getIssuerX500Principal());
    }

    /**
     * Utility method to test if a certificate is self-signed.
     * This is the case iff the subject and issuer X500Principals are equal AND
     * the certificate's subject public key can be used to verify the certificate.
     * In case of exception, returns false.
     */
    public static boolean isSelfSigned(X509Certificate x509Certificate, @Nullable String sigProvider) {
        Preconditions.checkNotNull(x509Certificate);

        if (isSelfIssued(x509Certificate)) {
            try {
                if (sigProvider == null) {
                    x509Certificate.verify(x509Certificate.getPublicKey());
                }
                else {
                    x509Certificate.verify(x509Certificate.getPublicKey(), sigProvider);
                }
                return true;
            }
            catch (GeneralSecurityException ignored) {}
        }
        return false;
    }

    /**
     * Returns a string representation of a certificate.
     */
    public static String toString(X509Certificate x509Certificate) {
        Preconditions.checkNotNull(x509Certificate);

        try {
            StringBuilder sb = new StringBuilder();
            sb.append("\nSubject   : ").append(x509Certificate.getSubjectX500Principal()).
               append("\nIssuer    : ").append(x509Certificate.getIssuerDN()).
               append("\nValid from: ").append(x509Certificate.getNotBefore()).
               append("\nValid to  : ").append(x509Certificate.getNotAfter()).
               append("\nX509v3 extensions").
               append("\n-----------------");
            appendAlternativeNames(sb, x509Certificate.getSubjectAlternativeNames(), "\nSubject alternative names:");
            appendAlternativeNames(sb, x509Certificate.getIssuerAlternativeNames(), "\nIssuer alternative names:");
//            if (x509Certificate instanceof X509CertImpl) {
//                AuthorityInfoAccessExtension authorityInfoAccessExtension = ((X509CertImpl)x509Certificate).getAuthorityInfoAccessExtension();
//                if (authorityInfoAccessExtension != null) {
//                    sb.append("\nAuthority information access:");
//                    @SuppressWarnings("unchecked") List<AccessDescription> accessDescriptions = (List<AccessDescription>)authorityInfoAccessExtension.get(AuthorityInfoAccessExtension.DESCRIPTIONS);
//                    if (accessDescriptions != null) {
//                        for (AccessDescription accessDescription : accessDescriptions) {
//                            sb.append("\n  Access method  : ").append(accessDescription.getAccessMethod()).
//                               append("\n  Access location: ").append(accessDescription.getAccessLocation());
//                        }
//                    }
//                }
//            }

            return sb.toString();
        }
//        catch (CertificateException | IOException ignored) {}
        catch (CertificateException ignored) {}

        return "";
    }

    /**
     * Returns a string representation of a certificate chain.
     */
    public static String toString(@Nullable X509Certificate[] x509CertificateChain) {
        StringBuilder sb = new StringBuilder("Certificate chain:");
        if (x509CertificateChain != null) {
            for (int i = 0; i < x509CertificateChain.length; i++) {
                X509Certificate x509Certificate = x509CertificateChain[i];
                if (x509Certificate == null) {
                    continue;
                }
                sb.append("\n[").append(i).append(']').append(toString(x509Certificate));
            }
        }
        return sb.toString();
    }

    /**
     * Append any alternative names from an X509 certificate.
     */
    private static void appendAlternativeNames(StringBuilder sb, Collection<List<?>> alternativeNames, String prefix) {
        // If this certificate does not contain a SubjectAltName extension, null is returned.
        // Otherwise, a Collection is returned with an entry representing each GeneralName included in the extension.
        // Each entry is a List whose first entry is an Integer (the name type, 0-8) and whose second entry is a String or
        // a byte array (the name, in string or ASN.1 DER encoded form, respectively).
        if (alternativeNames != null) {
            sb.append(prefix);
            for (List<?> alternativeName : alternativeNames) {
                sb.append("\n  Type: ").append(alternativeName.get(0)).append(" Name: ");
                Object name = alternativeName.get(1);
                if (name instanceof byte[]) {
                    sb.append(Arrays.toString((byte[])name));
                }
                else {
                    sb.append(name);
                }
            }
        }
    }
}
