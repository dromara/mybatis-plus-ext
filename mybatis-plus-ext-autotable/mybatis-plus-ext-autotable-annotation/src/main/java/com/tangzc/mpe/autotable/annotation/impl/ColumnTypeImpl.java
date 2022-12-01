package com.tangzc.mpe.autotable.annotation.impl;

import com.tangzc.mpe.autotable.annotation.ColumnType;
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
public class ColumnTypeImpl implements ColumnType {

    private String value = "";
    private int length = 0;
    private int decimalLength = 0;

    @Override
    public Class<? extends Annotation> annotationType() {
        return ColumnType.class;
    }
}
