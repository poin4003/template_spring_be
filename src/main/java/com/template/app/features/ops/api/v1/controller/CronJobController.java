package com.template.app.features.ops.api.v1.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.template.app.core.controller.BaseController;
import com.template.app.core.vo.ResultMessage;
import com.template.app.features.ops.entity.CronJobConfigEntity;
import com.template.app.features.ops.repository.CronJobConfigRepository;
import com.template.app.features.ops.service.CronJobService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/ops/cronjobs")
@Tag(name = "OPS Cronjob Management V1", description = "Manage dynamic cronjobs")
public class CronJobController extends BaseController {

    private final CronJobService cronJobService;
    private final CronJobConfigRepository cronJobConfigRepository;

    @GetMapping("")
    @Operation(summary = "Get all cronjob configs")
    public ResponseEntity<ResultMessage<List<CronJobConfigEntity>>> getAllCronjobs() {
        List<CronJobConfigEntity> configs = cronJobConfigRepository.selectList(null);
        log.info("Fetched {} cronjob configs", configs.size());
        return OK("Get cronjob configs success", configs);
    }

    @GetMapping("/{jobName}/status")
    @Operation(summary = "Get cronjob enable status from Redis")
    public ResponseEntity<ResultMessage<Boolean>> getCronjobStatus(@PathVariable String jobName) {
        boolean enabled = cronJobService.isJobEnabled(jobName);
        return OK("Get cronjob status success", enabled);
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh cronjobs from database and reschedule")
    public ResponseEntity<ResultMessage<Void>> refreshCronjobs() {
        cronJobService.refreshJobs();
        log.info("Manual refresh of cronjobs triggered");
        return OK("Refresh cronjobs success", null);
    }

    @PostMapping("/{jobName}/enable")
    @Operation(summary = "Enable a cronjob (Redis flag) and refresh schedules")
    public ResponseEntity<ResultMessage<Void>> enableCronjob(@PathVariable String jobName) {
        cronJobService.setJobStatus(jobName, true);
        log.info("Enabled cronjob '{}'", jobName);
        return OK("Enable cronjob success", null);
    }

    @PostMapping("/{jobName}/disable")
    @Operation(summary = "Disable a cronjob (Redis flag) and refresh schedules")
    public ResponseEntity<ResultMessage<Void>> disableCronjob(@PathVariable String jobName) {
        cronJobService.setJobStatus(jobName, false);
        log.info("Disabled cronjob '{}'", jobName);
        return OK("Disable cronjob success", null);
    }
}

