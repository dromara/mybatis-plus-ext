package com.tangzc.mpe.demo.autotable.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tangzc.mpe.base.repository.BaseRepository;
import com.tangzc.mpe.demo.autotable.entity.TestTable;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

/**
 * @author don
 */
@Component
public class TestTableRepository extends BaseRepository<TestTableMapper, TestTable> {
}

@Mapper
interface TestTableMapper extends BaseMapper<TestTable> {

}
