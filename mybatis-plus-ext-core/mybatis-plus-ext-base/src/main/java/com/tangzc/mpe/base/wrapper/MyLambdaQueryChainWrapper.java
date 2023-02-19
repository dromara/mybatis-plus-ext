package com.tangzc.mpe.base.wrapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.Query;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.toolkit.ExceptionUtils;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.conditions.AbstractChainWrapper;

import java.util.function.Predicate;

/**
 * 模仿{@link com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper}的实现逻辑，同时把{@link com.baomidou.mybatisplus.extension.conditions.query.ChainQuery}改为自定义的包装接口{@link BindChainQuery}
 * @author don
 */
public class MyLambdaQueryChainWrapper<T> extends AbstractChainWrapper<T, SFunction<T, ?>, MyLambdaQueryChainWrapper<T>, LambdaQueryWrapper<T>>
        implements BindChainQuery<T>, Query<MyLambdaQueryChainWrapper<T>, T, SFunction<T, ?>> {

    private final BaseMapper<T> baseMapper;

    public MyLambdaQueryChainWrapper(BaseMapper<T> baseMapper) {
        super();
        this.baseMapper = baseMapper;
        super.wrapperChildren = new LambdaQueryWrapper<>();
    }

    public MyLambdaQueryChainWrapper(Class<T> entityClass) {
        super();
        this.baseMapper = null;
        super.wrapperChildren = new LambdaQueryWrapper<>(entityClass);
    }

    public MyLambdaQueryChainWrapper(BaseMapper<T> baseMapper, T entity) {
        super();
        this.baseMapper = baseMapper;
        super.wrapperChildren = new LambdaQueryWrapper<>(entity);
    }

    public MyLambdaQueryChainWrapper(BaseMapper<T> baseMapper, Class<T> entityClass) {
        super();
        this.baseMapper = baseMapper;
        super.wrapperChildren = new LambdaQueryWrapper<>(entityClass);
    }

    @SafeVarargs
    @Override
    public final MyLambdaQueryChainWrapper<T> select(SFunction<T, ?>... columns) {
        wrapperChildren.select(columns);
        return typedThis;
    }

    @Override
    public MyLambdaQueryChainWrapper<T> select(Class<T> entityClass, Predicate<TableFieldInfo> predicate) {
        wrapperChildren.select(entityClass, predicate);
        return typedThis;
    }

    @Override
    public String getSqlSelect() {
        throw ExceptionUtils.mpe("can not use this method for \"%s\"", "getSqlSelect");
    }

    @Override
    public BaseMapper<T> getBaseMapper() {
        return baseMapper;
    }

    @Override
    public Class<T> getEntityClass() {
        return super.wrapperChildren.getEntityClass();
    }
}
