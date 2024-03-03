package com.tangzc.mpe.demo.condition;

import com.tangzc.mpe.condition.metadata.IDynamicConditionHandler;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class FilterByFixedUser implements IDynamicConditionHandler {

    @Override
    public List<Object> values() {

        return Collections.singletonList("111");
    }
}
