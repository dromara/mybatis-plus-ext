package com.tangzc.mpe.bind.manager;

import com.baomidou.mybatisplus.annotation.TableName;
import org.springframework.core.annotation.AnnotatedElementUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author don
 */
public class EntityTableNameManager {

    private static final Map<String, String> ENTITY_TABLE_NAME_CACHE_MAP = new ConcurrentHashMap<>();

    public static <ENTITY> String getTableName(Class<ENTITY> entityClass) {

        return ENTITY_TABLE_NAME_CACHE_MAP.computeIfAbsent(entityClass.getName(), k -> EntityTableNameManager.initTableName(entityClass));
    }

    private static String initTableName(Class<?> entityClass) {

        TableName tableNameAnno = AnnotatedElementUtils.findMergedAnnotation(entityClass, TableName.class);
        if (tableNameAnno != null) {
            return tableNameAnno.value();
        } else {
            return toSnakeCase(entityClass.getSimpleName());
        }

    }

    /***
     * 转换成小写蛇形命名（用于Java属性转换为小写数据库列名）
     */
    private static String toSnakeCase(String camelCaseStr) {

        // 全小写
        if (camelCaseStr.equals(camelCaseStr.toLowerCase())) {
            return camelCaseStr;
        }
        // 全大写直接return小写
        if (camelCaseStr.equals(camelCaseStr.toUpperCase())) {
            return camelCaseStr.toLowerCase();
        }
        // 大小写混合，则遇“大写”转换为“_小写”
        char[] chars = camelCaseStr.toCharArray();
        StringBuilder sb = new StringBuilder();
        for (char c : chars) {
            if (Character.isUpperCase(c)) {
                if (sb.length() > 0) {
                    sb.append("_");
                }
            }
            sb.append(Character.toLowerCase(c));
        }
        return sb.toString();
    }
}
