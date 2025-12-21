package com.template.app.features.auth.service.schema.result;

import java.util.UUID;

import lombok.Data;

@Data
public class KeyStoreResult {

    private UUID keyStoreId;

    private UUID userId;

    private String publicKey;

    private String privateKey;

    private String refreshToken;
}
