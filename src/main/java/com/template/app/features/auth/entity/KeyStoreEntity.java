package com.template.app.features.auth.entity;

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
@TableName(value = "key_store")
public class KeyStoreEntity extends BaseEntity {

    @TableId(value = "key_store_id", type = IdType.ASSIGN_UUID)
    private UUID keyStoreId;

    @TableField(value = "user_id")
    private UUID userId;

    @TableField(value = "public_key")
    private String publicKey;

    @TableField(value = "private_key")
    private String privateKey;

    @TableField(value = "refresh_token")
    private String refreshToken;
}
