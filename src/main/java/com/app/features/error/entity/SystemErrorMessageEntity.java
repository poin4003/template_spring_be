package com.app.features.error.entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import com.app.base.BaseEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "system_error_message")
@Data
@EqualsAndHashCode(callSuper = true)
public class SystemErrorMessageEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "error_defination_id", nullable = false)
    private SystemErrorDefinitionEntity errorDefinition;

    @Column(name = "language_code", nullable = false)
    private String languageCode;

    @Column(name = "content", nullable = false)
    private String content;
}
