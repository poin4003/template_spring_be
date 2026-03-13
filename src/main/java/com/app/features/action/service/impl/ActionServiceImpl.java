package com.app.features.action.service.impl;

import java.util.UUID;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.app.features.action.entity.ActionEntity;
import com.app.features.action.repository.ActionRepository;
import com.app.features.action.service.ActionService;
import com.app.features.action.service.schema.result.MatchedActionResult;
import com.app.features.error.enums.ErrorCategoryEnum;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ActionServiceImpl implements ActionService {
    
    private final ActionRepository actionRepo;

    @Override
    @Cacheable(
        value = "actions", 
        key = "#targetId + '_' + #errorCode + '_' + (#category != null ? #category.name() : 'NULL')",
        unless = "#result == null" 
    )
    public MatchedActionResult findBestMatch(UUID targetId, Integer errorCode, ErrorCategoryEnum category) {
        log.debug("Matching action for Target: {}, Code: {}, Cat: {}", targetId, errorCode, category);

        ActionEntity rule = actionRepo.findTopMatchedAction(targetId, errorCode, category).orElse(null);

        return MatchedActionResult.builder()
            .name(rule.getName())
            .actionType(rule.getActionType())
            .priority(rule.getPriority())
            .build();
    }
}
