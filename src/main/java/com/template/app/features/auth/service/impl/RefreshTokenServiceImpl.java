package com.template.app.features.auth.service.impl;

import java.time.Instant;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.template.app.features.auth.entity.ConsumedRefreshTokenEntity;
import com.template.app.features.auth.repository.ConsumedRefreshTokenRepository;
import com.template.app.features.auth.service.RefreshTokenService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {
    
    private final ConsumedRefreshTokenRepository consumedRefreshTokenRepository;

    // @Scheduled(cron = "0/10 * * * * *") 
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
