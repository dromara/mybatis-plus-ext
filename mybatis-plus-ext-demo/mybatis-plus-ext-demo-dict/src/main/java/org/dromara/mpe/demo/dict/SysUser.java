package org.dromara.mpe.demo.dict;

import org.dromara.mpe.autotable.annotation.Table;
import org.dromara.mpe.bind.metadata.annotation.JoinCondition;
import org.dromara.mpe.processer.annotation.AutoRepository;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@Table
@AutoRepository
public class SysUser {

    private String id;
    private String name;
    private String sex;
    /**
     * 此种方式目前并不是很优雅，理想方式是@Dict("sex")，达到理想方式，需要修改框架层面的代码
     * selfField = "sex", joinField = "dictKey"
     * 表示本表的sex字段与字典表的dictKey字段一一对应
     */
    @Dict(@JoinCondition(selfField = "sex", joinField = "dictKey"))
    private String sexVal;
}
