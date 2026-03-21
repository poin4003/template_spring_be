package com.app.features.ops.sync;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.core.mq.consumer.MqConsumerMeta;
import com.app.core.mq.consumer.MqConsumerRegistry;
import com.app.core.sync.SyncableDataService;
import com.app.features.ops.entity.MqConsumerConfigEntity;
import com.app.features.ops.entity.OpsConfigEntity;
import com.app.features.ops.enums.OpsStatusEnum;
import com.app.features.ops.enums.OpsTypeEnum;
import com.app.features.ops.repository.MqConsumerConfigRepository;
import com.app.features.ops.repository.OpsConfigRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MqConsumerSync implements SyncableDataService {

    private final OpsConfigRepository opsConfigRepo;
    private final MqConsumerConfigRepository mqConsumerRepo;
    private final MqConsumerRegistry mqConsumerRegistry;

    @Override
    public String getSyncType() {
        return "MQ_CONSUMER";
    }

    @Override
    public int getOrder() {
        return 4;
    }

    @Override
    @Transactional
    public void syncToDatabase() {
        log.info(">>> Starting Sync [{}] from Code Registry to DB...", getSyncType());

        int insertCount = 0;
        int updateCount = 0;

        Collection<MqConsumerMeta> consumers = mqConsumerRegistry.getAll();

        for (MqConsumerMeta meta : consumers) {
            try {
                String endpointName = meta.getListenerId() + "-endpoint";

                OpsConfigEntity opsConfig = opsConfigRepo
                        .findFirstByNameAndType(endpointName, OpsTypeEnum.MQ_BROKER_LISTENER).orElse(null);

                if (opsConfig != null) {
                    UUID opsConfigId = Objects.requireNonNull(opsConfig.getId());

                    MqConsumerConfigEntity detail = mqConsumerRepo.findById(opsConfigId)
                            .orElse(new MqConsumerConfigEntity());

                    boolean isChanged = false;

                    if (!meta.getTopic().equals(detail.getSourceName())) {
                        detail.setSourceName(meta.getTopic());
                        isChanged = true;
                    }

                    if (!meta.getConsumerGroup().equals(detail.getConsumerGroup())) {
                        detail.setConsumerGroup(meta.getConsumerGroup());
                        isChanged = true;
                    }

                    if (!meta.getListenerId().equals(detail.getHandlerKey())) {
                        detail.setHandlerKey(meta.getListenerId());
                        isChanged = true;
                    }

                    if (meta.isDlq() != (detail.getEnableDlq() != null && detail.getEnableDlq())) {
                        detail.setEnableDlq(meta.isDlq());
                        if (meta.isDlq() && (detail.getDlqName() == null || detail.getDlqName().isEmpty())) {
                            detail.setDlqName(meta.getTopic() + ".DLQ");
                        }
                        isChanged = true;
                    }

                    if (isChanged) {
                        mqConsumerRepo.save(detail);
                        updateCount++;
                        log.debug("[Sync] updated MQ Config for [{}]", endpointName);
                    }
                } else {
                    OpsConfigEntity newOpsConfig = new OpsConfigEntity();
                    newOpsConfig.setName(endpointName);
                    newOpsConfig.setType(OpsTypeEnum.MQ_BROKER_LISTENER);
                    newOpsConfig.setStatus(OpsStatusEnum.ACTIVE);

                    opsConfigRepo.save(newOpsConfig);

                    MqConsumerConfigEntity newDetail = new MqConsumerConfigEntity();
                    newDetail.setOpsConfig(newOpsConfig);
                    newDetail.setSourceName(meta.getTopic());
                    newDetail.setConsumerGroup(meta.getConsumerGroup());
                    newDetail.setHandlerKey(meta.getListenerId());
                    newDetail.setEnableDlq(meta.isDlq());

                    newDetail.setParallelism(1);
                    newDetail.setConfigData(Map.of("autoOffsetReset", "earliest"));
                    newDetail.setDescription("Auto-synced consumer for " + meta.getListenerId());

                    if (meta.isDlq()) {
                        newDetail.setDlqName(meta.getTopic() + ".DLQ");
                    }

                    mqConsumerRepo.save(newDetail);
                    insertCount++;
                    log.info("[Sync] Seeded new MQ Consumer: [{}] -> Topic: [{}]", endpointName, meta.getTopic());
                }
            } catch (Exception e) {
                log.error("[Sync] Error syncing consumer {}: {}", meta.getListenerId(), e.getMessage(), e);
            }
        }

        log.info(">>> Sync [{}] COMPLETE. Inserted: {}, updated: {}", getSyncType(), insertCount, updateCount);
    }
}
