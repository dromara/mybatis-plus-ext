package com.tangzc.mpe.demo.bind;

import com.tangzc.mpe.actable.annotation.ColumnComment;
import com.tangzc.mpe.actable.annotation.Table;
import com.tangzc.mpe.bind.metadata.annotation.BindEntity;
import com.tangzc.mpe.bind.metadata.annotation.JoinCondition;
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
    @ColumnComment("提交人")
    private String submitter;
    @BindEntity(conditions = @JoinCondition(selfField = "submitter"))
    private User submitterUser;
}
