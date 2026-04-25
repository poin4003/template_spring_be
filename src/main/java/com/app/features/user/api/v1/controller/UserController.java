package com.app.features.user.api.v1.controller;

import org.modelmapper.ModelMapper;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.app.core.response.ApiResult;
import com.app.features.user.api.v1.dto.request.CreateUserDto;
import com.app.features.user.api.v1.dto.response.UserDto;
import com.app.features.user.cqrs.command.CreateUserCmd;
import com.app.features.user.cqrs.query.GetManyUserQuery;
import com.app.features.user.cqrs.result.UserResult;
import com.app.features.user.entity.UserBaseEntity;

import an.awesome.pipelinr.Pipeline;
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
public class UserController {

    private final ModelMapper modelMapper;
    private final Pipeline pipeline;

    @PostMapping("/")
    @PreAuthorize("hasRole('ADMIN_SYSTEM')")
    @Operation(summary = "Create new user (Admin only)")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResult<UserDto> createUser(
            @Valid @RequestBody CreateUserDto req) {
        CreateUserCmd cmd = modelMapper.map(req, CreateUserCmd.class);

        UserResult result = pipeline.send(cmd);

        return ApiResult.ok(modelMapper.map(result, UserDto.class), "Create user sucess!");
    }

    @GetMapping("/")
    @PreAuthorize("hasRole('ADMIN_SYSTEM')")
    @Operation(summary = "Get many user (Admin only)")
    public ApiResult<Page<UserDto>> getManyUsers(
            @ParameterObject @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        GetManyUserQuery query = new GetManyUserQuery();

        query.setPageable(pageable);

        Page<UserResult> results = pipeline.send(query);

        Page<UserDto> res = results.map(result -> modelMapper.map(result, UserDto.class));

        return ApiResult.ok(res, "Get many user success");
    }

    @GetMapping("/info")
    public ApiResult<UserBaseEntity> getUserInfo(Authentication authentication) {
        UserBaseEntity principal = (UserBaseEntity) authentication.getPrincipal();
        return ApiResult.ok(principal, "Get user principal success!");
    }
}
