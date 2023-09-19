package com.tangzc.mpe.demo.bind.mid;

import com.tangzc.mpe.base.repository.BaseRepository;
import com.tangzc.mpe.demo.bind.CustomBaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

@Component
public class MenuRepository extends BaseRepository<MenuMapper, Menu> {

}

/**
 * 自定义CustomBaseMapper，同样可以拿到entity和BaseMapper的映射关系
 */
@Mapper
interface MenuMapper extends CustomBaseMapper<MenuMapper, Menu> {
}