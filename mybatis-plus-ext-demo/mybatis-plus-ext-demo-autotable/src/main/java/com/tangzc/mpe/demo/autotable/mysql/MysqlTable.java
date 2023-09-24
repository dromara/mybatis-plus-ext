package com.tangzc.mpe.demo.autotable.mysql;

import com.baomidou.mybatisplus.annotation.IdType;
import com.tangzc.mpe.autotable.annotation.Column;
import com.tangzc.mpe.autotable.annotation.ColumnComment;
import com.tangzc.mpe.autotable.annotation.ColumnDefault;
import com.tangzc.mpe.autotable.annotation.ColumnId;
import com.tangzc.mpe.autotable.annotation.ColumnType;
import com.tangzc.mpe.autotable.annotation.Ignore;
import com.tangzc.mpe.autotable.annotation.Index;
import com.tangzc.mpe.autotable.annotation.IndexField;
import com.tangzc.mpe.autotable.annotation.NotNull;
import com.tangzc.mpe.autotable.annotation.TableIndex;
import com.tangzc.mpe.autotable.annotation.TableIndexes;
import com.tangzc.mpe.autotable.annotation.UniqueIndex;
import com.tangzc.mpe.autotable.annotation.enums.DefaultValueEnum;
import com.tangzc.mpe.autotable.annotation.enums.IndexSortTypeEnum;
import com.tangzc.mpe.autotable.annotation.enums.IndexTypeEnum;
import com.tangzc.mpe.autotable.annotation.mysql.MysqlCharset;
import com.tangzc.mpe.autotable.strategy.mysql.data.MysqlTypeConstant;
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
@MysqlCharset(value = "utf8mb4")
// @Table(comment = "测试表", dsName = "my-mysql")
public class MysqlTable {

    @ColumnId(mode = IdType.AUTO, comment = "ID", type = MysqlTypeConstant.BIGINT)
    private String id;

    @Index
    @NotNull
    @ColumnDefault(type = DefaultValueEnum.EMPTY_STRING)
    @ColumnType(length = 100)
    @ColumnComment("用户名")
    private String username;

    @UniqueIndex
    @ColumnType(length = 20)
    @Column(comment = "电话", defaultValue = "+00 00000000", notNull = true)
    private String phone;

    @ColumnDefault("0")
    @ColumnComment("年龄")
    private Integer age;

    @Column(comment = "资产", length = 12, decimalLength = 6)
    private BigDecimal money;

    @ColumnType(MysqlTypeConstant.TEXT)
    @ColumnComment("个人简介")
    private String description;

    @ColumnDefault("true")
    @Column(comment = "激活状态")
    private Boolean active;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(comment = "注册时间")
    private LocalDateTime registerTime;

    @Ignore
    @Column(comment = "额外信息")
    private String extra;
}
