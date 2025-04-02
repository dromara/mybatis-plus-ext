package org.dromara.mpe.processer.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author don
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoRepository {

    /**
     * 完整的Mapper名称，指定后，将会替换掉默认生成策略
     */
    String value() default "";

    /**
     * 生成的Repository类的后缀，前半部分固定为Entity的名字
     */
    String suffix() default "";

    /**
     * 生成Repository所在的包名，默认与Entity包名一致
     */
    String packageName() default "";

    /**
     * <p>指定的Repository的父类，通常用于自定义Repository的场景
     * <p>要求：
     * <p>1、值需要是类的全路径
     * <p>2、自定义的父类Repository必须继承自org.dromara.mpe.base.repository.BaseRepository
     * <p>3、自定义的父类Repository必须保留泛型M,E
     */
    String superclassName() default "";

    /**
     * 标记是否在Repository上根据实体的@Table(dsName="xxx")自动生成@DS("xxx")注解
     */
    boolean withDSAnnotation() default false;
}
