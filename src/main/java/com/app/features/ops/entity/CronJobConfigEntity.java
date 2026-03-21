package com.app.features.ops.entity;

import java.util.UUID;

import com.app.core.base.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "cronjob_config")
@Data
@EqualsAndHashCode(callSuper = true)
public class CronJobConfigEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "id")
    private OpsConfigEntity opsConfigEntity;

    @Column(name = "name")
    private String name;

    @Column(name = "expression")
    private String expression;

    @Column(name = "job_type")
    private String jobType;

    @Column(name = "lock_at_most_for")
    private String lockAtMostFor;

    @Column(name = "lock_at_least_for")
    private String lockAtLeastFor;
}
