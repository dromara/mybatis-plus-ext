package com.tangzc.mpe.relevance;

import com.tangzc.mpe.relevance.builder.ByMidResultBuilder;
import com.tangzc.mpe.relevance.builder.ConditionSign;
import com.tangzc.mpe.relevance.metadata.BindEntityByMidDescription;
import com.tangzc.mpe.relevance.metadata.FieldDescription;
import com.tangzc.mpe.relevance.metadata.MidConditionDescription;
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
public class BindEntityByMidBinder<BEAN> implements Binder.IBinder<BEAN, BindEntityByMidDescription, MidConditionDescription> {

    @Override
    public <ENTITY> void fillData(List<BEAN> beans, ConditionSign<ENTITY, MidConditionDescription> conditionSign,
                                  List<BindEntityByMidDescription> fieldAnnotations) {

        ByMidResultBuilder.newInstance(beans, conditionSign, fieldAnnotations,
                new ByMidResultBuilder.FillDataCallback() {
                    @Override
                    public void fillBefore(Object bean, FieldDescription<?, MidConditionDescription> fieldAnnotation, List<?> entities) {
                        if (((BindEntityByMidDescription) fieldAnnotation).isDeepBind()) {
                            Binder.bind(entities, Collections.emptyList(), Collections.emptyList());
                        }
                    }

                    @Override
                    public List<?> changeDataList(Object bean, FieldDescription<?, MidConditionDescription> fieldAnnotation, List<?> entities) {

                        BindEntityByMidDescription entityDescription = (BindEntityByMidDescription) fieldAnnotation;

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
