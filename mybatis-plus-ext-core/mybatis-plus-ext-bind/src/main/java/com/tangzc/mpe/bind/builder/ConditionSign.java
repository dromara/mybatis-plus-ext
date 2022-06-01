package com.tangzc.mpe.bind.builder;

import com.tangzc.mpe.bind.metadata.OrderByDescription;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 关联条件的唯一签名，用于对相同条件的关联做合并
 * @author don
 */
@Getter
@AllArgsConstructor
public class ConditionSign<ENTITY, CONDITION_DESC> {

    private final Class<ENTITY> joinEntityClass;

    private final List<CONDITION_DESC> conditions;

    private final String customCondition;

    private final List<OrderByDescription> orderBys;

    private final String last;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ConditionSign<?, ?> that = (ConditionSign<?, ?>) o;
        String thisConditions = getSortConditionStr();
        String thisOrderBys = getSortOrderByStr();

        String thatConditions = that.getSortConditionStr();
        String thatCustomCondition = that.getCustomCondition();
        String thatOrderBys = that.getSortOrderByStr();
        String thatLast = that.getLast();
        return joinEntityClass.equals(that.joinEntityClass)
                && thisConditions.equals(thatConditions)
                && customCondition.equals(thatCustomCondition)
                && thisOrderBys.equals(thatOrderBys)
                && last.equals(thatLast);
    }

    @Override
    public int hashCode() {
        return Objects.hash(joinEntityClass, getSortConditionStr(), customCondition, getSortOrderByStr());
    }

    private String getSortConditionStr() {

        return conditions.stream()
                .map(Object::toString)
                .sorted()
                .collect(Collectors.joining("#"));
    }

    private String getSortOrderByStr() {

        return orderBys.stream()
                .map(Object::toString)
                .sorted()
                .collect(Collectors.joining("#"));
    }
}
