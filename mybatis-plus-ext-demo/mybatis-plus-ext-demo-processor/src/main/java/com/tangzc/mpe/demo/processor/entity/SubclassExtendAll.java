package com.tangzc.mpe.demo.processor.entity;

import com.tangzc.mpe.processer.annotation.AutoDefine;
import com.tangzc.mpe.processer.annotation.AutoMapper;
import com.tangzc.mpe.processer.annotation.AutoRepository;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@AutoDefine
@AutoMapper
@AutoRepository
public class SubclassExtendAll extends All {

    private String mark;
}
