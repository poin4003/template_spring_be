package com.app.features.sims.service;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import com.app.core.dto.PaginationResponse;
import com.app.features.sims.service.schema.command.SimCmd;
import com.app.features.sims.service.schema.query.SimFilterQuery;
import com.app.features.sims.service.schema.result.SimExcelExportResult;
import com.app.features.sims.service.schema.result.SimResult;

public interface SimService {

    SimResult createSim(SimCmd cmd);

    SimResult getSimById(UUID id);

    PaginationResponse<SimResult> getManySim(SimFilterQuery queryInput);

    List<SimExcelExportResult> getAllSimExcelExport();

    void processExcelDataAnalytics() throws InterruptedException;

    void importSimsFromExcel(MultipartFile file) throws IOException;
}
