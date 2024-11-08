package com.tangzc.mpe.demo.condition.daily;

import com.tangzc.autotable.annotation.ColumnComment;
import lombok.Data;
import lombok.experimental.Accessors;
import com.tangzc.mpe.annotation.InsertFillData;
import com.tangzc.mpe.autotable.annotation.Table;
import com.tangzc.mpe.condition.metadata.annotation.DynamicCondition;
import com.tangzc.mpe.demo.condition.FilterByCurrentUser;
import com.tangzc.mpe.demo.condition.UserIdAutoFillHandler;

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
