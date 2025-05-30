package org.dromara.mpe.demo.processor.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public class CustomSuperRepository<M extends BaseMapper<E>, E> extends CrudRepository<M, E> {
}
