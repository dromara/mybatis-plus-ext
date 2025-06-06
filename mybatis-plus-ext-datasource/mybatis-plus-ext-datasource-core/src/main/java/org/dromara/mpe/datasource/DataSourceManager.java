package org.dromara.mpe.datasource;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.dromara.mpe.datasource.annotation.DataSource;
import org.dromara.mpe.datasource.description.DataSourceConditionDescription;
import org.dromara.mpe.datasource.description.WaitUpdateDescription;
import org.dromara.mpe.datasource.description.WaitUpdateFieldDescription;
import org.dromara.mpe.magic.MapperExecuter;
import org.dromara.mpe.magic.util.BeanClassUtil;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author don
 */
@Slf4j
public class DataSourceManager {

    /**
     * 实体中，待更新字段分组缓存
     */
    private static final Map<String, Map<DescriptionSignature, WaitUpdateDescription>> ENTITY_GROUP_CACHE_MAP = new HashMap<>();

    /**
     * 实体下，字段与方法的缓存
     */
    private static final Map<String, Map<String, Method>> ENTITY_FIELD_METHOD_CACHE_MAP = new HashMap<>();

    /**
     * 添加 某个类的某个字段，需要被数据冗余
     *
     * @param entityClass 某个类
     * @param entityField 某个字段
     * @param dataSource  冗余条件
     */
    public static void addDataSource(Class<?> entityClass, Field entityField, DataSource dataSource) {

        Class<?> sourceClass = dataSource.source();
        if (!StringUtils.hasText(dataSource.sourceName()) && sourceClass == Void.class) {
            log.error("{}类上的{}字段，@DataSource缺少`source`或`sourceName`属性，" +
                    "自动更新数据功能将被忽略", entityClass, entityField.getName());
            return;
        }

        // 获取冗余数据的源的名称（全路径）
        String sourceName = dataSource.sourceName();
        if (!StringUtils.hasText(sourceName)) {
            sourceName = sourceClass.getName();
        }

        DescriptionSignature descriptionSignature = new DescriptionSignature(
                entityClass,
                sourceClass,
                Arrays.stream(dataSource.conditions())
                        .map(con -> new ConditionSignature(con.selfField(), con.sourceField()))
                        .collect(Collectors.toList()),
                dataSource.updateCondition()
        );
        // 根据源类全名称，找到其关联的所有需要更新的记录，并根据更新条件分组，进行分组更新
        WaitUpdateDescription waitUpdateDescription = ENTITY_GROUP_CACHE_MAP.computeIfAbsent(sourceName, k -> new HashMap<>(4))
                .computeIfAbsent(descriptionSignature, k -> {
                    List<DataSourceConditionDescription> conditionDescriptions = Arrays.stream(dataSource.conditions()).map(con -> {
                        Field selfField = BeanClassUtil.getField(entityClass, con.selfField());
                        Field sourceField = BeanClassUtil.getField(sourceClass, con.sourceField());
                        return new DataSourceConditionDescription(selfField, sourceField);
                    }).collect(Collectors.toList());
                    return new WaitUpdateDescription(entityClass, sourceClass, dataSource.updateCondition(), conditionDescriptions, new ArrayList<>());
                });

        Field sourceField = BeanClassUtil.getField(sourceClass, dataSource.field());
        waitUpdateDescription.getWaitUpdateFields().add(new WaitUpdateFieldDescription(entityField, sourceField));
    }

    /**
     * 触发更新
     *
     * @param entity 变化的数据实体
     * @param fields 具体产生变更需要对外通知更新的字段
     */
    public static <E> void triggerUpdate(E entity, SFunction<E, ?>... fields) {

        EntityUpdateDto<E> entityUpdateDto = EntityUpdateDto.create(entity, fields);

        Map<DescriptionSignature, WaitUpdateDescription> waitUpdateEntityDescGroupByCondition = ENTITY_GROUP_CACHE_MAP.get(entityUpdateDto.getEntityName());
        if (waitUpdateEntityDescGroupByCondition == null) {
            return;
        }

        // 针对分好组的数据，做分组批量更新(提升性能，同一个类下多个属性冗余了同一个源下的多个属性的情况)
        for (WaitUpdateDescription waitUpdateDescription : waitUpdateEntityDescGroupByCondition.values()) {
            // 待更新的字段
            executeUpdate(entityUpdateDto, waitUpdateDescription);
        }
    }

