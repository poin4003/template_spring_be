package com.app.features.error.entity;

import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import com.app.base.BaseEntity;
import com.app.features.error.enums.ErrorCategoryEnum;
import com.app.features.error.vo.ExceptionClassMapping;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Table(name = "system_error_definition")
@Data
@EqualsAndHashCode(callSuper = true)
public class SystemErrorDefinitionEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "code", unique = true, nullable = false)
    private Integer code;

    @Column(name = "alias_key", nullable = false)
    private String aliasKey;

    @Column(name = "http_status", nullable = false)
    private Integer httpStatus;

    @Column(name = "category", nullable = false)
    private ErrorCategoryEnum category;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "exception_class_name", columnDefinition = "jsonb")
    private ExceptionClassMapping exceptionClassName;

    @OneToMany(mappedBy = "errorDefinition", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<SystemErrorMessageEntity> messages;
}
