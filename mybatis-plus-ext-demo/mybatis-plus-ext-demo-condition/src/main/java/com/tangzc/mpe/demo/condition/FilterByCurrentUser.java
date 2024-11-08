package com.tangzc.mpe.demo.condition;

import org.apache.commons.lang3.RandomUtils;
import com.tangzc.mpe.condition.metadata.IDynamicConditionHandler;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class FilterByCurrentUser implements IDynamicConditionHandler {

    @Override
    public List<Object> values() {
        return Collections.singletonList(RandomUtils.nextInt(1, 3));
    }

    @Override
    public boolean enable() {
        // 动态判断是否启用该条件
        return true;
    }
}
