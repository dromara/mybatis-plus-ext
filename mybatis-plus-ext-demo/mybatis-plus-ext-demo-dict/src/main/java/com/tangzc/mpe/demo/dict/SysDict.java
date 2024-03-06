package com.tangzc.mpe.demo.dict;

import com.tangzc.mpe.autotable.annotation.Table;
import com.tangzc.mpe.processer.annotation.AutoRepository;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@Table
@AutoRepository
public class SysDict {

    private String dictKey;
    private String dictVal;
}
