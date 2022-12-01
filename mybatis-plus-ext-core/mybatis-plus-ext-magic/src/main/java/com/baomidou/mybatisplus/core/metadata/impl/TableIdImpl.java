package com.baomidou.mybatisplus.core.metadata.impl;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.lang.annotation.Annotation;

/**
 * @author don
 */
@Getter
@Setter
@Accessors(fluent = true)
public class TableIdImpl implements TableId {

    /**
     * 字段名（该值可无）
     */
    private String value = "";

    /**
     * 主键类型
     * {@link IdType}
     */
    private IdType type = IdType.NONE;

    @Override
    public Class<? extends Annotation> annotationType() {
        return TableField.class;
    }
}
