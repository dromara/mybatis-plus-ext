package com.tangzc.mpe.magic.util;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.IEnum;
import com.baomidou.mybatisplus.core.handlers.MybatisEnumTypeHandler;
import com.baomidou.mybatisplus.core.toolkit.ReflectionKit;

import java.util.Arrays;

public class EnumUtil {

    public static boolean isMpEnums(Class<?> enumClassType) {
        return MybatisEnumTypeHandler.isMpEnums(enumClassType);
    }

    /**
     * 获取枚举上 入库的字段类型
     * 如果使用了MP的枚举方案，那么会有一个指定的字段作为入库数据，此时数据类型为字段类型
     * 如果没有设置过，那就默认按照枚举的name()作为入库数据，此时类型为String
     */
    public static Class<?> getEnumFieldSaveDbType(Class<?> enumClassType) {
        if (enumClassType.isEnum()) {
            if (isMpEnums(enumClassType)) {
                if (IEnum.class.isAssignableFrom(enumClassType)) {
                    return ReflectionKit.getSuperClassGenericType(enumClassType, IEnum.class, 0);
                } else {
                    return Arrays.stream(enumClassType.getDeclaredFields())
                            .filter(field -> field.isAnnotationPresent(EnumValue.class))
                            .findFirst()
                            .orElseThrow(() -> new IllegalArgumentException(String.format("Could not find @EnumValue in Class: %s.", enumClassType.getName())))
                            .getType();
                }
            } else {
                return String.class;
            }
        } else {
            throw new IllegalArgumentException(String.format("Class: %s 非枚举类型", enumClassType.getName()));
        }
    }
}
