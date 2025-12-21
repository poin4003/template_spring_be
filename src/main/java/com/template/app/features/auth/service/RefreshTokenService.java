package com.template.app.features.auth.service;

public interface RefreshTokenService {
    
    void cleanupExpiredConsumedTokens();
}
