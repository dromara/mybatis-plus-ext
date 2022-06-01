package com.tangzc.mpe.bind.metadata;

import com.tangzc.mpe.bind.builder.ConditionSign;
import com.tangzc.mpe.bind.metadata.annotation.BindEntity;
import lombok.Getter;

import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 绑定实体的 字段注解描述
 *
 * @author don
 */
@Getter
public class BindEntityDescription extends FieldDescription<BindEntity, JoinConditionDescription> {

    /**
     * 注解条件：字段匹配信息
     */
    private final List<JoinConditionDescription> conditions;

    /**
     * 注解绑定的字段：是否深度绑定数据
     */
    private final boolean deepBind;

    public BindEntityDescription(String fieldName,
                                 Class<?> fieldClass,
                                 Method setMethod,
                                 boolean isCollection,
                                 BindEntity bindEntity,
                                 Class<?> entityClass,
                                 List<JoinConditionDescription> conditions,
                                 List<OrderByDescription> orderBys) {
        super(fieldName, fieldClass, setMethod, isCollection, bindEntity, entityClass,
                bindEntity.customCondition(), orderBys, bindEntity.last());
        this.conditions = conditions.stream().distinct().collect(Collectors.toList());
        this.deepBind = bindEntity.deepBind();
    }

    @Override
    public ConditionSign<?, JoinConditionDescription> conditionUniqueKey() {
        return new ConditionSign<>(entityClass, conditions, customCondition, orderBys, last);
    }
}
