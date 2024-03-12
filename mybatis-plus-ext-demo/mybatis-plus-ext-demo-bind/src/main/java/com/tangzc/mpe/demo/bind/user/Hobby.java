package com.tangzc.mpe.demo.bind.user;

import com.tangzc.mpe.autotable.annotation.Table;
import com.tangzc.mpe.processer.annotation.AutoDefine;
import lombok.Data;

@Data
@Table
@AutoDefine
public class Hobby {

    private String id;
    private String name;

    private String userId;
}
