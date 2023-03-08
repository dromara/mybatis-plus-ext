package com.tangzc.mpe.bind.binder;

import com.tangzc.mpe.bind.builder.ConditionSign;
import com.tangzc.mpe.bind.metadata.BindAggFuncDescription;
import com.tangzc.mpe.bind.metadata.JoinConditionDescription;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 执行字段绑定的绑定器
 *
 * @author don
 */
@Slf4j
@NoArgsConstructor(staticName = "newInstance")
public class BindAggFuncBinder<BEAN> implements IBinder<BEAN, BindAggFuncDescription, JoinConditionDescription> {

    /**
     * @param beans                 待填充的bean
     * @param conditionSign         被关联的表的查询描述
     * @param bindCountDescriptions 需要填充数据的表的字段集合
     */
    @Override
    public <ENTITY> void fillData(List<BEAN> beans,
                                  ConditionSign<ENTITY, JoinConditionDescription> conditionSign,
                                  List<BindAggFuncDescription> bindCountDescriptions) {

        // TODO 构建结果并赋值到beans中
    }
}
