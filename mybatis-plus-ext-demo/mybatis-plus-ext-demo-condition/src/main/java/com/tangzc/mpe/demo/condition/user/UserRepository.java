package com.tangzc.mpe.demo.condition.user;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tangzc.mpe.base.repository.BaseRepository;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

@Component
public class UserRepository extends BaseRepository<UserMapper, User> {

}
@Mapper
interface UserMapper extends BaseMapper<User> {
}