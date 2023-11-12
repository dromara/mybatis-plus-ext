package com.tangzc.mpe.demo.dict;

import com.tangzc.mpe.automapper.AutoRepository;
import com.tangzc.mpe.autotable.annotation.Table;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 简单设计，仅用于演示
 */
@Data
@Accessors(chain = true)
@Table
@AutoRepository
public class SysDict {

    private String dictKey;
    private String dictVal;
}
