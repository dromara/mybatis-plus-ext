package com.tangzc.mpe.demo.bind.mid;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tangzc.mpe.base.repository.BaseRepository;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

@Component
public class RoleRepository extends BaseRepository<RoleMapper, Role> {

}

@Mapper
interface RoleMapper extends BaseMapper<Role> {
}