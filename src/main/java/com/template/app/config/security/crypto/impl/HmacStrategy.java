package com.template.app.config.security.crypto.impl;

import java.security.Key;
import java.security.SecureRandom;
import java.util.Base64;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.template.app.config.security.crypto.CryptoStrategy;
import com.template.app.config.security.crypto.dto.KeyPairDto;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
@ConditionalOnProperty(name = "app.security.algo", havingValue = "HMAC", matchIfMissing = true)
public class HmacStrategy implements CryptoStrategy {

    @Override
    public KeyPairDto generateKey() {
        byte[] keyBytes = new byte[64];
        new SecureRandom().nextBytes(keyBytes);
        String secret = Base64.getEncoder().encodeToString(keyBytes);

        return new KeyPairDto(secret, secret);
    }

    @Override
    public Key getSigningKey(String keyStr) {
        return getHmacKey(keyStr);
    }

    @Override
    public Key getVerifyingKey(String keyStr) {
        return getHmacKey(keyStr);
    }

    @Override
    public SignatureAlgorithm getAlgorithm() {
        return SignatureAlgorithm.HS512;
    }

    private Key getHmacKey(String keyStr) {
        byte[] keyBytes = Base64.getDecoder().decode(keyStr);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
