package com.baomidou.mybatisplus.core.metadata;

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
        return "";
    }

    @Override
    public String schema() {
        return "";
    }

    @Override
    public boolean keepGlobalPrefix() {
        return false;
    }

    @Override
    public String resultMap() {
        return "";
    }

    @Override
    public boolean autoResultMap() {
        return false;
    }

    @Override
    public String[] excludeProperty() {
        return new String[0];
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return TableName.class;
    }
}
