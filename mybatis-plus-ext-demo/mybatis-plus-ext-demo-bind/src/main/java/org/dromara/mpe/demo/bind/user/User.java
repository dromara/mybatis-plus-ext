package org.dromara.mpe.demo.bind.user;

import org.dromara.mpe.annotation.InsertFillTime;
import org.dromara.mpe.autotable.annotation.Table;
import org.dromara.mpe.bind.metadata.annotation.AggFuncField;
import org.dromara.mpe.bind.metadata.annotation.BindAggFunc;
import org.dromara.mpe.bind.metadata.annotation.JoinCondition;
import org.dromara.mpe.bind.metadata.enums.AggFuncEnum;
import org.dromara.mpe.processer.annotation.AutoDefine;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@AutoDefine
@Table
public class User {

    private String id;
    private String name;
    @InsertFillTime
    private Long registeredDate;
    @BindAggFunc(entity = Hobby.class, aggField = @AggFuncField(func = AggFuncEnum.COUNT),
            conditions = @JoinCondition(selfField = "id", joinField = "userId"))
    private Long hobbyNum;
}
