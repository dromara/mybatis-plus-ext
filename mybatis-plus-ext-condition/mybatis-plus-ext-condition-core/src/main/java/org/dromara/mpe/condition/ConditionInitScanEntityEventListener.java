package org.dromara.mpe.condition;

import org.dromara.mpe.base.IEntityRegister;
import org.dromara.mpe.condition.metadata.annotation.DynamicCondition;
import org.dromara.mpe.magic.util.AnnotatedElementUtilsPlus;
import org.dromara.mpe.magic.util.BeanClassUtil;

import java.lang.reflect.Field;
import java.util.List;

/**
 * @author don
 */
//@Component
public class ConditionInitScanEntityEventListener implements IEntityRegister {

    @Override
    public void register(Class<?> entityClass) {
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
