package com.template.app.features.action.executor.context;

import java.util.Map;
import java.util.HashMap;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public abstract class BaseActionContext {
    
    private String triggerSource;

    @Builder.Default
    private Map<String, Object> attributes = new HashMap<>();
}
