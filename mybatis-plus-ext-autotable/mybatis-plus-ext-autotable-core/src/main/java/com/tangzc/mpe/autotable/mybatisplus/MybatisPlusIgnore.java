package com.tangzc.mpe.autotable.mybatisplus;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.core.metadata.AnnotatedElementUtilsPlus;
import com.tangzc.mpe.autotable.strategy.IgnoreExt;

import java.lang.reflect.Field;

/**
 * @author don
 */
public class MybatisPlusIgnore implements IgnoreExt {
    @Override
    public boolean isIgnoreField(Field field, Class<?> clazz) {
        // 追加逻辑，当TableField(exist=false)的注解修饰时，自动忽略该字段
        TableField tableField = AnnotatedElementUtilsPlus.findDeepMergedAnnotation(field, TableField.class);
        return tableField != null && !tableField.exist();
    }
}
