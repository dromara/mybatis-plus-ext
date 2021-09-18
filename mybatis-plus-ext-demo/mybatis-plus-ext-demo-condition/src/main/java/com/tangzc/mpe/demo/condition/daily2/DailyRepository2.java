package com.tangzc.mpe.demo.condition.daily2;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tangzc.mpe.base.repository.BaseRepository;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

@Component
public class DailyRepository2 extends BaseRepository<DailyMapper2, Daily2> {

}

@Mapper
interface DailyMapper2 extends BaseMapper<Daily2> {
}