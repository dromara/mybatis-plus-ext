package org.dromara.mpe.bind.metadata.builder;

import org.dromara.mpe.bind.metadata.BindAggFuncDescription;
import org.dromara.mpe.bind.metadata.BindEntityByMidDescription;
import org.dromara.mpe.bind.metadata.BindEntityDescription;
import org.dromara.mpe.bind.metadata.BindFieldByMidDescription;
import org.dromara.mpe.bind.metadata.BindFieldDescription;
import org.dromara.mpe.bind.metadata.ColumnDescription;
import org.dromara.mpe.bind.metadata.JoinConditionDescription;
import org.dromara.mpe.bind.metadata.MidConditionDescription;
import org.dromara.mpe.bind.metadata.OrderByDescription;
import org.dromara.mpe.bind.metadata.annotation.BindAggFunc;
import org.dromara.mpe.bind.metadata.annotation.BindEntity;
import org.dromara.mpe.bind.metadata.annotation.BindEntityByMid;
import org.dromara.mpe.bind.metadata.annotation.BindField;
import org.dromara.mpe.bind.metadata.annotation.BindFieldByMid;
import org.dromara.mpe.bind.metadata.annotation.JoinCondition;
import org.dromara.mpe.bind.metadata.annotation.JoinOrderBy;
import org.dromara.mpe.bind.metadata.annotation.MidCondition;
import org.dromara.mpe.magic.util.BeanClassUtil;
import org.dromara.mpe.magic.util.TableColumnNameUtil;
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

    public static <BEAN> BindAggFuncDescription build(Class<BEAN> beanClass, Field field, BindAggFunc bindAggFunc) {

        Class<?> entityClass = bindAggFunc.entity();
        Method setMethod = BeanClassUtil.getWriteMethod(beanClass, field);

        List<JoinConditionDescription> conditionList = getConditionList(beanClass, entityClass, bindAggFunc.conditions());
        return new BindAggFuncDescription(field, setMethod, bindAggFunc, entityClass, conditionList);
    }

    public static <BEAN> BindFieldDescription build(Class<BEAN> beanClass, Field field, BindField bindFieldAnno) {

        boolean isCollection = Collection.class.isAssignableFrom(field.getType());
        Class<?> entityClass = bindFieldAnno.entity();
        Method setMethod = BeanClassUtil.getWriteMethod(beanClass, field);

        String realColumnName = TableColumnNameUtil.getColumnName(entityClass, bindFieldAnno.field());
        Method readMethod = BeanClassUtil.getReadMethod(entityClass, bindFieldAnno.field());
        List<JoinConditionDescription> conditionList = getConditionList(beanClass, entityClass, bindFieldAnno.conditions());
        List<OrderByDescription> orderByList = getOrderByList(entityClass, bindFieldAnno.orderBy());
        return new BindFieldDescription(field, setMethod, isCollection, bindFieldAnno,
                entityClass, conditionList, orderByList, realColumnName, readMethod);
    }

    public static <BEAN> BindEntityDescription build(Class<BEAN> beanClass, Field field, BindEntity bindEntity) {

        boolean isCollection = Collection.class.isAssignableFrom(field.getType());
        Class<?> fieldClass = BeanClassUtil.getFieldRealClass(field);
        Class<?> entityClass = bindEntity.entity();
        if (entityClass == Void.class) {
            entityClass = fieldClass;
        }

        Method setMethod = BeanClassUtil.getWriteMethod(beanClass, field);

        final Class<?> finalEntityClass = entityClass;
        List<String> selectColumns = Arrays.stream(bindEntity.selectFields())
                .map(fieldName -> TableColumnNameUtil.getColumnName(finalEntityClass, fieldName))
                .collect(Collectors.toList());
        List<JoinConditionDescription> conditionList = getConditionList(beanClass, entityClass, bindEntity.conditions());
        List<OrderByDescription> orderByList = getOrderByList(entityClass, bindEntity.orderBy());
        return new BindEntityDescription(field, setMethod, isCollection, bindEntity,
                entityClass, selectColumns, conditionList, orderByList);
    }

    public static <BEAN> BindFieldByMidDescription build(Class<BEAN> beanClass, Field field, BindFieldByMid bindFieldByMid) {

        boolean isCollection = Collection.class.isAssignableFrom(field.getType());
        Class<?> fieldClass = BeanClassUtil.getFieldRealClass(field);
        Class<?> entityClass = bindFieldByMid.entity();
        if (entityClass == Void.class) {
            entityClass = fieldClass;
        }

        Method setMethod = BeanClassUtil.getWriteMethod(beanClass, field);
        String realColumnName = TableColumnNameUtil.getColumnName(entityClass, bindFieldByMid.field());
        Method bindFieldGetMethod = BeanClassUtil.getReadMethod(entityClass, bindFieldByMid.field());

        MidConditionDescription conditionList = getCondition(beanClass, entityClass, bindFieldByMid.conditions());
        List<OrderByDescription> orderByList = getOrderByList(entityClass, bindFieldByMid.orderBy());
        return new BindFieldByMidDescription(field, setMethod, isCollection, bindFieldByMid,
                entityClass, conditionList, orderByList, realColumnName, bindFieldGetMethod);
    }


    public static <BEAN> BindEntityByMidDescription build(Class<BEAN> beanClass, Field field, BindEntityByMid bindEntity) {

        boolean isCollection = Collection.class.isAssignableFrom(field.getType());
        Class<?> fieldClass = BeanClassUtil.getFieldRealClass(field);
        Class<?> entityClass = bindEntity.entity();
        if (entityClass == Void.class) {
            entityClass = fieldClass;
        }

        Method setMethod = BeanClassUtil.getWriteMethod(beanClass, field);

        final Class<?> finalEntityClass = entityClass;
        List<ColumnDescription> selectColumns = Arrays.stream(bindEntity.selectFields())
                .map(fieldName -> {
                    String columnName = TableColumnNameUtil.getColumnName(finalEntityClass, fieldName);
                    return new ColumnDescription(columnName, fieldName);
                })
                .collect(Collectors.toList());
        MidConditionDescription conditionList = getCondition(beanClass, entityClass, bindEntity.conditions());
        List<OrderByDescription> orderByList = getOrderByList(entityClass, bindEntity.orderBy());
        return new BindEntityByMidDescription(field, setMethod, isCollection, bindEntity,
                entityClass, selectColumns, conditionList, orderByList);
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
        String customCondition = midCondition.customCondition();
        return new MidConditionDescription(selfField, joinField,
                selfFieldGetMethod, joinFieldGetMethod,
                midCondition.midEntity(), selfMidField,
                selfMidFieldGetMethod, joinMidField, joinMidFieldGetMethod, customCondition);
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
