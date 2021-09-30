package com.tangzc.mpe.base;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tangzc.mpe.base.event.InitScanEntityEvent;
import com.tangzc.mpe.base.event.InitScanMapperEvent;
import com.tangzc.mpe.base.util.ReflectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author don
 */
@Slf4j
//@Component
public class MapperScanner {

    private static final Map<String, BaseMapper<?>> ENTITY_MAPPER_CACHE_MAP = new ConcurrentHashMap<>();

    @Resource
    private ApplicationEventPublisher applicationEventPublisher;
    @Autowired(required = false)
    private List<BaseMapper<?>> proxyMapperList;

    @EventListener
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {

        // 初始化所有的Entity和Mapper
        if (proxyMapperList == null) {
            return;
        }
        for (BaseMapper<?> proxyMapper : proxyMapperList) {
            Class<?> entityClass = ReflectionUtil.getEntityClass(proxyMapper);
            applicationEventPublisher.publishEvent(new InitScanMapperEvent(proxyMapper));
            applicationEventPublisher.publishEvent(new InitScanEntityEvent(entityClass));
            ENTITY_MAPPER_CACHE_MAP.put(entityClass.getName(), proxyMapper);
        }
    }

    public static <ENTITY> BaseMapper<ENTITY> getMapper(Class<ENTITY> entityClass) {

        String entityClassName = entityClass.getName();
        BaseMapper<?> baseMapper = ENTITY_MAPPER_CACHE_MAP.get(entityClassName);
        if (baseMapper == null) {
            throw new RuntimeException("未发现" + entityClassName + "的BaseMapper实现");
        }
        return (BaseMapper<ENTITY>) baseMapper;
    }

}
