package com.app.features.ops.service;

public interface CronJobService {
    void refreshJobs();

    boolean isJobEnabled(String jobName);

    void setJobStatus(String jobName, boolean enabled);
}
