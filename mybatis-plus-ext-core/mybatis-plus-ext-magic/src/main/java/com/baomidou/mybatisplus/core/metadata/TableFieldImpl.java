package com.baomidou.mybatisplus.core.metadata;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Getter;
import lombok.Setter;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.UnknownTypeHandler;

import java.lang.annotation.Annotation;

@Getter
@Setter
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
    public String value() {
        return value;
    }

    @Override
    public boolean exist() {
        return exist;
    }

    @Override
    public String condition() {
        return condition;
    }

    @Override
    public String update() {
        return update;
    }

    @Override
    public FieldStrategy insertStrategy() {
        return insertStrategy;
    }

    @Override
    public FieldStrategy updateStrategy() {
        return insertStrategy;
    }

    @Override
    public FieldStrategy whereStrategy() {
        return whereStrategy;
    }

    @Override
    public FieldFill fill() {
        return fill;
    }

    @Override
    public boolean select() {
        return select;
    }

    @Override
    public boolean keepGlobalFormat() {
        return keepGlobalFormat;
    }

    @Override
    public String property() {
        return property;
    }

    @Override
    public JdbcType jdbcType() {
        return jdbcType;
    }

    @Override
    public Class<? extends TypeHandler> typeHandler() {
        return typeHandler;
    }

    @Override
    public boolean javaType() {
        return javaType;
    }

    @Override
    public String numericScale() {
        return numericScale;
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return TableField.class;
    }
}
