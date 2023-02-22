package com.tangzc.mpe.demo.autotable.mysql;

import com.tangzc.mpe.autotable.annotation.*;
import com.tangzc.mpe.autotable.annotation.enums.IndexSortTypeEnum;
import com.tangzc.mpe.autotable.annotation.enums.IndexTypeEnum;
import lombok.Data;

/**
 * @author don
 */
@Data
// 表头同样可以声明单个索引（此处只是举例，等价于username字段上的@Index）
@TableIndex(name = "username_index", fields = {"username"}, type = IndexTypeEnum.UNIQUE)
// 需要在表头声明多个索引的情况下，需要用@TableIndexes包裹起来
@TableIndexes({
        // 声明普通联合索引
        @TableIndex(name = "username_phone_index", fields = {"username", "phone"}),
        // 声明唯一联合索引，单独指定phone的索引排序方式，构建索引的时候indexFields中字段的顺序权重高于fields中的字段
        @TableIndex(name = "username_phone_uni_index", fields = {"username"}, indexFields = {@IndexField(field = "phone", sort = IndexSortTypeEnum.DESC)}, type = IndexTypeEnum.UNIQUE),
})
@Table
public class MysqlTable2 {

    // 唯一索引
    @Index(type = IndexTypeEnum.UNIQUE)
    private String username;

    // 唯一索引快捷方式
    @UniqueIndex
    private String phone;

    // 普通索引：指定索引名称、注释、索引方法
    @Index(name = "active_index", comment = "激活状态索引")
    private Boolean active;
}
