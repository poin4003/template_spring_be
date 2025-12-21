package com.template.app.features.rbac.entity;

import java.util.List;
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
@TableName("roles")
public class RoleEntity extends BaseEntity {
    @TableId(value = "role_id", type = IdType.ASSIGN_UUID)
    private UUID roleId;

    @TableField("role_name")
    private String roleName;

    @TableField("role_key")
    private String roleKey;

    @TableField(exist = false)
    private List<PermissionEntity> permissions;
}
