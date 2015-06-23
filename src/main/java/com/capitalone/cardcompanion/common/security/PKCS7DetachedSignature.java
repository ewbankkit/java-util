//
// Copyright (C) Capital One Labs.
//

package com.capitalone.cardcompanion.common.security;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.io.ByteSource;
import org.bouncycastle.cert.jcajce.JcaCertStore;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSProcessableFile;
import org.bouncycastle.cms.CMSSignedDataGenerator;
import org.bouncycastle.cms.CMSTypedData;
import org.bouncycastle.cms.SignerInfoGenerator;
import org.bouncycastle.cms.jcajce.JcaSignerInfoGeneratorBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.bouncycastle.util.Store;

import javax.annotation.Nullable;
import java.io.File;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Collections;

import static org.bouncycastle.jce.provider.BouncyCastleProvider.PROVIDER_NAME;

/**
 * Create a PKCS #7 detached signature.
 * Not thread-safe.
 */
public final class PKCS7DetachedSignature {
    private final CMSSignedDataGenerator generator;

    static {
        if (Security.getProvider(PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }

    /**
     * Constructor.
     */
    public PKCS7DetachedSignature(
        PrivateKey                privateKey,
        X509Certificate           signingCertificate,
        @Nullable
        Iterable<X509Certificate> additionalCertificates
    ) throws Exception {
        Preconditions.checkNotNull(privateKey);
        Preconditions.checkNotNull(signingCertificate);

        ContentSigner contentSigner = new JcaContentSignerBuilder("SHA1withRSA").setProvider(PROVIDER_NAME).build(privateKey);
        DigestCalculatorProvider digestCalculatorProvider = new JcaDigestCalculatorProviderBuilder().setProvider(PROVIDER_NAME).build();
        SignerInfoGenerator signerInfoGenerator = new JcaSignerInfoGeneratorBuilder(digestCalculatorProvider).build(contentSigner, signingCertificate);
        Collection<X509Certificate> certificates = (additionalCertificates == null) ?
            Collections.singleton(signingCertificate) : ImmutableList.<X509Certificate>builder().addAll(additionalCertificates).add(signingCertificate).build();
        Store store = new JcaCertStore(certificates);

        generator = new CMSSignedDataGenerator();
        generator.addSignerInfoGenerator(signerInfoGenerator);
        generator.addCertificates(store);
    }

    /**
     * Signs the specified data.
     */
    public ByteSource sign(byte[] data) throws Exception {
        Preconditions.checkNotNull(data);

        return sign(new CMSProcessableByteArray(data));
    }

    /**
     * Signs the contents of the specified file.
     */
    public ByteSource sign(File file) throws Exception {
        Preconditions.checkNotNull(file);

        return sign(new CMSProcessableFile(file));
    }

    private ByteSource sign(CMSTypedData content) throws Exception {
        return ByteSource.wrap(generator.generate(content, false).getEncoded());
    }
}
