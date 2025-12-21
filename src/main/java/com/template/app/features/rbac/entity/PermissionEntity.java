package com.template.app.features.rbac.entity;

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
@TableName("permissions")
public class PermissionEntity extends BaseEntity {
    @TableId(value = "permission_id", type = IdType.ASSIGN_UUID)
    private UUID permissionId;

    @TableField(value = "permission_name")
    private String permissionName;

    @TableField(value = "permission_key")
    private String permissionKey;
}
