package com.template.app.features.error.service.impl;

import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.template.app.core.response.ResultCode;
import com.template.app.core.sync.SyncableDataService;
import com.template.app.features.error.entity.SystemErrorDefinationEntity;
import com.template.app.features.error.repository.SystemErrorDefinationRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ErrorCodeSyncServiceImpl implements SyncableDataService {

    private final SystemErrorDefinationRepository errorRepo;
    
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
            SystemErrorDefinationEntity existingDef = errorRepo.selectOne(
                new LambdaQueryWrapper<SystemErrorDefinationEntity>()
                    .eq(SystemErrorDefinationEntity::getErrorCode, enumCode.code())
            );

            if (existingDef == null) {
                SystemErrorDefinationEntity newDef = new SystemErrorDefinationEntity();

                UUID error_id = UUID.randomUUID();
                newDef.setErrorId(error_id);
                newDef.setErrorCode(enumCode.code());
                newDef.setAliasKey(enumCode.name());
                newDef.setHttpStatus(enumCode.httpStatus());
                newDef.setCategory(enumCode.getCategory());

                errorRepo.insert(newDef);
                inserted++;
            } else {
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

                if (isChanged) {
                    errorRepo.updateById(existingDef);
                    updated++;
                }
            }
        }

        log.info("Synced Error Defination. Inserted: {}, Updated: {}", inserted, updated);
    }
}
