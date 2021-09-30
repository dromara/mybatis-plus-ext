package com.tangzc.mpe.condition;

import com.tangzc.mpe.base.event.InitScanEntityEvent;
import com.tangzc.mpe.base.util.BeanClassUtil;
import com.tangzc.mpe.condition.metadata.annotation.DynamicCondition;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.AnnotatedElementUtils;

import java.lang.reflect.Field;
import java.util.List;

/**
 * @author don
 */
//@Component
public class ConditionInitScanEntityEventListener {

    @EventListener
    public void onApplicationEvent(InitScanEntityEvent event) {

        Class<?> entityClass = event.getEntityClass();
        List<Field> allDeclaredFields = BeanClassUtil.getAllDeclaredFields(entityClass);
        for (Field field : allDeclaredFields) {

            // 扫描所有的Entity中的DataSource注解
            DynamicCondition dynamicCondition = AnnotatedElementUtils.findMergedAnnotation(field, DynamicCondition.class);
            if (dynamicCondition != null) {
                DynamicConditionManager.add(entityClass, field, dynamicCondition);
            }
        }
    }
}
