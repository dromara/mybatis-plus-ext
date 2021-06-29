package com.tangzc.mpe.wrapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.conditions.query.ChainQuery;
import com.tangzc.mpe.relevance.Binder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author don
 */
public interface BindChainQuery<T> extends ChainQuery<T> {

    /**
     * 获取集合
     *
     * @param fields 绑定字段
     * @return 集合
     */
    default List<T> bindList(SFunction<T, ?>... fields) {
        List<T> list = getBaseMapper().selectList(getWrapper());
        if (fields == null || fields.length == 0) {
            Binder.bind(list);
        } else {
            Binder.bindOn(list, Arrays.stream(fields).collect(Collectors.toList()));
        }
        return list;
    }

    /**
     * 获取单个
     *
     * @param fields 绑定字段
     * @return 单个
     */
    default T bindOne(SFunction<T, ?>... fields) {
        T one = getBaseMapper().selectOne(getWrapper());
        if (fields == null || fields.length == 0) {
            Binder.bind(one);
        } else {
            Binder.bindOn(one, Arrays.stream(fields).collect(Collectors.toList()));
        }
        return one;
    }

    /**
     * 获取单个
     *
     * @param fields 绑定字段
     * @return 单个
     * @since 3.3.0
     */
    default Optional<T> bindOneOpt(SFunction<T, ?>... fields) {
        return Optional.ofNullable(bindOne(fields));
    }

    /**
     * 获取分页数据
     *
     * @param page 分页条件
     * @param fields 绑定字段
     * @return 分页数据
     */
    default <E extends IPage<T>> E bindPage(E page, SFunction<T, ?>... fields) {
        E pageRet = getBaseMapper().selectPage(page, getWrapper());
        if (fields == null || fields.length == 0) {
            Binder.bind(pageRet);
        } else {
            Binder.bindOn(pageRet, Arrays.stream(fields).collect(Collectors.toList()));
        }
        return pageRet;
    }

}
