package com.app.features.ops.scheduler;

public interface JobHandler {
    
    void execute();

    String getSupportedJobType();
}
