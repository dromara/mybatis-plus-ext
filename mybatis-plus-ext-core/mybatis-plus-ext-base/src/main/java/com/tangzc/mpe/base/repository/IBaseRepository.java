package com.tangzc.mpe.base.repository;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tangzc.mpe.base.wrapper.MyLambdaQueryChainWrapper;

/**
 * @param <E> Entity
 * @author don
 */
public interface IBaseRepository<E> extends IService<E> {

    /**
     * lambda快捷查询，自动绑定需要关联的字段
     *
     * @return MyLambdaQueryChainWrapper
     */
    default MyLambdaQueryChainWrapper<E> lambdaQueryPlus() {
        return new MyLambdaQueryChainWrapper<>(getBaseMapper());
    }
}
