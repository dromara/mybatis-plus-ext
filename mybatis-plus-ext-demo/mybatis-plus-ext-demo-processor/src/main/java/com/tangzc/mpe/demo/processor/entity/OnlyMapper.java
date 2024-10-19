package com.tangzc.mpe.demo.processor.entity;

import com.tangzc.mpe.processer.annotation.AutoDefine;
import com.tangzc.mpe.processer.annotation.AutoMapper;
import lombok.Data;

@AutoMapper
@Data
public class OnlyMapper {

    private String id;
    private String name;
    private int age;
}
