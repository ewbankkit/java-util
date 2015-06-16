/**
 * Copyright (C) Network Solutions, LLC.
 */

package com.netsol.adagent.util.ssl;

import java.security.GeneralSecurityException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.net.ssl.X509TrustManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import sun.security.x509.AccessDescription;
import sun.security.x509.AuthorityInfoAccessExtension;
import sun.security.x509.X509CertImpl;

/**
 * Base trust manager.
 */
public abstract class BaseTrustManager implements X509TrustManager {
    public static final String sccsId = "@(#) adagent-util_d16.16.0.latest 08/09/12 09:55:29 BaseTrustManager.java NSI";

    // For historical reasons we use the StandardTrustManager's class as the logger name.
    public static final Log logger = LogFactory.getLog(StandardTrustManager.class);

    protected final X509TrustManager x509TrustManager;

    /**
     * Constructor.
     */
    protected BaseTrustManager(X509TrustManager x509TrustManager) {
        this.x509TrustManager = x509TrustManager;
    }

    /**
     * Build a certificate path to a trusted root and return if it can be
     * validated and is trusted for client SSL authentication based on the
     * authentication type.
     */
    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        x509TrustManager.checkClientTrusted(chain, authType);
    }

    /**
     * Build a certificate path to a trusted root and return if it can be
     * validated and is trusted for server SSL authentication based on the
     * authentication type.
     */
    public abstract void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException;

    /**
     * Return an array of certificate authority certificates which are
     * trusted for authenticating peers.
     */
    public X509Certificate[] getAcceptedIssuers() {
        return x509TrustManager.getAcceptedIssuers();
    }

    /**
     * Return a loggable representation of a X509 certificate.
     */
    public static CharSequence loggableCertificate(X509Certificate x509Certificate) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("\nSubject   : ").append(x509Certificate.getSubjectX500Principal()).
               append("\nIssuer    : ").append(x509Certificate.getIssuerDN()).
               append("\nValid from: ").append(x509Certificate.getNotBefore()).
               append("\nValid to  : ").append(x509Certificate.getNotAfter()).
               append("\nX509v3 extensions").
               append("\n-----------------");
            appendAlternativeNames(sb, x509Certificate.getSubjectAlternativeNames(), "\nSubject alternative names:");
            appendAlternativeNames(sb, x509Certificate.getIssuerAlternativeNames(),  "\nIssuer alternative names:");
            if (x509Certificate instanceof X509CertImpl) {
                AuthorityInfoAccessExtension authorityInfoAccessExtension = ((X509CertImpl)x509Certificate).getAuthorityInfoAccessExtension();
                if (authorityInfoAccessExtension != null) {
                    sb.append("\nAuthority information access:");
                    @SuppressWarnings("unchecked")
                    List<AccessDescription> accessDescriptions = (List<AccessDescription>)authorityInfoAccessExtension.get(AuthorityInfoAccessExtension.DESCRIPTIONS);
                    if (accessDescriptions != null) {
                        for (AccessDescription accessDescription : accessDescriptions) {
                            sb.append("\n  Access method  : ").append(accessDescription.getAccessMethod()).
                               append("\n  Access location: ").append(accessDescription.getAccessLocation());
                        }
                    }
                }
            }

            return sb;
        }
        catch (Exception ex) {}

        return "";
    }

    /**
     * Utility method to test if a certificate is self-issued.
     * This is the case iff the subject and issuer X500Principals are equal.
     */
    protected static boolean isSelfIssued(X509Certificate x509Certificate) {
        return x509Certificate.getSubjectX500Principal().equals(x509Certificate.getIssuerX500Principal());
    }

    /**
     * Utility method to test if a certificate is self-signed.
     * This is the case iff the subject and issuer X500Principals are equal AND
     * the certificate's subject public key can be used to verify the certificate.
     * In case of exception, returns false.
     */
    public static boolean isSelfSigned(X509Certificate x509Certificate, String sigProvider) {
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
            catch (GeneralSecurityException ex) {}
        }
        return false;
    }

    /**
     * Log the certificate chain.
     */
    protected static void logCertificateChain(X509Certificate[] x509CertificateChain) {
        if (!logger.isDebugEnabled()) {
            return;
        }

        StringBuilder message = new StringBuilder("Certificate chain:");
        if (x509CertificateChain != null) {
            for (int i = 0; i < x509CertificateChain.length; i++) {
                X509Certificate x509Certificate = x509CertificateChain[i];
                if (x509Certificate == null) {
                    continue;
                }
                message.append("\n[").append(i).append(']').append(loggableCertificate(x509Certificate));
            }
        }
        logger.debug(message.toString());
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
