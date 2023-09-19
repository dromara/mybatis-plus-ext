package com.tangzc.mpe.base.wrapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.conditions.query.ChainQuery;
import com.tangzc.mpe.base.ext.BindHandler;
import com.tangzc.mpe.magic.util.SpringContextUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;


/**
 * 自定义ChainQuery，拓展了关于Bind的相关方法
 *
 * @author don
 */
public interface BindChainQuery<T> extends ChainQuery<T> {

    /**
     * 获取集合
     *
     * @return 集合
     */
    default List<T> bindList() {
        return bindList(Collections.emptyList());
    }

    /**
     * 获取集合
     *
     * @param field 绑定字段
     * @return 集合
     */
    default List<T> bindList(SFunction<T, ?> field) {
        List<SFunction<T, ?>> filedList = Collections.singletonList(field);
        return bindList(filedList);
    }

    /**
     * 获取集合
     *
     * @param field  绑定字段
     * @param field2 绑定字段
     * @return 集合
     */
    default List<T> bindList(SFunction<T, ?> field, SFunction<T, ?> field2) {
        List<SFunction<T, ?>> filedList = Arrays.asList(field, field2);
        return bindList(filedList);
    }

    /**
     * 获取集合
     *
     * @param field  绑定字段
     * @param field2 绑定字段
     * @param field3 绑定字段
     * @return 集合
     */
    default List<T> bindList(SFunction<T, ?> field, SFunction<T, ?> field2, SFunction<T, ?> field3) {
        List<SFunction<T, ?>> filedList = Arrays.asList(field, field2, field3);
        return bindList(filedList);
    }

    /**
     * 获取集合
     *
     * @param fields 绑定字段
     * @return 集合
     */
    @Deprecated
    default List<T> bindList(SFunction<T, ?> field, SFunction<T, ?> field2, SFunction<T, ?> field3, SFunction<T, ?>... fields) {
        List<SFunction<T, ?>> filedList = mergeFiledList(field, field2, field3, fields);
        return bindList(filedList);
    }

    default List<T> bindList(List<SFunction<T, ?>> filedList) {
        List<T> list = getBaseMapper().selectList(getWrapper());
        SpringContextUtil.getBeanOfType(BindHandler.class).bindOn(list, filedList);
        return list;
    }

    /* ---------------------------------------------------------------------------------------------------------------------------------- */

    /**
     * 获取单个
     *
     * @return 单个
     */
    default T bindOne() {
        return bindOne(Collections.emptyList());
    }

    /**
     * 获取单个
     *
     * @param field 绑定字段
     * @return 单个
     */
    default T bindOne(SFunction<T, ?> field) {
        List<SFunction<T, ?>> filedList = Collections.singletonList(field);
        return bindOne(filedList);
    }

    /**
     * 获取单个
     *
     * @param field  绑定字段
     * @param field2 绑定字段
     * @return 单个
     */
    default T bindOne(SFunction<T, ?> field, SFunction<T, ?> field2) {
        List<SFunction<T, ?>> filedList = Arrays.asList(field, field2);
        return bindOne(filedList);
    }

    /**
     * 获取单个
     *
     * @param field  绑定字段
     * @param field2 绑定字段
     * @param field3 绑定字段
     * @return 单个
     */
    default T bindOne(SFunction<T, ?> field, SFunction<T, ?> field2, SFunction<T, ?> field3) {
        List<SFunction<T, ?>> filedList = Arrays.asList(field, field2, field3);
        return bindOne(filedList);
    }

    /**
     * 获取单个
     *
     * @param fields 绑定字段
     * @return 单个
     */
    @Deprecated
    default T bindOne(SFunction<T, ?> field, SFunction<T, ?> field2, SFunction<T, ?> field3, SFunction<T, ?>... fields) {
        List<SFunction<T, ?>> filedList = mergeFiledList(field, field2, field3, fields);
        return bindOne(filedList);
    }

    default T bindOne(List<SFunction<T, ?>> filedList) {
        T one = getBaseMapper().selectOne(getWrapper());
        SpringContextUtil.getBeanOfType(BindHandler.class).bindOn(one, filedList);
        return one;
    }

