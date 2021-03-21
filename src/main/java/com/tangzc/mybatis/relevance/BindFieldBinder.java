package com.tangzc.mybatis.relevance;

import com.tangzc.mybatis.relevance.builder.ConditionSign;
import com.tangzc.mybatis.relevance.builder.ResultBuilder;
import com.tangzc.mybatis.relevance.metadata.BindFieldDescription;
import com.tangzc.mybatis.relevance.metadata.FieldDescription;
import com.tangzc.mybatis.relevance.metadata.JoinConditionDescription;
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
public class BindFieldBinder<BEAN> implements Binder.IBinder<BEAN, BindFieldDescription, JoinConditionDescription> {

    /**
     * @param beans             待填充的bean
     * @param conditionSign 被关联的表的查询描述
     * @param fieldAnnotations  需要填充数据的表的字段集合
     */
    @Override
    public <ENTITY> void fillData(List<BEAN> beans,
                                       ConditionSign<ENTITY, JoinConditionDescription> conditionSign,
                                       List<BindFieldDescription> fieldAnnotations) {

        ResultBuilder.newInstance(beans, conditionSign, fieldAnnotations,
                new ResultBuilder.FillDataCallback() {
                    @Override
                    public String[] selectColumns(List<?> beans, ConditionSign<?, JoinConditionDescription> entityJoinCondition,
                                                  List<? extends FieldDescription<?, JoinConditionDescription>> fieldAnnotationList) {

                        List<String> columns = fieldAnnotations.stream()
                                .map(bfd -> bfd.getBindAnnotation().field())
                                // 驼峰转下划线
                                .map(ColumnUtil::humpToLine)
                                .collect(Collectors.toList());

                        // 追加条件查询字段，用于标识查询数据的
                        for (JoinConditionDescription condition : entityJoinCondition.getConditions()) {
                            columns.add(ColumnUtil.humpToLine(condition.getJoinField()));
                        }

                        return columns.toArray(new String[0]);
                    }

                    @Override
                    public List<?> changeDataList(Object bean, FieldDescription<?, JoinConditionDescription> fieldAnnotation,
                                                  List<?> entities) {
                        return entities.stream().map(entity -> {
                            try {
                                return ((BindFieldDescription) fieldAnnotation).getBindFieldGetMethod().invoke(entity);
                            } catch (Exception e) {
                                log.error("绑定属性获取值失败", e);
                                return null;
                            }
                        }).collect(Collectors.toList());
                    }
                }).fillData();
    }
}
