package com.template.app.features.sims.excel;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.template.app.features.sims.producer.SimImportProducer;
import com.template.app.features.sims.service.schema.SimCoreMapStruct;
import com.template.app.features.sims.service.schema.command.SimCmd;
import com.template.app.features.sims.service.schema.command.SimExcelImportCmd;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class SimImportExcelListener extends AnalysisEventListener<SimExcelImportCmd> {
    
    private final SimImportProducer simImportProducer;
    private final SimCoreMapStruct simCoreMapStruct;

    @Override
    public void invoke(SimExcelImportCmd data, AnalysisContext context) {
        log.info("Processing row: {}", context.readRowHolder().getRowIndex());

        SimCmd cmd = simCoreMapStruct.excelToCommand(data);

        simImportProducer.sendSimToImportQueue(cmd);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        log.info("--- Excel import finished. All SIMs sent to Kafka queue. ---");
    }
    
    @Override
    public void onException(Exception exception, AnalysisContext context) throws Exception {
        log.error("Error reading Excel file at row {}: {}", context.readRowHolder().getRowIndex(), exception.getMessage());
        throw exception; 
    }
}
