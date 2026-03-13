package com.app.features.auth.api.dto.response;

import lombok.Data;

@Data
public class LoginResponse {

    private String accessToken;

    private String refreshToken;
}
