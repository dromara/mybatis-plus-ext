package com.tangzc.mpe.core.service;

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
public class BaseService<M extends BaseMapper<E>, E> extends ServiceImpl<M, E> implements IBaseService<E> {

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
}
