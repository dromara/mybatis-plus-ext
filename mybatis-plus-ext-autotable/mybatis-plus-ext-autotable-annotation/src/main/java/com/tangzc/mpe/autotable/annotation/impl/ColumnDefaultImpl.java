package com.tangzc.mpe.autotable.annotation.impl;

import com.tangzc.mpe.autotable.annotation.ColumnDefault;
import com.tangzc.mpe.autotable.annotation.enums.DefaultValueEnum;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.lang.annotation.Annotation;


/**
 * 字段的默认值
 * @author don
 */
@Getter
@Setter
@Accessors(fluent = true)
public class ColumnDefaultImpl implements ColumnDefault {

    /**
     * <p>列默认值类型</p>
     * <p>如果该值有值的情况下，将忽略 {@link #value} 的值</p>
     */
    private DefaultValueEnum type = DefaultValueEnum.UNDEFINED;

    /**
     * 字段的默认值
     */
    private String value = "";

    @Override
    public Class<? extends Annotation> annotationType() {
        return ColumnDefault.class;
    }
}
