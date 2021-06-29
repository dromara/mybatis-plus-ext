package com.tangzc.mpe.relevance.metadata;

import com.tangzc.mpe.relevance.builder.ConditionSign;
import com.tangzc.mpe.relevance.metadata.annotation.BindField;
import lombok.Getter;

import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 绑定字段的 字段注解描述
 *
 * @author don
 */
@Getter
public class BindFieldDescription extends FieldDescription<BindField, JoinConditionDescription> {

    /**
     * 注解条件：字段匹配信息
     */
    private final List<JoinConditionDescription> conditions;

    /**
     * 注解绑定的字段：字段匹配信息
     */
    private final Method bindFieldGetMethod;

    public BindFieldDescription(String fieldName, Class<?> fieldClass, Method setMethod, boolean isCollection,
                                BindField bindField, Class<?> entityClass, List<JoinConditionDescription> conditions,
                                List<OrderByDescription> orderBys, Method bindFieldGetMethod) {
        super(fieldName, fieldClass, setMethod, isCollection, bindField, entityClass, bindField.customCondition(), orderBys);
        this.conditions = conditions.stream().distinct().collect(Collectors.toList());
        this.bindFieldGetMethod = bindFieldGetMethod;
    }

    @Override
    public ConditionSign<?, JoinConditionDescription> conditionUniqueKey() {
        return new ConditionSign<>(entityClass, conditions, customCondition, orderBys);
    }
}
