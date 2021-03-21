package com.tangzc.mybatis.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;

/**
 *
 * @author don
 * @param <M> mapper
 * @param <E> Entity
 */
public class BaseService<M extends BaseMapper<E>, E> extends ServiceImpl<M, E> {

    private static final String LAST_ONE_LIMIT = "limit 0,1";

    /**
     * 快捷获取queryWrapper
     */
    public LambdaQueryWrapper<E> queryWrapper() {
        return Wrappers.lambdaQuery(getEntityClass());
    }

    /**
     * 快捷获取updateWrapper
     */
    public LambdaUpdateWrapper<E> updateWrapper() {
        return Wrappers.lambdaUpdate(getEntityClass());
    }

    /**
     * 查询最后一个匹配的数据
     *
     * @param queryWrapper 查询条件，通常带排序
     * @return 结果
     */
    public E getFirstOne(LambdaQueryWrapper<E> queryWrapper) {

        queryWrapper.last(LAST_ONE_LIMIT);
        return getOne(queryWrapper);
    }

    /**
     * 查询最后一个匹配的数据
     *
     * @param queryWrapper 查询条件，通常带排序
     * @return 结果
     */
    public E getFirstOne(QueryWrapper<E> queryWrapper) {

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
    public List<E> listLimit(LambdaQueryWrapper<E> queryWrapper, long start, long length) {

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
    public List<E> listLimit(QueryWrapper<E> queryWrapper, long start, long length) {

        queryWrapper.last("limit " + start + "," + length);
        return list(queryWrapper);
    }
}
