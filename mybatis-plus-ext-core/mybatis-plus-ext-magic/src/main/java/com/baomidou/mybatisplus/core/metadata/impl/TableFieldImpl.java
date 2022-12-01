package com.baomidou.mybatisplus.core.metadata.impl;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.UnknownTypeHandler;

import java.lang.annotation.Annotation;

/**
 * @author don
 */
@Getter
@Setter
@Accessors(fluent = true)
public class TableFieldImpl implements TableField {

    private String value = "";
    private boolean exist = true;
    private String condition = "";
    private String update = "";
    private FieldStrategy insertStrategy = FieldStrategy.DEFAULT;
    private FieldStrategy updateStrategy = FieldStrategy.DEFAULT;
    private FieldStrategy whereStrategy = FieldStrategy.DEFAULT;
    private FieldFill fill = FieldFill.DEFAULT;
    private boolean select = true;
    private boolean keepGlobalFormat = false;
    private String property = "";
    private JdbcType jdbcType = JdbcType.UNDEFINED;
    private Class<? extends TypeHandler> typeHandler = UnknownTypeHandler.class;
    private boolean javaType = false;
    private String numericScale = "";

    @Override
    public Class<? extends Annotation> annotationType() {
        return TableField.class;
    }
}
