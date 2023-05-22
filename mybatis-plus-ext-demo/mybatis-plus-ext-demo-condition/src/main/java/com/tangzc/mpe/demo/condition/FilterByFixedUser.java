package com.tangzc.mpe.demo.condition;

import com.tangzc.mpe.condition.metadata.IDynamicConditionHandler;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class FilterByFixedUser implements IDynamicConditionHandler {

    @Resource
    private HttpServletRequest request;

    @Override
    public List<Object> values() {

        return Collections.singletonList("111");
    }
}
