package com.template.app.features.sims.service.schema;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import com.template.app.config.mapstruct.GlobalMapperConfig;
import com.template.app.features.sims.entity.SimEntity;
import com.template.app.features.sims.enums.SimStatusEnum;
import com.template.app.features.sims.service.schema.command.SimCmd;
import com.template.app.features.sims.service.schema.command.SimExcelImportCmd;
import com.template.app.features.sims.service.schema.result.SimExcelExportResult;
import com.template.app.features.sims.service.schema.result.SimResult;

@Mapper(componentModel = "spring", 
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    uses = { GlobalMapperConfig.class }
)
public interface SimCoreMapStruct {
    
    SimResult toResult(SimEntity entity);

    SimEntity toEntity(SimResult result);

    @Mapping(target = "simId", ignore = true)
    SimEntity commandToEntity(SimCmd cmd);

    @Mapping(target = "simId", source = "simId", qualifiedByName = "uuidToString")
    @Mapping(target = "simStatus", source = "simStatus", qualifiedByName = "enumToString")
    @Mapping(target = "createdAt", source = "createdAt", qualifiedByName = "localDateTimeToString")
    @Mapping(target = "updatedAt", source = "updatedAt", qualifiedByName = "localDateTimeToString")
    SimExcelExportResult toExcelResult(SimEntity entity);

    @Mapping(target = "simStatus", source = "simStatusString", qualifiedByName = "mapStatusString")
    SimCmd excelToCommand(SimExcelImportCmd excelCmd);

    @Named("mapStatusString") 
    default SimStatusEnum mapSimStatusString(String statusString) {
        if (statusString == null || statusString.trim().isEmpty()) {
            return null; 
        }
        
        try {
            return SimStatusEnum.valueOf(statusString.trim().toUpperCase()); 
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                String.format("Invalid SimStatus value '%s'. Expected values: %s", 
                               statusString, java.util.Arrays.toString(SimStatusEnum.values()))
            );
        }
    }
}
