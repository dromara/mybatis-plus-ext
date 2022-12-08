package com.tangzc.mpe.bind.metadata;

import com.tangzc.mpe.magic.TableColumnNameUtil;
import com.tangzc.mpe.bind.metadata.annotation.JoinCondition;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.lang.reflect.Field;
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
    protected final Field selfField;

    /**
     * {@link JoinCondition#joinField()}
     */
    protected final Field joinField;
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
        return getSelfFieldName().equals(condition.getSelfFieldName()) && getJoinFieldName().equals(condition.getJoinFieldName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSelfFieldName(), getJoinFieldName());
    }

    @Override
    public String toString() {
        return getSelfFieldName() + "|" + getJoinFieldName();
    }

    public String getSelfFieldName() {
        return selfField.getName();
    }

    public String getJoinFieldName() {
        return joinField.getName();
    }

    public String getSelfColumnName() {
        return TableColumnNameUtil.getRealColumnName(selfField);
    }

    public String getJoinColumnName() {
        return TableColumnNameUtil.getRealColumnName(joinField);
    }
}
