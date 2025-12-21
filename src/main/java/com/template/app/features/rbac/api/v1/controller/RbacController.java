package com.template.app.features.rbac.api.v1.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.security.core.Authentication;

import com.template.app.core.vo.ResultMessage;
import com.template.app.features.user.entity.UserBaseEntity;

import io.swagger.v3.oas.annotations.tags.Tag;

import com.template.app.core.controller.BaseController;

import lombok.RequiredArgsConstructor;

@RestController
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
@Tag(name = "RBAC Management V1", description = "RBAC docs")
public class RbacController extends BaseController {
    @GetMapping("/info") 
    public ResponseEntity<ResultMessage<UserBaseEntity>> getUserInfo(Authentication authentication) {
        UserBaseEntity principal = (UserBaseEntity)authentication.getPrincipal();
        return OK(principal);
    }
}
