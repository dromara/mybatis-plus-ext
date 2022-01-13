package com.tangzc.mpe.actable.manager.handler;

import com.tangzc.mpe.actable.annotation.DsName;
import com.tangzc.mpe.actable.annotation.Table;
import com.tangzc.mpe.actable.annotation.TablePrimary;
import com.tangzc.mpe.actable.constants.Constants;
import com.tangzc.mpe.actable.utils.ClassScanner;
import com.tangzc.mpe.actable.utils.ClassTools;
import com.tangzc.mpe.actable.utils.ColumnUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.AnnotatedElementUtils;
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
public class StartUpHandler {

    /**
     * 数据库类型：mysql
     */
    public static final String MYSQL = "mysql";

    /**
     * 数据库类型
     */
    @Value(Constants.ACTABLE_DATABASE_TYPE_KEY_VALUE)
    private String databaseType;
    /**
     * 自动创建模式：update表示更新，create表示删除原表重新创建
     */
    @Value(Constants.ACTABLE_TABLE_AUTO_KEY_VALUE)
    private String tableAuto;

    /**
     * 要扫描的model所在的pack
     */
    @Value(Constants.ACTABLE_MODEL_PACK_KEY_VALUE)
    private String pack;

    @Resource
    private TableInitHandler tableInitHandler;

    @PostConstruct
    public void startHandler() {

        if (checkRunEnd()) {
            return;
        }

        if (StringUtils.isEmpty(pack)) {
            pack = ClassTools.getBootPackage();
        }

        // 拆成多个pack，支持多个
        String[] packs = pack.split(",|;");

        // 从包package中获取所有的Class
        Set<Class<?>> classes = ClassScanner.scan(packs, Table.class);

        // 处理重名表，并根据数据源分类
        Map<String, Set<Class<?>>> needCreateTableMap = filterRepeatTable(classes);

        log.info("databaseType=mysql，开始执行mysql的处理方法");
        tableInitHandler.initTable(needCreateTableMap);
    }

    private boolean checkRunEnd() {
        // 执行mysql的处理方法
        if (!MYSQL.equals(databaseType)) {
            log.warn("{}数据库暂不支持！无法自动创建表", databaseType);
            return true;
        }

        // 不做任何事情
        if (!"none".equals(tableAuto) && !"update".equals(tableAuto) && !"create".equals(tableAuto) && !"add".equals(tableAuto)) {
            log.warn("配置mybatis.table.auto错误无法识别，当前配置只支持[none/update/create/add]三种类型!");
            return true;
        }

        // 不做任何事情
        if ("none".equals(tableAuto)) {
            log.info("配置mybatis.table.auto=none，不需要做任何事情");
            return true;
        }
        return false;
    }

    /**
     * 处理重名表
     */
    private Map<String, Set<Class<?>>> filterRepeatTable(Set<Class<?>> classes) {

        Map<String, List<Class<?>>> classMap = classes.stream().collect(Collectors.groupingBy(ColumnUtils::getTableName));
        // <数据源，List<表>>
        Map<String, Set<Class<?>>> needCreateTable = new HashMap<>();
        classMap.forEach((tableName, sameClasses) -> {
            final Class<?> primaryClass;
            // 挑选出重名的表，找到其中标记primary的，用作生成数据表的依据
            if (sameClasses.size() > 1) {
                List<Class<?>> primaryClasses = sameClasses.stream()
                        .filter(clazz -> AnnotatedElementUtils.findMergedAnnotation(clazz, TablePrimary.class).value())
                        .collect(Collectors.toList());
                if (primaryClasses.isEmpty()) {
                    throw new RuntimeException("表名[" + tableName + "]出现重复，必须指定一个为@TablePrimary！");
                }
                if (primaryClasses.size() > 1) {
                    throw new RuntimeException("表名[" + tableName + "]出现重复，有且只能有一个为@TablePrimary！");
                }
                primaryClass = primaryClasses.get(0);
            } else {
                primaryClass = sameClasses.get(0);
            }
            final DsName mergedAnnotation = AnnotatedElementUtils.findMergedAnnotation(primaryClass, DsName.class);
            String dsName = mergedAnnotation != null ? mergedAnnotation.value() : "";
            needCreateTable.computeIfAbsent(dsName, k -> new HashSet<>()).add(primaryClass);
        });
        return needCreateTable;
    }
}
