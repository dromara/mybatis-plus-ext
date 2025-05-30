package org.dromara.mpe.datasource;

import org.dromara.mpe.base.IEntityRegister;
import org.dromara.mpe.datasource.annotation.DataSource;
import org.dromara.mpe.magic.util.AnnotatedElementUtilsPlus;
import org.dromara.mpe.magic.util.BeanClassUtil;

import java.lang.reflect.Field;
import java.util.List;

/**
 * @author don
 */
//@Component
public class DataSourceInitScanEntityEventListener implements IEntityRegister {

    @Override
    public void register(Class<?> entityClass) {
        List<Field> allDeclaredFields = BeanClassUtil.getAllDeclaredFieldsExcludeStatic(entityClass);
        for (Field entityField : allDeclaredFields) {

            // 扫描所有的Entity中的DataSource注解
            DataSource dataSource = AnnotatedElementUtilsPlus.findDeepMergedAnnotation(entityField, DataSource.class);
            if (dataSource != null) {
                DataSourceManager.addDataSource(entityClass, entityField, dataSource);
            }
        }
    }
}
