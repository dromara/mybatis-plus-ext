package com.tangzc.mpe.demo.processor.entity;

import com.tangzc.mpe.processer.annotation.AutoDefine;
import com.tangzc.mpe.processer.annotation.AutoRepository;
import lombok.Data;

@AutoDefine
@AutoRepository
@Data
public class TestTable {

    private String id;
    private String name;
    private int age;
}
