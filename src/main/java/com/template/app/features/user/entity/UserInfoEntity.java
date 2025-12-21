package com.template.app.features.user.entity;

import java.time.LocalDateTime;
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
@TableName("user_info")
public class UserInfoEntity extends BaseEntity {

    @TableId(value = "user_id", type = IdType.ASSIGN_UUID)
    private UUID userId;

    @TableField(value = "username")
    private String userName;

    @TableField(value = "user_phone_number")
    private String userPhoneNumber;

    @TableField(value = "user_birthday")
    private LocalDateTime userBirthday;
}
