package org.dromara.mpe.autotable;

import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.baomidou.dynamic.datasource.ds.ItemDataSource;
import org.dromara.autotable.core.dynamicds.DataSourceInfoExtractor;
import org.dromara.autotable.core.dynamicds.DataSourceManager;

import javax.sql.DataSource;

public class CustomDataSourceInfoExtractor implements DataSourceInfoExtractor {

    @Override
    public DbInfo extract(DataSource dataSource) {

        // 从动态数据源中获取数据源信息，因为动态数据源只是兼容了获取connection，但是并不能获取到jdbc url，这里需要拿到真实的数据源
        if (dataSource instanceof DynamicRoutingDataSource) {
            String datasourceName = DataSourceManager.getDatasourceName();
            ItemDataSource currentDatasource = (ItemDataSource)((DynamicRoutingDataSource) dataSource).getDataSource(datasourceName);
            DataSource realDataSource = currentDatasource.getRealDataSource();
            return DataSourceInfoExtractor.super.extract(realDataSource);
        }

        return DataSourceInfoExtractor.super.extract(dataSource);
    }
}
