package com.tangzc.mpe.bind.metadata;

import com.tangzc.mpe.bind.builder.ConditionSign;
import com.tangzc.mpe.bind.metadata.annotation.AggFuncField;
import com.tangzc.mpe.bind.metadata.annotation.BindAggFunc;
import com.tangzc.mpe.bind.metadata.enums.AggFuncEnum;
import com.tangzc.mpe.magic.TableColumnNameUtil;
import lombok.Getter;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 绑定函数的 字段注解描述
 *
 * @author don
 */
@Getter
public class BindAggFuncDescription extends FieldDescription<BindAggFunc, JoinConditionDescription> {

    /**
     * 注解条件：字段匹配信息
     */
    private final List<JoinConditionDescription> conditions;
    /**
     * 聚合函数
     */
    private final AggFuncEnum aggFunc;
    /**
     * 聚合函数字段的列名称
     */
    private final String aggColumnRealName;

    public BindAggFuncDescription(Field field, Method setMethod, BindAggFunc bindFunction,
                                  Class<?> entityClass, List<JoinConditionDescription> conditions) {
        super(field, setMethod, false, bindFunction, entityClass, bindFunction.customCondition(), Collections.emptyList(), "");
        this.conditions = conditions.stream().distinct().collect(Collectors.toList());
        AggFuncField aggFuncField = bindFunction.aggField();
        this.aggFunc = aggFuncField.func();
        this.aggColumnRealName = TableColumnNameUtil.getRealColumnName(entityClass, aggFuncField.field());
    }

    @Override
    public ConditionSign<?, JoinConditionDescription> conditionUniqueKey() {
        return new ConditionSign<>(entityClass, conditions, customCondition, orderBys, last);
    }
}
