package com.template.app.features.ops.entity;

import java.util.UUID;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.template.app.base.BaseEntity;
import com.template.app.features.ops.enums.EndpointStatusEnum;
import com.template.app.features.ops.enums.EndpointTypeEnum;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("service_endpoint_configs")
public class ServiceEndpointConfigEntity extends BaseEntity {
    @TableId(value="service_endpoint_config_id", type=IdType.ASSIGN_UUID)
    private UUID serviceEndpointConfigId;

    @TableField("endpoint_type")
    private EndpointTypeEnum endpointType;

    @TableField("endpoint_status")
    private EndpointStatusEnum endpointStatus; 
}
