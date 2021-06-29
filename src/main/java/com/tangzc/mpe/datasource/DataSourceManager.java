package com.tangzc.mpe.datasource;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.tangzc.mpe.common.ApplicationStartListener;
import com.tangzc.mpe.common.EntityMapperManager;
import com.tangzc.mpe.datasource.metadata.DataSourceDescription;
import com.tangzc.mpe.datasource.metadata.annotation.DataSource;
import com.tangzc.mpe.datasource.metadata.event.EntityUpdateEvent;
import com.tangzc.mpe.util.BeanClassUtil;
import com.tangzc.mpe.util.TableColumnUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.StringUtils;

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
public class DataSourceManager implements ApplicationListener<EntityUpdateEvent<?>>, ApplicationStartListener.EntityFieldScanner {

    private static final Map<String, List<DataSourceDescription>> ENTITY_LIST_MAP = new HashMap<>();
    /**
     * 实体中，待更新字段分组缓存
     */
    private static final Map<String, Map<DescriptionSignature, List<DataSourceDescription>>> ENTITY_GROUP_CACHE_MAP = new HashMap<>();
    /**
     * 实体下，字段与方法的缓存
     */
    private static final Map<String, Map<String, Method>> ENTITY_FIELD_METHOD_CACHE_MAP = new HashMap<>();

    @Override
    public void scan(Class<?> entityClass, Field field) {

        DataSource dataSource = AnnotationUtils.findAnnotation(field, DataSource.class);
        if (dataSource == null) {
            return;
        }

        if(StringUtils.isEmpty(dataSource.sourceName()) && dataSource.source() == Void.class) {
            log.error("{}类上的{}字段，@DataSource缺少`source`或`sourceName`属性，" +
                    "自动更新数据功能将被忽略", entityClass, field.getName());
            return;
        }

        String sourceName = dataSource.sourceName();
        if(StringUtils.isEmpty(sourceName)) {
            sourceName = dataSource.source().getName();
        }

        ENTITY_LIST_MAP.computeIfAbsent(sourceName, k -> new ArrayList<>())
                .add(new DataSourceDescription(entityClass, field, dataSource));
    }

    @Override
    public void onApplicationEvent(EntityUpdateEvent<?> entityUpdateEvent) {

        String entityName = entityUpdateEvent.getEntityName();
        Map<DescriptionSignature, List<DataSourceDescription>> descListGroup = ENTITY_GROUP_CACHE_MAP.computeIfAbsent(entityName, k -> {
            List<DataSourceDescription> dataSourceDescriptions = ENTITY_LIST_MAP.get(entityName);
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
        String updateEntityName = entityUpdateEvent.getEntityName();
        Class<?> updateEntityClass = entityUpdateEvent.getEntityClass();
        for (DataSourceDescription desc : descList) {
            String fieldName = desc.getDataSource().field();
            if (entityUpdateEvent.getFields().isEmpty() || entityUpdateEvent.getFields().contains(fieldName)) {

                if (updateWrapper == null) {
                    updateWrapper = Wrappers.update();
                }

                try {
                    Method readMethod = ENTITY_FIELD_METHOD_CACHE_MAP.computeIfAbsent(updateEntityName, k -> new HashMap<>(1))
                            .computeIfAbsent(fieldName, k -> BeanClassUtil.getReadMethod(updateEntityClass, fieldName));
                    Object val = readMethod.invoke(entityUpdateEvent.getEntity());
                    updateWrapper.set(TableColumnUtil.humpToLine(desc.getEntityField().getName()), val);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        if (updateWrapper != null) {

            for (ConditionSignature condition : description.conditions) {
                try {
                    Method readMethod = ENTITY_FIELD_METHOD_CACHE_MAP.computeIfAbsent(updateEntityName, k -> new HashMap<>(1))
                            .computeIfAbsent(condition.sourceField, k -> BeanClassUtil.getReadMethod(updateEntityClass, condition.sourceField));
                    Object val = readMethod.invoke(entityUpdateEvent.getEntity());
                    updateWrapper.eq(TableColumnUtil.humpToLine(condition.selfField), val);
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
