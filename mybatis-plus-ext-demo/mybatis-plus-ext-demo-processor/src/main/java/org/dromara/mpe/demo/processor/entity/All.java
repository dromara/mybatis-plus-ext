package org.dromara.mpe.demo.processor.entity;

import org.dromara.mpe.processer.annotation.AutoDefine;
import org.dromara.mpe.processer.annotation.AutoMapper;
import org.dromara.mpe.processer.annotation.AutoRepository;
import lombok.Data;

@AutoDefine
@AutoMapper
@AutoRepository
@Data
public class All {

    private String id;
    private String name;
    private int age;
}
