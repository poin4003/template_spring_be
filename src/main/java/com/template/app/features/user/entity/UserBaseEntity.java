package com.template.app.features.user.entity;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.template.app.features.rbac.entity.RoleEntity;
import com.template.app.features.user.enums.UserStatusEnum;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("user_base")
public class UserBaseEntity extends BaseUserDetailEntity {

    @TableId(value = "user_id", type = IdType.ASSIGN_UUID)
    private UUID userId;

    @TableField(value = "user_email")
    private String userEmail;

    @TableField(value = "user_password")
    private String userPassword;

    @TableField(value = "user_status")
    private UserStatusEnum userStatus;

    @TableField(value = "user_login_time")
    private LocalDateTime userLoginTime;

    @TableField(value = "user_logout_time")
    private LocalDateTime userLogoutTime;

    @TableField(value = "user_login_ip")
    private String userLoginIp;

    @TableField(value = "del_flag")
    private String delFlag;

    @TableField(exist = false)
    private List<RoleEntity> roles;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new HashSet<>();

        if (this.roles == null) return authorities;

        authorities.addAll(roles.stream()
           .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getRoleKey()))
           .collect(Collectors.toList()));

        authorities.addAll(roles.stream()
            .flatMap(role -> role.getPermissions().stream())
            .map(permissionKey -> new SimpleGrantedAuthority(permissionKey.getPermissionKey()))
            .collect(Collectors.toList()));

        return authorities;
    }

    @Override
    public String getPassword() {
        return this.userPassword;
    }

    @Override
    public String getUsername() {
        return this.userEmail;
    }

    @Override
    public UserStatusEnum getUserStatus() {
        return this.userStatus;
    }

    @Override
    public String getDelFlag() {
        return this.delFlag;
    }
}
