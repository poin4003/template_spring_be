package com.template.app.config.security.crypto.impl;

import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.template.app.config.security.crypto.CryptoStrategy;
import com.template.app.config.security.crypto.dto.KeyPairDto;

import io.jsonwebtoken.SignatureAlgorithm;

@Component
@ConditionalOnProperty(name = "app.security.algo", havingValue = "RSA")
public class RsaStrategy implements CryptoStrategy{
    
    @Override
    public KeyPairDto generateKey() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048);
            KeyPair pair = keyGen.generateKeyPair();
            String privateKey = Base64.getEncoder().encodeToString(pair.getPrivate().getEncoded());
            String publicKey = Base64.getEncoder().encodeToString(pair.getPublic().getEncoded());
            return new KeyPairDto(privateKey, publicKey);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Key getSigningKey(String keyStr) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(keyStr);
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
            return KeyFactory.getInstance("RSA").generatePrivate(spec);
        } catch (Exception e) { 
            throw new RuntimeException(e);
        }
    }

    @Override 
    public Key getVerifyingKey(String keyStr) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(keyStr);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            return KeyFactory.getInstance("RSA").generatePublic(spec);           
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public SignatureAlgorithm getAlgorithm() {
        return SignatureAlgorithm.RS256;
    }
}
