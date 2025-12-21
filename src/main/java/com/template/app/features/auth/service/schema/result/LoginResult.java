package com.template.app.features.auth.service.schema.result;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResult {

    private String accessToken;

    private String refreshToken;

    private UUID userId;

    private String userEmail;
}
