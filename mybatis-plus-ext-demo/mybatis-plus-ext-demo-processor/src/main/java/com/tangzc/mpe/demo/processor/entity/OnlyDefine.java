package com.tangzc.mpe.demo.processor.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.tangzc.mpe.autotable.annotation.Table;
import com.tangzc.mpe.processer.annotation.AutoDefine;
import com.tangzc.mpe.processer.annotation.AutoRepository;
import lombok.Data;

@Data
@AutoDefine
@TableName("only_define")
public class OnlyDefine {

    private String id;
    private String name;
    private int age;
}
