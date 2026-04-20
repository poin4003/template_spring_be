package com.app.features.action.service;

import com.app.features.action.service.schema.result.MatchedActionResult;
import com.app.features.error.enums.ErrorCategoryEnum;

public interface ActionService {
    MatchedActionResult findBestMatch(String targetKey, Integer errorCode, ErrorCategoryEnum category);
}
