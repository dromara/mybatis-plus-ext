package com.tangzc.mpe.core.util;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

@Slf4j
public class ReflectionUtil {

    public static <ENTITY> Class<ENTITY> getEntityClass(BaseMapper<ENTITY> proxyMapper) {

        Class<ENTITY> mapperClass = (Class<ENTITY>) proxyMapper.getClass().getGenericInterfaces()[0];
        return getEntityClass(mapperClass);
    }

    public static <ENTITY> Class<ENTITY> getEntityClass(Class<ENTITY> mapperClass) {

        try {
            Type[] types = mapperClass.getGenericInterfaces();
            if (types.length > 0 && types[0] != null) {
                ParameterizedType genericType = (ParameterizedType) types[0];
                Type[] superTypes = genericType.getActualTypeArguments();
                if (superTypes != null && superTypes.length > 0 && superTypes[0] != null) {
                    String entityClassName = superTypes[0].getTypeName();
                    if (entityClassName.length() > 1) {
                        return (Class<ENTITY>) Class.forName(entityClassName);
                    }
                }
            }
        } catch (Exception e) {
            log.warn("解析Mapper({})泛型上的Entity出错", mapperClass);
        }
        return null;
    }
}
