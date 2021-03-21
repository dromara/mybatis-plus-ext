package com.tangzc.mybatis.relevance;

import com.tangzc.mybatis.relevance.builder.ByMidResultBuilder;
import com.tangzc.mybatis.relevance.builder.ConditionSign;
import com.tangzc.mybatis.relevance.metadata.BindFieldByMidDescription;
import com.tangzc.mybatis.relevance.metadata.FieldDescription;
import com.tangzc.mybatis.relevance.metadata.MidConditionDescription;
import com.tangzc.mybatis.util.ColumnUtil;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 执行字段绑定的绑定器
 *
 * @author don
 */
@Slf4j
@NoArgsConstructor(staticName = "newInstance")
public class BindFieldByMidBinder<BEAN> implements Binder.IBinder<BEAN, BindFieldByMidDescription, MidConditionDescription> {

    @Override
    public <ENTITY> void fillData(List<BEAN> beans, ConditionSign<ENTITY, MidConditionDescription> conditionSign,
                                  List<BindFieldByMidDescription> fieldAnnotations) {

        ByMidResultBuilder.newInstance(beans, conditionSign, fieldAnnotations,
                new ByMidResultBuilder.FillDataCallback() {
                    @Override
                    public String[] selectColumns(List<?> beans, ConditionSign<?, MidConditionDescription> conditionSign,
                                                  List<? extends FieldDescription<?, MidConditionDescription>> fieldDescriptions) {

                        List<String> columns = fieldAnnotations.stream()
                                .map(bfd -> bfd.getBindAnnotation().field())
                                // 驼峰转下划线
                                .map(ColumnUtil::humpToLine)
                                .collect(Collectors.toList());

                        // 追加条件查询字段，用于标识查询数据的
                        for (MidConditionDescription condition : conditionSign.getConditions()) {
                            columns.add(ColumnUtil.humpToLine(condition.getJoinField()));
                        }

                        return columns.toArray(new String[0]);
                    }

                    @Override
                    public List<?> changeDataList(Object bean, FieldDescription<?, MidConditionDescription> fieldAnnotation, List<?> entities) {

                        return entities.stream().map(entity -> {
                            try {
                                return ((BindFieldByMidDescription) fieldAnnotation).getBindFieldGetMethod().invoke(entity);
                            } catch (Exception e) {
                                log.error("绑定属性获取值失败", e);
                                return null;
                            }
                        }).collect(Collectors.toList());
                    }
                }).fillData();
    }
}
