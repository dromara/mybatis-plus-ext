package com.tangzc.mpe.bind.metadata;

import com.tangzc.mpe.bind.builder.ConditionSign;
import com.tangzc.mpe.bind.metadata.annotation.BindFieldByMid;
import lombok.Getter;

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

    public BindFieldByMidDescription(String fieldName,
                                     Class<?> fieldClass,
                                     Method setMethod,
                                     boolean isCollection,
                                     BindFieldByMid bindFieldByMid,
                                     Class<?> entityClass,
                                     MidConditionDescription condition,
                                     List<OrderByDescription> orderBys,
                                     Method bindFieldGetMethod) {
        super(fieldName, fieldClass, setMethod, isCollection, bindFieldByMid, entityClass,
                bindFieldByMid.customCondition(), orderBys);
        this.condition = condition;
        this.bindFieldGetMethod = bindFieldGetMethod;
    }

    @Override
    public ConditionSign<?, MidConditionDescription> conditionUniqueKey() {
        return new ConditionSign<>(entityClass, Collections.singletonList(condition), customCondition, orderBys);
    }
}
