package com.tangzc.mpe.core.handler;

import java.lang.reflect.Field;

/**
 * 框架内部使用，不对外提供
 */
public class DefaultAuditHandler implements AutoFillHandler<String> {

    @Override
    public String getVal(Object object, Class<?> clazz, Field field) {
        throw new RuntimeException("使用@OptionUser及其衍生的注解的前提，必须实现一个AutoFillHandler接口，并交由spring管理");
    }
}
