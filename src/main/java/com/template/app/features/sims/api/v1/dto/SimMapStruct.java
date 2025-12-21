package com.template.app.features.sims.api.v1.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import com.template.app.config.mapstruct.GlobalMapperConfig;
import com.template.app.features.sims.api.v1.dto.request.CreateSimRequest;
import com.template.app.features.sims.api.v1.dto.response.SimResponse;
import com.template.app.features.sims.service.schema.command.SimCmd;
import com.template.app.features.sims.service.schema.result.SimResult;

@Mapper(componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    uses = { GlobalMapperConfig.class }
)
public interface SimMapStruct {

    SimCmd toCommand(CreateSimRequest request);
    
    @Mapping(target = "simId", source = "simId", qualifiedByName = "uuidToString") 
    @Mapping(target = "simStatus", source = "simStatus", qualifiedByName = "enumToString")
    SimResponse toResponse(SimResult result);
}
