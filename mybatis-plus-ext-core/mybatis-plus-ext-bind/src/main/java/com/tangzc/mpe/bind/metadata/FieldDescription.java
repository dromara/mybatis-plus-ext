package com.tangzc.mpe.bind.metadata;

import com.tangzc.mpe.bind.builder.ConditionSign;
import lombok.Getter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 字段注解描述
 *
 * @author don
 */
@Getter
public abstract class FieldDescription<BIND_ANNOTATION extends Annotation, CONDITION_DESC> {
    /**
     * 被修饰字段：名
     */
    protected String fieldName;
    /**
     * 被修饰字段：类型
     */
    protected Class<?> fieldClass;
    /**
     * 被修饰字段：set方法
     */
    protected Method setMethod;
    /**
     * 被修饰字段：是否为集合
     */
    protected boolean isCollection;
    /**
     * 被修饰字段：注解
     */
    protected BIND_ANNOTATION bindAnnotation;

    /**
     * 关联表的Entity
     */
    protected Class<?> entityClass;

    /**
     * 注解条件：针对绑定表的自定义查询条件，例如：is_deleted=0 and enable=1
     */
    protected String customCondition;
    /**
     * 注解条件：排序
     */
    protected List<OrderByDescription> orderBys;
    /**
     * 最后的sql拼接，通常是limit ?
     */
    protected String last;

    public FieldDescription(String fieldName, Class<?> fieldClass, Method setMethod, boolean isCollection,
                            BIND_ANNOTATION bindAnnotation, Class<?> entityClass,
                            String customCondition, List<OrderByDescription> orderBys, String last) {
        this.fieldName = fieldName;
        this.fieldClass = fieldClass;
        this.setMethod = setMethod;
        this.isCollection = isCollection;
        this.bindAnnotation = bindAnnotation;
        this.entityClass = entityClass;
        this.customCondition = customCondition;
        this.orderBys = orderBys.stream().distinct().collect(Collectors.toList());
        this.last = last;
    }

    /**
     * 获取字段描述中，条件的唯一标志形式
     */
    public abstract ConditionSign<?, CONDITION_DESC> conditionUniqueKey();
}
