package org.dasein.cloud.azurepack.utils;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.pkcs.RSAPrivateKey;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.util.PrivateKeyFactory;
import org.bouncycastle.jcajce.provider.asymmetric.x509.PEMUtil;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemObjectParser;
import org.bouncycastle.util.io.pem.PemReader;
import org.dasein.cloud.ContextRequirements;
import org.dasein.cloud.InternalException;
import org.dasein.cloud.ProviderContext;
import org.dasein.cloud.azurepack.AzurePackCloud;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.security.cert.*;
import java.security.cert.Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.List;

/**
 * X509 certficate management for integration with Azure's outmoded form of authentication.
 * @author George Reese (george.reese@imaginary.com)
 * @author Tim Freeman (timothy.freeman@enstratus.com)
 * @since 2012.04.1
 * @version 2012.04.1
 */
public class AzureX509 {
    static public final String ENTRY_ALIAS = "";
    static public final String PASSWORD    = "memory";

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    private KeyStore keystore;

    public AzureX509(AzurePackCloud provider) throws InternalException {
        ProviderContext ctx = provider.getContext();
        try {
            String apiShared = "";
            String apiSecret = "";
            try {
                List<ContextRequirements.Field> fields = provider.getContextRequirements().getConfigurableValues();
                for(ContextRequirements.Field f : fields ) {
                    if(f.type.equals(ContextRequirements.FieldType.KEYPAIR)){
                        byte[][] keyPair = (byte[][])ctx.getConfigurationValue(f);
                        apiShared = new String(keyPair[0], "utf-8");
                        apiSecret = new String(keyPair[1], "utf-8");
                    }
                }
            }
            catch (UnsupportedEncodingException ignore) {}
            //  System.out.println(apiShared);
            //  System.out.println(apiSecret);

            X509Certificate certificate = certFromString(apiShared);
            PrivateKey privateKey = keyFromString(apiSecret);

            keystore = createJavaKeystore(certificate, privateKey);
        }
        catch( Exception e ) {
            throw new InternalException(e);
        }
    }

    private X509Certificate certFromString(String pem) throws Exception {
        PemObject pemObject = (PemObject) readPemObject(pem);
        ByteArrayInputStream inputStream= new ByteArrayInputStream(pemObject.getContent());
        try {
            CertificateFactory certFact = CertificateFactory.getInstance("X.509");
            return (X509Certificate) certFact.generateCertificate(inputStream);
        } catch (CertificateException e) {
            throw new Exception("problem parsing cert: " + e.toString(),e);
        }
    }

    private KeyStore createJavaKeystore(X509Certificate cert, PrivateKey key) throws NoSuchProviderException, KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException {
        KeyStore store = KeyStore.getInstance("JKS", "SUN");
        char[] pw = PASSWORD.toCharArray();

        store.load(null, pw);
        store.setKeyEntry(ENTRY_ALIAS, key, pw, new java.security.cert.Certificate[] {cert});
        return store;
    }
    public KeyStore getKeystore() {
        return keystore;
    }

    private PrivateKey keyFromString(String pem) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        PemObject pemObject = (PemObject) readPemObject(pem);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(pemObject.getContent());
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(keySpec);
    }

    private Object readPemObject(String pemString) throws IOException {
        StringReader strReader = new StringReader(pemString);
        PemReader pemReader = new PemReader(strReader);

        try {
            return pemReader.readPemObject();
        }
        finally {
            strReader.close();
            pemReader.close();
        }
    }
}
