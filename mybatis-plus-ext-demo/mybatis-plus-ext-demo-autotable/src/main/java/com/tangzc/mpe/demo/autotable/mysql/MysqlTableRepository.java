package com.tangzc.mpe.demo.autotable.mysql;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tangzc.mpe.base.repository.BaseRepository;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

/**
 * @author don
 */
@DS("my-mysql")
@Component
public class MysqlTableRepository extends BaseRepository<MysqlTableMapper, MysqlTable> {
}

@Mapper
interface MysqlTableMapper extends BaseMapper<MysqlTable> {

}
