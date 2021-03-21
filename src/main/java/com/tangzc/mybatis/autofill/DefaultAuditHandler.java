package com.tangzc.mybatis.autofill;

import java.lang.reflect.Field;

/**
 * 框架内部使用，不对外提供
 */
public class DefaultAuditHandler implements AutoFillHandler<String> {

    @Override
    public String getVal(Object object, Class<?> clazz, Field field) {
        throw new RuntimeException("必须实现一个AuditHandler");
    }
}
