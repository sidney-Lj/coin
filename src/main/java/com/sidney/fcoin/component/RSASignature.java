package com.sidney.fcoin.component;

import com.google.common.base.Preconditions;
import com.sidney.fcoin.component.exception.InitialKeyException;
import com.sidney.fcoin.component.exception.SignatureFailedException;
import com.sidney.fcoin.component.exception.VerifyFailedException;
import com.sidney.fcoin.component.properties.KeyPrefer;
import com.sidney.fcoin.component.properties.RSASignatureProperties;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Enumeration;

public abstract class RSASignature {

    private String signAlgorithm = "SHA1withRSA";   //签名算法
    private PrivateKey privateKey;
    private PublicKey publicKey;

    public RSASignature(RSASignatureProperties properties) {
        KeyPrefer prefer = properties.getKeyPrefer();
        switch (prefer) {
            case CERTIFILE:
                initWithCertiFile(properties);
                break;
            case STRINGVALUE:
                initWithStringKey(properties);
                break;
            default:
                break;
        }
    }

    public RSASignature(InputStream p12InputStream, String p12password, InputStream thirdCrtInputStream) {
        this.privateKey = getPrivateKey(getKeyStore(p12InputStream, p12password.toCharArray()), p12password);
        this.publicKey = getCertificatePublicKey(getCertificate(thirdCrtInputStream));
    }

    public RSASignature(InputStream p12InputStream, String p12password, InputStream thirdCrtInputStream, String signAlgorithm) {
        this.privateKey = getPrivateKey(getKeyStore(p12InputStream, p12password.toCharArray()), p12password);
        this.publicKey = getCertificatePublicKey(getCertificate(thirdCrtInputStream));
        this.signAlgorithm = signAlgorithm;
    }

    protected void initWithStringKey(RSASignatureProperties properties) {
        RSASignatureProperties.StringKeyProperties stringKey = properties.getStringKey();
        String ownPriviteKey = stringKey.getOwnPriviteKey();
        Preconditions.checkArgument(StringUtils.isNotBlank(ownPriviteKey), "ownPriviteKey not specified");
        String thirdPublicKey = stringKey.getThirdPublicKey();
        Preconditions.checkArgument(StringUtils.isNotBlank(thirdPublicKey), "thirdPublicKey not specified");
        byte[] priviteKeyBytes = Base64.decodeBase64(ownPriviteKey);
        byte[] publicKeyBytes = Base64.decodeBase64(thirdPublicKey);
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            this.privateKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(priviteKeyBytes));
            this.publicKey = keyFactory.generatePublic(new X509EncodedKeySpec(publicKeyBytes));
        } catch (Exception e) {
            throw new InitialKeyException("init RSA keys error", e);
        }
        this.signAlgorithm = properties.getSignAlgorithm();
    }

    protected void initWithCertiFile(RSASignatureProperties properties) {
        DefaultResourceLoader resourceLoader = new DefaultResourceLoader();
        RSASignatureProperties.KeyStoreProperties keyStore = properties.getKeyStore();
        String p12 = keyStore.getFileSystemPath();
        if (StringUtils.isBlank(p12)) {
            p12 = keyStore.getClassPathFile();
        }
        Preconditions.checkArgument(StringUtils.isNotBlank(p12), "keystore not specified");
        Resource p12Resource = resourceLoader.getResource(p12);
        if (!p12Resource.exists()) {
            throw new InitialKeyException("resource not exists:" + p12);
        }
        RSASignatureProperties.CertificateProperties thirdCertificate = properties.getThirdCertificate();
        String third_certificate = thirdCertificate.getFileSystemPath();
        if (StringUtils.isBlank(third_certificate)) {
            third_certificate = thirdCertificate.getClassPathFile();
        }
        Preconditions.checkArgument(StringUtils.isNotBlank(third_certificate), "thirdCertificate not specified");
        Resource thirdCerResource = resourceLoader.getResource(third_certificate);
        if (!thirdCerResource.exists()) {
            throw new InitialKeyException("resource not exists:" + third_certificate);
        }
        String password = keyStore.getPassword();
        Preconditions.checkArgument(StringUtils.isNotBlank(password), "keystore password not specified");
        try (InputStream p12Input = p12Resource.getInputStream(); InputStream thirdInput = thirdCerResource.getInputStream()) {
            this.privateKey = getPrivateKey(getKeyStore(p12Input, password.toCharArray()), password);
            this.publicKey = getCertificatePublicKey(getCertificate(thirdInput));
            this.signAlgorithm = properties.getSignAlgorithm();
        } catch (IOException e) {
            throw new InitialKeyException("read resource error", e);
        }
    }

    /**
     * 签名
     *
     * @param input 待签字符串
     * @return 签名字符串
     */
    public String sign(String input) {
        try {
            byte[] bytes = input.getBytes("UTF-8");
            byte[] signed = sign(bytes);
            return Base64.encodeBase64String(signed);
        } catch (UnsupportedEncodingException e) {
            throw new SignatureFailedException("sign error.", e);
        }
    }

    public byte[] sign(byte[] input) {
        try {
            Signature signature = Signature.getInstance(signAlgorithm);
            signature.initSign(privateKey);
            signature.update(input);
            return signature.sign();
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            throw new SignatureFailedException("sign error.", e);
        }
    }

    /**
     * 验签
     *
     * @param signed   签名后的字符串
     * @param unsigned 未签名的原始字符串
     * @return 签名是否匹配
     */
    public boolean verify(String signed, String unsigned) {
        byte[] signedBytes = Base64.decodeBase64(signed);
        try {
            return verify(signedBytes, unsigned.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new VerifyFailedException("verify error.", e);
        }
    }

    public boolean verify(byte[] signedBytes, byte[] unsignedBytes) {
        try {
            Signature signature = Signature.getInstance(signAlgorithm);
            signature.initVerify(publicKey);
            signature.update(unsignedBytes);
            return signature.verify(signedBytes);
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            throw new VerifyFailedException("verify error.", e);
        }
    }

    private Certificate getCertificate(InputStream certFileInputStream) {
        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            return cf.generateCertificate(certFileInputStream);
        } catch (CertificateException e) {
            throw new InitialKeyException("init certificate error.", e);
        }
    }

    private KeyStore getKeyStore(InputStream ksInputStream, char[] pwds) {
        try {
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            keyStore.load(ksInputStream, pwds);
            return keyStore;
        } catch (Exception e) {
            throw new InitialKeyException("init keystore error.", e);
        }
    }

    private String getKeyAlias(KeyStore keyStore) {
        try {
            Enumeration<String> aliases = keyStore.aliases();
            if (aliases.hasMoreElements()) {
                String alias = aliases.nextElement();
                if (keyStore.isKeyEntry(alias)) {
                    return alias;
                }
            }
        } catch (KeyStoreException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    private PrivateKey getPrivateKey(KeyStore keyStore, String pwd) {
        try {
            String alias = getKeyAlias(keyStore);
            return (PrivateKey) keyStore.getKey(alias, pwd.toCharArray());
        } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException e) {
            throw new RuntimeException(e);
        }
    }

    private PublicKey getKeyStorePublicKey(KeyStore keyStore) {
        try {
            String alias = getKeyAlias(keyStore);
            return keyStore.getCertificate(alias).getPublicKey();
        } catch (KeyStoreException e) {
            throw new RuntimeException(e);
        }
    }

    private PublicKey getCertificatePublicKey(Certificate certificate) {
        return certificate.getPublicKey();
    }

}
