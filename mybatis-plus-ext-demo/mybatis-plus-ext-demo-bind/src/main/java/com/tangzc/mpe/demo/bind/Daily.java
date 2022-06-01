package com.tangzc.mpe.demo.bind;

import com.tangzc.mpe.bind.metadata.annotation.BindEntity;
import com.tangzc.mpe.bind.metadata.annotation.JoinCondition;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Daily {

    private String id;
    private String content;
    private String submitter;
    @BindEntity(conditions = @JoinCondition(selfField = "submitter", joinField = "name"), last = "limit 1")
    private User submitterUser;
}
