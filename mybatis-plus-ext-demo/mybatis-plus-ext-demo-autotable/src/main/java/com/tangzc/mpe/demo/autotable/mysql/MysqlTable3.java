package com.tangzc.mpe.demo.autotable.mysql;

import com.tangzc.mpe.autotable.annotation.*;
import com.tangzc.mpe.autotable.annotation.enums.DefaultValueEnum;
import com.tangzc.mpe.autotable.strategy.mysql.data.MysqlTypeConstant;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author don
 */
@Data
// @Table
public class MysqlTable3 {

    // 主键自增
    @AutoIncrement
    // 指定字段注释、类型（数据库数字类型可以跟java字符串类型相互转化）、长度
    // 注意字段名称id会被自动认定为主键不需要再额外指定
    @Column(comment = "id主键", type = MysqlTypeConstant.BIGINT, length = 32)
    private String id;

    // 通过isPrimary指定 是否主键。id2与id形成联合主键。
    @Column(comment = "id主键2", type = MysqlTypeConstant.BIGINT, length = 32, isPrimary = true)
    private String id2;

    // 字段非NULL
    @NotNull
    // 字段默认值是空字符串
    @ColumnDefault(type = DefaultValueEnum.EMPTY_STRING)
    // 指定字段长度
    @ColumnType(length = 100)
    // 指定字段注释
    @ColumnComment("用户名")
    private String username;

    // 设置默认值为0
    @ColumnDefault("0")
    @ColumnComment("年龄")
    private Integer age;

    @ColumnType(length = 20)
    // 设置注释、默认值、不为空
    @Column(comment = "电话", defaultValue = "+00 00000000", notNull = true)
    private String phone;

    // 设置注释、小数（等同于@ColumnType(length = 12, decimalLength = 6)）
    @Column(comment = "资产", length = 12, decimalLength = 6)
    private BigDecimal money;

    // boolean值设置默认值
    @ColumnDefault("true")
    @Column(comment = "激活状态")
    private Boolean active;

    // 单独设置字段类型
    @ColumnType(MysqlTypeConstant.TEXT)
    @ColumnComment("个人简介")
    private String description;

    // 设置默认值为当前时间
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(comment = "注册时间")
    private LocalDateTime registerTime;

    // 忽略该字段，不参与建表
    @Ignore
    @Column(comment = "额外信息")
    private String extra;
}
