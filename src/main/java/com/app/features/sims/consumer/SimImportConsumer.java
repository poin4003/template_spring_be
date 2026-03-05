package com.app.features.sims.consumer;

import org.springframework.stereotype.Component;

import com.app.core.exception.ExceptionFactory;
import com.app.core.mq.handler.MessageHandler;
import com.app.features.sims.service.SimService;
import com.app.features.sims.service.schema.command.SimCmd;
import com.app.features.sims.service.schema.result.SimResult;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("simImportConsumer")
@RequiredArgsConstructor
public class SimImportConsumer implements MessageHandler<SimCmd> {
   
    private final SimService simService;

    @Override
    public void handle(SimCmd cmd) {
        if (cmd == null) {
            log.error("Received null request due to deserialization error. Skipping");
            return;
        }

        if ("9999999999".equals(cmd.getSimPhoneNumber())) {
            log.warn(">>> SIMULATION: Triggering Artificial Error for DLQ Test! <<<");
            throw ExceptionFactory.importSimError();
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
