package com.app.features.user.api.v1.controller;

import org.modelmapper.ModelMapper;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.core.controller.BaseController;
import com.app.core.vo.ResultMessage;
import com.app.features.user.api.v1.dto.query.GetManyUserDto;
import com.app.features.user.api.v1.dto.request.CreateUserDto;
import com.app.features.user.api.v1.dto.response.UserDto;
import com.app.features.user.cqrs.command.CreateUserCmd;
import com.app.features.user.cqrs.query.GetManyUserQuery;
import com.app.features.user.cqrs.result.UserResult;

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
public class UserController extends BaseController {

    private final ModelMapper modelMapper;
    private final Pipeline pipeline;

    @PostMapping("/")
    @PreAuthorize("hasRole('ADMIN_SYSTEM')")
    @Operation(summary = "Create new user (Admin only)")
    public ResponseEntity<ResultMessage<UserDto>> createUser(
        @Valid @RequestBody CreateUserDto req
    ) {
        CreateUserCmd cmd = modelMapper.map(req, CreateUserCmd.class);

        UserResult result = pipeline.send(cmd);

        return Created(modelMapper.map(result, UserDto.class));
    }

    @GetMapping("/")
    @PreAuthorize("hasRole('ADMIN_SYSTEM')")
    @Operation(summary = "Get many user (Admin only)")
    public ResponseEntity<ResultMessage<Page<UserDto>>> getManyUsers(
        @ParameterObject GetManyUserDto req
    ) {
        GetManyUserQuery query = modelMapper.map(req, GetManyUserQuery.class);

        Page<UserResult> results = pipeline.send(query);

        Page<UserDto> res = results.map(result -> modelMapper.map(result, UserDto.class));

        return OK("Get many user success", res);   
    }
}
