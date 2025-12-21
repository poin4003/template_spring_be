package com.template.app.features.user.api.v1.dto.response;

import java.util.UUID;

import lombok.Data;

@Data
public class UserResponse {

    private UUID userId;

    private String userEmail;

    private String userStatus;

    private String createdAt;

    private String updatedAt;
}
