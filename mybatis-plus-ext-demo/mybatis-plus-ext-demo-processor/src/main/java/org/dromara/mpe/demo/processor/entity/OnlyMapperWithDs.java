package org.dromara.mpe.demo.processor.entity;

import org.dromara.mpe.autotable.annotation.Table;
import org.dromara.mpe.processer.annotation.AutoMapper;
import lombok.Data;

@Table(dsName = "test")
@AutoMapper(withDSAnnotation = true)
@Data
public class OnlyMapperWithDs {

    private String id;
    private String name;
    private int age;
}
