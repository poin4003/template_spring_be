package com.template.app.features.ops.api.v1.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.template.app.core.controller.BaseController;
import com.template.app.core.vo.ResultMessage;
import com.template.app.features.ops.service.CronJobService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/ops/cronjob")
@RequiredArgsConstructor
@Tag(name = "Ops cronjob", description = "Ops cronjob manual control")
public class CronJobOperationController extends BaseController {

    private final CronJobService cronJobService;

    @PostMapping("/toggle/{jobName}")
    @Operation(
        summary = "Manually enable/disable a specific cron job",
        description = "Forcefully sets the status of a cron job in Redis. If disabled, the job will skip execution even if triggered by schedule."
    )
    public ResponseEntity<ResultMessage<String>> toggleCronJobStatus(
        @PathVariable String jobName,
        @RequestParam boolean enabled
    ) {
        cronJobService.setJobStatus(jobName, enabled);

        String response = String.format("Cronjob '%s' status updated to: '%s'", 
                                        jobName, enabled ? "ENABLED" : "DISABLED");

        return OK(response);
    }

    @GetMapping("/status/{jobName}")
    @Operation(
        summary = "Check current status of a cron job",
        description = "Returns TRUE if the job is enabled (or not configured in Redis), FALSE if manually disabled."
    )
    public ResponseEntity<ResultMessage<Boolean>> getCronJobStatus(
        @PathVariable String jobName
    ) {
        return OK(cronJobService.isJobEnabled(jobName));
    }
}
