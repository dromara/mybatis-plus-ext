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
public class Sqlite3TableRepository extends BaseRepository<Sqlite3TableMapper, Sqlite3Table> {
}

@Mapper
interface Sqlite3TableMapper extends BaseMapper<Sqlite3Table> {

}
