package com.app.features.action.vo.config;

import com.app.features.action.annotation.ActionConf;
import com.app.features.action.enums.ActionTypeEnum;
import com.app.features.action.vo.BaseActionConfig;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@ActionConf(ActionTypeEnum.MQ_RETRY)
public class MqRetryActionConfig extends BaseActionConfig {

    private Integer maxAttemps;
    
    private Long backoffPeriod;

    private Double backoffMultiplier;
 
}
