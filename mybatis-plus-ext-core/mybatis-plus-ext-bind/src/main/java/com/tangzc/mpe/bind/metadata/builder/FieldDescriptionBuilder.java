package com.tangzc.mpe.bind.metadata.builder;

import com.tangzc.mpe.base.util.BeanClassUtil;
import com.tangzc.mpe.bind.metadata.BindEntityByMidDescription;
import com.tangzc.mpe.bind.metadata.BindEntityDescription;
import com.tangzc.mpe.bind.metadata.BindFieldByMidDescription;
import com.tangzc.mpe.bind.metadata.BindFieldDescription;
import com.tangzc.mpe.bind.metadata.JoinConditionDescription;
import com.tangzc.mpe.bind.metadata.MidConditionDescription;
import com.tangzc.mpe.bind.metadata.OrderByDescription;
import com.tangzc.mpe.bind.metadata.annotation.BindEntity;
import com.tangzc.mpe.bind.metadata.annotation.BindEntityByMid;
import com.tangzc.mpe.bind.metadata.annotation.BindField;
import com.tangzc.mpe.bind.metadata.annotation.BindFieldByMid;
import com.tangzc.mpe.bind.metadata.annotation.JoinCondition;
import com.tangzc.mpe.bind.metadata.annotation.JoinOrderBy;
import com.tangzc.mpe.bind.metadata.annotation.MidCondition;
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
        Class<?> entityClass = bindField.entity();
        Method setMethod = BeanClassUtil.getWriteMethod(beanClass, field);

        Method readMethod = BeanClassUtil.getReadMethod(entityClass, bindField.field());
        List<JoinConditionDescription> conditionList = getConditionList(beanClass, entityClass, bindField.conditions());
        List<OrderByDescription> orderByList = getOrderByList(entityClass, bindField.orderBy());
        return new BindFieldDescription(field, setMethod, isCollection, bindField,
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

        List<JoinConditionDescription> conditionList = getConditionList(beanClass, entityClass, bindEntity.conditions());
        List<OrderByDescription> orderByList = getOrderByList(entityClass, bindEntity.orderBy());
        return new BindEntityDescription(field, setMethod, isCollection, bindEntity,
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

        MidConditionDescription conditionList = getCondition(beanClass, entityClass, bindFieldByMid.conditions());
        List<OrderByDescription> orderByList = getOrderByList(entityClass, bindFieldByMid.orderBy());
        return new BindFieldByMidDescription(field, setMethod, isCollection, bindFieldByMid,
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

        MidConditionDescription conditionList = getCondition(beanClass, entityClass, bindEntity.conditions());
        List<OrderByDescription> orderByList = getOrderByList(entityClass, bindEntity.orderBy());
        return new BindEntityByMidDescription(field, setMethod, isCollection, bindEntity,
                entityClass, conditionList, orderByList);
    }

    private static <BEAN> List<JoinConditionDescription> getConditionList(Class<BEAN> beanClass, Class<?> joinClazz, JoinCondition[] joinConditions) {

        return Arrays.stream(joinConditions)
                .map(jc -> {
                    Field selfField = BeanClassUtil.getField(beanClass, jc.selfField());
                    Field joinField = BeanClassUtil.getField(joinClazz, jc.joinField());
                    Method selfFieldGetMethod = BeanClassUtil.getReadMethod(beanClass, jc.selfField());
                    Method joinFieldGetMethod = BeanClassUtil.getReadMethod(joinClazz, jc.joinField());
                    return new JoinConditionDescription(selfField, joinField, selfFieldGetMethod, joinFieldGetMethod);
                }).collect(Collectors.toList());
    }

    private static <BEAN> MidConditionDescription getCondition(Class<BEAN> beanClass, Class<?> joinClazz, MidCondition midCondition) {

        Field selfField = BeanClassUtil.getField(beanClass, midCondition.selfField());
        Field joinField = BeanClassUtil.getField(joinClazz, midCondition.joinField());
        Field selfMidField = BeanClassUtil.getField(midCondition.midEntity(), midCondition.selfMidField());
        Field joinMidField = BeanClassUtil.getField(midCondition.midEntity(), midCondition.joinMidField());
        Method selfFieldGetMethod = BeanClassUtil.getReadMethod(beanClass, midCondition.selfField());
        Method joinFieldGetMethod = BeanClassUtil.getReadMethod(joinClazz, midCondition.joinField());
        Method selfMidFieldGetMethod = BeanClassUtil.getReadMethod(midCondition.midEntity(), midCondition.selfMidField());
        Method joinMidFieldGetMethod = BeanClassUtil.getReadMethod(midCondition.midEntity(), midCondition.joinMidField());
        return new MidConditionDescription(selfField, joinField,
                selfFieldGetMethod, joinFieldGetMethod,
                midCondition.midEntity(), selfMidField,
                selfMidFieldGetMethod, joinMidField, joinMidFieldGetMethod);
    }

    private static List<OrderByDescription> getOrderByList(Class<?> entityClass, JoinOrderBy[] orderBy) {
        return Arrays.stream(orderBy)
                .map(ob -> {
                    Field field = BeanClassUtil.getField(entityClass, ob.field());
                    return new OrderByDescription(field, ob.isAsc());
                })
                .collect(Collectors.toList());
    }
}
