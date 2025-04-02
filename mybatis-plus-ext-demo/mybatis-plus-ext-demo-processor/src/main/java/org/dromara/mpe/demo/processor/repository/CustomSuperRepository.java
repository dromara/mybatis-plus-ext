package org.dromara.mpe.demo.processor.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.dromara.mpe.base.repository.BaseRepository;
import org.springframework.stereotype.Repository;

@Repository
public class CustomSuperRepository<M extends BaseMapper<E>, E> extends BaseRepository<M, E> {
}
