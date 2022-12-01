package com.tangzc.mpe.autotable;

import com.tangzc.mpe.autotable.annotation.Table;
import com.tangzc.mpe.autotable.constants.RunMode;
import com.tangzc.mpe.autotable.dynamicds.IDynamicDatasourceHandler;
import com.tangzc.mpe.autotable.properties.AutoTableProperties;
import com.tangzc.mpe.autotable.utils.ClassScanner;
import com.tangzc.mpe.magic.TableColumnUtil;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 启动时进行处理的实现类
 *
 * @author chenbin.sun
 */
@Slf4j
@Conditional(ProfileCondition.class)
public class StartUp {

    @Resource
    private AutoTableProperties autoTableProperties;
    @Resource
    private IDynamicDatasourceHandler dynamicDatasourceHandler;

    @PostConstruct
    public void startHandler() {

        // 判断模式，非none，才执行逻辑
        if (autoTableProperties.getMode() == RunMode.none) {
            return;
        }

        // 获取扫描包路径
        String[] packs = getModelPackage();

        // 从包package中获取所有的Class
        Set<Class<?>> classes = ClassScanner.scan(packs, Table.class);

        // 处理重名表，并根据数据源分类
        // <数据源，List<表>>
        Map<String, Set<Class<?>>> needHandleTableMap = filterTable(classes);

        dynamicDatasourceHandler.initTable(needHandleTableMap);
    }

    @NotNull
    private String[] getModelPackage() {
        String[] packs = autoTableProperties.getModelPackage();
        if (StringUtils.isEmpty(packs)) {
            packs = new String[]{getBootPackage()};
        }
        return packs;
    }

    /**
     * 处理ignore and repeat表
     */
    private Map<String, Set<Class<?>>> filterTable(Set<Class<?>> classes) {

        // <表名，List<java对象>>
        Map<String, List<Class<?>>> tableClassMap = classes.stream()
                .collect(Collectors.groupingBy(TableColumnUtil::getTableName));
        // <数据源，List<表>>
        Map<String, Set<Class<?>>> needHandleTableMap = new HashMap<>(tableClassMap.size());
        tableClassMap.forEach((tableName, sameClasses) -> {
            final Class<?> primaryClass;
            // 挑选出重名的表，找到其中标记primary的，用作生成数据表的依据
            if (sameClasses.size() > 1) {
                List<Class<?>> primaryClasses = sameClasses.stream()
                        .filter(StartUp::isPrimary)
                        .collect(Collectors.toList());
                if (primaryClasses.isEmpty()) {
                    throw new RuntimeException("表名[" + tableName + "]出现重复，必须为其中一个@Table指定primary！");
                }
                if (primaryClasses.size() > 1) {
                    throw new RuntimeException("表名[" + tableName + "]出现重复，有且只能有一个@Table的primary为true！");
                }
                primaryClass = primaryClasses.get(0);
            } else {
                primaryClass = sameClasses.get(0);
            }
            Table tableAnno = AnnotatedElementUtils.findMergedAnnotation(primaryClass, Table.class);
            needHandleTableMap.computeIfAbsent(tableAnno.dsName(), k -> new HashSet<>()).add(primaryClass);
        });
        return needHandleTableMap;
    }

    private static boolean isPrimary(Class<?> clazz) {
        Table annotation = AnnotatedElementUtils.findMergedAnnotation(clazz, Table.class);
        return annotation != null && annotation.primary();
    }

    private static String getBootPackage() {
        StackTraceElement[] stackTrace = new RuntimeException().getStackTrace();
        for (StackTraceElement stackTraceElement : stackTrace) {
            if ("main".equals(stackTraceElement.getMethodName())) {
                return ClassUtils.getPackageName(stackTraceElement.getClassName());
            }
        }
        throw new RuntimeException("未找到主默认包");
    }
}
