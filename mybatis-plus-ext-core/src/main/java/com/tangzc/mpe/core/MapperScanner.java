package com.tangzc.mpe.core;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tangzc.mpe.core.event.InitScanEntityEvent;
import com.tangzc.mpe.core.event.InitScanMapperEvent;
import com.tangzc.mpe.core.util.ReflectionUtil;
import com.tangzc.mpe.core.util.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author don
 */
@Slf4j
//@Component
public class MapperScanner implements ApplicationListener<ContextRefreshedEvent> {

    private static final Map<Class<?>, BaseMapper<?>> ENTITY_MAPPER_CACHE_MAP = new ConcurrentHashMap<>();

    @Resource
    private ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {

        // 初始化所有的Entity和Mapper
        List<BaseMapper> proxyMapperList = SpringContextUtil.getBeansOfTypeList(BaseMapper.class);
        for (BaseMapper<?> proxyMapper : proxyMapperList) {
            Class<?> entityClass = ReflectionUtil.getEntityClass(proxyMapper);
            applicationEventPublisher.publishEvent(new InitScanMapperEvent(proxyMapper));
            applicationEventPublisher.publishEvent(new InitScanEntityEvent(entityClass));
            ENTITY_MAPPER_CACHE_MAP.put(entityClass, proxyMapper);
        }
    }

    public static <ENTITY> BaseMapper<ENTITY> getMapper(Class<ENTITY> entityClass) {

        BaseMapper<?> baseMapper = ENTITY_MAPPER_CACHE_MAP.get(entityClass);
        if (baseMapper == null) {
            throw new RuntimeException("未发现" + entityClass.getName() + "的BaseMapper实现");
        }
        return (BaseMapper<ENTITY>) baseMapper;
    }

}
