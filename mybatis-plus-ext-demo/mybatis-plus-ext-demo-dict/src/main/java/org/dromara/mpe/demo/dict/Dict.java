package org.dromara.mpe.demo.dict;

import org.dromara.mpe.bind.metadata.annotation.BindField;
import org.dromara.mpe.bind.metadata.annotation.JoinCondition;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义字典注解
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
// 声明字典关联关系
@BindField(entity = SysDict.class, field = SysDictDefine.dictVal, conditions = @JoinCondition(selfField = "", joinField = ""))
@JoinCondition(selfField = "", joinField = SysDictDefine.dictKey)
public @interface Dict {

    @AliasFor(annotation = JoinCondition.class, attribute = "selfField")
    String value();
}
