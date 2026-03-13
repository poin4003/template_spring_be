package com.app.features.action.executor;

import com.app.features.action.executor.context.BaseActionContext;
import com.app.features.action.vo.BaseActionConfig;

public interface ActionExecutor<T extends BaseActionConfig> {
    
    Class<T> getSupportedConfigClass();

    void execute(T config, BaseActionContext context);
}
