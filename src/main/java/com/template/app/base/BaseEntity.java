package com.template.app.base;

import com.baomidou.mybatisplus.annotation.TableField;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public abstract class BaseEntity extends AuditableEntity {
    @TableField("note")
    private String note;

    @TableField("description")
    private String description;
}
