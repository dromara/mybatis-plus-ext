package com.tangzc.mpe.demo.autotable.pgsql;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tangzc.mpe.base.repository.BaseRepository;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

/**
 * @author don
 */
@DS("my-pgsql")
@Component
public class PgsqlTableRepository extends BaseRepository<PgsqlTableMapper, PgsqlTable> {
}

@Mapper
interface PgsqlTableMapper extends BaseMapper<PgsqlTable> {

}
