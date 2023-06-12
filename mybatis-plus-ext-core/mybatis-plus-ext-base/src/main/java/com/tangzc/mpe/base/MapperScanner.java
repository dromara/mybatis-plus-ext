package com.tangzc.mpe.base;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.tangzc.mpe.base.event.InitScanEntityEvent;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSession;
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

    @Resource
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

    public static <ENTITY> BaseMapper<ENTITY> getMapper(Class<ENTITY> entityClass) {

        TableInfo tableInfo = TableInfoHelper.getTableInfo(entityClass);

        String entityClassName = entityClass.getName();
        BaseMapper<?> mapper;
        try (SqlSession sqlSession = SqlHelper.sqlSession(entityClass)) {
            mapper = (BaseMapper<?>) tableInfo.getConfiguration().getMapper(entityClass, sqlSession);
        }
        if (mapper == null) {
            throw new RuntimeException("未发现" + entityClassName + "的BaseMapper实现");
        }
        return (BaseMapper<ENTITY>) mapper;
    }
}
