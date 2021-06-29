package com.tangzc.mpe.common;

import com.tangzc.mpe.util.BeanClassUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.List;

/**
 * @author don
 */
@Slf4j
public class ApplicationStartListener implements ApplicationListener<ContextRefreshedEvent> {

    @Resource
    private List<EntityFieldScanner> scanners;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {

        // 初始化所有的Entity和Mapper
        EntityMapperManager.initEntityMapper();

        List<Class<?>> entityList = EntityMapperManager.getEntityList();
        for (Class<?> entityClass : entityList) {
            List<Field> allDeclaredFields = BeanClassUtil.getAllDeclaredFields(entityClass);
            for (Field field : allDeclaredFields) {
                scanners.forEach(scanner -> scanner.scan(entityClass, field));
            }
        }
    }

    @FunctionalInterface
    public interface EntityFieldScanner {
        /**
         * 遍历表上每个字段的回调
         */
        void scan(Class<?> entityClass, Field field);
    }
}
