package org.dromara.mpe.demo.bind.mid;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.dromara.mpe.base.repository.BaseRepository;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

@Component
public class RoleMenuRepository extends BaseRepository<RoleMenuMapper, RoleMenu> {

}

@Mapper
interface RoleMenuMapper extends BaseMapper<RoleMenu> {
}
