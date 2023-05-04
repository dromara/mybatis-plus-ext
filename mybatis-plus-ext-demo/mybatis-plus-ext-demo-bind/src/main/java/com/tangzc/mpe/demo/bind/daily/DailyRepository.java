package com.tangzc.mpe.demo.bind.daily;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tangzc.mpe.base.repository.BaseRepository;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

@Component
public class DailyRepository extends BaseRepository<DailyMapper, Daily> {

}
