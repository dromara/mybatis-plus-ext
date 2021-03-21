package com.tangzc.mybatis.relevance;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.LambdaUtils;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.core.toolkit.support.SerializedLambda;
import com.tangzc.mybatis.relevance.builder.ConditionSign;
import com.tangzc.mybatis.relevance.manager.BeanAnnotationManager;
import com.tangzc.mybatis.relevance.metadata.BeanDescription;
import com.tangzc.mybatis.relevance.metadata.FieldDescription;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.property.PropertyNamer;
import org.springframework.util.CollectionUtils;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 数据级联处理
 *
 * @author don
 */
@Slf4j
public class Binder {

    @SafeVarargs
    public static <BEAN> void bindOn(BEAN bean, SFunction<BEAN, ?> firstField, SFunction<BEAN, ?>... otherFields) {
        if (bean == null) {
            return;
        }
        bindOn(Collections.singletonList(bean), firstField, otherFields);
    }

    @SafeVarargs
    public static <BEAN> void bindOn(IPage<BEAN> bean, SFunction<BEAN, ?> firstField, SFunction<BEAN, ?>... otherFields) {
        bindOn(bean.getRecords(), firstField, otherFields);
    }

    @SafeVarargs
    public static <BEAN> void bindOn(List<BEAN> beans, SFunction<BEAN, ?> firstField, SFunction<BEAN, ?>... otherFields) {

        List<SFunction<BEAN, ?>> bindFields = new ArrayList<>();
        bindFields.add(firstField);
        if (otherFields != null) {
            bindFields.addAll(Arrays.asList(otherFields));
        }
        bindOn(beans, bindFields);
    }

    public static <BEAN> void bindOn(BEAN bean, List<SFunction<BEAN, ?>> bindFields) {
        if (bean == null) {
            return;
        }
        bindOn(Collections.singletonList(bean), bindFields);
    }

    public static <BEAN> void bindOn(IPage<BEAN> bean, List<SFunction<BEAN, ?>> bindFields) {
        bindOn(bean.getRecords(), bindFields);
    }

    public static <BEAN> void bindOn(List<BEAN> beans, List<SFunction<BEAN, ?>> bindFields) {

        List<String> bindFieldNames = new ArrayList<>();

        Function<SFunction<BEAN, ?>, String> getFieldName = sf -> {
            SerializedLambda lambda = LambdaUtils.resolve(sf);
            return PropertyNamer.methodToProperty(lambda.getImplMethodName());
        };

        if (bindFields != null) {
            for (SFunction<BEAN, ?> otherField : bindFields) {
                bindFieldNames.add(getFieldName.apply(otherField));
            }
        }

        bind(beans, bindFieldNames, Collections.emptyList());
    }

    public static <BEAN> void bind(BEAN bean) {
        if (bean == null) {
            return;
        }
        bind(Collections.singletonList(bean));
    }

    public static <BEAN> void bind(IPage<BEAN> page) {
        if (page == null) {
            return;
        }
        bind(page.getRecords());
    }

    public static <BEAN> void bind(List<BEAN> beans) {
        if (beans == null) {
            return;
        }
        bind(beans, Collections.emptyList(), Collections.emptyList());
    }

    public static <BEAN> void bind(List<BEAN> beans, List<String> includeField, List<String> ignoreField) {

        if (CollectionUtils.isEmpty(beans)) {
            return;
        }

        Class<BEAN> beanClass = (Class<BEAN>) beans.get(0).getClass();
        BeanDescription<BEAN> beanAnnotation = BeanAnnotationManager.getBeanAnnotation(beanClass, includeField, ignoreField);
        if (beanAnnotation.isValid()) {
            BindFieldBinder.<BEAN>newInstance().doBind(beans, beanAnnotation.getBindFieldAnnotations());
            BindEntityBinder.<BEAN>newInstance().doBind(beans, beanAnnotation.getBindEntityAnnotations());
            BindFieldByMidBinder.<BEAN>newInstance().doBind(beans, beanAnnotation.getBindFieldByMidDescriptions());
            BindEntityByMidBinder.<BEAN>newInstance().doBind(beans, beanAnnotation.getBindEntityByMidAnnotations());
        }
    }

    public interface IBinder<BEAN, FD extends FieldDescription<? extends Annotation, CONDITION_DESC>, CONDITION_DESC> {

        /**
         * 绑定操作
         * @param beans
         * @param fieldDescriptions
         */
        default void doBind(List<BEAN> beans, List<FD> fieldDescriptions) {

                if(fieldDescriptions.isEmpty()) {
                    return;
                }

                // 合并相同条件的关联，减少查询
                Map<ConditionSign<?, CONDITION_DESC>, List<FD>> fieldAnnotationListGroupByCondition =
                        fieldDescriptions.stream().collect(Collectors.groupingBy(fd -> fd.conditionUniqueKey()));

                // 填充数据
                fieldAnnotationListGroupByCondition.forEach(
                        (entityJoinCondition, fieldAnnotationList) -> fillData(beans, entityJoinCondition, fieldAnnotationList)
                );
        }

        <ENTITY> void fillData(List<BEAN> beans, ConditionSign<ENTITY, CONDITION_DESC> entityJoinCondition, List<FD> fieldAnnotationList);
    }
}