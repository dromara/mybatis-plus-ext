package com.tangzc.mpe.base.repository;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tangzc.mpe.base.wrapper.MyLambdaQueryChainWrapper;

/**
 * 拓展lambdaQuery能力，集成了bind相关的查询能力
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
