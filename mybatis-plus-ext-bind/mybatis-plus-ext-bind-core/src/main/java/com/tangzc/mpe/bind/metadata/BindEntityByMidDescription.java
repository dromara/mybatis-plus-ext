package com.tangzc.mpe.bind.metadata;

import com.tangzc.mpe.bind.metadata.annotation.BindEntityByMid;
import lombok.Getter;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

/**
 * 绑定实体的 字段注解描述
 *
 * @author don
 */
@Getter
public class BindEntityByMidDescription extends FieldDescription<BindEntityByMid, MidConditionDescription> {

    private final MidConditionDescription condition;

    public BindEntityByMidDescription(Field field,
                                      Method setMethod,
                                      boolean isCollection,
                                      BindEntityByMid bindEntityByMid,
                                      Class<?> entityClass,
                                      MidConditionDescription condition,
                                      List<OrderByDescription> orderBys) {
        super(field, setMethod, isCollection, bindEntityByMid, entityClass,
                bindEntityByMid.customCondition(), orderBys, bindEntityByMid.last());
        this.condition = condition;
    }

    @Override
    public ConditionSign<?, MidConditionDescription> conditionUniqueKey() {
        return new ConditionSign<>(entityClass, Collections.singletonList(condition), customCondition, orderBys, last);
    }
}
