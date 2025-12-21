package com.template.app.features.auth.service.schema;

import java.util.UUID;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import com.template.app.config.mapstruct.GlobalMapperConfig;
import com.template.app.features.auth.entity.KeyStoreEntity;
import com.template.app.features.auth.service.schema.result.KeyStoreResult;
import com.template.app.features.auth.service.schema.result.LoginResult;
import com.template.app.features.auth.service.schema.result.UserPrincipal;
import com.template.app.features.user.entity.UserBaseEntity;

@Mapper(componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    uses = { GlobalMapperConfig.class }
)
public interface AuthCoreMapStruct {

    @Mapping(target = "authorities", expression = "java(userBase.getAuthorities())")
    UserPrincipal toUserPrincipal(UserBaseEntity userBase);

    KeyStoreResult toKeyStoreResult(KeyStoreEntity keyStore);

    LoginResult toLoginResult(
        String accessToken, 
        String refreshToken,
        UUID userId,
        String userEmail
    );
}
