package com.template.app.features.rbac.entity;

import java.io.Serializable;
import java.util.UUID;

import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

@Data
@TableName("role_permissions")
public class RolePermissionEntity implements Serializable {
    private UUID roleId;
    
    private UUID permissionId;
}
