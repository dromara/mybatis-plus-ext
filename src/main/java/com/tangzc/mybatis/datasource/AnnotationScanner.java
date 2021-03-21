package com.tangzc.mybatis.datasource;

import com.tangzc.mybatis.common.EntityMapperManager;
import com.tangzc.mybatis.datasource.metadata.annotation.DataSource;
import com.tangzc.mybatis.util.BeanClassUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.Field;
import java.util.List;

/**
 * 扫描所有Entity中的DataSource注解
 * @author don
 */
@Slf4j
public class AnnotationScanner {

    public static void scanDataSourceAnnotation() {

        List<Class<?>> entityList = EntityMapperManager.getEntityList();
        for (Class<?> entityClass : entityList) {
            List<Field> allDeclaredFields = BeanClassUtil.getAllDeclaredFields(entityClass);
            for (Field field : allDeclaredFields) {
                DataSource dataSource = AnnotationUtils.findAnnotation(field, DataSource.class);
                if(dataSource != null) {
                    DataSourceManager.addDataSource(entityClass, field, dataSource);
                }
            }
        }
    }
}
