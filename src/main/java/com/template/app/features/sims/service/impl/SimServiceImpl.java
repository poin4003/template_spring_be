package com.template.app.features.sims.service.impl;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.template.app.core.dto.PaginationResponse;
import com.template.app.core.exception.ExceptionFactory;
import com.template.app.features.sims.entity.SimEntity;
import com.template.app.features.sims.excel.SimImportExcelListener;
import com.template.app.features.sims.producer.SimImportProducer;
import com.template.app.features.sims.repository.SimRepsitory;
import com.template.app.features.sims.service.SimService;
import com.template.app.features.sims.service.schema.SimCoreMapStruct;
import com.template.app.features.sims.service.schema.command.SimCmd;
import com.template.app.features.sims.service.schema.command.SimExcelImportCmd;
import com.template.app.features.sims.service.schema.query.SimFilterQuery;
import com.template.app.features.sims.service.schema.result.SimExcelExportResult;
import com.template.app.features.sims.service.schema.result.SimResult;
import com.template.app.utils.TaskRunnerUtils;
import com.template.app.utils.ThreadPoolUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class SimServiceImpl implements SimService{
    private final SimRepsitory simRepo;
    private final SimCoreMapStruct simCoreMapStruct;
    private final SimImportProducer simImportProducer;

    @Override
    public SimResult createSim(SimCmd cmd) {
        SimEntity exist = simRepo.selectOne(
            new QueryWrapper<SimEntity>().eq("sim_phone_number", cmd.getSimPhoneNumber())
        );

        if (exist != null) { 
            throw ExceptionFactory.dataAlreadyExists("PhoneNumber " + cmd.getSimPhoneNumber());
        }

        SimEntity simEntity = simCoreMapStruct.commandToEntity(cmd);
        simEntity.setSimId(UUID.randomUUID());

        simRepo.insert(simEntity);

        return simCoreMapStruct.toResult(simEntity);
    }

    @Override
    public void importSimsFromExcel(MultipartFile file) throws IOException {
        SimImportExcelListener listener = new SimImportExcelListener(simImportProducer, simCoreMapStruct);

        log.info("Starting to read Excel file: {}", file.getOriginalFilename());

        try {
            EasyExcel.read(
                file.getInputStream(),
                SimExcelImportCmd.class,
                listener
            ).sheet().doRead();
            
        } catch (IOException e) {
            log.error("Error reading input stream for Excel file: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public SimResult getSimById(UUID id) {
        SimEntity simEntity = simRepo.selectById(id);

        if (simEntity == null) { 
            log.error("Service:-> getSimById | {}", id);
            throw ExceptionFactory.dataNotFound("Sim ID " + id);
        }

        return simCoreMapStruct.toResult(simEntity);
    }

    @Override
    public PaginationResponse<SimResult> getManySim(SimFilterQuery queryInput) {
        IPage<SimEntity> pageObject = new Page<>(queryInput.getCurrentPage(), queryInput.getPageSize());

        IPage<SimEntity> entityPage = simRepo.selectPage(pageObject, null);

        List<SimResult> responseList = entityPage.getRecords().stream()
                    .map(simCoreMapStruct::toResult)
                    .collect(Collectors.toList());

        return PaginationResponse.of(
            responseList,
            entityPage.getTotal(),
            entityPage.getCurrent(),
            entityPage.getSize()
        );
    }

    @Override
    public List<SimExcelExportResult> getAllSimExcelExport() {
        List<SimEntity> allSimEntities = simRepo.selectList(null);

        List<SimExcelExportResult> allSimResponses = allSimEntities.stream()
                    .map(simCoreMapStruct::toExcelResult)
                    .collect(Collectors.toList());

        return allSimResponses;
    }

    @Override
    public void processExcelDataAnalytics() throws InterruptedException {
        int totalCount = 999999;
        int chunkRows = 5000;
        int chunkLoop = totalCount / chunkRows + (totalCount % chunkRows == 0 ? 0 : 1);
        int ccuPoolRate = 3;
        int threadLoop = chunkLoop / ccuPoolRate + (chunkLoop % ccuPoolRate == 0 ? 0 : 1);

        ThreadPoolTaskExecutor threadPoolTaskExecutor = ThreadPoolUtils.createThreadPoolTaskExecutor(9, 12, threadLoop * ccuPoolRate, 60, "thread-excel-");
        
        try {
            TaskRunnerUtils.runInParallelBatches(totalCount, ccuPoolRate, threadLoop, threadPoolTaskExecutor, 
                (startRows, endRows) -> {
                    System.out.println("Processing: startRows " + startRows + " | endRows " + endRows);
                }
            );
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            threadPoolTaskExecutor.shutdown();
        }
    }
}
