package com.tangzc.mpe.condition;

import com.tangzc.mpe.base.event.InitScanEntityEvent;
import com.tangzc.mpe.condition.metadata.annotation.DynamicCondition;
import com.tangzc.mpe.magic.util.AnnotatedElementUtilsPlus;
import com.tangzc.mpe.magic.util.BeanClassUtil;
import org.springframework.context.event.EventListener;

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
        List<Field> allDeclaredFields = BeanClassUtil.getAllDeclaredFieldsExcludeStatic(entityClass);
        for (Field field : allDeclaredFields) {

            // 扫描所有的Entity中的DataSource注解
            DynamicCondition dynamicCondition = AnnotatedElementUtilsPlus.findDeepMergedAnnotation(field, DynamicCondition.class);
            if (dynamicCondition != null) {
                DynamicConditionManager.add(entityClass, field, dynamicCondition);
            }
        }
    }
}
