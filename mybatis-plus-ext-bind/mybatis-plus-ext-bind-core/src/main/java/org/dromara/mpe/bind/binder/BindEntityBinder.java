package org.dromara.mpe.bind.binder;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.mpe.bind.builder.ResultBuilder;
import org.dromara.mpe.bind.metadata.BindEntityDescription;
import org.dromara.mpe.bind.metadata.FieldDescription;
import org.dromara.mpe.bind.metadata.JoinConditionDescription;
import org.springframework.beans.BeanUtils;

import java.util.Collection;
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
public class BindEntityBinder<BEAN> implements IBinder<BEAN, BindEntityDescription, JoinConditionDescription> {

    /**
     * @param beans            待填充的集合
     * @param conditionSign    分组条件
     * @param fieldAnnotations 相同条件的字段集合
     * @param <ENTITY>         被关联类的类型
     */
    @Override
    public <ENTITY> void fillData(List<BEAN> beans,
                                  FieldDescription.ConditionSign<ENTITY, JoinConditionDescription> conditionSign,
                                  List<BindEntityDescription> fieldAnnotations) {

        ResultBuilder.FillDataCallback fillDataCallback = new ResultBuilder.FillDataCallback() {

            @Override
            public String[] selectColumns(List<?> beans, FieldDescription.ConditionSign<?, JoinConditionDescription> entityJoinCondition,
                                          List<? extends FieldDescription<?, JoinConditionDescription>> fieldAnnotationList) {

                // 如果有一个字段没有设置selectColumns，则直接返回空，表示查询全部列
                for (BindEntityDescription fieldAnnotation : fieldAnnotations) {
                    if (fieldAnnotation.getSelectColumns().isEmpty()) {
                        return new String[0];
                    }
                }

                Set<String> columns = fieldAnnotations.stream()
                        .map(BindEntityDescription::getSelectColumns)
                        .flatMap(Collection::stream)
                        .collect(Collectors.toSet());

                // 如果有字段设置了selectColumns，则追加条件查询字段
                if (!columns.isEmpty()) {
                    // 追加条件查询字段，用于标识查询数据的
                    for (JoinConditionDescription condition : entityJoinCondition.getConditions()) {
                        columns.add(condition.getJoinColumnName());
                    }
                }

                return columns.toArray(new String[0]);
            }

            @Override
            public List<?> changeDataList(Object bean, FieldDescription<?, JoinConditionDescription> fieldAnnotation, List<?> entities) {

                BindEntityDescription entityDescription = (BindEntityDescription) fieldAnnotation;

                // 当查询的数据类型与接收的不匹配
                if (entityDescription.getEntityClass() != entityDescription.getFieldClass()) {
                    return entities.stream().map(entity -> {
                        try {
                            Object newInstance = entityDescription.getFieldClass().newInstance();
                            BeanUtils.copyProperties(entity, newInstance);
                            return newInstance;
                        } catch (InstantiationException | IllegalAccessException e) {
                            throw new RuntimeException(entityDescription.getEntityClass() + "转" +
                                    entityDescription.getFieldClass() + "的过程中发生错误。", e);
                        }
                    }).collect(Collectors.toList());
                }

                return entities;
            }
        };
        ResultBuilder.newInstance(beans, conditionSign, fieldAnnotations, fillDataCallback).fillData();
    }
}
