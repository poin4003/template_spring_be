package com.template.app.features.sims.consumer;

import org.springframework.stereotype.Component;

import com.template.app.features.sims.service.SimService;
import com.template.app.features.sims.service.schema.command.SimCmd;
import com.template.app.features.sims.service.schema.result.SimResult;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class SimImportConsumer {

    private final SimService simService;
 
    public void importSingleSim(SimCmd cmd) {

        if (cmd == null) {
            log.error("Received null request due to deserialization error. Skipping");
            return;
        }

        log.info("Received created Sim: {}", cmd);

        try {
            SimResult result = simService.createSim(cmd);
            log.info("Successfully created SIM: {}", result.getSimPhoneNumber());
        } catch (Exception e) {
            log.error("Error processing SIM import for phone number {}: {}", cmd.getSimPhoneNumber(), e.getMessage());
            throw e;
        }
    }
}
