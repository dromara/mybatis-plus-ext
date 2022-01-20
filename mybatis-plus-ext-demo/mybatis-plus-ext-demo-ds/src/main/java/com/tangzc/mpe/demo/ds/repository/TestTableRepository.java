package com.tangzc.mpe.demo.ds.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tangzc.mpe.base.repository.BaseRepository;
import com.tangzc.mpe.demo.ds.entity.TestTable;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
public class TestTableRepository extends BaseRepository<TestTableMapper, TestTable> {
}

@Mapper
interface TestTableMapper extends BaseMapper<TestTable> {}
