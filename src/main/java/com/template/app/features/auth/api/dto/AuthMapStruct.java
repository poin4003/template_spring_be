package com.template.app.features.auth.api.dto;

import org.mapstruct.ReportingPolicy;

import com.template.app.config.mapstruct.GlobalMapperConfig;
import com.template.app.features.auth.api.dto.response.LoginResponse;
import com.template.app.features.auth.service.schema.result.LoginResult;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    uses = { GlobalMapperConfig.class }
)
public interface AuthMapStruct {
    LoginResponse toLoginResponse(LoginResult result);
}
