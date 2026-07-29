package org.dromara.mpe.test.dict;

import lombok.Data;
import lombok.experimental.Accessors;
import org.dromara.mpe.autotable.annotation.Table;

@Data
@Accessors(chain = true)
@Table
public class SysUser {

    private String id;
    private String name;
    private String sex;

    @Dict(SysUserDefine.sex)
    private String sexVal;
}
