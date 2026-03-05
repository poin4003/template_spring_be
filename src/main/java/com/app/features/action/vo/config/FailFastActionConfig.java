package com.app.features.action.vo.config;

import com.app.features.action.annotation.ActionConf;
import com.app.features.action.enums.ActionTypeEnum;
import com.app.features.action.vo.BaseActionConfig;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@ActionConf(ActionTypeEnum.FAIL_FAST)
public class FailFastActionConfig extends BaseActionConfig {
    
}
