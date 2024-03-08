package com.tangzc.mpe.bind.metadata;

import com.tangzc.mpe.bind.metadata.annotation.BindAggFunc;
import com.tangzc.mpe.bind.metadata.annotation.BindEntity;
import com.tangzc.mpe.bind.metadata.annotation.BindEntityByMid;
import com.tangzc.mpe.bind.metadata.annotation.BindField;
import com.tangzc.mpe.bind.metadata.annotation.BindFieldByMid;
import com.tangzc.mpe.bind.metadata.builder.FieldDescriptionBuilder;
import com.tangzc.mpe.magic.util.AnnotatedElementUtilsPlus;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * bean上的绑定关系描述
 *
 * @author don
 */
@Getter
@Slf4j
public class BeanDescription<BEAN> {

    private final Class<BEAN> beanClass;

    /**
     * 关联聚合函数注解
     */
    private final List<BindAggFuncDescription> bindAggFuncAnnotations = new ArrayList<>();

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

    public void findBindAnnotation(Field field) {

        BindAggFunc bindAggFunc = AnnotatedElementUtilsPlus.findDeepMergedAnnotation(field, BindAggFunc.class);
        if (bindAggFunc != null) {
            BindAggFuncDescription fieldDescription = FieldDescriptionBuilder.build(beanClass, field, bindAggFunc);
            this.bindAggFuncAnnotations.add(fieldDescription);
            return;
        }

        BindField bindField = AnnotatedElementUtilsPlus.findDeepMergedAnnotation(field, BindField.class);
        if (bindField != null) {
            BindFieldDescription fieldDescription = FieldDescriptionBuilder.build(beanClass, field, bindField);
            this.bindFieldAnnotations.add(fieldDescription);
            return;
        }

        BindEntity bindEntity = AnnotatedElementUtilsPlus.findDeepMergedAnnotation(field, BindEntity.class);
        if (bindEntity != null) {
            BindEntityDescription fieldDescription = FieldDescriptionBuilder.build(beanClass, field, bindEntity);
            this.bindEntityAnnotations.add(fieldDescription);
            return;
        }

        BindFieldByMid bindFieldByMid = AnnotatedElementUtilsPlus.findDeepMergedAnnotation(field, BindFieldByMid.class);
        if (bindFieldByMid != null) {
            BindFieldByMidDescription fieldDescription = FieldDescriptionBuilder.build(beanClass, field, bindFieldByMid);
            this.bindFieldByMidDescriptions.add(fieldDescription);
            return;
        }

        BindEntityByMid bindEntityByMid = AnnotatedElementUtilsPlus.findDeepMergedAnnotation(field, BindEntityByMid.class);
        if (bindEntityByMid != null) {
            BindEntityByMidDescription fieldDescription = FieldDescriptionBuilder.build(beanClass, field, bindEntityByMid);
            this.bindEntityByMidAnnotations.add(fieldDescription);
            return;
        }
        log.debug("字段{}没有Bind注解", field.getName());
    }

    public boolean isValid() {

        return !this.bindAggFuncAnnotations.isEmpty()
                || !this.bindFieldAnnotations.isEmpty()
                || !this.bindEntityAnnotations.isEmpty()
                || !this.bindFieldByMidDescriptions.isEmpty()
                || !this.bindEntityByMidAnnotations.isEmpty();
    }

}
