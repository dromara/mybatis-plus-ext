package com.tangzc.mpe.core.handler;

import java.io.Serializable;
import java.lang.reflect.Field;

/**
 * 指定获取数据操作人的方式
 * @author don
 */
public interface IOptionByAutoFillHandler<ID_TYPE extends Serializable> extends AutoFillHandler<ID_TYPE> {

    @Override
    ID_TYPE getVal(Object object, Class<?> clazz, Field field);
}
