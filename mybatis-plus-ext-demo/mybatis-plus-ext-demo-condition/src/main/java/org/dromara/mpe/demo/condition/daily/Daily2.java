package org.dromara.mpe.demo.condition.daily;

import com.tangzc.autotable.annotation.ColumnComment;
import org.dromara.mpe.annotation.InsertFillData;
import org.dromara.mpe.autotable.annotation.Table;
import org.dromara.mpe.condition.metadata.annotation.DynamicCondition;
import org.dromara.mpe.demo.condition.FilterByFixedUser;
import org.dromara.mpe.demo.condition.UserIdAutoFillHandler;
import org.dromara.mpe.processer.annotation.AutoRepository;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@AutoRepository
@Table(value = "daily", comment = "日报")
public class Daily2 {

    @ColumnComment("主键")
    private String id;
    @ColumnComment("内容")
    private String content;
    @DynamicCondition(FilterByFixedUser.class)
    @InsertFillData(UserIdAutoFillHandler.class)
    @ColumnComment("提交人")
    private Integer submitter;
}
