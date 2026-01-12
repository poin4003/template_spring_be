package com.template.app.features.action.service;

import java.util.UUID;

import com.template.app.features.action.service.schema.result.MatchedActionResult;
import com.template.app.features.error.enums.ErrorCategoryEnum;

public interface ActionService {
    MatchedActionResult findBestMatch(UUID targetId, Integer errorCode, ErrorCategoryEnum category);
}
