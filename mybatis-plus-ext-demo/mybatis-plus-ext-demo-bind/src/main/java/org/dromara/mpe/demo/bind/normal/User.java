package org.dromara.mpe.demo.bind.normal;

import lombok.Data;
import lombok.experimental.Accessors;
import org.dromara.mpe.annotation.InsertFillTime;
import org.dromara.mpe.autotable.annotation.Table;
import org.dromara.mpe.bind.metadata.annotation.AggFuncField;
import org.dromara.mpe.bind.metadata.annotation.BindAggFunc;
import org.dromara.mpe.bind.metadata.annotation.JoinCondition;
import org.dromara.mpe.bind.metadata.enums.AggFuncEnum;

@Data
@Accessors(chain = true)
@Table
public class User {

    private String id;
    private String name;
    @InsertFillTime
    private Long registeredDate;
    // 暂未实现，测试代码，切勿使用
    @BindAggFunc(entity = Hobby.class, aggField = @AggFuncField(func = AggFuncEnum.COUNT),
            conditions = @JoinCondition(selfField = "id", joinField = "userId"))
    private Long hobbyNum;
}
