package com.tangzc.mpe.autotable.annotation.impl;

import com.tangzc.mpe.autotable.annotation.ColumnComment;
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
public class ColumnCommentImpl implements ColumnComment {

    /**
     * 字段备注
     */
    private String value = "";

    @Override
    public Class<? extends Annotation> annotationType() {
        return ColumnComment.class;
    }
}
