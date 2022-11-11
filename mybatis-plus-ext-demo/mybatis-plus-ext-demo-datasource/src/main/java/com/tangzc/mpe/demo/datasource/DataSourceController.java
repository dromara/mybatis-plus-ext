package com.tangzc.mpe.demo.datasource;

import com.tangzc.mpe.demo.datasource.repository.SourceObjectRepository;
import com.tangzc.mpe.demo.datasource.repository.TargetObjectRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("datasource")
public class DataSourceController {

    @Resource
    private TargetObjectRepository targetObjectRepository;
    @Resource
    private SourceObjectRepository sourceObjectRepository;

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
