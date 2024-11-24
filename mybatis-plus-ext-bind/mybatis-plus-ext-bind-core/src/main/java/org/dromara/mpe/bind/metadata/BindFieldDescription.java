package org.dromara.mpe.bind.metadata;

import org.dromara.mpe.bind.metadata.annotation.BindField;
import lombok.Getter;

import java.lang.reflect.Field;
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
     * 注解绑定的字段：字段的get方法
     */
    private final Method bindFieldGetMethod;

    /**
     * 注解绑定的字段：真是字段名
     */
    private final String realColumnName;

    public BindFieldDescription(Field field, Method setMethod, boolean isCollection,
                                BindField bindAnnotation, Class<?> entityClass, List<JoinConditionDescription> conditions,
                                List<OrderByDescription> orderBys, String realColumnName, Method bindFieldGetMethod) {
        super(field, setMethod, isCollection, bindAnnotation, entityClass, bindAnnotation.customCondition(), orderBys, bindAnnotation.last());
        this.conditions = conditions.stream().distinct().collect(Collectors.toList());
        this.realColumnName = realColumnName;
        this.bindFieldGetMethod = bindFieldGetMethod;
    }

    @Override
    public ConditionSign<?, JoinConditionDescription> conditionUniqueKey() {
        return new ConditionSign<>(entityClass, conditions, customCondition, orderBys, last);
    }
}
