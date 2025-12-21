package com.template.app.features.ops.service.impl;

import org.springframework.stereotype.Service;

import com.template.app.core.cache.RedisService;
import com.template.app.features.ops.service.CronJobService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class CronJobServiceImpl implements CronJobService {
    
    private final RedisService redisService;

    private static final String CRON_STATUS_PREFIX = "ops:cronjob:status";

    public boolean isJobEnabled(String jobName) {
        String key = CRON_STATUS_PREFIX + jobName;
        String status = redisService.getString(key);

        return status == null || Boolean.parseBoolean(status);
    }

    public void setJobStatus(String jobName, boolean enabled) {
        String key = CRON_STATUS_PREFIX + jobName;
        redisService.setString(key, String.valueOf(enabled));
        log.info("Manual OP: set cronjob '{}' status to {}", jobName, enabled);
    }
}
