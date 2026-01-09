package com.template.app.features.actionRule.vo;

import java.io.Serializable;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ActionConfig implements Serializable {

    private Integer maxAttempts;

    private Long backoffPeriod;

    private Double backoffMultiplier;

    private String deadLetterTarget;

    private String deadLetterTargetSuffix;

    private String alertRecipient;

    private String classBluePrintKey;
}
