//
// Kit's Java Utils.
//

package com.github.ewbankkit.security;

import com.github.ewbankkit.base.Either;
import com.google.common.base.Preconditions;
import com.google.common.io.ByteProcessor;
import com.google.common.io.ByteSource;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.ECPointUtil;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.bouncycastle.jce.spec.ECNamedCurveSpec;

import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.X509Certificate;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPublicKeySpec;

import static org.bouncycastle.jce.provider.BouncyCastleProvider.PROVIDER_NAME;

/**
 * SHA-256 with ECDSA signature.
 * Use the Bouncy Castle provider.
 */
@SuppressWarnings("NullableProblems")
@ThreadSafe
public final class SHA256WithECDSASignature {
    private static final String EC_NAMED_CURVE      = "prime256v1";
    private static final String KEY_ALGORITHM       = "ECDSA";
    private static final String SIGNATURE_ALGORITHM = "SHA256withECDSA";

    @Nullable
    private final PrivateKey                         privateKey;
    private final Either<PublicKey, X509Certificate> publicKeyOrX509Certificate;

    static {
        if (Security.getProvider(PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }

    /**
     * Constructor.
     */
    public SHA256WithECDSASignature(KeyPair keyPair) {
        Preconditions.checkNotNull(keyPair);

        privateKey = keyPair.getPrivate();
        publicKeyOrX509Certificate = Either.left(keyPair.getPublic());
    }

    /**
     * Constructor.
     */
    public SHA256WithECDSASignature(X509CertificateAndPrivateKey x509CertificateAndPrivateKey) {
        Preconditions.checkNotNull(x509CertificateAndPrivateKey);

        privateKey = x509CertificateAndPrivateKey.getPrivateKey();
        publicKeyOrX509Certificate = Either.right(x509CertificateAndPrivateKey.getX509Certificate());
    }

    /**
     * Signs the specified data.
     */
    public byte[] sign(ByteSource byteSource) throws GeneralSecurityException {
        Preconditions.checkNotNull(byteSource);

        final Signature sSignature = Signature.getInstance(SIGNATURE_ALGORITHM, PROVIDER_NAME);
        sSignature.initSign(privateKey);

        try {
            return byteSource.read(new ByteProcessor<byte[]>() {
                /**
                 * This method will be called for each chunk of bytes in an input stream.
                 */
                @Override
                public boolean processBytes(byte[] buf, int off, int len) throws IOException {
                    try {
                        sSignature.update(buf, off, len);
                    }
                    catch (SignatureException ex) {
                        throw new IOException(ex);
                    }
                    return true;
                }

                /**
                 * Return the result of processing all the bytes.
                 */
                @Override
                public byte[] getResult() {
                    try {
                        return sSignature.sign();
                    }
                    catch (SignatureException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            });
        }
        catch (Exception ex) {
            Throwable cause = ex.getCause();
            if (cause instanceof SignatureException) {
                throw (SignatureException)cause;
            }
            throw new GeneralSecurityException(ex);
        }
    }

    /**
     * Verifies the specified signature.
     */
    @SuppressWarnings("UnnecessaryUnboxing")
    public boolean verify(ByteSource byteSource, final byte[] signature) throws GeneralSecurityException {
        Preconditions.checkNotNull(byteSource);
        Preconditions.checkNotNull(signature);

        final Signature vSignature = Signature.getInstance(SIGNATURE_ALGORITHM, PROVIDER_NAME);
        if (publicKeyOrX509Certificate.isLeft()) {
            vSignature.initVerify(publicKeyOrX509Certificate.getLeft());
        }
        else {
            vSignature.initVerify(publicKeyOrX509Certificate.getRight());
        }

        try {
            return byteSource.read(new ByteProcessor<Boolean>() {
                /**
                 * This method will be called for each chunk of bytes in an input stream.
                 */
                @Override
                public boolean processBytes(byte[] buf, int off, int len) throws IOException {
                    try {
                        vSignature.update(buf, off, len);
                    }
                    catch (SignatureException ex) {
                        throw new IOException(ex);
                    }
                    return true;
                }

                /**
                 * Return the result of processing all the bytes.
                 */
                @Override
                @SuppressWarnings("UnnecessaryBoxing")
                public Boolean getResult() {
                    try {
                        return Boolean.valueOf(vSignature.verify(signature));
                    }
                    catch (SignatureException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }).booleanValue();
        }
        catch (Exception ex) {
            Throwable cause = ex.getCause();
            if (cause instanceof SignatureException) {
                throw (SignatureException)cause;
            }
            throw new GeneralSecurityException(ex);
        }
    }

    /**
     * Generates a public key from the specified bytes.
     */
    public static PublicKey generatePublicKey(ByteSource byteSource) throws IOException, GeneralSecurityException {
        ECNamedCurveParameterSpec namedCurveSpec = ECNamedCurveTable.getParameterSpec(EC_NAMED_CURVE);
        ECParameterSpec ecSpec = new ECNamedCurveSpec(
            namedCurveSpec.getName(),
            namedCurveSpec.getCurve(),
            namedCurveSpec.getG(),
            namedCurveSpec.getN()
        );
        ECPublicKeySpec ecPublicKeySpec = new ECPublicKeySpec(
            ECPointUtil.decodePoint(ecSpec.getCurve(), byteSource.read()),
            ecSpec
        );

        return KeyFactory.getInstance(KEY_ALGORITHM, PROVIDER_NAME).generatePublic(ecPublicKeySpec);
    }

    /**
     * Generates a new key pair.
     */
    static KeyPair generateKeyPair(int len) throws GeneralSecurityException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KEY_ALGORITHM, PROVIDER_NAME);
        keyPairGenerator.initialize(len);
        return keyPairGenerator.generateKeyPair();
    }
}
