package org.dromara.mpe.demo.datasource.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.dromara.mpe.base.repository.BaseRepository;
import org.dromara.mpe.demo.datasource.TargetObject;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

/**
 * @author don
 */
@Component
public class TargetObjectRepository extends BaseRepository<TargetObjectMapper, TargetObject> {

    // @PostConstruct
    // public void init() {
    //     for (int i = 0; i < 5; i++) {
    //         TargetObject entity = new TargetObject();
    //         int sourceId = (i + 1);
    //         entity.setId(""+sourceId);
    //         entity.setName("target:" + sourceId);
    //         entity.setSourceId("" + sourceId);
    //         this.save(entity);
    //     }
    // }
}

@Mapper
interface TargetObjectMapper extends BaseMapper<TargetObject> {
}
