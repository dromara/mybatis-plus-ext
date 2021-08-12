package com.tangzc.mpe.bind.metadata;

import com.tangzc.mpe.bind.metadata.annotation.JoinCondition;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * @author don
 */
@Getter
@AllArgsConstructor
public class JoinConditionDescription {

    /**
     * {@link JoinCondition#selfField()}
     */
    protected final String selfField;
    /**
     * {@link JoinCondition#joinField()}
     */
    protected final String joinField;
    /**
     * 条件匹配字段 {@link JoinCondition#selfField()} 的get方法
     */
    protected final Method selfFieldGetMethod;
    /**
     * 条件匹配字段 {@link JoinCondition#joinField()} 的get方法
     */
    protected final Method joinFieldGetMethod;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        JoinConditionDescription condition = (JoinConditionDescription) o;
        return selfField.equals(condition.selfField) && joinField.equals(condition.joinField);
    }

    @Override
    public int hashCode() {
        return Objects.hash(selfField, joinField);
    }

    @Override
    public String toString() {
        return selfField + "|" + joinField;
    }
}
