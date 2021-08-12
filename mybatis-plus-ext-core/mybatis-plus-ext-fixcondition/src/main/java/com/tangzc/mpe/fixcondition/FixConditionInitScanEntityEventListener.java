package com.tangzc.mpe.fixcondition;

import com.tangzc.mpe.base.event.InitScanEntityEvent;
import com.tangzc.mpe.base.util.BeanClassUtil;
import com.tangzc.mpe.fixcondition.metadata.annotation.FixedCondition;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.Field;
import java.util.List;

/**
 * @author don
 */
//@Component
public class FixConditionInitScanEntityEventListener implements ApplicationListener<InitScanEntityEvent> {
    @Override
    public void onApplicationEvent(InitScanEntityEvent event) {

        Class<?> entityClass = event.getEntityClass();
        List<Field> allDeclaredFields = BeanClassUtil.getAllDeclaredFields(entityClass);
        for (Field field : allDeclaredFields) {

            // 扫描所有的Entity中的DataSource注解
            FixedCondition fixedCondition = AnnotationUtils.findAnnotation(field, FixedCondition.class);
            if (fixedCondition != null) {
                FixedConditionManager.add(entityClass, field, fixedCondition);
            }
        }
    }
}
