package com.baomidou.mybatisplus.core.metadata.impl;

import com.baomidou.mybatisplus.annotation.TableName;
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
public class TableNameImpl implements TableName {

    private String value = "";
    private String schema = "";
    private boolean keepGlobalPrefix = false;
    private String resultMap = "";
    private boolean autoResultMap = false;
    private String[] excludeProperty = {};

    @Override
    public Class<? extends Annotation> annotationType() {
        return TableName.class;
    }
}
