package com.template.app.features.ops.service.impl;

import java.util.List;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import com.template.app.features.ops.entity.MqConsumerDetailEntity;
import com.template.app.features.ops.entity.ServiceEndpointConfigEntity;
import com.template.app.features.ops.enums.EndpointTypeEnum;
import com.template.app.features.ops.repository.MqConsumerDetailRepository;
import com.template.app.features.ops.repository.ServiceEndpointConfigRepository;
import com.template.app.features.ops.service.DynamicMqListenerService;
import com.template.app.features.ops.service.OpsEndpointService;
import com.template.app.features.ops.service.schema.OpsCoreMapStruct;
import com.template.app.features.ops.service.schema.command.MqConsumerRegistrationCmd;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Lazy(false)
@RequiredArgsConstructor
public class OpsEndpointServiceImpl implements OpsEndpointService {

    private final ServiceEndpointConfigRepository configRepo;
    private final MqConsumerDetailRepository mqConsumerDetailRepository;
    private final OpsCoreMapStruct opsCoreMapStruct;
    private final DynamicMqListenerService dynamicMqListenerService;

    @Override
    @EventListener(ApplicationReadyEvent.class)
    public void initializeActiveEndpoints() {
        log.info("=== [OPS-MANAGER] Starting scan and init endpoints ===");

        List<ServiceEndpointConfigEntity> activeConfigs = configRepo.findActiveMqConfigs();

        int successCount = 0;

        for (ServiceEndpointConfigEntity config : activeConfigs) {
            try {
                if (EndpointTypeEnum.MQ_BROKER_LISTENER.equals(config.getEndpointType())) {
                    boolean isSuccess = processMqEndpoint(config);
                    if (isSuccess) successCount++;
                }
            } catch (Exception e) {
                log.error(
                    "[OPS][INIT][FAILED] configId={} endpointType={}",
                    config.getServiceEndpointConfigId(),
                    config.getEndpointType(),
                    e
                );
            }
        }

        log.info("=== [OPS-MANAGER] Complete initialize {}/{} configs ===",
            successCount, activeConfigs.size()
        );
    }

    private boolean processMqEndpoint(ServiceEndpointConfigEntity config) {
        MqConsumerDetailEntity detail = mqConsumerDetailRepository.selectById(config.getServiceEndpointConfigId());

        if (detail == null) {
            log.warn("Continue config ID: {}: not found detail data in table mq_consumer_details", config.getServiceEndpointConfigId());

            return false;
        }

        MqConsumerRegistrationCmd cmd = opsCoreMapStruct.toMqCmd(config, detail);

        dynamicMqListenerService.registerMqConsumer(cmd);
        
        return true;
    }
}
