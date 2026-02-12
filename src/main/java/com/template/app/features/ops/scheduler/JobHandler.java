package com.template.app.features.ops.scheduler;

public interface JobHandler {
    
    void execute();

    String getSupportedJobType();
}
