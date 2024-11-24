package org.dromara.mpe.bind.metadata;

import org.dromara.mpe.bind.metadata.annotation.BindEntity;
import lombok.Getter;

import java.lang.reflect.Field;
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
     * 自定义的查询字段
     */
    private final List<String> selectColumns;
    /**
     * 注解条件：字段匹配信息
     */
    private final List<JoinConditionDescription> conditions;

    public BindEntityDescription(Field field,
                                 Method setMethod,
                                 boolean isCollection,
                                 BindEntity bindEntity,
                                 Class<?> entityClass,
                                 List<String> selectColumns,
                                 List<JoinConditionDescription> conditions,
                                 List<OrderByDescription> orderBys) {
        super(field, setMethod, isCollection, bindEntity, entityClass,
                bindEntity.customCondition(), orderBys, bindEntity.last());
        this.selectColumns = selectColumns.stream().distinct().collect(Collectors.toList());
        this.conditions = conditions.stream().distinct().collect(Collectors.toList());
    }

    @Override
    public ConditionSign<?, JoinConditionDescription> conditionUniqueKey() {
        return new ConditionSign<>(entityClass, conditions, customCondition, orderBys, last);
    }
}
