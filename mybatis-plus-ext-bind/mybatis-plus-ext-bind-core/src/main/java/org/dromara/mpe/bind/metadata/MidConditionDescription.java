package org.dromara.mpe.bind.metadata;

import org.dromara.mpe.bind.metadata.annotation.MidCondition;
import org.dromara.mpe.magic.util.TableColumnNameUtil;
import lombok.Getter;

import java.lang.reflect.Field;
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
    private final Field selfField;
    /**
     * {@link MidCondition#joinField()}
     */
    private final Field joinField;
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
    private final Field selfMidField;
    /**
     * 条件匹配字段 {@link MidCondition#selfMidField()} 的get方法
     */
    protected final Method selfMidFieldGetMethod;
    /**
     * {@link MidCondition#joinMidField()}
     */
    private final Field joinMidField;
    /**
     * 条件匹配字段 {@link MidCondition#joinMidField()} 的get方法
     */
    protected final Method joinMidFieldGetMethod;

    private final String customCondition;

    public MidConditionDescription(Field selfField, Field joinField, Method selfFieldGetMethod, Method joinFieldGetMethod, Class<?> midEntity, Field selfMidField, Method selfMidFieldGetMethod, Field joinMidField, Method joinMidFieldGetMethod, String customCondition) {
        this.selfField = selfField;
        this.joinField = joinField;
        this.selfFieldGetMethod = selfFieldGetMethod;
        this.joinFieldGetMethod = joinFieldGetMethod;
        this.midEntity = midEntity;
        this.selfMidField = selfMidField;
        this.selfMidFieldGetMethod = selfMidFieldGetMethod;
        this.joinMidField = joinMidField;
        this.joinMidFieldGetMethod = joinMidFieldGetMethod;
        this.customCondition = customCondition;
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
                getSelfFieldName().equals(condition.getSelfFieldName()) &&
                getJoinFieldName().equals(condition.getJoinFieldName()) &&
                getSelfMidFieldName().equals(condition.getSelfMidFieldName()) &&
                getJoinMidFieldName().equals(condition.getJoinMidFieldName()) &&
                getCustomCondition().equals(condition.getCustomCondition());
    }

    @Override
    public int hashCode() {
        return Objects.hash(midEntity, getSelfFieldName(), getJoinFieldName(), getSelfMidFieldName(), getJoinMidFieldName(), getCustomCondition());
    }

    @Override
    public String toString() {
        return midEntity.getName() + "|" + getSelfFieldName() + "|" + getJoinFieldName() + "|" + getSelfMidFieldName() + "|" + getJoinMidFieldName() + "|" + getCustomCondition();
    }

    public String getSelfFieldName() {
        return selfField.getName();
    }

    public String getJoinFieldName() {
        return joinField.getName();
    }

    public String getSelfMidFieldName() {
        return selfMidField.getName();
    }

    public String getJoinMidFieldName() {
        return joinMidField.getName();
    }

    public String getSelfColumnName() {
        return TableColumnNameUtil.getColumnName(selfField);
    }

    public String getJoinColumnName() {
        return TableColumnNameUtil.getColumnName(joinField);
    }

    public String getSelfMidColumnName() {
        return TableColumnNameUtil.getColumnName(selfMidField);
    }

    public String getJoinMidColumnName() {
        return TableColumnNameUtil.getColumnName(joinMidField);
    }
}
