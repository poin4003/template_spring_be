package com.template.app.features.error.service.impl;

import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.template.app.core.response.ResultCode;
import com.template.app.core.sync.SyncableDataService;
import com.template.app.features.error.entity.SystemErrorDefinationEntity;
import com.template.app.features.error.entity.SystemErrorMessageEntity;
import com.template.app.features.error.repository.SystemErrorDefinationRepository;
import com.template.app.features.error.repository.SystemErrorMessageRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ErrorMessageSyncServiceImpl implements SyncableDataService {

    private final SystemErrorDefinationRepository errorRepo;
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
            SystemErrorDefinationEntity def = errorRepo.selectOne(
                new LambdaQueryWrapper<SystemErrorDefinationEntity>()
                    .select(SystemErrorDefinationEntity::getErrorId)
                    .eq(SystemErrorDefinationEntity::getErrorCode, enumCode.code())
            );

            if (def == null) continue;

            boolean exists = errorMessageRepo.exists(
                new LambdaQueryWrapper<SystemErrorMessageEntity>()
                    .eq(SystemErrorMessageEntity::getErrorDefinationId, def.getErrorId())
                    .eq(SystemErrorMessageEntity::getLanguageCode, defaultLang)
            );

            if (!exists) {
                SystemErrorMessageEntity msg = new SystemErrorMessageEntity();

                UUID msg_id = UUID.randomUUID();
                msg.setMessageId(msg_id);
                msg.setErrorDefinationId(def.getErrorId());
                msg.setLanguageCode(defaultLang);
                msg.setMessageContent(enumCode.message());

                errorMessageRepo.insert(msg);
                synced++;
            }
        }

        log.info("Synced Default Messages. New: {}", synced);
    }
}
