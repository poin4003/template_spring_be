package com.app.features.auth.service;

import java.util.UUID;

import com.app.features.auth.service.schema.command.LoginCmd;
import com.app.features.auth.service.schema.result.LoginResult;

public interface AuthService {
    LoginResult login(LoginCmd cmd);

    LoginResult refreshToken(String refreshToken);

    void logout(UUID keyStoreId, UUID userId);
}
