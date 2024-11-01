package org.dromara.mpe.bind.binder;

import org.dromara.mpe.bind.metadata.BindAggFuncDescription;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 执行字段绑定的绑定器
 *
 * @author don
 */
@Slf4j
@NoArgsConstructor(staticName = "newInstance")
public class BindAggFuncBinder<BEAN> {

    /**
     * @param beans             待填充的bean
     * @param fieldDescriptions 待绑定的字段
     */
    public void doBind(List<BEAN> beans, List<BindAggFuncDescription> fieldDescriptions) {

        Map<BindAggFuncDescription.ConditionSign, List<BindAggFuncDescription>> groupByDesc =
                fieldDescriptions.stream().collect(Collectors.groupingBy(BindAggFuncDescription::groupBy));

        // TODO 构建结果并赋值到beans中
        if (groupByDesc.size() > 0) {
            throw new RuntimeException("暂未支持函数绑定功能");
        }
    }
}
