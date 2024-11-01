package org.dromara.mpe.demo.condition.daily;

import com.tangzc.autotable.annotation.ColumnComment;
import org.dromara.mpe.annotation.InsertFillData;
import org.dromara.mpe.autotable.annotation.Table;
import org.dromara.mpe.condition.metadata.annotation.DynamicCondition;
import org.dromara.mpe.demo.condition.FilterByCurrentUser;
import org.dromara.mpe.demo.condition.UserIdAutoFillHandler;
import org.dromara.mpe.processer.annotation.AutoRepository;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@AutoRepository
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
    private int submitter;
}
