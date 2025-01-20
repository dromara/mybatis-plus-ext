package org.dromara.mpe.demo.bind.normal;

import lombok.Data;
import lombok.experimental.Accessors;
import org.dromara.mpe.autotable.annotation.Table;
import org.dromara.mpe.bind.metadata.annotation.BindEntity;
import org.dromara.mpe.bind.metadata.annotation.BindField;
import org.dromara.mpe.bind.metadata.annotation.JoinCondition;
import org.dromara.mpe.processer.annotation.AutoDefine;
import org.dromara.mpe.processer.annotation.AutoRepository;

@Data
@Accessors(chain = true)
@AutoDefine
@AutoRepository
@Table
public class Article {

    private String id;
    private String content;
    private String submitter;
    @BindEntity(selectFields = {UserDefine.id, UserDefine.name}, conditions = @JoinCondition(selfField = ArticleDefine.submitter, joinField = UserDefine.id))
    private User submitterUser;
    @BindField(entity = User.class, field = UserDefine.registeredDate, conditions = @JoinCondition(selfField = ArticleDefine.submitter, joinField = UserDefine.id))
    private Long registeredDate;
}
