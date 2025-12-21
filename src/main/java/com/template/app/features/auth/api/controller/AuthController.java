package com.template.app.features.auth.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.template.app.core.controller.BaseController;
import com.template.app.core.vo.ResultMessage;
import com.template.app.features.auth.api.dto.AuthMapStruct;
import com.template.app.features.auth.api.dto.request.LoginRequest;
import com.template.app.features.auth.api.dto.request.RefreshTokenRequest;
import com.template.app.features.auth.api.dto.response.LoginResponse;
import com.template.app.features.auth.service.AuthService;
import com.template.app.features.auth.service.schema.command.LoginCmd;
import com.template.app.features.auth.service.schema.result.LoginResult;
import com.template.app.features.auth.service.schema.result.UserPrincipal;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@Slf4j
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication V1", description = "Auth docs")
public class AuthController extends BaseController {

    private final AuthService authService;
    private final AuthMapStruct authMapStruct;

    @PostMapping("/login")
    public ResponseEntity<ResultMessage<LoginResponse>> login(
        @RequestBody LoginRequest request
        // @ClientIp String ipAddress
    ) {
        String ipAddress = "192.168.1.100";

        LoginCmd cmd = LoginCmd.builder()
                            .userEmail(request.getUserEmail())
                            .userPassword(request.getUserPassword())
                            .ipAddress(ipAddress)
                            .build();

        LoginResult result = authService.login(cmd);

        return OK("Login success", authMapStruct.toLoginResponse(result)); 
    }

    @PostMapping("/refresh")
    public ResponseEntity<ResultMessage<LoginResponse>> refreshToken(
        @RequestBody RefreshTokenRequest request
    ) {
        LoginResult result = authService.refreshToken(request.getRefreshToken());

        return OK("Refresh token success", authMapStruct.toLoginResponse(result));
    }

    @PostMapping("/logout")
    public ResponseEntity<ResultMessage<Void>> logout(@AuthenticationPrincipal UserPrincipal currentUser) {
        if (currentUser != null && currentUser.getKeyStore() != null)  {
            authService.logout(
                currentUser.getKeyStore().getKeyStoreId(),
                currentUser.getUserId()
            );
        }
        return OK("Logout success", null);
    }
}
