package com.tangzc.mpe.core.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tangzc.mpe.core.wrapper.MyLambdaQueryChainWrapper;

/**
 * @param <E> Entity
 * @author don
 */
public interface IBaseService<E> extends IService<E> {

    /**
     * lambda快捷查询，自动绑定需要关联的字段
     *
     * @return MyLambdaQueryChainWrapper
     */
    default MyLambdaQueryChainWrapper<E> lambdaQueryPlus() {
        return new MyLambdaQueryChainWrapper<>(getBaseMapper());
    }
}
