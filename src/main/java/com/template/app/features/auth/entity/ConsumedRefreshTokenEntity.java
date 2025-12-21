package com.template.app.features.auth.entity;

import java.time.Instant;
import java.util.UUID;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.template.app.base.BaseEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "consumed_refresh_tokens")
public class ConsumedRefreshTokenEntity extends BaseEntity { 
    @TableId(value = "token_history_id", type = IdType.ASSIGN_UUID)
    private UUID tokenHistoryId;

    @TableField(value = "key_store_id")
    private UUID keyStoreId;

    @TableField(value = "user_id")
    private UUID userId;

    @TableField(value = "token_value")
    private String tokenValue;

    @TableField(value = "expiry_date")
    private Instant expiryDate;

    @TableField(value = "used_at")
    private Instant usedAt;
}
