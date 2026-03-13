package com.app.features.error.service.impl;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.app.core.response.ResultCode;
import com.app.core.sync.SyncableDataService;
import com.app.features.error.repository.SystemErrorDefinitionRepository;
import com.app.features.error.entity.SystemErrorDefinitionEntity;
import com.app.features.error.vo.ExceptionClassMapping;
import com.app.features.error.vo.JavaExceptionConfig;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ErrorCodeSyncServiceImpl implements SyncableDataService {

    private final SystemErrorDefinitionRepository errorRepo;
    
    @Override
    public String getSyncType() {
        return "ERROR_DEFINATIONS";
    }

    @Override
    public int getOrder() {
        return 1;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void syncToDatabase() {
        int inserted = 0;
        int updated = 0;

        for (ResultCode enumCode : ResultCode.values()) {
            Optional<SystemErrorDefinitionEntity> existingOps = errorRepo.findByCode(enumCode.code());

            String className = enumCode.getExceptionClass().getName(); 

            ExceptionClassMapping jsonMapping = ExceptionClassMapping.builder()
                .java(JavaExceptionConfig.builder()
                    .className(className)
                    .matchStrategy("EQUALS")
                    .build()
                ).build();

            if (existingOps.isEmpty()) {
                SystemErrorDefinitionEntity newDef = new SystemErrorDefinitionEntity();

                UUID error_id = UUID.randomUUID();
                newDef.setId(error_id);
                newDef.setCode(enumCode.code());
                newDef.setAliasKey(enumCode.name());
                newDef.setHttpStatus(enumCode.httpStatus());
                newDef.setCategory(enumCode.getCategory());
                newDef.setExceptionClassName(jsonMapping);

                errorRepo.save(newDef);
                inserted++;
            } else {
                SystemErrorDefinitionEntity existingDef = existingOps.get();
                boolean isChanged = false;

                if (!enumCode.name().equals(existingDef.getAliasKey())) {
                    log.warn("Alias mismatch for code {}: DB={}, Code={}. Updating DB...",
                        enumCode.code(), existingDef.getAliasKey(), enumCode.name()
                    );
                    
                    isChanged = true;
                    existingDef.setAliasKey(enumCode.name());
                }

                if (enumCode.httpStatus() != existingDef.getHttpStatus()) {
                    isChanged = true;
                    existingDef.setHttpStatus(enumCode.httpStatus());
                }
                
                if (enumCode.getCategory() != existingDef.getCategory()) {
                    isChanged = true;
                    existingDef.setCategory(enumCode.getCategory());
                }
                
                if (existingDef.getExceptionClassName() == null 
                    || !existingDef.getExceptionClassName().equals(jsonMapping)
                ) {
                    isChanged = true;
                    existingDef.setExceptionClassName(jsonMapping);
                }

                if (isChanged) {
                    errorRepo.save(existingDef);
                    updated++;
                }
            }
        }

        log.info("Synced Error Defination. Inserted: {}, Updated: {}", inserted, updated);
    }
}
