package com.tangzc.mpe.demo.autotable.sqlite;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tangzc.mpe.base.repository.BaseRepository;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

/**
 * @author don
 */
@DS("my-sqlite")
@Component
public class SqliteTableRepository extends BaseRepository<SqliteTableMapper, SqliteTable> {
}

@Mapper
interface SqliteTableMapper extends BaseMapper<SqliteTable> {

}
