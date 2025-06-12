package org.dromara.mpe.demo.autotable.mysql;

import com.baomidou.mybatisplus.annotation.IdType;
import org.dromara.autotable.annotation.ColumnComment;
import org.dromara.autotable.annotation.ColumnDefault;
import org.dromara.autotable.annotation.ColumnType;
import org.dromara.autotable.annotation.Index;
import org.dromara.autotable.annotation.IndexField;
import org.dromara.autotable.annotation.TableIndex;
import org.dromara.autotable.annotation.TableIndexes;
import org.dromara.autotable.annotation.enums.DefaultValueEnum;
import org.dromara.autotable.annotation.enums.IndexSortTypeEnum;
import org.dromara.autotable.annotation.enums.IndexTypeEnum;
import org.dromara.autotable.annotation.mysql.MysqlCharset;
import org.dromara.autotable.annotation.mysql.MysqlTypeConstant;
import lombok.Data;
import org.dromara.mpe.autofill.annotation.Exclude;
import org.dromara.mpe.autotable.annotation.Column;
import org.dromara.mpe.autotable.annotation.ColumnId;
import org.dromara.mpe.autotable.annotation.Table;
import org.dromara.mpe.autotable.annotation.UniqueIndex;
import org.dromara.mpe.processer.annotation.AutoMapper;

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
@MysqlCharset(charset = "utf8mb4", collate = "utf8mb4_0900_ai_ci")
@AutoMapper(withDSAnnotation = true)
@Table(comment = "测试表", dsName = "my-mysql")
public class MysqlTable {

    @ColumnId(mode = IdType.AUTO, comment = "ID", type = MysqlTypeConstant.BIGINT)
    private String id;

    @Index
    @ColumnDefault(type = DefaultValueEnum.EMPTY_STRING)
    @ColumnType(value = MysqlTypeConstant.VARCHAR, length = 100)
    @ColumnComment("用户名")
    private String username;

    @UniqueIndex
    @ColumnType(value = MysqlTypeConstant.VARCHAR, length = 20)
    @Column(comment = "电话", defaultValue = "+00 00000000", notNull = true)
    private String phone;

    @ColumnDefault("0")
    @ColumnComment("年龄")
    private Integer age;

    @Column(comment = "我的资产", length = 12, decimalLength = 6)
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

    @Exclude
    @Column(comment = "额外信息")
    private String extra;
}
