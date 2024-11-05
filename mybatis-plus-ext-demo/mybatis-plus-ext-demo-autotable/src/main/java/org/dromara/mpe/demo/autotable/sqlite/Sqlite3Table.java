package org.dromara.mpe.demo.autotable.sqlite;

import com.baomidou.mybatisplus.annotation.IdType;
import com.tangzc.autotable.annotation.ColumnComment;
import com.tangzc.autotable.annotation.ColumnDefault;
import com.tangzc.autotable.annotation.ColumnType;
import com.tangzc.autotable.annotation.Index;
import com.tangzc.autotable.annotation.IndexField;
import com.tangzc.autotable.annotation.TableIndex;
import com.tangzc.autotable.annotation.TableIndexes;
import com.tangzc.autotable.annotation.enums.DefaultValueEnum;
import com.tangzc.autotable.annotation.enums.IndexSortTypeEnum;
import com.tangzc.autotable.annotation.enums.IndexTypeEnum;
import com.tangzc.autotable.annotation.mysql.MysqlTypeConstant;
import lombok.Data;
import org.dromara.mpe.annotation.Exclude;
import org.dromara.mpe.autotable.annotation.Column;
import org.dromara.mpe.autotable.annotation.ColumnId;
import org.dromara.mpe.autotable.annotation.Table;
import org.dromara.mpe.autotable.annotation.UniqueIndex;
import org.dromara.mpe.processer.annotation.AutoMapper;

import java.math.BigDecimal;

/**
 * @author don
 */
@Data
@TableIndex(name = "name_index", fields = {"username"}, type = IndexTypeEnum.NORMAL)
@TableIndexes({
        @TableIndex(name = "name_age_index", fields = {"age", "username"}),
        @TableIndex(name = "phone_index", fields = {}, indexFields = {@IndexField(field = "phone", sort = IndexSortTypeEnum.DESC)}, type = IndexTypeEnum.UNIQUE)
})
@AutoMapper(withDSAnnotation = true)
@Table(comment = "测试表", dsName = "my-sqlite")
public class Sqlite3Table {

    @ColumnId(mode = IdType.AUTO, comment = "ID", type = "bigint")
    private String id;

    @Index
    @ColumnDefault(type = DefaultValueEnum.EMPTY_STRING)
    @ColumnType(value = MysqlTypeConstant.VARCHAR, length = 120)
    @ColumnComment("用户名")
    private String username;

    @ColumnDefault("0")
    @ColumnComment("年龄")
    private Integer age;

    @UniqueIndex
    @ColumnType(value = MysqlTypeConstant.VARCHAR, length = 20)
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
    private String registerTime;

    @Exclude
    @Column(comment = "额外信息")
    private String extra;
}
