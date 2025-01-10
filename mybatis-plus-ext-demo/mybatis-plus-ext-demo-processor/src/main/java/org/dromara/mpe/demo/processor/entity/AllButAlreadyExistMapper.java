package org.dromara.mpe.demo.processor.entity;

import lombok.Data;
import org.dromara.mpe.processer.annotation.AutoDefine;
import org.dromara.mpe.processer.annotation.AutoMapper;
import org.dromara.mpe.processer.annotation.AutoRepository;

@AutoDefine
@AutoMapper
@AutoRepository
@Data
public class AllButAlreadyExistMapper {

    private String id;
    private String name;
    private int age;
}
