package com.app.features.ops.service.impl;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import com.app.features.ops.entity.MqConsumerConfigEntity;
import com.app.features.ops.entity.OpsConfigEntity;
import com.app.features.ops.enums.OpsStatusEnum;
import com.app.features.ops.enums.OpsTypeEnum;
import com.app.features.ops.repository.MqConsumerConfigRepository;
import com.app.features.ops.repository.OpsConfigRepository;
import com.app.features.ops.service.MqListenerService;
import com.app.features.ops.service.OpsConfigService;
import com.app.features.ops.service.schema.MqConsumerSetup;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class OpsConfigServiceImpl implements OpsConfigService {

    private final OpsConfigRepository opsConfigRepo;
    private final MqConsumerConfigRepository mqConsumerConfigRepo;
    private final MqListenerService mqListenerService;
    private final ModelMapper modelMapper;

    @EventListener(ApplicationReadyEvent.class)
    @Override
    public void initializeActiveEndpoints() {
        log.info("=== [OPS-MANAGER] Starting scan and init endpoints ===");

        List<OpsConfigEntity> activeConfigs = opsConfigRepo.findByStatus(OpsStatusEnum.ACTIVE);

        int successCount = 0;

        for (OpsConfigEntity config : activeConfigs) {
            try {
                if (OpsTypeEnum.MQ_BROKER_LISTENER.equals(config.getType())) {
                    boolean isSuccess = processMqEndpoint(config);
                    if (isSuccess)
                        successCount++;
                }
            } catch (Exception e) {
                log.error(
                        "[OPS][INIT][FAILED] configId={} endpointType={}",
                        config.getId(),
                        config.getType(),
                        e);
            }
        }

        log.info("=== [OPS-MANAGER] Complete initialize {}/{} configs ===",
                successCount, activeConfigs.size());
    }

    private boolean processMqEndpoint(OpsConfigEntity opsConfig) {
        UUID opsConfigId = Objects.requireNonNull(opsConfig.getId(), "OpsConfig ID must not be null!");

        MqConsumerConfigEntity mqConfig = mqConsumerConfigRepo.findById(opsConfigId)
                .orElse(null);

        if (mqConfig == null) {
            log.warn("Skipping MQ Setup [{}]: Detail data not found in mq_consumer_details for ID {}",
                    opsConfig.getName(), opsConfigId);
            return false;
        }

        MqConsumerSetup setup = modelMapper.map(mqConfig, MqConsumerSetup.class);

        setup.setEndpointId(opsConfig.getId());

        mqListenerService.registerMqConsumer(setup);

        return true;
    }
}
