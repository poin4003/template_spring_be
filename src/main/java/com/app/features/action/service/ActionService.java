package com.app.features.action.service;

import java.util.UUID;

import com.app.features.action.service.schema.result.MatchedActionResult;
import com.app.features.error.enums.ErrorCategoryEnum;

public interface ActionService {
    MatchedActionResult findBestMatch(UUID targetId, Integer errorCode, ErrorCategoryEnum category);
}
