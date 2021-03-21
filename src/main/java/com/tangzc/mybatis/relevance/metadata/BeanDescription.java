package com.tangzc.mybatis.relevance.metadata;

import com.tangzc.mybatis.relevance.metadata.annotation.BindEntity;
import com.tangzc.mybatis.relevance.metadata.annotation.BindEntityByMid;
import com.tangzc.mybatis.relevance.metadata.annotation.BindField;
import com.tangzc.mybatis.relevance.metadata.annotation.BindFieldByMid;
import com.tangzc.mybatis.relevance.metadata.builder.FieldDescriptionBuilder;
import lombok.Getter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * bean上的绑定关系描述
 *
 * @author don
 */
@Getter
public class BeanDescription<BEAN> {

    private final Class<BEAN> beanClass;

    /**
     * 字段关联注解
     */
    private final List<BindFieldDescription> bindFieldAnnotations = new ArrayList<>();

    /**
     * 字段关联注解
     */
    private final List<BindEntityDescription> bindEntityAnnotations = new ArrayList<>();

    /**
     * 中间表字段关联注解
     */
    private final List<BindFieldByMidDescription> bindFieldByMidDescriptions = new ArrayList<>();

    /**
     * 中间表实体关联注解
     */
    private final List<BindEntityByMidDescription> bindEntityByMidAnnotations = new ArrayList<>();

    public BeanDescription(Class<BEAN> beanClass) {
        this.beanClass = beanClass;
    }

    public <BIND_ANNOTATION extends Annotation> void tryAddBindAnnotation(Field field, BIND_ANNOTATION annotation) {

        if (annotation instanceof BindField) {
            BindFieldDescription fieldDescription = FieldDescriptionBuilder.build(beanClass, field, (BindField) annotation);
            this.bindFieldAnnotations.add(fieldDescription);
            return;
        }

        if (annotation instanceof BindEntity) {
            BindEntityDescription fieldDescription = FieldDescriptionBuilder.build(beanClass, field, (BindEntity) annotation);
            this.bindEntityAnnotations.add(fieldDescription);
        }

        if (annotation instanceof BindFieldByMid) {
            BindFieldByMidDescription fieldDescription = FieldDescriptionBuilder.build(beanClass, field, (BindFieldByMid) annotation);
            this.bindFieldByMidDescriptions.add(fieldDescription);
        }

        if (annotation instanceof BindEntityByMid) {
            BindEntityByMidDescription fieldDescription = FieldDescriptionBuilder.build(beanClass, field, (BindEntityByMid) annotation);
            this.bindEntityByMidAnnotations.add(fieldDescription);
        }
    }

    public boolean isValid() {

        return !this.bindFieldAnnotations.isEmpty() || !this.bindEntityAnnotations.isEmpty();
    }

}
