package com.tangzc.mpe.condition;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.tangzc.mpe.base.util.TableColumnUtil;
import com.tangzc.mpe.condition.metadata.DynamicConditionDescription;
import com.tangzc.mpe.condition.metadata.annotation.DynamicCondition;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author don
 */
@Slf4j
public class DynamicConditionManager {

    private static final Table<String, String, List<DynamicConditionDescription>> DYN_CON_CACHE = HashBasedTable.create();

    public static void add(Class<?> entityClass, Field field, DynamicCondition dynamicCondition) {

        String tableName = TableColumnUtil.getTableName(entityClass);

        List<DynamicConditionDescription> dynamicConditionDescriptions = DYN_CON_CACHE.get(tableName, entityClass);
        if (dynamicConditionDescriptions == null) {
            dynamicConditionDescriptions = new ArrayList<>();
            DYN_CON_CACHE.put(tableName, entityClass.getName(), dynamicConditionDescriptions);
        }
        dynamicConditionDescriptions.add(new DynamicConditionDescription(entityClass, field, dynamicCondition));
    }

    public static List<DynamicConditionDescription> getDynamicCondition(String tableName) {

        Map<String, List<DynamicConditionDescription>> row = DYN_CON_CACHE.row(tableName);

        // 不存在，直接返回
        if (row.isEmpty()) {
            return Collections.emptyList();
        }

        // 只有一个表实体
        if (row.size() == 1) {
            return row.values().stream().findFirst().get();
        }

        // 多个表实体的情况（多个实体对应一个表）
        try {
            StackTraceElement[] stackTrace = new RuntimeException().getStackTrace();
            String mapperProxyClassName = null;
            for (int i = 0; i < stackTrace.length; i++) {
                // 找到MP的代理类，下一个就是Mapper的代理类
                if ("com.baomidou.mybatisplus.core.override.MybatisMapperProxy".equals(stackTrace[i].getClassName())) {
                    mapperProxyClassName = stackTrace[i + 1].getClassName();
                    break;
                }
            }
            if (StringUtils.isEmpty(mapperProxyClassName)) {
                throw new RuntimeException("未找到执行操作的Mapper类。");
            }
            Class<?> mapperClass = ((Class<?>) Class.forName(mapperProxyClassName).getGenericInterfaces()[0]);
            String entityClass = getEntityClass(mapperClass);
            if (entityClass == null) {
                throw new RuntimeException("找到了Mapper但是未找到Mapper上的实体。");
            }
            return row.get(entityClass);
        } catch (Exception e) {
            throw new RuntimeException("动态条件，多个实体映射一个表的情况下，未找到当前查询的实体。", e);
        }
    }

    private static <ENTITY> String getEntityClass(Class<ENTITY> mapperClass) {

        try {
            Type[] types = mapperClass.getGenericInterfaces();
            if (types.length > 0 && types[0] != null) {
                ParameterizedType genericType = (ParameterizedType) types[0];
                Type[] superTypes = genericType.getActualTypeArguments();
                if (superTypes != null && superTypes.length > 0 && superTypes[0] != null) {
                    return superTypes[0].getTypeName();
                }
            }
        } catch (Exception e) {
            log.warn("解析Mapper({})泛型上的Entity出错", mapperClass);
        }
        return null;
    }
}
