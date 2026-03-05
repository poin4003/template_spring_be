package com.app.features.auth.api.dto.request;

import lombok.Data;

@Data
public class RefreshTokenRequest {
    
    private String refreshToken;
}
