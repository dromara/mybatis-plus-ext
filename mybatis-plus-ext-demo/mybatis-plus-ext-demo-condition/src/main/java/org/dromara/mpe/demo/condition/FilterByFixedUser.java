package org.dromara.mpe.demo.condition;

import org.dromara.mpe.condition.metadata.IDynamicConditionHandler;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class FilterByFixedUser implements IDynamicConditionHandler {

    public static Integer fixId = 2;

    @Override
    public List<Object> values() {

        return Collections.singletonList(fixId);
    }
}
