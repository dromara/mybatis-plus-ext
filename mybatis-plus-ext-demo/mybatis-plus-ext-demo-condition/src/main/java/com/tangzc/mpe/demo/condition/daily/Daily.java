package com.tangzc.mpe.demo.condition.daily;

import com.tangzc.mpe.autotable.annotation.ColumnComment;
import com.tangzc.mpe.autotable.annotation.Table;
import com.tangzc.mpe.annotation.InsertOptionUser;
import com.tangzc.mpe.condition.metadata.annotation.DynamicCondition;
import com.tangzc.mpe.demo.condition.FilterByCurrentUser;
import com.tangzc.mpe.demo.condition.UserIdAutoFillHandler;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@Table(comment = "日报", primary = true)
public class Daily {

    @ColumnComment("主键")
    private String id;
    @ColumnComment("内容")
    private String content;
    @DynamicCondition(FilterByCurrentUser.class)
    @InsertOptionUser(UserIdAutoFillHandler.class)
    @ColumnComment("提交人")
    private String submitter;
}
