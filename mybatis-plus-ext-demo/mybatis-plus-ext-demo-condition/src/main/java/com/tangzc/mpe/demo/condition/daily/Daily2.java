package com.tangzc.mpe.demo.condition.daily;

import com.tangzc.autotable.annotation.ColumnComment;
import com.tangzc.mpe.annotation.InsertFillData;
import com.tangzc.mpe.autotable.annotation.Table;
import com.tangzc.mpe.condition.metadata.annotation.DynamicCondition;
import com.tangzc.mpe.demo.condition.FilterByFixedUser;
import com.tangzc.mpe.demo.condition.UserIdAutoFillHandler;
import com.tangzc.mpe.processer.annotation.AutoRepository;
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
