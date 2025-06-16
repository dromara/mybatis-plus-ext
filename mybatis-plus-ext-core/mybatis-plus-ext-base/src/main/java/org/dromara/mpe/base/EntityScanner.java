package org.dromara.mpe.base;

import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author don
 */
@Slf4j
//@Component
public class EntityScanner implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired(required = false)
    private List<IEntityRegister> entityRegisters;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {

        if (CollectionUtils.isEmpty(entityRegisters)) {
            return;
        }

        List<TableInfo> tableInfos = TableInfoHelper.getTableInfos();

        // 初始化所有的Entity和Mapper
        if (tableInfos.isEmpty()) {
            return;
        }
        for (TableInfo tableInfo : tableInfos) {

            Class<?> entityClass = tableInfo.getEntityType();
            if (entityClass != null) {
                entityRegisters.forEach(entityRegister -> {
                    entityRegister.register(entityClass);
                });
            }
        }
    }
}
