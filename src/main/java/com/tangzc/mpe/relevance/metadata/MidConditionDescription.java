package com.tangzc.mpe.relevance.metadata;

import com.tangzc.mpe.relevance.metadata.annotation.MidCondition;
import lombok.Getter;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * @author don
 */
@Getter
//@AllArgsConstructor
public class MidConditionDescription {

    /**
     * {@link MidCondition#selfField()}
     */
    private final String selfField;
    /**
     * {@link MidCondition#joinField()}
     */
    private final String joinField;
    /**
     * 条件匹配字段 {@link MidCondition#selfField()} 的get方法
     */
    private final Method selfFieldGetMethod;
    /**
     * 条件匹配字段 {@link MidCondition#joinField()} 的get方法
     */
    private final Method joinFieldGetMethod;

    /**
     * 中间表Entity，需要对应创建其Mapper
     */
    private final Class<?> midEntity;

    /**
     * {@link MidCondition#selfMidField()}
     */
    private final String selfMidField;
    /**
     * 条件匹配字段 {@link MidCondition#selfMidField()} 的get方法
     */
    protected final Method selfMidFieldGetMethod;
    /**
     * {@link MidCondition#joinMidField()}
     */
    private final String joinMidField;
    /**
     * 条件匹配字段 {@link MidCondition#joinMidField()} 的get方法
     */
    protected final Method joinMidFieldGetMethod;

    public MidConditionDescription(String selfField, String joinField, Method selfFieldGetMethod, Method joinFieldGetMethod, Class<?> midEntity, String selfMidField, Method selfMidFieldGetMethod, String joinMidField, Method joinMidFieldGetMethod) {
        this.selfField = selfField;
        this.joinField = joinField;
        this.selfFieldGetMethod = selfFieldGetMethod;
        this.joinFieldGetMethod = joinFieldGetMethod;
        this.midEntity = midEntity;
        this.selfMidField = selfMidField;
        this.selfMidFieldGetMethod = selfMidFieldGetMethod;
        this.joinMidField = joinMidField;
        this.joinMidFieldGetMethod = joinMidFieldGetMethod;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MidConditionDescription condition = (MidConditionDescription) o;
        return midEntity.equals(condition.midEntity) &&
                selfField.equals(condition.selfField) &&
                joinField.equals(condition.joinField) &&
                selfMidField.equals(condition.selfMidField) &&
                joinMidField.equals(condition.joinMidField);
    }

    @Override
    public int hashCode() {
        return Objects.hash(midEntity, selfField, joinField, selfMidField, joinMidField);
    }

    @Override
    public String toString() {
        return midEntity.getName() + "|" + selfField + "|" + joinField + "|" + selfMidField + "|" + joinMidField;
    }
}
