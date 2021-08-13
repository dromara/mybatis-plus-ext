package com.tangzc.mpe.demo.condition;

import com.tangzc.mpe.condition.metadata.IDynamicConditionHandler;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;

@Component
public class DailyDynamicConditionHandler implements IDynamicConditionHandler {

    @Resource
    private HttpServletRequest request;

    @Override
    public List<Object> values() {

        String id = request.getParameter("id");
        return Collections.singletonList(id);
    }

    @Override
    public boolean enable() {
        String id = request.getParameter("id");
        return id != null;
    }
}
