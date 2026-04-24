package com.app.features.rbac.api.v1.controller;

import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.core.response.ApiResult;
import com.app.features.user.entity.UserBaseEntity;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
@Tag(name = "RBAC Management V1", description = "RBAC docs")
public class RbacController {
    @GetMapping("/info") 
    public ApiResult<UserBaseEntity> getUserInfo(Authentication authentication) {
        UserBaseEntity principal = (UserBaseEntity)authentication.getPrincipal();
        return ApiResult.ok(principal, "Get user principal success!");
    }
}
