package com.tangzc.mpe.base.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.repository.CrudRepository;
import com.tangzc.mpe.base.event.EntityUpdateEvent;
import com.tangzc.mpe.magic.util.SpringContextUtil;

import java.util.Collection;

/**
 * 扩展了MP的ServiceImpl的功能，拦截了通过id更新数据的方法，对外发送通知
 *
 * @param <M> mapper
 * @param <E> Entity
 * @author don
 */
public abstract class BaseRepository<M extends BaseMapper<E>, E> extends CrudRepository<M, E> implements IBaseRepository<E> {

    @Override
    public boolean updateById(E entity) {
        boolean result = super.updateById(entity);
        if (result) {
            SpringContextUtil.publishEvent(EntityUpdateEvent.create(entity));
        }
        return result;
    }

    @Override
    public boolean updateBatchById(Collection<E> entityList, int batchSize) {
        boolean result = super.updateBatchById(entityList, batchSize);
        if (result) {
            for (E entity : entityList) {
                SpringContextUtil.publishEvent(EntityUpdateEvent.create(entity));
            }
        }
        return result;
    }
}
