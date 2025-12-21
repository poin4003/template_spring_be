package com.template.app.features.sims.api.v1.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.excel.EasyExcel;
import com.template.app.core.controller.BaseController;
import com.template.app.core.dto.PaginationResponse;
import com.template.app.core.exception.ExceptionFactory;
import com.template.app.core.vo.ResultMessage;
import com.template.app.features.sims.api.v1.dto.SimMapStruct;
import com.template.app.features.sims.api.v1.dto.request.CreateSimRequest;
import com.template.app.features.sims.api.v1.dto.response.SimResponse;
import com.template.app.features.sims.service.SimService;
import com.template.app.features.sims.service.schema.query.SimFilterQuery;
import com.template.app.features.sims.service.schema.result.SimExcelExportResult;
import com.template.app.features.sims.service.schema.result.SimResult;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;
import java.util.UUID;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/sim")
@Tag(name = "SIM Management V1", description = "Sim docs")
public class SimController extends BaseController {
    private final SimService simService; 
    private final SimMapStruct simMapStruct;

    @PostMapping("")
    @Operation(summary = "Create a new SIM", description = "Create a new SIM")
    public ResponseEntity<ResultMessage<SimResponse>> createSim(@Valid @RequestBody CreateSimRequest request) {
        SimResult result = simService.createSim(simMapStruct.toCommand(request));
        return Created(simMapStruct.toResponse(result));
    }

    @PostMapping(value = "/import_sim", consumes = {"multipart/form-data"})
    @Operation(
        summary = "Import SIM data from Excel file", 
        description = "Uploads an Excel file to push SIM data to Kafka queue."
    )
    public ResponseEntity<ResultMessage<String>> importSims(
        @RequestPart("file") @Schema(type = "string", format = "binary") MultipartFile file
    ) throws IOException {
        if (file.isEmpty()) {
            throw ExceptionFactory.dataNotFound("Please, send request with file");
        }

        simService.importSimsFromExcel(file);

        return OK("File import sim already in progress");
    }

    @PostMapping("/test/excel")
    @Operation(summary = "Create a new excel", description = "Create a new excel")
    public void createExcel() {
        try {
            simService.processExcelDataAnalytics();
        } catch (Exception e) {
            System.out.println("Exception");
        }
    }

    @GetMapping("")
    @Operation(summary = "Get list of SIM", description = "Get list of SIM")
    public ResponseEntity<ResultMessage<PaginationResponse<SimResponse>>> getManySims(
        @ParameterObject SimFilterQuery query
    ) {
        PaginationResponse<SimResponse> response = simService.getManySim(query)
                                                            .map(simMapStruct::toResponse);

        return OK("Get many sim success", response);
    }

    @GetMapping("/export_sims")
    @Operation(summary = "Export all SIM to excel file", description = "Get list of all SIM")
    @ApiResponses(value = {
        @ApiResponse(
        responseCode = "200", 
        description = "File Excel successfully created", 
        content = @Content(mediaType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    ),
    })
    public void exportAllSimToExcel(HttpServletResponse response) throws IOException{
        List<SimExcelExportResult> dataToExport = simService.getAllSimExcelExport();

        String fileName = URLEncoder.encode("simList", "UTF-8").replaceAll("\\+", "%20");

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment;filename*=utf-8''" + fileName + System.currentTimeMillis() + ".xlsx");

        try {
            EasyExcel.write(response.getOutputStream(), SimExcelExportResult.class)
                    .sheet("Sim data")
                    .doWrite(dataToExport);
        } catch (IOException e) {
            throw new RuntimeException("Export Excel Failed: " + e.getMessage(), e);
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get SIM by ID", description = "Get SIM by ID")
    @RateLimiter(name = "default")
    @CircuitBreaker(name = "checkRandom")
    public ResponseEntity<ResultMessage<SimResponse>> getSimById(@PathVariable UUID id) {
        log.info("Controller:-> getSimById | {}", id);
        SimResult result = simService.getSimById(id);
        return OK("Get sim success", simMapStruct.toResponse(result));
    }
}
