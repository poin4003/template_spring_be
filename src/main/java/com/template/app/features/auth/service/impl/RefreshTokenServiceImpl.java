package com.template.app.features.auth.service.impl;

import java.time.Instant;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.template.app.features.auth.entity.ConsumedRefreshTokenEntity;
import com.template.app.features.auth.repository.ConsumedRefreshTokenRepository;
import com.template.app.features.auth.service.RefreshTokenService;
import com.template.app.features.ops.service.CronJobService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;

@Service
@Slf4j
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {
    
    private final ConsumedRefreshTokenRepository consumedRefreshTokenRepository;
    private final CronJobService cronJobService;

    private static final String JOB_NAME_AUTH_CLEANUP = "AuthCleanupExpiredConsumedTokens";

    // @Scheduled(cron = "0/10 * * * * *") 
    @Scheduled(cron = "0 0 0/6 * * ?")
    @SchedulerLock(
        name = JOB_NAME_AUTH_CLEANUP,
        lockAtLeastFor = "PT5M",
        lockAtMostFor = "PT30M"
    )
    @Transactional
    public void cleanupExpiredConsumedTokenJob() {
        if (!cronJobService.isJobEnabled(JOB_NAME_AUTH_CLEANUP)) {
            log.warn("[OPS] Cronjob '{}' is manually DISABLED via Redis. Skipping...", JOB_NAME_AUTH_CLEANUP);
            return;
        }

        this.cleanupExpiredConsumedTokens();
    }

    @Override
    public void cleanupExpiredConsumedTokens() {
        log.info("Starting cleanup of expired consumed refresh token...");

        Instant now = Instant.now();

        int deleteCount = consumedRefreshTokenRepository.delete(
            new LambdaQueryWrapper<ConsumedRefreshTokenEntity>()
                .lt(ConsumedRefreshTokenEntity::getExpiryDate, now)
        );

        log.info("Finished cleanup. Deleted {} expired consumed token at {}", deleteCount, now);
    }
}
