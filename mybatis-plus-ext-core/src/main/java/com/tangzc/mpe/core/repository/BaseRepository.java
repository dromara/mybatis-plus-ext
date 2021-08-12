package com.tangzc.mpe.core.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tangzc.mpe.core.event.EntityUpdateEvent;
import com.tangzc.mpe.core.util.SpringContextUtil;

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

    // MP的bug，待修复
    @Override
    protected Class<E> currentMapperClass() {
        return (Class<E>) this.getResolvableType().as(BaseRepository.class).getGeneric(0).getType();
    }

    @Override
    protected Class<E> currentModelClass() {
        return (Class<E>) this.getResolvableType().as(BaseRepository.class).getGeneric(1).getType();
    }
}
