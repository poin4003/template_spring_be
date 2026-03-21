package com.app.features.action.entity;

import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.app.core.base.BaseEntity;
import com.app.features.action.enums.ActionStatusEnum;
import com.app.features.action.enums.ActionTypeEnum;
import com.app.features.action.enums.TargetTypeEnum;
import com.app.features.action.vo.BaseActionConfig;
import com.app.features.error.entity.SystemErrorDefinitionEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "action")
@Data
@EqualsAndHashCode(callSuper = true)
public class ActionEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;

    @Column(name = "target_type")
    private TargetTypeEnum targetType;

    @Column(name = "target_id")
    private UUID targetId;

    @Column(name = "action_type")
    private ActionTypeEnum actionType;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "config_data", columnDefinition = "jsonb")
    private BaseActionConfig configData;

    private Integer priority;

    private ActionStatusEnum status;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "action_mapping",
        joinColumns = @JoinColumn(name = "action_id"),
        inverseJoinColumns = @JoinColumn(name = "error_id")
    )
    private List<SystemErrorDefinitionEntity> errors;
}