    /**
     * 更新
     *
     * @param entityUpdateEvent     源数据事件
     * @param waitUpdateDescription 待更新数据的描述
     * @param <E>                   待更新的实体
     */
    private static <E> void executeUpdate(EntityUpdateDto<?> entityUpdateEvent, WaitUpdateDescription waitUpdateDescription) {

        Object sourceEntity = entityUpdateEvent.getEntity();

        // 组装update slq语句的set部分
        UpdateWrapper<E> updateWrapper = null;
        for (WaitUpdateFieldDescription waitUpdateField : waitUpdateDescription.getWaitUpdateFields()) {
            String sourceFieldName = waitUpdateField.getSourceFieldName();
            if (entityUpdateEvent.getFields().isEmpty() || entityUpdateEvent.getFields().contains(sourceFieldName)) {

                if (updateWrapper == null) {
                    updateWrapper = Wrappers.update();
                }

                try {
                    Object sourceFieldVal = getFieldVal(sourceEntity, sourceFieldName);
                    // 组装sql
                    updateWrapper.set(waitUpdateField.getEntityColumnName(), sourceFieldVal);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        // 当前bean存在需要被set的字段，组装update sql的where条件部分
        if (updateWrapper != null) {
            for (DataSourceConditionDescription condition : waitUpdateDescription.getDataSourceConditions()) {
                try {
                    String sourceConditionFieldName = condition.getSourceFieldName();
                    // 获取源字段的读取方法，并缓存
                    Object sourceConditionFieldVal = getFieldVal(sourceEntity, sourceConditionFieldName);
                    updateWrapper.eq(condition.getSelfColumnName(), sourceConditionFieldVal);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            // 拼接自定义的sql条件
            String updateCondition = waitUpdateDescription.getUpdateCondition();
            if (StringUtils.hasText(updateCondition)) {
                updateWrapper.apply(updateCondition);
            }

            // 获取mapper， 执行sql
            Class<E> waitUpdateEntityClass = (Class<E>) waitUpdateDescription.getEntityClass();
            UpdateWrapper<E> finalUpdateWrapper = updateWrapper;
            MapperExecuter.getMapperExecute(waitUpdateEntityClass, mapper -> mapper.update(null, finalUpdateWrapper));
        }
    }

    private static Object getFieldVal(Object sourceEntity, String sourceFieldName) throws IllegalAccessException, InvocationTargetException {
        Class<?> sourceEntityClass = sourceEntity.getClass();
        String sourceEntityClassName = sourceEntityClass.getName();
        // 获取并构建该源相关的缓存
        Map<String, Method> sourceEntityFieldMethodCache = ENTITY_FIELD_METHOD_CACHE_MAP.computeIfAbsent(sourceEntityClassName, k -> new HashMap<>(1));
        // 获取源字段的读取方法，并缓存
        Method sourceFieldReadMethod = sourceEntityFieldMethodCache.computeIfAbsent(sourceFieldName, k -> BeanClassUtil.getReadMethod(sourceEntityClass, sourceFieldName));
        // 获取源字段值
        return sourceFieldReadMethod.invoke(sourceEntity);
    }

    @Data
    @AllArgsConstructor
    private static class DescriptionSignature {
        private Class<?> entityClass;
        private Class<?> sourceClass;
        private List<ConditionSignature> conditions;

        private String updateCondition;
    }

    @Data
    @AllArgsConstructor
    private static class ConditionSignature {
        private String selfField;
        private String sourceField;
    }
}
