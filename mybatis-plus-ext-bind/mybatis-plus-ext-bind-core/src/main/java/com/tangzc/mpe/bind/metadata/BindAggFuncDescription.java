package com.tangzc.mpe.bind.metadata;

import com.tangzc.mpe.bind.metadata.annotation.AggFuncField;
import com.tangzc.mpe.bind.metadata.annotation.BindAggFunc;
import com.tangzc.mpe.bind.metadata.enums.AggFuncEnum;
import com.tangzc.mpe.magic.BeanClassUtil;
import com.tangzc.mpe.magic.TableColumnNameUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 绑定函数的 字段注解描述
 *
 * @author don
 */
@Getter
public class BindAggFuncDescription {

    /**
     * 被修饰字段：名
     */
    private final Field field;
    /**
     * 被修饰字段：类型
     */
    private final Class<?> fieldClass;
    /**
     * 被修饰字段：set方法
     * 最终赋值的时候调用
     */
    private final Method setMethod;
    /**
     * 被修饰字段：注解
     */
    private final BindAggFunc bindAnnotation;

    /**
     * 关联表的Entity
     */
    private final Class<?> entityClass;

    /**
     * 注解条件：针对绑定表的自定义查询条件，例如：is_deleted=0 and enable=1
     */
    private final String customCondition;

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
        this.field = field;
        this.fieldClass = BeanClassUtil.getFieldRealClass(field);
        ;
        this.setMethod = setMethod;
        this.bindAnnotation = bindFunction;
        this.entityClass = entityClass;
        this.customCondition = bindFunction.customCondition();
        this.conditions = conditions.stream().distinct().collect(Collectors.toList());
        AggFuncField aggFuncField = bindFunction.aggField();
        this.aggFunc = aggFuncField.func();
        if (StringUtils.hasText(aggFuncField.field())) {
            this.aggColumnRealName = TableColumnNameUtil.getRealColumnName(entityClass, aggFuncField.field());
        } else {
            this.aggColumnRealName = aggFuncField.field();
        }
    }

    public static ConditionSign groupBy(BindAggFuncDescription description) {
        return new ConditionSign(description.getEntityClass(), description.getCustomCondition(), description.getConditions(), description.getAggFunc(), description.getAggColumnRealName());
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ConditionSign {

        /**
         * 关联表的Entity
         */
        private Class<?> entityClass;

        /**
         * 注解条件：针对绑定表的自定义查询条件，例如：is_deleted=0 and enable=1
         */
        private String customCondition;

        /**
         * 注解条件：字段匹配信息
         */
        private List<JoinConditionDescription> conditions;
        /**
         * 聚合函数
         */
        private AggFuncEnum aggFunc;
        /**
         * 聚合函数字段的列名称
         */
        private String aggColumnRealName;

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            BindAggFuncDescription.ConditionSign that = (BindAggFuncDescription.ConditionSign) o;
            String thisConditions = getSortConditionStr();
            String thatConditions = that.getSortConditionStr();
            return entityClass.equals(that.entityClass)
                    && thisConditions.equals(thatConditions)
                    && customCondition.equals(that.customCondition)
                    && aggFunc.equals(that.aggFunc)
                    && aggColumnRealName.equals(that.aggColumnRealName);
        }

        @Override
        public int hashCode() {
            return Objects.hash(entityClass, getSortConditionStr(), customCondition, aggFunc, aggColumnRealName);
        }

        private String getSortConditionStr() {

            return conditions.stream()
                    .map(Object::toString)
                    .sorted()
                    .collect(Collectors.joining("#"));
        }
    }
}
