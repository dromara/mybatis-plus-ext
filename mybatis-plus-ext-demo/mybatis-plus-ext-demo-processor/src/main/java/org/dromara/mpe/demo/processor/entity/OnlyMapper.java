package org.dromara.mpe.demo.processor.entity;

import org.dromara.mpe.processer.annotation.AutoMapper;
import lombok.Data;

@AutoMapper
@Data
public class OnlyMapper {

    private String id;
    private String name;
    private int age;
}
