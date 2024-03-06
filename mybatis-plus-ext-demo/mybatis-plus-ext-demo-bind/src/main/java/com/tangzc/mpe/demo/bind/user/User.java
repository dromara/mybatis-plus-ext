package com.tangzc.mpe.demo.bind.user;

import com.tangzc.mpe.annotation.InsertFillTime;
import com.tangzc.mpe.autotable.annotation.Table;
import com.tangzc.mpe.bind.metadata.annotation.AggFuncField;
import com.tangzc.mpe.bind.metadata.annotation.BindAggFunc;
import com.tangzc.mpe.bind.metadata.annotation.JoinCondition;
import com.tangzc.mpe.bind.metadata.enums.AggFuncEnum;
import com.tangzc.mpe.processer.annotation.AutoDefine;
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
