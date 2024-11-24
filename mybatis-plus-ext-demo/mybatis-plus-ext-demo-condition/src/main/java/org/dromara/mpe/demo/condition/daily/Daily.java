package org.dromara.mpe.demo.condition.daily;

import org.dromara.autotable.annotation.ColumnComment;
import lombok.Data;
import lombok.experimental.Accessors;
import org.dromara.mpe.annotation.InsertFillData;
import org.dromara.mpe.autotable.annotation.Table;
import org.dromara.mpe.condition.metadata.annotation.DynamicCondition;
import org.dromara.mpe.demo.condition.FilterByCurrentUser;
import org.dromara.mpe.demo.condition.UserIdAutoFillHandler;

@Data
@Accessors(chain = true)
@Table(comment = "日报")
public class Daily {

    @ColumnComment("主键")
    private String id;
    @ColumnComment("内容")
    private String content;
    @DynamicCondition(FilterByCurrentUser.class)
    @InsertFillData(UserIdAutoFillHandler.class)
    @ColumnComment("提交人")
    private Integer submitter;
}
