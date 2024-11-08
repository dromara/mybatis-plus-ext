package com.tangzc.mpe.demo.dict;

import lombok.Data;
import lombok.experimental.Accessors;
import com.tangzc.mpe.autotable.annotation.Table;

@Data
@Accessors(chain = true)
@Table
public class SysUser {

    private String id;
    private String name;
    private String sex;
    // 字典值，基于自身对象属性sex去加载
    @Dict(SysUserDefine.sex)
    private String sexVal;
}
