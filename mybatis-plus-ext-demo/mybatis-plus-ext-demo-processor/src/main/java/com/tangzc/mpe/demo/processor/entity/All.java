package com.tangzc.mpe.demo.processor.entity;

import com.tangzc.mpe.processer.annotation.AutoDefine;
import com.tangzc.mpe.processer.annotation.AutoMapper;
import com.tangzc.mpe.processer.annotation.AutoRepository;
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
