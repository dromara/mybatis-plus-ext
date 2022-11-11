package com.tangzc.mpe.datasource;

import com.tangzc.mpe.base.event.InitScanEntityEvent;
import com.tangzc.mpe.base.util.BeanClassUtil;
import com.tangzc.mpe.datasource.annotation.DataSource;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.AnnotatedElementUtils;

import java.lang.reflect.Field;
import java.util.List;

/**
 * @author don
 */
//@Component
public class DataSourceInitScanEntityEventListener {

    @EventListener
    public void onApplicationEvent(InitScanEntityEvent event) {

        Class<?> entityClass = event.getEntityClass();
        List<Field> allDeclaredFields = BeanClassUtil.getAllDeclaredFields(entityClass);
        for (Field entityField : allDeclaredFields) {

            // 扫描所有的Entity中的DataSource注解
            DataSource dataSource = AnnotatedElementUtils.findMergedAnnotation(entityField, DataSource.class);
            if (dataSource != null) {
                DataSourceManager.addDataSource(entityClass, entityField, dataSource);
            }
        }
    }
}
