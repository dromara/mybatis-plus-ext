package com.tangzc.mybatis.relevance;

import com.tangzc.mybatis.relevance.builder.ConditionSign;
import com.tangzc.mybatis.relevance.builder.ResultBuilder;
import com.tangzc.mybatis.relevance.metadata.BindEntityDescription;
import com.tangzc.mybatis.relevance.metadata.FieldDescription;
import com.tangzc.mybatis.relevance.metadata.JoinConditionDescription;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 执行字段绑定的绑定器
 *
 * @author don
 */
@Slf4j
@NoArgsConstructor(staticName = "newInstance")
public class BindEntityBinder<BEAN> implements Binder.IBinder<BEAN, BindEntityDescription, JoinConditionDescription> {

    @Override
    public <ENTITY> void fillData(List<BEAN> beans,
                                  ConditionSign<ENTITY, JoinConditionDescription> conditionSign,
                                  List<BindEntityDescription> fieldAnnotations) {

        ResultBuilder.newInstance(beans, conditionSign, fieldAnnotations,
                new ResultBuilder.FillDataCallback() {
                    @Override
                    public void fillBefore(Object bean, FieldDescription<?, JoinConditionDescription> fieldAnnotation, List<?> entities) {
                        if (((BindEntityDescription) fieldAnnotation).isDeepBind()) {
                            Binder.bind(entities, Collections.emptyList(), Collections.emptyList());
                        }
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
                }).fillData();
    }
}
