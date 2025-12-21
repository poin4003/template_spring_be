package com.template.app.features.auth.api.dto.response;

import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponse {

    private String accessToken;

    private String refreshToken;

    private UUID userId;

    private String userEmail;
}
