package org.dromara.mpe.demo.datasource;

import org.dromara.mpe.demo.datasource.repository.SourceObjectRepository;
import org.dromara.mpe.demo.datasource.repository.TargetObjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("datasource")
public class DataSourceController {

    @Autowired
    private TargetObjectRepository targetObjectRepository;
    @Autowired
    private SourceObjectRepository sourceObjectRepository;

    @GetMapping("init")
    public void init() {
        SourceObject sourceObject = new SourceObject();
        sourceObject.setName("source");
        sourceObjectRepository.save(sourceObject);

        TargetObject targetObject = new TargetObject();
        targetObject.setName("target");
        targetObject.setSourceId(sourceObject.getId());
        targetObjectRepository.save(targetObject);
    }

    @GetMapping("list")
    public List<TargetObject> list() {

        return targetObjectRepository.lambdaQueryPlus().bindList();
    }

    @GetMapping("edit")
    public void edit() {

        List<SourceObject> list = sourceObjectRepository.list();
        for (SourceObject sourceObject : list) {
            sourceObject.setName(sourceObject.getName() + ":update");
        }
        sourceObjectRepository.updateBatchById(list);
    }
}
