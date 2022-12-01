package com.tangzc.mpe.autotable.annotation.enums;

/**
 * <p>默认值类型</p>
 * <p>当指定默认值类型({@link #UNDEFINED}除外)的时候，会忽略自定义默认值，并按照默认值的类型匹配值</p>
 * @author don
 */
public enum DefaultValueEnum {

    /**
     * 未定义：在注解中必须填写一个值，同时表示无意义
     */
    UNDEFINED,
    /**
     * 空字符串：仅限于字符串类型
     */
    EMPTY_STRING,
    /**
     * null值
     */
    NULL;

    public static boolean isValid(DefaultValueEnum defaultValueEnum) {
        return defaultValueEnum != null && defaultValueEnum != UNDEFINED;
    }

    public static boolean isInvalid(DefaultValueEnum defaultValueEnum) {
        return !isValid(defaultValueEnum);
    }
}
