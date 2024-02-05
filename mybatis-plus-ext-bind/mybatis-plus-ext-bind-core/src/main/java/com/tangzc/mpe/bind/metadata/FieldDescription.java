package com.tangzc.mpe.bind.metadata;

import com.tangzc.mpe.magic.BeanClassUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 字段注解描述
 *
 * @author don
 */
@Getter
public abstract class FieldDescription<BIND_ANNOTATION extends Annotation, CONDITION_DESC> {
    /**
     * 被修饰字段：名
     */
    protected Field field;
    /**
     * 被修饰字段：类型
     */
    protected Class<?> fieldClass;
    /**
     * 被修饰字段：set方法
     * 最终赋值的时候调用
     */
    protected Method setMethod;
    /**
     * 被修饰字段：是否为集合
     */
    protected boolean isCollection;
    /**
     * 被修饰字段：注解
     */
    protected BIND_ANNOTATION bindAnnotation;

    /**
     * 关联表的Entity
     */
    protected Class<?> entityClass;

    /**
     * 注解条件：针对绑定表的自定义查询条件，例如：is_deleted=0 and enable=1
     */
    protected String customCondition;
    /**
     * 注解条件：排序
     */
    protected List<OrderByDescription> orderBys;
    /**
     * 最后的sql拼接，通常是limit ?
     */
    protected String last;

    public FieldDescription(Field field, Method setMethod, boolean isCollection,
                            BIND_ANNOTATION bindAnnotation, Class<?> entityClass,
                            String customCondition, List<OrderByDescription> orderBys, String last) {
        this.field = field;
        this.fieldClass = BeanClassUtil.getFieldRealClass(field);;
        this.setMethod = setMethod;
        this.isCollection = isCollection;
        this.bindAnnotation = bindAnnotation;
        this.entityClass = entityClass;
        this.customCondition = customCondition;
        this.orderBys = orderBys.stream().distinct().collect(Collectors.toList());
        this.last = last;
    }

    public String getFieldName() {
        return field.getName();
    }

    /**
     * 获取字段描述中，条件的唯一标志形式
     */
    public abstract ConditionSign<?, CONDITION_DESC> conditionUniqueKey();

    /**
     * 关联条件的唯一签名，用于对相同条件的关联做合并
     * @author don
     */
    @Getter
    @AllArgsConstructor
    public static class ConditionSign<ENTITY, CONDITION_DESC> implements Serializable {

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

            // 只要有last，就认为是不同的，因为无法合并last上的sql，大概率是用的limit 1之类的
            if (StringUtils.hasText(this.last) || StringUtils.hasText(thatLast)) {
                return false;
            }

            return joinEntityClass.equals(that.joinEntityClass)
                    && thisConditions.equals(thatConditions)
                    && customCondition.equals(thatCustomCondition)
                    && thisOrderBys.equals(thatOrderBys)
                    && last.equals(thatLast);
        }

        @Override
        public int hashCode() {
            return Objects.hash(joinEntityClass, getSortConditionStr(), customCondition, getSortOrderByStr(), last);
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
}
