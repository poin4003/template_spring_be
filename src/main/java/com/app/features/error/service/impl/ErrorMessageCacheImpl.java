package com.app.features.error.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.app.core.constant.AppConstants;
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
public class ErrorMessageCacheImpl implements SyncableDataService {

    private final SystemErrorDefinitionRepository errorRepo;
    private final SystemErrorMessageRepository errorMessageRepo;

    private final Map<Integer, Map<String, String>> cache = new ConcurrentHashMap<>();

    @Override
    public String getSyncType() {
        return "LOAD_ERROR_MESSAGE_CACHE";
    }

    @Override
    public int getOrder() {
        return 3;
    }

    @Override
    public void syncToDatabase() {
        this.refresh();
    }

    public void refresh() {
        try {
            log.info("[Cache] Start refreshing error message...");

            List<SystemErrorDefinitionEntity> definations = errorRepo.findAll();

            if (definations.isEmpty()) {
                log.warn("[Cache] No error definations found.");
                return;
            }

            Map<UUID, Integer> definationIdToCodeMap = definations.stream()
                    .collect(Collectors.toMap(
                            SystemErrorDefinitionEntity::getId,
                            SystemErrorDefinitionEntity::getCode));

            List<SystemErrorMessageEntity> messages = errorMessageRepo
                    .findByErrorDefinitionIdIn(definationIdToCodeMap.keySet());

            Map<Integer, Map<String, String>> newCache = new HashMap<>();

            for (SystemErrorMessageEntity msg : messages) {
                Integer errorCode = definationIdToCodeMap.get(msg.getId());

                if (errorCode != null) {
                    newCache.computeIfAbsent(errorCode, k -> new HashMap<>())
                            .put(msg.getLanguageCode(), msg.getContent());
                }
            }

            cache.clear();
            cache.putAll(newCache);

            log.info("[Cache] Refreshed sucessfully. Loaded {} error codes.", cache.size());

        } catch (Exception e) {
            log.error("Load cache failed", e);
        }
    }

    public String getMessage(Integer code) {
        Map<String, String> langMap = cache.get(code);

        if (langMap == null)
            return null;

        String content = langMap.get(AppConstants.SYSTEM_LANGUAGE);

        if (content == null) {
            content = langMap.get("en");
        }

        return content;
    }
}
