package org.dromara.mpe.demo.datasource.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.dromara.mpe.base.repository.BaseRepository;
import org.dromara.mpe.demo.datasource.SourceObject;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

/**
 * @author don
 */
@Component
public class SourceObjectRepository extends BaseRepository<SourceObjectMapper, SourceObject> {

    // @PostConstruct
    // public void init() {
    //     for (int i = 0; i < 5; i++) {
    //         SourceObject entity = new SourceObject();
    //         int sourceId = (i + 1);
    //         entity.setId(""+sourceId);
    //         entity.setName("source:" + sourceId);
    //         this.save(entity);
    //     }
    // }
}

@Mapper
interface SourceObjectMapper extends BaseMapper<SourceObject> {
}
