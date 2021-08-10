package com.tangzc.mpe.core.wrapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.conditions.query.ChainQuery;
import com.tangzc.mpe.core.event.BindEvent;
import com.tangzc.mpe.core.event.BindIPageEvent;
import com.tangzc.mpe.core.event.BindListEvent;
import com.tangzc.mpe.core.util.SpringContextUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * @author don
 */
public interface BindChainQuery<T> extends ChainQuery<T> {

    /**
     * 获取集合
     *
     * @return 集合
     */
    default List<T> bindList() {
        List<T> list = getBaseMapper().selectList(getWrapper());
//        Binder.bind(list);
        SpringContextUtil.publishEvent(new BindListEvent<>(list));
        return list;
    }

    /**
     * 获取集合
     *
     * @param fields 绑定字段
     * @return 集合
     */
    default List<T> bindList(SFunction<T, ?> field, SFunction<T, ?>... fields) {
        List<T> list = getBaseMapper().selectList(getWrapper());
        List<SFunction<T, ?>> filedList = mergeFiledList(field, fields);
//        Binder.bindOn(list, filedList);
        SpringContextUtil.publishEvent(new BindListEvent<>(list, filedList));
        return list;
    }

    /**
     * 获取单个
     *
     * @return 单个
     */
    default T bindOne() {
        T one = getBaseMapper().selectOne(getWrapper());
//        Binder.bind(one);
        SpringContextUtil.publishEvent(new BindEvent<>(one));
        return one;
    }

    /**
     * 获取单个
     *
     * @param fields 绑定字段
     * @return 单个
     */
    default T bindOne(SFunction<T, ?> field, SFunction<T, ?>... fields) {
        T one = getBaseMapper().selectOne(getWrapper());
        List<SFunction<T, ?>> filedList = mergeFiledList(field, fields);
//        Binder.bindOn(one, filedList);
        SpringContextUtil.publishEvent(new BindEvent<>(one, filedList));
        return one;
    }

    /**
     * 获取单个
     *
     * @return 单个
     * @since 3.3.0
     */
    default Optional<T> bindOneOpt() {
        return Optional.ofNullable(bindOne());
    }


    /**
     * 获取单个
     *
     * @param fields 绑定字段
     * @return 单个
     * @since 3.3.0
     */
    default Optional<T> bindOneOpt(SFunction<T, ?> field, SFunction<T, ?>... fields) {
        return Optional.ofNullable(bindOne(field, fields));
    }

    /**
     * 获取分页数据
     *
     * @param page 分页条件
     * @return 分页数据
     */
    default <E extends IPage<T>> E bindPage(E page) {
        E pageRet = getBaseMapper().selectPage(page, getWrapper());
//        Binder.bind(pageRet);
        SpringContextUtil.publishEvent(new BindIPageEvent<>(pageRet));
        return pageRet;
    }

    /**
     * 获取分页数据
     *
     * @param page   分页条件
     * @param fields 绑定字段
     * @return 分页数据
     */
    default <E extends IPage<T>> E bindPage(E page, SFunction<T, ?> field, SFunction<T, ?>... fields) {
        E pageRet = getBaseMapper().selectPage(page, getWrapper());
        List<SFunction<T, ?>> filedList = mergeFiledList(field, fields);
//        Binder.bindOn(pageRet, filedList);
        SpringContextUtil.publishEvent(new BindIPageEvent<>(pageRet, filedList));
        return pageRet;
    }

    default List<SFunction<T, ?>> mergeFiledList(SFunction<T, ?> field, SFunction<T, ?>[] fields) {
        List<SFunction<T, ?>> filedList = new ArrayList<>();
        filedList.add(field);
        if (fields != null && fields.length > 0) {
            filedList.addAll(Arrays.asList(fields));
        }
        return filedList;
    }

}
