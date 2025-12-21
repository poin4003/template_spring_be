package com.template.app.features.user.service.schema.result;

import java.time.LocalDateTime;
import java.util.UUID;

import com.template.app.features.user.enums.UserStatusEnum;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResult {

    private UUID userId;

    private String userEmail;

    private UserStatusEnum userStatus;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
