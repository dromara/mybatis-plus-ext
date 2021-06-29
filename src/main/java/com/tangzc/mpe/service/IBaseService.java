package com.tangzc.mpe.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.IService;
import com.tangzc.mpe.wrapper.MyLambdaQueryChainWrapper;

import java.io.Serializable;
import java.util.List;
import java.util.function.Consumer;

/**
 * @param <E> Entity
 * @author don
 */
public interface IBaseService<E> extends IService<E> {

    String LAST_ONE_LIMIT = "limit 0,1";

    /**
     * 快捷获取queryWrapper
     */
    @Deprecated
    default LambdaQueryWrapper<E> queryWrapper() {
        return Wrappers.lambdaQuery(getEntityClass());
    }

    /**
     * 快捷获取updateWrapper
     */
    @Deprecated
    default LambdaUpdateWrapper<E> updateWrapper() {
        return Wrappers.lambdaUpdate(getEntityClass());
    }

    /**
     * 查询第一个匹配的数据
     *
     * @param queryWrapper 查询条件，通常带排序
     * @return 结果
     */
    @Deprecated
    default E getFirstOne(LambdaQueryWrapper<E> queryWrapper) {

        queryWrapper.last(LAST_ONE_LIMIT);
        return getOne(queryWrapper);
    }

    /**
     * 查询第一个匹配的数据
     *
     * @param queryWrapper 查询条件，通常带排序
     * @return 结果
     */
    @Deprecated
    default E getFirstOne(QueryWrapper<E> queryWrapper) {

        queryWrapper.last(LAST_ONE_LIMIT);
        return getOne(queryWrapper);
    }

    /**
     * 范围限定查询
     *
     * @param queryWrapper 查询条件
     * @param start        起始查询位置
     * @param length       查询长度
     * @return 结果集合
     */
    @Deprecated
    default List<E> listLimit(LambdaQueryWrapper<E> queryWrapper, long start, long length) {

        queryWrapper.last("limit " + start + "," + length);
        return list(queryWrapper);
    }

    /**
     * 范围限定查询
     *
     * @param queryWrapper 查询条件
     * @param start        起始查询位置
     * @param length       查询长度
     * @return 结果集合
     */
    @Deprecated
    default List<E> listLimit(QueryWrapper<E> queryWrapper, long start, long length) {

        queryWrapper.last("limit " + start + "," + length);
        return list(queryWrapper);
    }

    /**
     * 快捷更新，根据数据id
     * 注意：此方法未考虑分布式情况下的线程安全问题
     *
     * @param id      数据id
     * @param updater 更新逻辑
     * @return 更新结果
     */
    default boolean updateById(Serializable id, Consumer<E> updater) {
        synchronized (String.valueOf(id).intern()) {
            E entity = this.getById(id);
            updater.accept(entity);
            return this.updateById(entity);
        }
    }

    /**
     * lambda快捷查询，自动绑定需要关联的字段
     * @return MyLambdaQueryChainWrapper
     */
    default MyLambdaQueryChainWrapper<E> lambdaQueryPlus() {
        return new MyLambdaQueryChainWrapper<>(getBaseMapper());
    }
}
