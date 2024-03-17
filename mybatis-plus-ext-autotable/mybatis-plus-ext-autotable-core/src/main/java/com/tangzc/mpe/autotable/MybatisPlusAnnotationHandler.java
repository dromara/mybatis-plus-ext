package com.tangzc.mpe.autotable;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.toolkit.LambdaUtils;
import com.tangzc.mpe.magic.MyAnnotationHandler;
import com.tangzc.mpe.magic.util.AnnotatedElementUtilsPlus;
import com.tangzc.mpe.magic.util.AnnotationDefaultValueHelper;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * 重新定义MybatisPlus的注解处理，兼容AutoTable的注解
 */
public class MybatisPlusAnnotationHandler extends MyAnnotationHandler {
    @Override
    public <T extends Annotation> T getAnnotation(Class<?> beanClass, Class<T> annotationClass) {
        T annotation = super.getAnnotation(beanClass, annotationClass);
        T tableName = getTableNameAnnotation(beanClass, annotationClass, annotation);
        if (tableName != null) {
            return tableName;
        }
        return annotation;
    }

    private <T extends Annotation> T getTableNameAnnotation(Class<?> beanClass, Class<T> annotationClass, T annotation) {

        if (annotationClass != TableName.class) {
            return null;
        }

        // 兼容AutoTable的TableName注解
        com.tangzc.autotable.annotation.TableName autoTableTableName = AnnotatedElementUtilsPlus.getDeepMergedAnnotation(beanClass, com.tangzc.autotable.annotation.TableName.class);
        if (autoTableTableName == null) {
            return null;
        }

        /* 实体上存在AutoTable的TableName注解，需要进行兼容处理 */

        if (annotation == null) {
            // 实体上未曾声明过MP的TableName的注解（包括继承TableName的子注解）,直接使用AutoTable的TableName的转化为MP的TableName
            return (T) AnnotationDefaultValueHelper.getAnnotationWithDefaultValues(TableName.class,
                    valMap -> valMap.put(LambdaUtils.extract(TableName::value).getImplMethodName(), autoTableTableName.value()));
        }

        TableName tableName = (TableName) annotation;
        if (tableName.value().isEmpty()) {
            // 声明过了TableName，但是没有明确的指定value，使用AutoTable的TableName的value，赋值到MP的TableName的value上
            TableName finalTableName = tableName;
            tableName = AnnotationDefaultValueHelper.getAnnotationWithDefaultValues(TableName.class,
                    valMap -> {
                        Method[] methods = TableName.class.getDeclaredMethods();
                        try {
                            for (Method method : methods) {
                                valMap.put(method.getName(), method.invoke(finalTableName));
                            }
                        } catch (Exception e) {
                            throw new RuntimeException("MPE兼容AutoTable的TableName逻辑出错", e);
                        }

                        // 重置value的值为AutoTable的TableName的value
                        valMap.put(LambdaUtils.extract(TableName::value).getImplMethodName(), autoTableTableName.value());
                    });
            // AnnotationDefaultValueHelper.setAnnoVal(tableName, LambdaUtils.extract(TableName::value).getImplMethodName(), autoTableTableName.value());
        }
        return (T) tableName;
    }
}
