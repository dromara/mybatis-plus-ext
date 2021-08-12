package com.tangzc.mpe.base.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.ReflectionKit;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tangzc.mpe.base.event.EntityUpdateEvent;
import com.tangzc.mpe.base.util.SpringContextUtil;

import java.util.Collection;

/**
 * @param <M> mapper
 * @param <E> Entity
 * @author don
 */
public abstract class BaseRepository<M extends BaseMapper<E>, E> extends ServiceImpl<M, E> implements IBaseRepository<E> {

    @Override
    public boolean updateById(E entity) {
        boolean result = super.updateById(entity);
        if(result) {
            SpringContextUtil.getApplicationContext().publishEvent(EntityUpdateEvent.create(entity));
        }
        return result;
    }

    @Override
    public boolean updateBatchById(Collection<E> entityList, int batchSize) {
        boolean result = super.updateBatchById(entityList, batchSize);
        if(result) {
            for (E entity : entityList) {
                SpringContextUtil.getApplicationContext().publishEvent(EntityUpdateEvent.create(entity));
            }
        }
        return result;
    }

    @Override
    protected Class<M> currentMapperClass() {
        return (Class<M>) ReflectionKit.getSuperClassGenericType(this.getClass(), BaseRepository.class, 0);
    }

    @Override
    protected Class<E> currentModelClass() {
        return (Class<E>) ReflectionKit.getSuperClassGenericType(this.getClass(), BaseRepository.class, 1);
    }
}
