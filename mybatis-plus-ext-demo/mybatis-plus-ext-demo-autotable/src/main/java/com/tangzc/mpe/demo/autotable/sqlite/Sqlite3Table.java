package com.tangzc.mpe.demo.autotable.sqlite;

import com.tangzc.mpe.autotable.annotation.*;
import com.tangzc.mpe.autotable.annotation.enums.DefaultValueEnum;
import com.tangzc.mpe.autotable.annotation.enums.IndexSortTypeEnum;
import com.tangzc.mpe.autotable.annotation.enums.IndexTypeEnum;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author don
 */
@Data
@TableIndex(name = "name_index", fields = {"username"}, type = IndexTypeEnum.NORMAL)
@TableIndexes({
        @TableIndex(name = "name_age_index", fields = {"age", "username"}),
        @TableIndex(name = "phone_index", fields = {}, indexFields = {@IndexField(field = "phone", sort = IndexSortTypeEnum.DESC)}, type = IndexTypeEnum.UNIQUE)
})
@Table(comment = "测试表", dsName = "my-sqlite")
public class Sqlite3Table {

    @AutoIncrement
    @Column(comment = "ID", type = "bigint")
    private String id;

    @Index
    @NotNull
    @ColumnDefault(type = DefaultValueEnum.EMPTY_STRING)
    @ColumnType(length = 100)
    @ColumnComment("用户名")
    private String username;

    @ColumnDefault("0")
    @ColumnComment("年龄")
    private Integer age;

    @UniqueIndex
    @ColumnType(length = 20)
    @Column(comment = "电话", defaultValue = "+00 00000000", notNull = true)
    private String phone;

    @Column(comment = "资产", length = 12, decimalLength = 6)
    private BigDecimal money;

    @ColumnDefault("true")
    @Column(comment = "激活状态")
    private Boolean active;

    @ColumnType(value = "text")
    @ColumnComment("个人简介")
    private String description;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(comment = "注册时间")
    private LocalDateTime registerTime;

    @Ignore
    @Column(comment = "额外信息")
    private String extra;
}
