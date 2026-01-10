package com.template.app.features.ops.service.schema;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import com.template.app.config.mapstruct.GlobalMapperConfig;
import com.template.app.features.ops.entity.MqConsumerDetailEntity;
import com.template.app.features.ops.entity.ServiceEndpointConfigEntity;
import com.template.app.features.ops.service.schema.command.MqConsumerRegistrationCmd;

@Mapper(componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    uses = { GlobalMapperConfig.class }
)
public interface OpsCoreMapStruct {
    
    @Mapping(target = "endpointId", source = "config.serviceEndpointConfigId")
    MqConsumerRegistrationCmd toMqCmd(ServiceEndpointConfigEntity config, MqConsumerDetailEntity detail);

}
