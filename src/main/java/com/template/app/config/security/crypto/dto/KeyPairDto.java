package com.template.app.config.security.crypto.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class KeyPairDto {

    private String privateKey;

    private String publicKey; 
}
