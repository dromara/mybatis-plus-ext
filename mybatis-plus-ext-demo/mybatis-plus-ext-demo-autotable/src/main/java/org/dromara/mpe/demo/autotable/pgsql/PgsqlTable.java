package org.dromara.mpe.demo.autotable.pgsql;

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
import com.tangzc.autotable.annotation.pgsql.PgsqlTypeConstant;
import lombok.Data;
import org.dromara.mpe.annotation.Exclude;
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
