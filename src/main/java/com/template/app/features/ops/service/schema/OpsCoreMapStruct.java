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
    @Mapping(target = "sourceName", source = "detail.sourceName")
    @Mapping(target = "consumerGroup", source = "detail.consumerGroup")
    @Mapping(target = "parallelism", source = "detail.parallelism")
    @Mapping(target = "handlerKey", source = "detail.handlerKey")
    @Mapping(target = "handlerMethod", source = "detail.handlerMethod")
    @Mapping(target = "transportConfig", source = "detail.transportConfig") 
    MqConsumerRegistrationCmd toMqCmd(ServiceEndpointConfigEntity config, MqConsumerDetailEntity detail);

}
