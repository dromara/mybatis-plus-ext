package com.tangzc.mpe.demo.bind.daily;

import com.baomidou.mybatisplus.annotation.TableField;
import com.tangzc.mpe.automapper.AutoMapper;
import com.tangzc.mpe.autotable.annotation.Table;
import com.tangzc.mpe.bind.metadata.annotation.BindEntity;
import com.tangzc.mpe.bind.metadata.annotation.JoinCondition;
import com.tangzc.mpe.demo.bind.user.User;
import lombok.Data;
import lombok.experimental.Accessors;

@AutoMapper
@Data
@Accessors(chain = true)
@Table
public class Daily {

    private String id;
    private String content;
    @TableField("submit")
    private String submitter;
    @BindEntity(conditions = @JoinCondition(selfField = "submitter", joinField = "name"), last = "limit 1")
    private User submitterUser;
}
