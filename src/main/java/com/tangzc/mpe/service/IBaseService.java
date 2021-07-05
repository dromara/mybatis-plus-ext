package com.tangzc.mpe.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tangzc.mpe.wrapper.MyLambdaQueryChainWrapper;

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
