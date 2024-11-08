package com.tangzc.mpe.demo.bind.normal;

import lombok.Data;
import lombok.experimental.Accessors;
import com.tangzc.mpe.autotable.annotation.Table;
import com.tangzc.mpe.bind.metadata.annotation.BindEntity;
import com.tangzc.mpe.bind.metadata.annotation.BindField;
import com.tangzc.mpe.bind.metadata.annotation.JoinCondition;
import com.tangzc.mpe.processer.annotation.AutoDefine;
import com.tangzc.mpe.processer.annotation.AutoRepository;

@Data
@Accessors(chain = true)
@AutoDefine
@AutoRepository
@Table
public class Article {

    private String id;
    private String content;
    private String submitter;
    @BindEntity(selectFields = {UserDefine.id, UserDefine.name}, conditions = @JoinCondition(selfField = ArticleDefine.submitter, joinField = UserDefine.id), last = "limit 1")
    private User submitterUser;
    @BindField(entity = User.class, field = UserDefine.registeredDate, conditions = @JoinCondition(selfField = ArticleDefine.submitter, joinField = UserDefine.id), last = "limit 1")
    private Long registeredDate;
}
