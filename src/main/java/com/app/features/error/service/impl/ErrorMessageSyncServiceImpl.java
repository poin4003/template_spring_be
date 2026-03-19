package com.app.features.error.service.impl;

import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.app.core.response.ResultCode;
import com.app.core.sync.SyncableDataService;
import com.app.features.error.entity.SystemErrorDefinitionEntity;
import com.app.features.error.entity.SystemErrorMessageEntity;
import com.app.features.error.repository.SystemErrorDefinitionRepository;
import com.app.features.error.repository.SystemErrorMessageRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ErrorMessageSyncServiceImpl implements SyncableDataService {

    private final SystemErrorDefinitionRepository errorRepo;
    private final SystemErrorMessageRepository errorMessageRepo;

    @Override
    public String getSyncType() {
        return "ERROR_MESSAGE_DEFAULTS";
    }

    @Override
    public int getOrder() {
        return 2;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void syncToDatabase() {
        int synced = 0;
        String defaultLang = "en";

        for (ResultCode enumCode : ResultCode.values()) {
            Optional<SystemErrorDefinitionEntity> def = errorRepo.findByCode(enumCode.code());

            if (def.isEmpty())
                continue;

            SystemErrorDefinitionEntity errorDef = def.get();

            boolean hasMessage = errorMessageRepo.existsByErrorDefinitionIdAndLanguageCode(errorDef.getId(), defaultLang);

            if (!hasMessage) {
                SystemErrorMessageEntity msg = new SystemErrorMessageEntity();

                msg.setErrorDefinition(errorDef);
                msg.setLanguageCode(defaultLang);
                msg.setContent(enumCode.message());

                errorMessageRepo.save(msg);
                synced++;
            }
        }

        log.info("Synced Default Messages. New: {}", synced);
    }
}
