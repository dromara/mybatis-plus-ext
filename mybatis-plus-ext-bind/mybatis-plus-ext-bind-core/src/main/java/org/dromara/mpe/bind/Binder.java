package org.dromara.mpe.bind;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import org.dromara.mpe.bind.binder.BindAggFuncBinder;
import org.dromara.mpe.bind.binder.BindEntityBinder;
import org.dromara.mpe.bind.binder.BindEntityByMidBinder;
import org.dromara.mpe.bind.binder.BindFieldBinder;
import org.dromara.mpe.bind.binder.BindFieldByMidBinder;
import org.dromara.mpe.bind.manager.BeanAnnotationManager;
import org.dromara.mpe.bind.metadata.BeanDescription;
import org.dromara.mpe.magic.util.BeanClassUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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

        if (bindFields != null) {

            for (SFunction<BEAN, ?> otherFieldSF : bindFields) {
                bindFieldNames.add(BeanClassUtil.getFieldName(otherFieldSF));
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
            BindAggFuncBinder.<BEAN>newInstance().doBind(beans, beanAnnotation.getBindAggFuncAnnotations());
            BindFieldBinder.<BEAN>newInstance().doBind(beans, beanAnnotation.getBindFieldAnnotations());
            BindEntityBinder.<BEAN>newInstance().doBind(beans, beanAnnotation.getBindEntityAnnotations());
            BindFieldByMidBinder.<BEAN>newInstance().doBind(beans, beanAnnotation.getBindFieldByMidDescriptions());
            BindEntityByMidBinder.<BEAN>newInstance().doBind(beans, beanAnnotation.getBindEntityByMidAnnotations());
        }
    }

}
