package com.tangzc.mybatis.datasource.metadata;

import com.tangzc.mybatis.datasource.metadata.annotation.DataSource;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.lang.reflect.Field;

/**
 * @author don
 */
@Data
@AllArgsConstructor
public class DataSourceDescription {

    private Class<?> entityClass;

    private Field entityField;

    private DataSource dataSource;
}
