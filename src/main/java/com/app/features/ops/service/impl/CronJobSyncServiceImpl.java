package com.app.features.ops.service.impl;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.core.constant.AppConstants;
import com.app.core.sync.SyncableDataService;
import com.app.features.ops.annotation.CronJobDef;
import com.app.features.ops.entity.CronJobConfigEntity;
import com.app.features.ops.entity.OpsConfigEntity;
import com.app.features.ops.enums.OpsStatusEnum;
import com.app.features.ops.enums.OpsTypeEnum;
import com.app.features.ops.repository.CronJobConfigRepository;
import com.app.features.ops.repository.OpsConfigRepository;
import com.app.features.ops.scheduler.JobHandler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CronJobSyncServiceImpl implements SyncableDataService {

    private final CronJobConfigRepository cronJobRepo;
    private final OpsConfigRepository opsConfigRepo;
    private final ApplicationContext applicationContext;

    @Override
    public String getSyncType() {
        return "SYNC_CRONJOB_CONFIGS";
    }

    @Override
    public int getOrder() {
        return 5;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void syncToDatabase() {
        log.info("[Sync] Scanning for Cronjob defination...");

        List<CronJobConfigEntity> existingJobs = cronJobRepo.findAll();

        Set<String> existingJobTypes = existingJobs.stream()
                .map(CronJobConfigEntity::getJobType)
                .collect(Collectors.toSet());

        int insertCount = 0;

        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(CronJobDef.class));

        for (BeanDefinition bd : scanner.findCandidateComponents(AppConstants.BASE_PACKAGE)) {
            try {
                Class<?> clazz = Class.forName(bd.getBeanClassName());
                CronJobDef def = clazz.getAnnotation(CronJobDef.class);

                if (def == null) {
                    log.warn("[Sync] Annotation is null for class {}. Skipping.", clazz.getSimpleName());
                    continue;
                }

                JobHandler handler = (JobHandler) applicationContext.getBean(clazz);
                String jobType = handler.getSupportedJobType();

                if (existingJobTypes.contains(jobType)) {
                    log.debug("[Sync] JobType [{}] already exists in DB. Skipping.");
                    continue;
                }

                UUID newId = UUID.randomUUID();

                OpsConfigEntity opsConfig = new OpsConfigEntity();
                opsConfig.setId(newId);
                opsConfig.setName(def.jobName().toLowerCase().replace(" ", "-"));

                opsConfig.setType(OpsTypeEnum.CRONJOB);
                opsConfig.setStatus(OpsStatusEnum.ACTIVE);
                opsConfigRepo.save(opsConfig);

                CronJobConfigEntity cronConfig = new CronJobConfigEntity();
                cronConfig.setId(newId);
                cronConfig.setName(def.jobName());
                cronConfig.setExpression(def.cronExpression());
                cronConfig.setJobType(jobType);
                cronConfig.setLockAtLeastFor(def.lockAtLeastFor());
                cronConfig.setLockAtMostFor(def.lockAtMostFor());
                cronJobRepo.save(cronConfig);

                log.info("[Sync] Seeded new CronJob: [{}] with cron: [{}]", jobType, def.cronExpression());
                insertCount++;
            } catch (ClassNotFoundException e) {
                log.error("[Sync] Failed to load CronJob class: {}", bd.getBeanClassName(), e);
            } catch (Exception e) {
                log.error("[Sync] Error processing bean definition: {}", bd.getBeanClassName(), e);
            }
        }

        if (insertCount > 0) {
            log.info("[Sync] Successfully seeded {} new CronJobs.", insertCount);
        } else {
            log.info("[Sync] No new Cronjobs to seed. Database is up to date.");
        }
    }
}
