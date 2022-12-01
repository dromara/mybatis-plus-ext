package com.tangzc.mpe.autotable.annotation.impl;

import com.tangzc.mpe.autotable.annotation.Primary;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.lang.annotation.*;


/**
 * 标志该字段为主键
 * @author don
 */
@Getter
@Setter
@Accessors(fluent = true)
public class PrimaryImpl implements Primary {

    private boolean value = false;

    @Override
    public Class<? extends Annotation> annotationType() {
        return Primary.class;
    }
}
