package org.dromara.mpe.demo.autotable.pgsql;

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
import org.dromara.autotable.annotation.mysql.MysqlTypeConstant;
import org.dromara.autotable.annotation.pgsql.PgsqlTypeConstant;
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
@TableIndex(name = "name_index", fields = {PgsqlTableDefine.username}, type = IndexTypeEnum.NORMAL)
@TableIndexes({
        @TableIndex(name = "name_age_index", fields = {PgsqlTableDefine.age, PgsqlTableDefine.username}),
        @TableIndex(name = "phone_index", fields = {}, indexFields = {@IndexField(field = PgsqlTableDefine.phone, sort = IndexSortTypeEnum.DESC)}, type = IndexTypeEnum.UNIQUE)
})
@AutoMapper(withDSAnnotation = true)
@Table(comment = "测试表", dsName = "my-pgsql")
public class PgsqlTable {

    @ColumnId(mode = IdType.AUTO, comment = "ID", type = PgsqlTypeConstant.INT8)
    private String id;

    @Index
    @ColumnDefault(type = DefaultValueEnum.EMPTY_STRING)
    @ColumnType(value = MysqlTypeConstant.VARCHAR, length = 100)
    @ColumnComment("用户名")
    private String username;

    @ColumnDefault("12")
    @ColumnComment("年龄")
    private Integer age;

    @UniqueIndex
    @ColumnType(value = MysqlTypeConstant.VARCHAR, length = 20)
    @Column(comment = "电话", notNull = true, defaultValueType = DefaultValueEnum.EMPTY_STRING)
    private String phone;

    @Column(comment = "资产", length = 12, decimalLength = 4, notNull = true)
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

    @Exclude
    @Column(comment = "额外信息")
    private String extra;
}
