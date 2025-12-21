package com.template.app.features.user.api.v1.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import com.template.app.config.mapstruct.GlobalMapperConfig;
import com.template.app.features.user.api.v1.dto.request.UserCreationRequest;
import com.template.app.features.user.api.v1.dto.response.UserResponse;
import com.template.app.features.user.service.schema.command.UserCreationCmd;
import com.template.app.features.user.service.schema.result.UserResult;

@Mapper(componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    uses = { GlobalMapperConfig.class }
)
public interface UserMapStruct {
    
    UserCreationCmd toCommand(UserCreationRequest request);

    @Mapping(target = "userId", source = "userId", qualifiedByName = "uuidToString")
    @Mapping(target = "userStatus", source = "userStatus", qualifiedByName = "enumToString")
    UserResponse toResponse(UserResult result);

}
