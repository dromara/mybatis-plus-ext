package org.dromara.mpe.demo.processor.entity;

import org.dromara.mpe.autotable.annotation.Table;
import org.dromara.mpe.processer.annotation.AutoRepository;
import lombok.Data;

@Table(dsName = "test")
@AutoRepository(withDSAnnotation = true)
@Data
public class OnlyRepositoryWithDs {

    private String id;
    private String name;
    private int age;
}
