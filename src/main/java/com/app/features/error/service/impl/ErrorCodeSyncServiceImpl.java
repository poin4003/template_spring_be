package com.app.features.error.service.impl;

import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.app.core.response.ResultCode;
import com.app.core.sync.SyncableDataService;
import com.app.features.error.entity.SystemErrorDefinitionEntity;
import com.app.features.error.repository.SystemErrorDefinitionRepository;
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
            String className = enumCode.getExceptionClass().getName();
            ExceptionClassMapping jsonMapping = ExceptionClassMapping.builder()
                    .java(JavaExceptionConfig.builder()
                            .className(className)
                            .matchStrategy("EQUALS")
                            .build())
                    .build();

            Optional<SystemErrorDefinitionEntity> existingOps = errorRepo.findByCode(enumCode.code());

            if (existingOps.isPresent()) {
                SystemErrorDefinitionEntity existingDef = existingOps.get();
                boolean isChanged = false;

                if (!enumCode.name().equals(existingDef.getAliasKey())) {
                    existingDef.setAliasKey(enumCode.name());
                    isChanged = true;
                }

                if (enumCode.httpStatus() != existingDef.getHttpStatus()) {
                    existingDef.setHttpStatus(enumCode.httpStatus());
                    isChanged = true;
                }

                if (enumCode.getCategory() != existingDef.getCategory()) {
                    existingDef.setCategory(enumCode.getCategory());
                    isChanged = true;
                }

                if (existingDef.getExceptionClassName() == null
                        || !existingDef.getExceptionClassName().equals(jsonMapping)) {
                    existingDef.setExceptionClassName(jsonMapping);
                    isChanged = true;
                }

                if (isChanged) {
                    updated++;
                    log.debug("Marked for update: Error Code {}", enumCode.code());
                }

            } else {
                SystemErrorDefinitionEntity newDef = new SystemErrorDefinitionEntity();

                newDef.setCode(enumCode.code());
                newDef.setAliasKey(enumCode.name());
                newDef.setHttpStatus(enumCode.httpStatus());
                newDef.setCategory(enumCode.getCategory());
                newDef.setExceptionClassName(jsonMapping);

                errorRepo.save(newDef);
                inserted++;
                log.debug("Inserted new Error Code: {}", enumCode.code());
            }
        }

        log.info(">>> Sync [{}] COMPLETE. Inserted: {}, Updated: {}", getSyncType(), inserted, updated);
    }
}
