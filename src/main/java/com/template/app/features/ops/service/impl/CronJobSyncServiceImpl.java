package com.template.app.features.ops.service.impl;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.template.app.core.constant.AppConstants;
import com.template.app.core.sync.SyncableDataService;
import com.template.app.features.ops.annotation.CronJobDef;
import com.template.app.features.ops.entity.CronJobConfigEntity;
import com.template.app.features.ops.entity.ServiceEndpointConfigEntity;
import com.template.app.features.ops.enums.EndpointStatusEnum;
import com.template.app.features.ops.enums.EndpointTypeEnum;
import com.template.app.features.ops.repository.CronJobConfigRepository;
import com.template.app.features.ops.repository.ServiceEndpointConfigRepository;
import com.template.app.features.ops.scheduler.JobHandler;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CronJobSyncServiceImpl implements SyncableDataService {

    private final CronJobConfigRepository cronJobRepo;
    private final ServiceEndpointConfigRepository endpointRepo;
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

        List<CronJobConfigEntity> existingJobs = cronJobRepo.selectList(
                new LambdaQueryWrapper<CronJobConfigEntity>().select(CronJobConfigEntity::getJobType));

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

                ServiceEndpointConfigEntity endpoint = new ServiceEndpointConfigEntity();
                endpoint.setServiceEndpointConfigId(newId);
                endpoint.setEndpointName(def.jobName().toLowerCase().replace(" ", "-"));

                endpoint.setEndpointType(EndpointTypeEnum.CRONJOB);
                endpoint.setEndpointStatus(EndpointStatusEnum.ACTIVE);
                endpointRepo.insert(endpoint);

                CronJobConfigEntity cronConfig = new CronJobConfigEntity();
                cronConfig.setEndpointConfigId(newId);
                cronConfig.setCronjobName(def.jobName());
                cronConfig.setCronExpression(def.cronExpression());
                cronConfig.setJobType(jobType);
                cronConfig.setLockAtLeastFor(def.lockAtLeastFor());
                cronConfig.setLockAtMostFor(def.lockAtMostFor());
                cronJobRepo.insert(cronConfig);

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
