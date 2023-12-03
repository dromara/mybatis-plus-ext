package com.tangzc.mpe.demo.autotable.mysql;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.tangzc.mpe.autotable.annotation.Column;
import com.tangzc.mpe.autotable.annotation.ColumnComment;
import com.tangzc.mpe.autotable.annotation.ColumnId;
import com.tangzc.mpe.autotable.annotation.Ignore;
import com.tangzc.mpe.autotable.strategy.mysql.data.MysqlTypeConstant;
import lombok.Data;

/**
 * @author don
 */
@Data
// @Table
public class MysqlTable3 extends BaseTable {

    // 指定主键自增注释、类型（数据库数字类型可以跟java字符串类型相互转化）、长度
    // 注意字段名称id会被自动认定为主键不需要再额外指定
    @ColumnComment("id主键（因为我是独立注解，所以我是大哥，会覆盖下面的）")
    @ColumnId(value = "id", comment = "id主键", type = MysqlTypeConstant.BIGINT, length = 32)
    @TableId(type = IdType.AUTO)
    private String id;

    // 单独设置字段类型
    @ColumnComment("姓名")
    private String name;

    // 忽略该字段，不参与建表
    @Ignore
    @Column(comment = "额外信息")
    private String extra;
}
