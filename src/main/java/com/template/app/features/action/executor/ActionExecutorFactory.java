package com.template.app.features.action.executor;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.template.app.features.action.executor.context.BaseActionContext;
import com.template.app.features.action.vo.BaseActionConfig;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ActionExecutorFactory {
    
    private final Map<Class<? extends BaseActionConfig>, ActionExecutor> executorMap;

    public ActionExecutorFactory(List<ActionExecutor<?>> executors) {
        this.executorMap = executors.stream()
            .collect(Collectors.toMap(ActionExecutor::getSupportedConfigClass, Function.identity()));
    }

    public void executeAction(BaseActionConfig config, BaseActionContext context) {
        if (config == null) {
            log.warn("ActionConfig is null, skipping execution.");
            return;
        }

        ActionExecutor executor = executorMap.get(config.getClass());

        if (executor == null) {
            log.error("No executor found for config type: {}", config.getClass().getName());
            return;
        }

        log.info(">>> Executing Action [{}] for Context [{}]", config.getClass().getSimpleName(), context.getTriggerSource());
        executor.execute(config, context);
    }
}
