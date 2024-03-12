package com.tangzc.mpe.autotable;

import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceProperties;
import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import com.tangzc.autotable.core.dynamicds.IDataSourceHandler;
import com.tangzc.mpe.autotable.annotation.Table;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.AnnotatedElementUtils;

/**
 * 多数据源模式
 *
 * @author don
 */
@Slf4j
@ConditionalOnClass(DynamicDataSourceProperties.class)
@ConditionalOnProperty(
        prefix = DynamicDataSourceProperties.PREFIX,
        name = {"enabled"},
        havingValue = "true",
        matchIfMissing = true
)
public class DynamicDatasourceHandler implements IDataSourceHandler<String> {

    @Autowired
    private DynamicDataSourceProperties dynamicDataSourceProperties;

    @Override
    public void useDataSource(String dsName) {
        // 设置数据源
        DynamicDataSourceContextHolder.push(dsName);
    }

    @Override
    public void clearDataSource(String serializable) {
        // 清空数据源配置
        DynamicDataSourceContextHolder.poll();
    }

    @NonNull
    @Override
    public String getDataSourceName(Class clazz) {

        Table tableAnno = AnnotatedElementUtils.findMergedAnnotation(clazz, Table.class);
        if (tableAnno != null) {
            return tableAnno.dsName();
        }
        return dynamicDataSourceProperties.getPrimary();
    }
}
