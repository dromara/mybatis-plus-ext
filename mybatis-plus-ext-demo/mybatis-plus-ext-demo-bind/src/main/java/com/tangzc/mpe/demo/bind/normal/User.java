package com.tangzc.mpe.demo.bind.normal;

import lombok.Data;
import lombok.experimental.Accessors;
import com.tangzc.mpe.annotation.InsertFillTime;
import com.tangzc.mpe.autotable.annotation.Table;
import com.tangzc.mpe.bind.metadata.annotation.AggFuncField;
import com.tangzc.mpe.bind.metadata.annotation.BindAggFunc;
import com.tangzc.mpe.bind.metadata.annotation.JoinCondition;
import com.tangzc.mpe.bind.metadata.enums.AggFuncEnum;

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
