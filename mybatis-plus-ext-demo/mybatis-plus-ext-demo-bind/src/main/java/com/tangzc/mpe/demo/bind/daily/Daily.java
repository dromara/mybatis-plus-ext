package com.tangzc.mpe.demo.bind.daily;

import com.baomidou.mybatisplus.annotation.TableField;
import com.tangzc.mpe.autotable.annotation.Table;
import com.tangzc.mpe.bind.metadata.annotation.BindEntity;
import com.tangzc.mpe.bind.metadata.annotation.JoinCondition;
import com.tangzc.mpe.demo.bind.user.User;
import com.tangzc.mpe.demo.bind.user.UserDefine;
import com.tangzc.mpe.processer.annotation.AutoDefine;
import com.tangzc.mpe.processer.annotation.AutoRepository;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@AutoDefine
@AutoRepository
@Table
public class Daily {

    private String id;
    private String content;
    @TableField("submit")
    private String submitter;
    @BindEntity(conditions = @JoinCondition(selfField = DailyDefine.submitter, joinField = UserDefine.id), last = "limit 1")
    private User submitterUser;
}
