package org.dromara.mpe.demo.datasource;

import org.dromara.autotable.springboot.EnableAutoTableTest;
import org.dromara.mpe.datasource.DataSourceManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@EnableAutoTableTest
@SpringBootTest
class DemoDatasourceApplicationTests {


    @Autowired
    private TargetObjectRepository targetObjectRepository;
    @Autowired
    private SourceObjectRepository sourceObjectRepository;

    @BeforeEach
    public void init() {
        SourceObject sourceObject = new SourceObject();
        sourceObject.setId("1");
        sourceObject.setName("source");
        sourceObjectRepository.save(sourceObject);

        TargetObject targetObject = new TargetObject();
        targetObject.setId("1");
        targetObject.setName("target");
        targetObject.setSourceId(sourceObject.getId());
        targetObjectRepository.save(targetObject);
    }

    @Test
    public void test() {

        SourceObject sourceObject = sourceObjectRepository.getById("1");
        String newName = sourceObject.getName() + ":update";
        sourceObject.setName(newName);
        sourceObjectRepository.updateById(sourceObject);
        DataSourceManager.triggerUpdate(sourceObject, SourceObject::getName);

        TargetObject targetObject = targetObjectRepository.lambdaQueryPlus()
                .eq(TargetObject::getSourceId, sourceObject.getId())
                .one();

        assert targetObject.getExtra().equals(newName);
    }
}
