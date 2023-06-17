package com.tangzc.mpe.bind.metadata;

import com.tangzc.mpe.bind.metadata.annotation.BindFieldByMid;
import lombok.Getter;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

/**
 * 绑定字段的 字段注解描述
 *
 * @author don
 */
@Getter
public class BindFieldByMidDescription extends FieldDescription<BindFieldByMid, MidConditionDescription> {

    private final MidConditionDescription condition;

    /**
     * 注解绑定的字段：字段匹配信息
     */
    private final Method bindFieldGetMethod;

    /**
     * 注解绑定的字段：真是字段名
     */
    private final String realColumnName;

    public BindFieldByMidDescription(Field field,
                                     Method setMethod,
                                     boolean isCollection,
                                     BindFieldByMid bindFieldByMid,
                                     Class<?> entityClass,
                                     MidConditionDescription condition,
                                     List<OrderByDescription> orderBys,
                                     String realColumnName,
                                     Method bindFieldGetMethod) {
        super(field, setMethod, isCollection, bindFieldByMid, entityClass,
                bindFieldByMid.customCondition(), orderBys, bindFieldByMid.last());
        this.condition = condition;
        this.realColumnName = realColumnName;
        this.bindFieldGetMethod = bindFieldGetMethod;
    }

    @Override
    public ConditionSign<?, MidConditionDescription> conditionUniqueKey() {
        return new ConditionSign<>(entityClass, Collections.singletonList(condition), customCondition, orderBys, last);
    }
}
