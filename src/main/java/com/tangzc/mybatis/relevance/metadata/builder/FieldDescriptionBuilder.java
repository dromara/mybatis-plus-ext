package com.tangzc.mybatis.relevance.metadata.builder;

import com.tangzc.mybatis.relevance.metadata.BindEntityByMidDescription;
import com.tangzc.mybatis.relevance.metadata.BindEntityDescription;
import com.tangzc.mybatis.relevance.metadata.BindFieldByMidDescription;
import com.tangzc.mybatis.relevance.metadata.BindFieldDescription;
import com.tangzc.mybatis.relevance.metadata.JoinConditionDescription;
import com.tangzc.mybatis.relevance.metadata.MidConditionDescription;
import com.tangzc.mybatis.relevance.metadata.OrderByDescription;
import com.tangzc.mybatis.relevance.metadata.annotation.BindEntity;
import com.tangzc.mybatis.relevance.metadata.annotation.BindEntityByMid;
import com.tangzc.mybatis.relevance.metadata.annotation.BindField;
import com.tangzc.mybatis.relevance.metadata.annotation.BindFieldByMid;
import com.tangzc.mybatis.relevance.metadata.annotation.JoinCondition;
import com.tangzc.mybatis.relevance.metadata.annotation.JoinOrderBy;
import com.tangzc.mybatis.relevance.metadata.annotation.MidCondition;
import com.tangzc.mybatis.util.BeanClassUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 字段注解描述
 *
 * @author don
 */
@Slf4j
@Getter
public class FieldDescriptionBuilder {

    public static <BEAN> BindFieldDescription build(Class<BEAN> beanClass, Field field, BindField bindField) {

        boolean isCollection = Collection.class.isAssignableFrom(field.getType());
        Class<?> fieldClass = BeanClassUtil.getFieldRealClass(field);
        Class<?> entityClass = bindField.entity();
        String fieldName = field.getName();
        Method setMethod = BeanClassUtil.getWriteMethod(beanClass, field);

        Method readMethod = BeanClassUtil.getReadMethod(entityClass, bindField.field());
        List<JoinConditionDescription> conditionList = getConditionList(beanClass, entityClass, bindField.condition());
        List<OrderByDescription> orderByList = getOrderByList(bindField.orderBy());
        return new BindFieldDescription(fieldName, fieldClass, setMethod, isCollection, bindField,
                entityClass, conditionList, orderByList, readMethod);
    }

    public static <BEAN> BindEntityDescription build(Class<BEAN> beanClass, Field field, BindEntity bindEntity) {

        boolean isCollection = Collection.class.isAssignableFrom(field.getType());
        Class<?> fieldClass = BeanClassUtil.getFieldRealClass(field);
        Class<?> entityClass = bindEntity.entity();
        if (entityClass == Void.class) {
            entityClass = fieldClass;
        }

        Method setMethod = BeanClassUtil.getWriteMethod(beanClass, field);

        String fieldName = field.getName();
        List<JoinConditionDescription> conditionList = getConditionList(beanClass, entityClass, bindEntity.condition());
        List<OrderByDescription> orderByList = getOrderByList(bindEntity.orderBy());
        return new BindEntityDescription(fieldName, fieldClass, setMethod, isCollection, bindEntity,
                entityClass, conditionList, orderByList);
    }

    public static <BEAN> BindFieldByMidDescription build(Class<BEAN> beanClass, Field field, BindFieldByMid bindFieldByMid) {

        boolean isCollection = Collection.class.isAssignableFrom(field.getType());
        Class<?> fieldClass = BeanClassUtil.getFieldRealClass(field);
        Class<?> entityClass = bindFieldByMid.entity();
        if (entityClass == Void.class) {
            entityClass = fieldClass;
        }

        Method setMethod = BeanClassUtil.getWriteMethod(beanClass, field);
        Method bindFieldGetMethod = BeanClassUtil.getReadMethod(entityClass, bindFieldByMid.field());

        String fieldName = field.getName();
        MidConditionDescription conditionList = getCondition(beanClass, entityClass, bindFieldByMid.condition());
        List<OrderByDescription> orderByList = getOrderByList(bindFieldByMid.orderBy());
        return new BindFieldByMidDescription(fieldName, fieldClass, setMethod, isCollection, bindFieldByMid,
                entityClass, conditionList, orderByList, bindFieldGetMethod);
    }


    public static <BEAN> BindEntityByMidDescription build(Class<BEAN> beanClass, Field field, BindEntityByMid bindEntity) {

        boolean isCollection = Collection.class.isAssignableFrom(field.getType());
        Class<?> fieldClass = BeanClassUtil.getFieldRealClass(field);
        Class<?> entityClass = bindEntity.entity();
        if (entityClass == Void.class) {
            entityClass = fieldClass;
        }

        Method setMethod = BeanClassUtil.getWriteMethod(beanClass, field);

        String fieldName = field.getName();
        MidConditionDescription conditionList = getCondition(beanClass, entityClass, bindEntity.condition());
        List<OrderByDescription> orderByList = getOrderByList(bindEntity.orderBy());
        return new BindEntityByMidDescription(fieldName, fieldClass, setMethod, isCollection, bindEntity,
                entityClass, conditionList, orderByList);
    }

    private static <BEAN> List<JoinConditionDescription> getConditionList(Class<BEAN> beanClass, Class<?> joinClazz, JoinCondition[] joinConditions) {

        return Arrays.stream(joinConditions)
                .map(jc -> {
                    Method selfFieldGetMethod = BeanClassUtil.getReadMethod(beanClass, jc.selfField());
                    Method joinFieldGetMethod = BeanClassUtil.getReadMethod(joinClazz, jc.joinField());
                    return new JoinConditionDescription(jc.selfField(), jc.joinField(), selfFieldGetMethod, joinFieldGetMethod);
                }).collect(Collectors.toList());
    }

    private static <BEAN> MidConditionDescription getCondition(Class<BEAN> beanClass, Class<?> joinClazz, MidCondition midCondition) {

        Method selfFieldGetMethod = BeanClassUtil.getReadMethod(beanClass, midCondition.selfField());
        Method joinFieldGetMethod = BeanClassUtil.getReadMethod(joinClazz, midCondition.joinField());
        Method selfMidFieldGetMethod = BeanClassUtil.getReadMethod(midCondition.midEntity(), midCondition.selfMidField());
        Method joinMidFieldGetMethod = BeanClassUtil.getReadMethod(midCondition.midEntity(), midCondition.joinMidField());
        return new MidConditionDescription(midCondition.selfField(), midCondition.joinField(),
                selfFieldGetMethod, joinFieldGetMethod,
                midCondition.midEntity(), midCondition.selfMidField(),
                selfMidFieldGetMethod, midCondition.joinMidField(), joinMidFieldGetMethod);
    }

    private static List<OrderByDescription> getOrderByList(JoinOrderBy[] orderBy) {
        return Arrays.stream(orderBy)
                .map(ob -> new OrderByDescription(ob.field(), ob.isAsc()))
                .collect(Collectors.toList());
    }
}
