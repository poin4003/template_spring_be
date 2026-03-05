package com.app.features.ops.entity;

import java.util.UUID;

import com.app.base.BaseEntity;
import com.app.features.ops.enums.OpsStatusEnum;
import com.app.features.ops.enums.OpsTypeEnum;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "ops_config")
@Data
@EqualsAndHashCode(callSuper = true)
public class OpsConfigEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;

    private OpsTypeEnum type;

    private OpsStatusEnum status; 

    @Column(name = "config_version")
    private Long configVersion;
}
