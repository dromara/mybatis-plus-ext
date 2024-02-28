package com.tangzc.mpe.autotable;

import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceProperties;
import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import com.tangzc.autotable.core.dynamicds.IDataSourceHandler;
import com.tangzc.mpe.autotable.annotation.Table;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotatedElementUtils;

import javax.annotation.Resource;

/**
 * 多数据源模式
 *
 * @author don
 */
@Slf4j
public class DynamicDatasourceHandler implements IDataSourceHandler<String> {
    @Resource
    private DynamicDataSourceProperties dynamicDataSourceProperties;

    @Override
    public void useDataSource(String dsName) {
        DynamicDataSourceContextHolder.push(dsName);
    }

    @Override
    public void clearDataSource(String serializable) {
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
