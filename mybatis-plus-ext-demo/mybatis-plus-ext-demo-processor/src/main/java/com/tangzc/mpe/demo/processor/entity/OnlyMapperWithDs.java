package com.tangzc.mpe.demo.processor.entity;

import com.tangzc.mpe.autotable.annotation.Table;
import com.tangzc.mpe.processer.annotation.AutoMapper;
import lombok.Data;

@Table(dsName = "test")
@AutoMapper(withDSAnnotation = true)
@Data
public class OnlyMapperWithDs {

    private String id;
    private String name;
    private int age;
}
