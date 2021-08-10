package com.tangzc.mpe.datasource;

import com.tangzc.mpe.core.event.InitScanEntityEvent;
import com.tangzc.mpe.core.util.BeanClassUtil;
import com.tangzc.mpe.datasource.annotation.DataSource;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.Field;
import java.util.List;

/**
 * @author don
 */ //@Component
public class DataSourceInitScanEntityEventListener implements ApplicationListener<InitScanEntityEvent> {
    @Override
    public void onApplicationEvent(InitScanEntityEvent event) {

        Class<?> entityClass = event.getEntityClass();
        List<Field> allDeclaredFields = BeanClassUtil.getAllDeclaredFields(entityClass);
        for (Field field : allDeclaredFields) {

            // 扫描所有的Entity中的DataSource注解
            DataSource dataSource = AnnotationUtils.findAnnotation(field, DataSource.class);
            if (dataSource != null) {
                DataSourceManager.addDataSource(entityClass, field, dataSource);
            }
        }
    }
}
