package org.dromara.mpe.demo.processor.entity;

import lombok.Data;
import org.dromara.mpe.processer.annotation.AutoMapper;

@AutoMapper(superclassName = "org.dromara.mpe.demo.processor.mapper.CustomSuperMapper")
@Data
public class OnlyMapperCustomSupper {

    private String id;
    private String name;
    private int age;
}
