package org.dromara.mpe.base;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.ClassUtils;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import org.dromara.mpe.base.event.InitScanEntityEvent;
import org.dromara.mpe.magic.util.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

import java.util.List;

/**
 * @author don
 */
@Slf4j
//@Component
public class MapperScanner {

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @EventListener
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {

        List<TableInfo> tableInfos = TableInfoHelper.getTableInfos();

        // 初始化所有的Entity和Mapper
        if (tableInfos.isEmpty()) {
            return;
        }
        for (TableInfo tableInfo : tableInfos) {

            Class<?> entityClass = tableInfo.getEntityType();
            if (entityClass != null) {
                applicationEventPublisher.publishEvent(new InitScanEntityEvent(entityClass));
            }
        }
    }

    public static <ENTITY, R> R getMapperExecute(Class<ENTITY> entityClass, SFunction<BaseMapper<ENTITY>, R> sFunction) {
        try {
            // 如果mapper存在spring中，则使用spring中的mapper，自带多数据源切换等策略
            TableInfo tableInfo = TableInfoHelper.getTableInfo(entityClass);
            Class<? extends BaseMapper<ENTITY>> mapperClass = (Class<? extends BaseMapper<ENTITY>>) ClassUtils.toClassConfident(tableInfo.getCurrentNamespace());
            BaseMapper<ENTITY> springMapper = SpringContextUtil.getBeanOfType(mapperClass);
            return sFunction.apply(springMapper);
        } catch (Exception ignore) {
            return SqlHelper.execute(entityClass, sFunction);
        }
    }
}
