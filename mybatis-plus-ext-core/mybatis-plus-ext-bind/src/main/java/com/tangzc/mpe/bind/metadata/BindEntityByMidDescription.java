package com.tangzc.mpe.bind.metadata;

import com.tangzc.mpe.bind.builder.ConditionSign;
import com.tangzc.mpe.bind.metadata.annotation.BindEntityByMid;
import lombok.Getter;

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

    /**
     * 注解绑定的字段：是否深度绑定数据
     */
    private final boolean deepBind;

    public BindEntityByMidDescription(String fieldName,
                                      Class<?> fieldClass,
                                      Method setMethod,
                                      boolean isCollection,
                                      BindEntityByMid bindEntityByMid,
                                      Class<?> entityClass,
                                      MidConditionDescription condition,
                                      List<OrderByDescription> orderBys) {
        super(fieldName, fieldClass, setMethod, isCollection, bindEntityByMid, entityClass,
                bindEntityByMid.customCondition(), orderBys, bindEntityByMid.last());
        this.condition = condition;
        this.deepBind = bindEntityByMid.deepBind();
    }

    @Override
    public ConditionSign<?, MidConditionDescription> conditionUniqueKey() {
        return new ConditionSign<>(entityClass, Collections.singletonList(condition), customCondition, orderBys, last);
    }
}
