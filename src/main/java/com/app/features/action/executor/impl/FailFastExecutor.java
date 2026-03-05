package com.app.features.action.executor.impl;

import org.springframework.stereotype.Component;

import com.app.features.action.executor.ActionExecutor;
import com.app.features.action.executor.context.BaseActionContext;
import com.app.features.action.vo.config.FailFastActionConfig;

@Component
public class FailFastExecutor implements ActionExecutor<FailFastActionConfig> {

    @Override
    public Class<FailFastActionConfig> getSupportedConfigClass() {
        return FailFastActionConfig.class;
    }

    @Override
    public void execute(FailFastActionConfig config, BaseActionContext context) { 
        throw new RuntimeException("Fail Fast Action Triggered via Configuration");
    }    
}
