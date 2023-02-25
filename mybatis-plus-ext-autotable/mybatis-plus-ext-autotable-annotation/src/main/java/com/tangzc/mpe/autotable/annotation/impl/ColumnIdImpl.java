package com.tangzc.mpe.autotable.annotation.impl;

import com.baomidou.mybatisplus.annotation.IdType;
import com.tangzc.mpe.autotable.annotation.ColumnId;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.lang.annotation.Annotation;


/**
 * 标志该字段为主键
 *
 * @author don
 */
@Getter
@Setter
@Accessors(fluent = true)
public class ColumnIdImpl implements ColumnId {

    private String value = "";

    private IdType mode = IdType.NONE;

    private String type = "";

    private int length = -1;

    private String comment = "";

    @Override
    public Class<? extends Annotation> annotationType() {
        return ColumnId.class;
    }
}
