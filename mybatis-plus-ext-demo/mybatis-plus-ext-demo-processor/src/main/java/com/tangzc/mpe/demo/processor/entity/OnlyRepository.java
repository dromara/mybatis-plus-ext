package com.tangzc.mpe.demo.processor.entity;

import com.tangzc.mpe.processer.annotation.AutoRepository;
import lombok.Data;

@AutoRepository
@Data
public class OnlyRepository {

    private String id;
    private String name;
    private int age;
}
