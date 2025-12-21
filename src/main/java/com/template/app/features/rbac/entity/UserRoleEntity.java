package com.template.app.features.rbac.entity;

import java.io.Serializable;
import java.util.UUID;

import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

@Data
@TableName("user_roles")
public class UserRoleEntity implements Serializable {
    private UUID userId;

    private UUID roleId;
}
