package com.app.features.sims.api.v1.controller;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.excel.EasyExcel;
import com.app.core.controller.BaseController;
import com.app.core.exception.ExceptionFactory;
import com.app.core.vo.ResultMessage;
import com.app.features.sims.api.v1.dto.query.GetManySimDto;
import com.app.features.sims.api.v1.dto.query.SimFilterDto;
import com.app.features.sims.api.v1.dto.request.CreateSimDto;
import com.app.features.sims.api.v1.dto.response.SimDto;
import com.app.features.sims.cqrs.command.CreateSimCmd;
import com.app.features.sims.cqrs.query.GetManySimQuery;
import com.app.features.sims.cqrs.query.GetSimByIdQuery;
import com.app.features.sims.cqrs.result.SimResult;
import com.app.features.sims.excel.dto.SimExcelExport;
import com.app.features.sims.service.SimService;

import an.awesome.pipelinr.Pipeline;
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

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/sim")
@Tag(name = "SIM Management V1", description = "Sim docs")
public class SimController extends BaseController {

    private final Pipeline pipeline;
    private final SimService simService;
    private final ModelMapper modelMapper;

    @PostMapping("")
    @Operation(summary = "Create a new SIM", description = "Create a new SIM")
    public ResponseEntity<ResultMessage<SimDto>> createSim(@Valid @RequestBody CreateSimDto req) {
        CreateSimCmd cmd = modelMapper.map(req, CreateSimCmd.class);

        SimResult result = pipeline.send(cmd);

        return Created(modelMapper.map(result, SimDto.class));
    }

    @PostMapping(value = "/import_sim", consumes = { "multipart/form-data" })
    @Operation(summary = "Import SIM data from Excel file", description = "Uploads an Excel file to push SIM data to Kafka queue.")
    public ResponseEntity<ResultMessage<String>> importSims(
            @RequestPart("file") @Schema(type = "string", format = "binary") MultipartFile file) throws IOException {
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
    public ResponseEntity<ResultMessage<Page<SimDto>>> getManySims(
            @ParameterObject GetManySimDto req) {
        GetManySimQuery query = modelMapper.map(req, GetManySimQuery.class);

        if (req.getFilter() != null) {
            modelMapper.map(req.getFilter(), query);
        }

        Page<SimResult> results = pipeline.send(query);

        Page<SimDto> response = results.map(result -> modelMapper.map(result, SimDto.class));

        return OK("Get many sim success", response);
    }

    @GetMapping("/export_sims")
    @Operation(summary = "Export all SIM to excel file", description = "Get list of all SIM")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "File Excel successfully created", content = @Content(mediaType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")),
    })
    public void exportAllSimToExcel(
            @ParameterObject SimFilterDto req,
            HttpServletResponse res) throws IOException {
        List<SimExcelExport> dataToExport = simService.getAllSimExcelExport(req);

        String fileName = URLEncoder.encode("simList", "UTF-8").replaceAll("\\+", "%20");

        res.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8");
        res.setHeader("Content-Disposition",
                "attachment;filename*=utf-8''" + fileName + System.currentTimeMillis() + ".xlsx");

        try {
            EasyExcel.write(res.getOutputStream(), SimExcelExport.class)
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
    public ResponseEntity<ResultMessage<SimDto>> getSimById(@PathVariable UUID id) {
        log.info("Controller:-> getSimById | {}", id);

        GetSimByIdQuery query = new GetSimByIdQuery(id);

        SimResult result = pipeline.send(query);

        return OK("Get sim success", modelMapper.map(result, SimDto.class));
    }
}
