package com.template.app.features.action.vo.config;

import com.template.app.features.action.annotation.ActionConf;
import com.template.app.features.action.enums.ActionTypeEnum;
import com.template.app.features.action.vo.BaseActionConfig;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@ActionConf(ActionTypeEnum.IGNORE)
public class IgnoreActionConfig extends BaseActionConfig {
    
}
