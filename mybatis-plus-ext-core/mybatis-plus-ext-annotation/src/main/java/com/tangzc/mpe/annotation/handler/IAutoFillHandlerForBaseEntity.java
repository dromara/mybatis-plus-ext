package com.tangzc.mpe.annotation.handler;

import java.io.Serializable;

/**
 * 指定获取数据操作人的方式，如果使用了 BaseEntity 的话，需要实现该接口
 *
 * @author don
 */
public interface IAutoFillHandlerForBaseEntity<ID_TYPE extends Serializable> extends AutoFillHandler<ID_TYPE> {

}
