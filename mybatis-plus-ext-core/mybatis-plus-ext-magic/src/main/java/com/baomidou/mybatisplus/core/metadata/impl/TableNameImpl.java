package com.baomidou.mybatisplus.core.metadata.impl;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.lang.annotation.Annotation;

/**
 * @author don
 */
@Getter
@Setter
public class TableNameImpl implements TableName {

    private String value = "";
    private String schema = "";
    private boolean keepGlobalPrefix = false;
    private String resultMap = "";
    private boolean autoResultMap = false;
    private String[] excludeProperty = {};


    @Override
    public String value() {
        return value;
    }

    @Override
    public String schema() {
        return schema;
    }

    @Override
    public boolean keepGlobalPrefix() {
        return keepGlobalPrefix;
    }

    @Override
    public String resultMap() {
        return resultMap;
    }

    @Override
    public boolean autoResultMap() {
        return autoResultMap;
    }

    @Override
    public String[] excludeProperty() {
        return excludeProperty;
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return TableName.class;
    }
}
