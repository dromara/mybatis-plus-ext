package com.tangzc.mpe.demo.condition.daily2;

import com.baomidou.mybatisplus.annotation.TableName;
import com.tangzc.mpe.actable.annotation.ColumnComment;
import com.tangzc.mpe.actable.annotation.Table;
import com.tangzc.mpe.annotation.InsertOptionUser;
import com.tangzc.mpe.condition.metadata.annotation.DynamicCondition;
import com.tangzc.mpe.demo.condition.FilterByFixedUser;
import com.tangzc.mpe.demo.condition.UserIdAutoFillHandler;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@Table(comment = "日报")
@TableName("daily")
public class Daily2 {

    @ColumnComment("主键")
    private String id;
    @ColumnComment("内容")
    private String content;
    @DynamicCondition(FilterByFixedUser.class)
    @InsertOptionUser(UserIdAutoFillHandler.class)
    @ColumnComment("提交人")
    private String submitter;
}
