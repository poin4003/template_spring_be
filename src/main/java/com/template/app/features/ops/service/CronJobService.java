package com.template.app.features.ops.service;

public interface CronJobService {
    
    boolean isJobEnabled(String jobName);

    public void setJobStatus(String jobName, boolean enabled);
}
