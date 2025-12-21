package com.template.app.config.security.crypto;

import java.security.Key;

import com.template.app.config.security.crypto.dto.KeyPairDto;

import io.jsonwebtoken.SignatureAlgorithm;

public interface CryptoStrategy {
    
    KeyPairDto generateKey();

    Key getSigningKey(String keyStr);

    Key getVerifyingKey(String keyStr);

    SignatureAlgorithm getAlgorithm();
}
