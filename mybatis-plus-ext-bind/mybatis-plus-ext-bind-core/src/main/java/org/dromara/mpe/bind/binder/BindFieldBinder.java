package org.dromara.mpe.bind.binder;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.mpe.bind.builder.ResultBuilder;
import org.dromara.mpe.bind.metadata.BindFieldDescription;
import org.dromara.mpe.bind.metadata.FieldDescription;
import org.dromara.mpe.bind.metadata.JoinConditionDescription;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 执行字段绑定的绑定器
 *
 * @author don
 */
@Slf4j
@NoArgsConstructor(staticName = "newInstance")
public class BindFieldBinder<BEAN> implements IBinder<BEAN, BindFieldDescription, JoinConditionDescription> {

    /**
     * @param beans                 待填充的bean
     * @param conditionSign         被关联的表的查询描述
     * @param bindFieldDescriptions 需要填充数据的表的字段集合
     */
    @Override
    public <ENTITY> void fillData(List<BEAN> beans,
                                  FieldDescription.ConditionSign<ENTITY, JoinConditionDescription> conditionSign,
                                  List<BindFieldDescription> bindFieldDescriptions) {

        ResultBuilder.FillDataCallback fillDataCallback = new ResultBuilder.FillDataCallback() {
            @Override
            public String[] selectColumns(List<?> beans, FieldDescription.ConditionSign<?, JoinConditionDescription> entityJoinCondition,
                                          List<? extends FieldDescription<?, JoinConditionDescription>> fieldAnnotationList) {

                Set<String> columns = bindFieldDescriptions.stream()
                        .map(BindFieldDescription::getRealColumnName)
                        .collect(Collectors.toSet());

                // 追加条件查询字段，用于标识查询数据的
                for (JoinConditionDescription condition : entityJoinCondition.getConditions()) {
                    columns.add(condition.getJoinColumnName());
                }

                return columns.toArray(new String[0]);
            }

            @Override
            public List<?> changeDataList(Object bean, FieldDescription<?, JoinConditionDescription> fieldAnnotation,
                                          List<?> entities) {
                // 将对象集合转化为单字段的集合
                return entities.stream().map(entity -> {
                    try {
                        return ((BindFieldDescription) fieldAnnotation).getBindFieldGetMethod().invoke(entity);
                    } catch (Exception e) {
                        log.error("绑定属性获取值失败", e);
                        return null;
                    }
                }).collect(Collectors.toList());
            }
        };
        ResultBuilder.newInstance(beans, conditionSign, bindFieldDescriptions, fillDataCallback).fillData();
    }
}
