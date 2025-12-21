package com.template.app.features.user.service.schema;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import com.template.app.config.mapstruct.GlobalMapperConfig;
import com.template.app.features.user.entity.UserBaseEntity;
import com.template.app.features.user.service.schema.result.UserResult;

@Mapper(componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    uses = { GlobalMapperConfig.class }
)
public interface UserCoreMapStruct {

    UserResult toUserResult(UserBaseEntity userBase);
}
