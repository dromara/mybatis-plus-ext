package com.tangzc.mybatis.datasource;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.tangzc.mybatis.common.EntityMapperManager;
import com.tangzc.mybatis.datasource.metadata.DataSourceDescription;
import com.tangzc.mybatis.datasource.metadata.annotation.DataSource;
import com.tangzc.mybatis.datasource.metadata.event.EntityUpdateEvent;
import com.tangzc.mybatis.util.BeanClassUtil;
import com.tangzc.mybatis.util.ColumnUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author don
 */
@Slf4j
public class DataSourceManager implements ApplicationListener<EntityUpdateEvent<?>> {

    private static final Map<Class<?>, List<DataSourceDescription>> ENTITY_LIST_MAP = new HashMap<>();
    /**
     * 实体中，待更新字段分组缓存
     */
    private static final Map<Class<?>, Map<DescriptionSignature, List<DataSourceDescription>>> ENTITY_GROUP_CACHE_MAP = new HashMap<>();
    /**
     * 实体下，字段与方法的缓存
     */
    private static final Map<Class<?>, Map<String, Method>> ENTITY_FIELD_METHOD_CACHE_MAP = new HashMap<>();

    public static void addDataSource(Class<?> entityClass, Field field, DataSource dataSource) {

        Class<?> sourceClass = dataSource.source();

        ENTITY_LIST_MAP.computeIfAbsent(sourceClass, k -> new ArrayList<>())
                .add(new DataSourceDescription(entityClass, field, dataSource));
    }

    @Override
    public void onApplicationEvent(EntityUpdateEvent<?> entityUpdateEvent) {

        Class<?> entityClass = entityUpdateEvent.getEntityClass();
        Map<DescriptionSignature, List<DataSourceDescription>> descListGroup = ENTITY_GROUP_CACHE_MAP.computeIfAbsent(entityClass, k -> {
            List<DataSourceDescription> dataSourceDescriptions = ENTITY_LIST_MAP.get(entityClass);
            if (dataSourceDescriptions == null) {
                return Collections.emptyMap();
            }
            // 根据更新条件分组
            return dataSourceDescriptions.stream().collect(Collectors.groupingBy(this::groupBy));
        });

        for (Map.Entry<DescriptionSignature, List<DataSourceDescription>> descListEntry : descListGroup.entrySet()) {

            // 要更新的表及条件
            DescriptionSignature description = descListEntry.getKey();
            // 待更新的字段
            List<DataSourceDescription> descList = descListEntry.getValue();

            executeUpdate(entityUpdateEvent, description, descList);
        }
    }

    private <E> void executeUpdate(EntityUpdateEvent<?> entityUpdateEvent, DescriptionSignature description, List<DataSourceDescription> descList) {
        // 声明mapper
        Class<E> entityClass = (Class<E>) description.getEntityClass();
        BaseMapper<E> mapper = EntityMapperManager.getMapper(entityClass);
        // 组装条件
        UpdateWrapper<E> updateWrapper = null;
        Class<?> updateEntityClass = entityUpdateEvent.getEntityClass();
        for (DataSourceDescription desc : descList) {
            String fieldName = desc.getDataSource().field();
            if (entityUpdateEvent.getFields().isEmpty() || entityUpdateEvent.getFields().contains(fieldName)) {

                if (updateWrapper == null) {
                    updateWrapper = Wrappers.update();
                }

                try {
                    Method readMethod = ENTITY_FIELD_METHOD_CACHE_MAP.computeIfAbsent(updateEntityClass, k -> new HashMap<>(1))
                            .computeIfAbsent(fieldName, k -> BeanClassUtil.getReadMethod(updateEntityClass, fieldName));
                    Object val = readMethod.invoke(entityUpdateEvent.getEntity());
                    updateWrapper.set(ColumnUtil.humpToLine(desc.getEntityField().getName()), val);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        if (updateWrapper != null) {

            for (ConditionSignature condition : description.conditions) {
                try {
                    Method readMethod = ENTITY_FIELD_METHOD_CACHE_MAP.computeIfAbsent(updateEntityClass, k -> new HashMap<>(1))
                            .computeIfAbsent(condition.sourceField, k -> BeanClassUtil.getReadMethod(updateEntityClass, condition.sourceField));
                    Object val = readMethod.invoke(entityUpdateEvent.getEntity());
                    updateWrapper.eq(ColumnUtil.humpToLine(condition.selfField), val);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            mapper.update(null, updateWrapper);
        }
    }

    private DescriptionSignature groupBy(DataSourceDescription desc) {
        return new DescriptionSignature(
                desc.getEntityClass(),
                Arrays.stream(desc.getDataSource().condition())
                        .map(con -> new ConditionSignature(con.selfField(), con.sourceField()))
                        .collect(Collectors.toList()));
    }

    @Data
    @AllArgsConstructor
    private static class DescriptionSignature {
        private Class<?> entityClass;
        private List<ConditionSignature> conditions;
    }

    @Data
    @AllArgsConstructor
    private static class ConditionSignature {
        private String selfField;
        private String sourceField;
    }
}
