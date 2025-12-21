package com.template.app.features.sims.entity;

import java.util.UUID;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.template.app.base.BaseEntity;
import com.template.app.features.sims.enums.SimStatusEnum;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sims")
public class SimEntity extends BaseEntity {
    @TableId(value = "sim_id", type = IdType.ASSIGN_UUID)
    private UUID simId;

    @TableField("sim_phone_number")
    private String simPhoneNumber;

    @TableField("sim_status")
    private SimStatusEnum simStatus;

    @TableField("sim_selling_price")
    private Integer simSellingPrice;

    @TableField("sim_dealer_price")
    private Integer simDealerPrice;

    @TableField("sim_import_price")
    private Integer simImportPrice;
}
