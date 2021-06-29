package com.tangzc.mpe.common;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tangzc.mpe.util.ReflectionUtil;
import com.tangzc.mpe.util.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author don
 */
@Slf4j
public class EntityMapperManager {

    public static final Map<Class<?>, BaseMapper<?>> ENTITY_MAPPER_CACHE_MAP = new ConcurrentHashMap<>();

    public static <ENTITY> BaseMapper<ENTITY> getMapper(Class<ENTITY> entityClass) {

        BaseMapper<?> baseMapper = ENTITY_MAPPER_CACHE_MAP.get(entityClass);
        if (baseMapper == null) {
            throw new RuntimeException("未发现" + entityClass.getName() + "的BaseMapper实现");
        }
        return (BaseMapper<ENTITY>) baseMapper;
    }

    public static List<Class<?>> getEntityList() {
        return new ArrayList<>(ENTITY_MAPPER_CACHE_MAP.keySet());
    }

    public static List<BaseMapper<?>> getMapperList() {
        return new ArrayList<>(ENTITY_MAPPER_CACHE_MAP.values());
    }

    public static void initEntityMapper() {

        List<BaseMapper> proxyMapperList = SpringContextUtil.getBeansOfTypeList(BaseMapper.class);
        for (BaseMapper<?> proxyMapper : proxyMapperList) {
            Class<?> entityClass = ReflectionUtil.getEntityClass(proxyMapper);
            ENTITY_MAPPER_CACHE_MAP.put(entityClass, proxyMapper);
        }
    }
}
