package com.tangzc.mpe.autotable.annotation.impl;

import com.tangzc.mpe.autotable.annotation.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.lang.annotation.Annotation;


/**
 * 标志该字段不允许为空
 * @author don
 */
@Getter
@Setter
@Accessors(fluent = true)
public class NotNullImpl implements NotNull {

    private boolean value = false;

    @Override
    public Class<? extends Annotation> annotationType() {
        return NotNull.class;
    }
}
