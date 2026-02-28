package com.template.app.features.action.executor.impl;

import org.springframework.stereotype.Component;

import com.template.app.features.action.executor.ActionExecutor;
import com.template.app.features.action.executor.context.BaseActionContext;
import com.template.app.features.action.vo.config.IgnoreActionConfig;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class IgnoreExecutor implements ActionExecutor<IgnoreActionConfig> {
    
    @Override
    public Class<IgnoreActionConfig> getSupportedConfigClass() {
        return IgnoreActionConfig.class;
    }

    @Override
    public void execute(IgnoreActionConfig config, BaseActionContext context) {
        log.info("Action IGNORE triggered. Doing nothing: {}", context.getTriggerSource());
    }
}
