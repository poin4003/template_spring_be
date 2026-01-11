package com.template.app.features.error.entity;

import java.util.UUID;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.template.app.base.BaseEntity;
import com.template.app.features.error.enums.ErrorCategoryEnum;
import com.template.app.features.error.vo.ExceptionClassMapping;
import com.template.app.handler.JsonTypeHanlder;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("system_error_definations")
public class SystemErrorDefinationEntity extends BaseEntity {

    @TableId(value = "error_id", type = IdType.ASSIGN_UUID) 
    private UUID errorId;

    @TableField("error_code")
    private Integer errorCode;

    @TableField("alias_key")
    private String aliasKey;

    @TableField("http_status")
    private Integer httpStatus;

    @TableField("category")
    private ErrorCategoryEnum category;

    @TableField(value = "exception_class_name", typeHandler = JsonTypeHanlder.class)
    private ExceptionClassMapping exceptionClassName;
}
