package com.app.features.ops.entity;

import java.util.Map;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.app.base.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "mq_consumer_config")
@Data
@EqualsAndHashCode(callSuper = true)
public class MqConsumerConfigEntity extends BaseEntity { 

    @Id
    private UUID endpointConfigId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "id")
    private OpsConfigEntity opsConfig;

    @Column(name = "source_name")
    private String sourceName;

    @Column(name = "consumer_group")
    private String consumerGroup;

    private Integer parallelism;

    @Column(name = "handler_key")
    private String handlerKey;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "config_data")
    private Map<String, Object> configData;

    @Column(name = "enable_dlq")
    private Boolean enableDlq;

    @Column(name = "dlq_name")
    private String dlqName;
}
