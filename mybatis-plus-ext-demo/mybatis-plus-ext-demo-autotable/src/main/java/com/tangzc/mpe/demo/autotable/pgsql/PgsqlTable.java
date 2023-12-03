package com.tangzc.mpe.demo.autotable.pgsql;

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
import com.tangzc.mpe.autotable.strategy.pgsql.data.PgsqlTypeConstant;
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
// @Table(comment = "测试表", dsName = "my-pgsql")
public class PgsqlTable {

    @ColumnId(mode = IdType.AUTO, comment = "ID", type = PgsqlTypeConstant.INT8)
    private String id;

    @Index
    @NotNull
    @ColumnDefault(type = DefaultValueEnum.EMPTY_STRING)
    @ColumnType(length = 100)
    @ColumnComment("用户名")
    private String username;

    @ColumnDefault("12")
    @ColumnComment("年龄")
    private Integer age;

    @UniqueIndex
    @ColumnType(length = 20)
    @Column(comment = "电话", notNull = true)
    private String phone;

    @Column(comment = "资产", length = 12, decimalLength = 4)
    private BigDecimal money;

    @ColumnDefault("true")
    @Column(comment = "激活状态")
    private Boolean active;

    @ColumnType(value = PgsqlTypeConstant.TEXT)
    @ColumnComment("个人简介")
    private String description;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(comment = "注册时间")
    private LocalDateTime registerTime;

    @Ignore
    @Column(comment = "额外信息")
    private String extra;
}
