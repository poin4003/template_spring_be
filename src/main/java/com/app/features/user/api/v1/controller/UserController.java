package com.app.features.user.api.v1.controller;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

import com.app.core.controller.BaseController;
import com.app.core.dto.PaginationResponse;
import com.app.core.vo.ResultMessage;
import com.app.features.user.api.v1.dto.UserMapStruct;
import com.app.features.user.api.v1.dto.request.UserCreationRequest;
import com.app.features.user.api.v1.dto.response.UserResponse;
import com.app.features.user.service.UserService;
import com.app.features.user.service.schema.command.UserCreationCmd;
import com.app.features.user.service.schema.query.UserQuery;
import com.app.features.user.service.schema.result.UserResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
@Tag(name = "USER Management V1", description = "User docs")
public class UserController extends BaseController {

    private final UserService userService;
    private final UserMapStruct userMapStruct;

    @PostMapping("/")
    @PreAuthorize("hasRole('ADMIN_SYSTEM')")
    @Operation(summary = "Create new user (Admin only)")
    public ResponseEntity<ResultMessage<UserResponse>> createUser(
        @Valid @RequestBody UserCreationRequest request
    ) {
        UserCreationCmd cmd = userMapStruct.toCommand(request);

        UserResult result = userService.createUser(cmd);

        return Created(userMapStruct.toResponse(result));
    }

    @GetMapping("/")
    @PreAuthorize("hasRole('ADMIN_SYSTEM')")
    @Operation(summary = "Get many user (Admin only)")
    public ResponseEntity<ResultMessage<PaginationResponse<UserResponse>>> getManyUsers(
        @ParameterObject UserQuery query
    ) {
        PaginationResponse<UserResponse> response = userService.getManyUsers(query)
                                                            .map(userMapStruct::toResponse);

        return OK("Get many user success", response);   
    }
}
