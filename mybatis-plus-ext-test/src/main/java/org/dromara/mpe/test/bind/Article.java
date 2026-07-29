package org.dromara.mpe.test.bind;

import lombok.Data;
import lombok.experimental.Accessors;
import org.dromara.mpe.autotable.annotation.Table;
import org.dromara.mpe.bind.metadata.annotation.BindEntity;
import org.dromara.mpe.bind.metadata.annotation.BindField;
import org.dromara.mpe.bind.metadata.annotation.JoinCondition;

@Data
@Accessors(chain = true)
@Table
public class Article {

    private String id;
    private String content;
    private String submitter;

    @BindEntity(
            selectFields = {BindUserDefine.id, BindUserDefine.name},
            conditions = @JoinCondition(selfField = ArticleDefine.submitter, joinField = BindUserDefine.id)
    )
    private BindUser submitterUser;

    @BindField(
            entity = BindUser.class, field = BindUserDefine.registeredDate,
            conditions = @JoinCondition(selfField = ArticleDefine.submitter, joinField = BindUserDefine.id)
    )
    private Long registeredDate;
}