    /* ---------------------------------------------------------------------------------------------------------------------------------- */

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
     * @return 单个
     * @since 3.3.0
     */
    default Optional<T> bindOneOpt(SFunction<T, ?> field) {
        return Optional.ofNullable(bindOne(field));
    }

    /**
     * 获取单个
     *
     * @return 单个
     * @since 3.3.0
     */
    default Optional<T> bindOneOpt(SFunction<T, ?> field, SFunction<T, ?> field2) {
        return Optional.ofNullable(bindOne(field, field2));
    }

    /**
     * 获取单个
     *
     * @return 单个
     * @since 3.3.0
     */
    default Optional<T> bindOneOpt(SFunction<T, ?> field, SFunction<T, ?> field2, SFunction<T, ?> field3) {
        return Optional.ofNullable(bindOne(field, field2, field3));
    }


    /**
     * 获取单个
     *
     * @param fields 绑定字段
     * @return 单个
     * @since 3.3.0
     */
    @Deprecated
    default Optional<T> bindOneOpt(SFunction<T, ?> field, SFunction<T, ?> field2, SFunction<T, ?> field3, SFunction<T, ?>... fields) {
        return Optional.ofNullable(bindOne(field, field2, field3, fields));
    }

    /* ---------------------------------------------------------------------------------------------------------------------------------- */

    /**
     * 获取分页数据
     *
     * @param page 分页条件
     * @return 分页数据
     */
    default <E extends IPage<T>> E bindPage(E page) {
        return bindPage(page, Collections.emptyList());
    }

    /**
     * 获取分页数据
     *
     * @param page  分页条件
     * @param field 绑定字段
     * @return 分页数据
     */
    default <E extends IPage<T>> E bindPage(E page, SFunction<T, ?> field) {
        List<SFunction<T, ?>> filedList = Collections.singletonList(field);
        return bindPage(page, filedList);
    }

    /**
     * 获取分页数据
     *
     * @param page  分页条件
     * @param field 绑定字段
     * @return 分页数据
     */
    default <E extends IPage<T>> E bindPage(E page, SFunction<T, ?> field, SFunction<T, ?> field2) {
        List<SFunction<T, ?>> filedList = Arrays.asList(field, field2);
        return bindPage(page, filedList);
    }

    /**
     * 获取分页数据
     *
     * @param page  分页条件
     * @param field 绑定字段
     * @return 分页数据
     */
    default <E extends IPage<T>> E bindPage(E page, SFunction<T, ?> field, SFunction<T, ?> field2, SFunction<T, ?> field3) {
        List<SFunction<T, ?>> filedList = Arrays.asList(field, field2, field3);
        return bindPage(page, filedList);
    }

    /**
     * 获取分页数据
     *
     * @param page   分页条件
     * @param fields 绑定字段
     * @return 分页数据
     */
    default <E extends IPage<T>> E bindPage(E page, SFunction<T, ?> field, SFunction<T, ?> field2, SFunction<T, ?> field3, SFunction<T, ?>... fields) {
        List<SFunction<T, ?>> filedList = mergeFiledList(field, field2, field3, fields);
        return bindPage(page, filedList);
    }

    default <E extends IPage<T>> E bindPage(E page, List<SFunction<T, ?>> filedList) {
        E pageRet = getBaseMapper().selectPage(page, getWrapper());
        SpringContextUtil.getBeanOfType(BindHandler.class).bindOn(pageRet, filedList);
        return pageRet;
    }

    default List<SFunction<T, ?>> mergeFiledList(SFunction<T, ?> field, SFunction<T, ?> field2, SFunction<T, ?> field3, SFunction<T, ?>[] fields) {
        List<SFunction<T, ?>> filedList = new ArrayList<>();
        filedList.add(field);
        filedList.add(field2);
        filedList.add(field3);
        if (fields != null && fields.length > 0) {
            filedList.addAll(Arrays.asList(fields));
        }
        return filedList;
    }
}
