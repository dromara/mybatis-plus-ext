package org.dromara.mpe.bind.metadata;

import org.dromara.mpe.bind.metadata.annotation.BindEntityByMid;
import lombok.Getter;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 绑定实体的 字段注解描述
 *
 * @author don
 */
@Getter
public class BindEntityByMidDescription extends FieldDescription<BindEntityByMid, MidConditionDescription> {

    /**
     * 自定义的查询字段
     */
    private final List<ColumnDescription> selectColumns;

    /**
     * 自定义的查询条件
     */
    private final MidConditionDescription condition;

    public BindEntityByMidDescription(Field field,
                                      Method setMethod,
                                      boolean isCollection,
                                      BindEntityByMid bindEntityByMid,
                                      Class<?> entityClass,
                                      List<ColumnDescription> selectColumns,
                                      MidConditionDescription condition,
                                      List<OrderByDescription> orderBys) {
        super(field, setMethod, isCollection, bindEntityByMid, entityClass,
                bindEntityByMid.customCondition(), orderBys, bindEntityByMid.last());
        this.selectColumns = selectColumns.stream().distinct().collect(Collectors.toList());
        this.condition = condition;
    }

    @Override
    public ConditionSign<?, MidConditionDescription> conditionUniqueKey() {
        return new ConditionSign<>(entityClass, Collections.singletonList(condition), customCondition, orderBys, last);
    }
}
